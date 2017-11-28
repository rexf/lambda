package general.spec

import general.AlgoController
import io.vertx.core.json.JsonObject
import scheduler.IClock

import java.util.function.BiConsumer
import java.util.function.Consumer

interface IAlgoFramework {
    val timer: IClock
    var algoController: AlgoController
    fun subscribeMarketData(param: JsonObject, consumer: BiConsumer<IAlgoFramework, IMarketData>? = null)
    fun subscribePosition(param: JsonObject, consumer: BiConsumer<IAlgoFramework, IPosition>? = null)
    fun subscribeOrderExecution(param: JsonObject, consumer: BiConsumer<IAlgoFramework, IExecution>? = null)

    fun setPeriodic(consumer: Consumer<IAlgoFramework>, delayInMs: Long, intervalInMs: Long)
    fun setOnce(consumer: Consumer<IAlgoFramework>, delayInMs: Long)

    fun sendMessage(message: Any)
    fun sendText(message: String)
}
