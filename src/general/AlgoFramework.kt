package general

import general.spec.IAlgoFramework
import general.spec.IExecution
import general.spec.IMarketData
import general.spec.IPosition
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import scheduler.IClock
import java.util.function.BiConsumer
import java.util.function.Consumer

class AlgoFramework(private val vertx: Vertx, override val timer: IClock) : IAlgoFramework {
    override var algoController = AlgoController(framework = this, lastModified = timer.getTime(), clazzName = "")

    override fun subscribeMarketData(param: JsonObject, consumer: BiConsumer<IAlgoFramework, IMarketData>?) {

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
        vertx.setTimer(delayInMs, { consumer.accept(this) })
    }

    override fun sendMessage(message: Any) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    init {
        vertx.setPeriodic(15000, {
            sendText("State: " + algoController.state.description)
        })

    }

    override fun sendText(msg: String) {
        val json = JsonObject()
        json.put("Algo Message", msg)
        vertx.eventBus().publish(JsonObject::class.qualifiedName, json)
    }

}