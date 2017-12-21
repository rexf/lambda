package base.spec

import base.thread.scheduler.IClock


interface IAlgoFramework {
    val clock: IClock
    var algo: IAlgo

    fun subscribeMarketData(vararg symbols: String)
    fun subscribePosition(vararg books: String)
    fun subscribeSignals(vararg types: String)
    fun subscribeExecutions()

    fun setPeriodic(eventId: String, delayInMs: Long, intervalInMs: Long)
    fun setOnce(eventId: String, delayInMs: Long)

    fun sendText(message: String)
    fun prepareOrder(symbol: String, side: IOrder.Side, prc: Double, qty: Long, destination: String) : IOrder
    fun log(message: String)
}
