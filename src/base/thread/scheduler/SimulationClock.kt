package base.thread.scheduler

import base.thread.IDispatcher
import org.apache.logging.log4j.LogManager
import org.joda.time.DateTime
import java.util.concurrent.TimeUnit


class SimulationClock(private val dispatcher: IDispatcher) : IClock {
    private val referenceTime = System.currentTimeMillis()

    companion object {
        private val logger = LogManager.getLogger(SimulationClock::class)!!
    }

    private data class SimulationEvent(val key: String, val runnable: () -> Unit, val eventTime: Long, val interval: Long = 0, val unit: TimeUnit = TimeUnit.MILLISECONDS)

    private val list = sortedSetOf(comparator = compareBy<SimulationEvent> { it.eventTime })

    var currentTimeInMs: Long = 0

    override fun schedule(key: String, runnable: () -> Unit, delay: Long, unit: TimeUnit) {
        list.add(SimulationEvent(key = key, runnable = runnable, eventTime = convertDelayToFuture(delay, unit), unit = unit))
    }

    override fun schedule(key: String, runnable: () -> Unit, delay: Long, interval: Long, unit: TimeUnit) {
        list.add(SimulationEvent(key = key, runnable = runnable, eventTime = convertDelayToFuture(delay, unit), interval = interval, unit = unit))
    }

    override fun schedule(key: String, runnable: () -> Unit, time: DateTime) {
        list.add(SimulationEvent(key = key, runnable = runnable, eventTime = time.millis))
    }

    override fun schedule(key: String, runnable: () -> Unit, time: DateTime, interval: Long, intervalUnit: TimeUnit) {
        list.add(SimulationEvent(key = key, runnable = runnable, eventTime = time.millis, interval = interval, unit = intervalUnit))
    }

    override fun getTime(): DateTime {
        return DateTime(currentTimeInMs)
    }

    private fun convertDelayToFuture(delay: Long, unit: TimeUnit): Long {
        val adjust = delay.toInt()
        val moment = when (unit) {
            TimeUnit.MILLISECONDS -> getTime().plusMillis(adjust)
            TimeUnit.SECONDS -> getTime().plusSeconds(adjust)
            TimeUnit.MINUTES -> getTime().plusMinutes(adjust)
            TimeUnit.HOURS -> getTime().plusHours(adjust)
            TimeUnit.DAYS -> getTime().plusHours(adjust)
            else -> getTime()
        }
        return moment.millis
    }

    fun moveNext() {
        val evnt = list.pollFirst()
        evnt?.let {
            currentTimeInMs = it.eventTime

            if (logger.isDebugEnabled) {
                logger.debug("SimulationClock is now :${DateTime(currentTimeInMs).toLocalDateTime()}")
            }

            dispatcher.dispatch(it.key, it.runnable)
            if (it.interval > 0) {
                schedule(it.key, it.runnable, it.interval, it.unit)
            }
        }
    }

}

