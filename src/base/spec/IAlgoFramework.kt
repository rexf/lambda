package base.spec

import base.thread.scheduler.IClock


interface IAlgoFramework {
    val clock: IClock
    var algo: IAlgo

    fun subscribeMarketData(vararg rics: String)
    fun subscribePosition(vararg books: String)
    fun subscribeSignals(vararg types: String)
    fun subscribeExecutions()

    fun setPeriodic(eventId: String, delayInMs: Long, intervalInMs: Long)
    fun setOnce(eventId: String, delayInMs: Long)

    fun sendText(message: String)
    fun log(message: String)
}
