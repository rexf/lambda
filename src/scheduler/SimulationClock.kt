package scheduler

import org.joda.time.DateTime
import thread.IDispatcher
import java.util.concurrent.TimeUnit

class SimulationClock(private val dispatcher: IDispatcher) : IClock {

    private val list = sortedSetOf(comparator = compareBy<SimulationEvent> { it.eventTime })

    private var currentTimeInMs: Long = 0

    override fun schedule(key: String, runnable: () -> Unit, delay: Long, unit: TimeUnit) {
        list.add(SimulationEvent(key = key, runnable = runnable, eventTime = convertDelayToFuture(delay, unit), interval = 0, unit = unit))
    }

    override fun schedule(key: String, runnable: () -> Unit, delay: Long, interval: Long, unit: TimeUnit) {
        list.add(SimulationEvent(key = key, runnable = runnable, eventTime = convertDelayToFuture(delay, unit), interval = interval, unit = unit))
    }

    override fun schedule(key: String, runnable: () -> Unit, time: DateTime) {

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun schedule(key: String, runnable: () -> Unit, time: DateTime, interval: Long, intervalUnit: TimeUnit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
            dispatcher.dispatch(it.key, it.runnable)
            if (it.interval > 0) {
                schedule(it.key, it.runnable, it.interval, it.unit)
            }
        }
    }

}

data class SimulationEvent(val key: String, val runnable: () -> Unit, val eventTime: Long, val interval: Long, val unit: TimeUnit)