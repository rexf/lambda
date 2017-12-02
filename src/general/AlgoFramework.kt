package general

import container.LambdaContainer
import spec.IAlgo
import spec.IAlgoFramework
import spec.IExecution
import spec.IPosition
import io.vertx.core.json.JsonObject
import org.apache.logging.log4j.LogManager
import scheduler.IClock
import java.util.function.BiConsumer
import java.util.function.Consumer
import kotlin.properties.Delegates

class AlgoFramework(private val lambda: LambdaContainer) : IAlgoFramework {
    companion object {
        val logger = LogManager.getLogger(AlgoFramework::class)!!
    }

    override val clock: IClock
        get() = lambda.clock

    override var algo: IAlgo by Delegates.notNull()

    override fun subscribeMarketData(vararg rics: String) {

    }

    override fun subscribePosition(param: JsonObject, consumer: BiConsumer<IAlgoFramework, IPosition>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun subscribeOrderExecution(param: JsonObject, consumer: BiConsumer<IAlgoFramework, IExecution>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setPeriodic(consumer: Consumer<IAlgoFramework>, delayInMs: Long, intervalInMs: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setOnce(consumer: Consumer<IAlgoFramework>, delayInMs: Long) {
//        vertx.setTimer(delayInMs, { consumer.accept(this) })
    }

    override fun sendMessage(message: Any) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun sendText(message: String) {
        val json = JsonObject()
        json.put("Algo Message", message)
        lambda.vertx.eventBus().publish(JsonObject::class.qualifiedName, json)
    }

    override fun log(message: String) {
        logger.info(message)
//        println(message)
    }

}