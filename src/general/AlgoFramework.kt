package general

import container.LambdaContainer
import general.spec.IAlgo
import general.spec.IAlgoFramework
import general.spec.IExecution
import general.spec.IPosition
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import scheduler.IClock
import java.util.function.BiConsumer
import java.util.function.Consumer
import kotlin.properties.Delegates

class AlgoFramework(private val lambda: LambdaContainer) : IAlgoFramework {
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
        Vertx.vertx().eventBus().publish(JsonObject::class.qualifiedName, json)
    }

}