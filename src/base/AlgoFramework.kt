package base

import base.container.LambdaContainer
import base.spec.*
import base.thread.scheduler.IClock
import io.vertx.core.json.JsonObject
import org.apache.logging.log4j.LogManager
import simulator.Exchange
import kotlin.properties.Delegates

class AlgoFramework(private val lambda: LambdaContainer) : IAlgoFramework {

    companion object {
        private val logger = LogManager.getLogger(AlgoFramework::class)!!
    }

    val outbound = Exchange(lambda.dispatcher, lambda.clock, 2)

    override val clock: IClock
        get() = lambda.clock

    override var algo: IAlgo by Delegates.notNull()

    override fun subscribeMarketData(vararg symbols: String) {

    }

    override fun subscribePosition(vararg books: String) {

    }

    override fun subscribeSignals(vararg types: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun subscribeExecutions() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setPeriodic(eventId: String, delayInMs: Long, intervalInMs: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setOnce(eventId: String, delayInMs: Long) {
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

    override fun prepareOrder(symbol: String, side: IOrder.Side, prc: Double, qty: Long): IOrder {
        return Order(symbol, side, prc, qty, outbound)
    }

}