package container

import general.AlgoFramework
import enumerate.AlgoState
import spec.IAlgoFramework
import io.vertx.core.AbstractVerticle
import io.vertx.core.Handler
import io.vertx.core.eventbus.Message
import io.vertx.core.http.ServerWebSocket
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import kt.KotlinAlgoLoader
import org.apache.logging.log4j.LogManager
import scheduler.IClock
import thread.IDispatcher
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.properties.Delegates


class LambdaContainer(val dispatcher: IDispatcher,
                      val clock: IClock,
                      private val httpPort: Int,
                      private val wsPort: Int) : AbstractVerticle() {
    companion object {
        val logger = LogManager.getLogger(LambdaContainer::class)
    }

    private var framework: IAlgoFramework by Delegates.notNull()
    private val setOfWs = mutableSetOf<ServerWebSocket>()

    override fun start() {
        super.start()
        framework = AlgoFramework(this)

        val router = Router.router(vertx)

        // should not support file upload by GET/POST here
        // router.route().handler(BodyHandler.create().setUploadsDirectory("uploads").setDeleteUploadedFilesOnEnd(true))

        router.get("/").handler(this::getRoot)

        vertx.eventBus().consumer(JsonObject::class.qualifiedName, Handler<Message<JsonObject>> {
            val msg = it.body().toString()
            setOfWs.forEach {
                try {
                    it.writeTextMessage(msg)
                } catch (t: Throwable) {
                    t.printStackTrace()
                }
            }
        })


        vertx.createHttpServer().requestHandler(router::accept).listen(httpPort)
        vertx.createHttpServer().websocketHandler(this::handleWS).listen(wsPort)
        logger.info("Listening to port : Http( $httpPort) & Ws( $wsPort )")
    }

    private fun handleWS(sws: ServerWebSocket) {
        logger.info("New WS connection... ${sws.remoteAddress()}")
        sws.binaryMessageHandler {
            val code = it.toString(Charset.defaultCharset())

            vertx.executeBlocking<String>({ future ->
                try {
                    sendText("source code received. size = ${code.length}, compiling...")

                    loadKotlinAlgo(code)
                    framework.algo.state = AlgoState.Initialized

                    future.complete("loaded.")
                } catch (t: Throwable) {
                    t.printStackTrace()

                    future.fail("failed")
                }

            }, { result ->
                logger.info(result.result())
                if (result.succeeded()) {
                    sendText("Running performance test")
                    perfTest(framework)
                    sendText("performance test done")
                }
            })
        }

        sws.textMessageHandler {
            // Do not expect text stream
            logger.info("text stream: " + it)
        }

        sws.closeHandler {
            logger.info("Closing WS connection ... ${sws.remoteAddress()}")
            setOfWs.remove(sws)
        }
        sws.exceptionHandler {
            println("Error! Removing WS connection ... ${sws.remoteAddress()}")
            setOfWs.remove(sws)
        }
        setOfWs.add(sws)
        sendText("Your host ${sws.remoteAddress().host()}")
    }

    private fun sendText(msg: String) {
        val json = JsonObject()
        json.put("Container Output", msg)
        vertx.eventBus().publish(JsonObject::class.qualifiedName, json)
    }

    private fun loadKotlinAlgo(code: String) {
        with(framework) {
            try {
                algo = KotlinAlgoLoader(code)
                algo.init(framework)

                algo.state = AlgoState.Initialized


                return
            } catch (t: Throwable) {
                t.printStackTrace()
            }
            algo.state = AlgoState.InitializationFailed
        }
    }


    private fun perfTest(framework: IAlgoFramework) {
        val now = System.currentTimeMillis()
        for (i in 1..10_000_000) {
            framework.algo.handleEvent?.invoke()
        }
        framework.sendText("Time spent: ${System.currentTimeMillis() - now}")
    }

    private fun getRoot(ctx: RoutingContext) {
        val response = ctx.response()
        response.putHeader("content-type", "text/html; charset=utf-8")
        val ba = Files.readAllBytes(Paths.get("resources/web/index.html"))
        val content = ba.toString(Charset.forName("UTF-8"))
        response.end(content)

    }
}

