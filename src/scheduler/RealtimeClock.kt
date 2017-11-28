package scheduler

import org.joda.time.DateTime
import thread.IDispatcher
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class RealtimeClock(private val dispatcher: IDispatcher) : IClock {
    var executor = Executors.newScheduledThreadPool(1)


    override fun schedule(key: String, runnable: () -> Unit, delay: Long, unit: TimeUnit) {
        executor.schedule({ dispatcher.dispatch(key, runnable) }, delay, unit)
    }

    override fun schedule(key: String, runnable: () -> Unit, delay: Long, interval: Long, unit: TimeUnit) {

        executor.scheduleAtFixedRate({ dispatcher.dispatch(key, runnable) }, delay, interval, unit)
    }

    override fun schedule(key: String, runnable: () -> Unit, time: DateTime) {
        val diff = time.minus(getTime())

        schedule(key = key, runnable = runnable, delay = if (diff > 0) diff else 0)
    }

    override fun schedule(key: String, runnable: () -> Unit, time: DateTime, interval: Long, intervalUnit: TimeUnit) {
        val diff = time.minus(getTime())

        schedule(key = key, runnable = runnable, delay = if (diff > 0) diff else 0, interval = interval)
    }

    override fun getTime(): DateTime {
        return DateTime()
    }
}

private fun DateTime.minus(dateTime: DateTime): Long {
    return this.millis - dateTime.millis
}
