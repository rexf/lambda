package base

import base.container.LambdaContainer
import base.spec.*
import base.thread.scheduler.IClock
import io.vertx.core.json.JsonObject
import main.AppMain
import org.apache.logging.log4j.LogManager
import org.joda.time.DateTime
import simulator.Exchange
import kotlin.properties.Delegates

class AlgoFramework(private val lambda: LambdaContainer) : IAlgoFramework {

    companion object {
        private val logger = LogManager.getLogger(AlgoFramework::class)!!
    }

    override val clock: IClock = lambda.clock
    override var algo: IAlgo by Delegates.notNull()


    override fun subscribeMarketData(vararg symbols: String) {
        AppMain.marketDataService

    }

    override fun subscribePosition(vararg books: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun subscribeSignals(vararg types: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun subscribeExecutions() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setPeriodic(eventId: String, delayInMs: Long, intervalInMs: Long) {
        lambda.clock.schedule(eventId, {
            algo.handleTimerEvent?.invoke(eventId)
        }, delayInMs, intervalInMs )
    }
    override fun setPeriodic(eventId: String, datetime: DateTime, intervalInMs: Long) {
        lambda.clock.schedule(eventId, {
            algo.handleTimerEvent?.invoke(eventId)
        }, datetime, intervalInMs )
    }

    override fun setOnce(eventId: String, delayInMs: Long) {
        lambda.clock.schedule(eventId, {
            algo.handleTimerEvent?.invoke(eventId)
        }, delayInMs)
    }
    override fun setOnce(eventId: String, datetime: DateTime) {
        lambda.clock.schedule(eventId, {
            algo.handleTimerEvent?.invoke(eventId)
        }, datetime)
    }

    override fun sendText(message: String) {
        val json = JsonObject()
        json.put("Algo Message", message)
        lambda.vertx.eventBus().publish(JsonObject::class.qualifiedName, json)
    }

    override fun log(message: String) {
        logger.info(message)
    }

    override fun prepareOrder(symbol: String, side: IOrder.Side, prc: Double, qty: Long, destination: String): IOrder {
        return Order(symbol, side, prc, qty, destination)
    }

}