package container

import general.AlgoFramework
import general.AlgoState
import general.spec.IAlgo
import general.spec.IAlgoFramework
import io.vertx.core.AbstractVerticle
import io.vertx.core.Handler
import io.vertx.core.eventbus.Message
import io.vertx.core.http.ServerWebSocket
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import kt.KotlinAlgoLoader
import kt.KotlinCompiler
import org.apache.logging.log4j.LogManager
import scheduler.IClock
import thread.IDispatcher
import java.io.File
import java.net.URL
import java.net.URLClassLoader
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

    private var algoFramework: IAlgoFramework by Delegates.notNull()
    private val setOfWs = mutableSetOf<ServerWebSocket>()

    override fun start() {
        super.start()
        algoFramework = AlgoFramework(this)

        val router = Router.router(vertx)
        router.route().handler(BodyHandler.create().setUploadsDirectory("uploads").setDeleteUploadedFilesOnEnd(true))
        router.get("/").handler(this::getRoot)

        vertx.eventBus().consumer(JsonObject::class.qualifiedName, Handler<Message<JsonObject>> {
            val msg = it.body().toString()
            setOfWs.forEach { it.writeTextMessage(msg) }
        })


        vertx.createHttpServer().requestHandler(router::accept).listen(httpPort)
        vertx.createHttpServer().websocketHandler(this::handleWS).listen(wsPort)
        logger.info("Listening to port : Http( 8888 ) & Ws( 8889 )")
    }

    private fun handleWS(sws: ServerWebSocket) {
        logger.info("New WS connection... ${sws.remoteAddress()}")
        sws.binaryMessageHandler {
            val code = it.toString(Charset.defaultCharset())

            vertx.executeBlocking<String>({ future ->
                try {
                    sendText("source code received. size = ${code.length}, compiling...")

                    compileKotlin(code)
                    algoFramework.algo.state = AlgoState.Initialized

                    future.complete("loaded.")
                } catch (t: Throwable) {
                    t.printStackTrace()

                    future.fail("failed")
                }

            }, { result ->
                println(result.result())
                if (result.succeeded()) {
                    sendText("Running performance test")
                    perfTest(algo = algoFramework.algo, framework = algoFramework)
                    sendText("performance test done")
                }
            })

        }

        sws.textMessageHandler {

            println("text stream: " + it)

        }

        sws.closeHandler {
            println("Closing WS connection ... ${sws.remoteAddress()}")
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

    private fun compileKotlinClass(code: String) {
        val tmpPath = "/tmp/Example.kt/"
        File(tmpPath).deleteOnExit()
        File(tmpPath + "/inp/").mkdirs()
        File(tmpPath + "/out/").mkdirs()

        val inp = tmpPath + "/inp/Example.kt"
        val out = tmpPath + "/out/"
        with(File(inp)) {
            createNewFile()
            writeText(code, Charset.defaultCharset())
            deleteOnExit()
        }

        KotlinCompiler.compile(inp, out)
        File(out).deleteOnExit()

        val clazz = URLClassLoader(listOf(URL("file://$out")).toTypedArray()).loadClass("Example")
        algoFramework.algo = clazz.newInstance() as IAlgo

    }

    private fun compileKotlin(code: String) {
        try {
            algoFramework.algo = KotlinAlgoLoader(code)
            algoFramework.algo.state = AlgoState.Initialized

            return
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        algoFramework.algo.state = AlgoState.InitializationFailed

    }


    private fun perfTest(algo: IAlgo, framework: IAlgoFramework) {
        val now = System.currentTimeMillis()
        for (i in 1..10_000_000) {
            algo.handleEvent?.invoke()
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

