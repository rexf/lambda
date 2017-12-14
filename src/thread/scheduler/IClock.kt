package thread.scheduler

import org.joda.time.DateTime
import java.util.concurrent.TimeUnit

interface IClock {
    fun getTime(): DateTime
    fun schedule(key: String, runnable: () -> Unit, delay: Long, unit: TimeUnit = TimeUnit.MILLISECONDS)
    fun schedule(key: String, runnable: () -> Unit, delay: Long, interval: Long, unit: TimeUnit = TimeUnit.MILLISECONDS)
    fun schedule(key: String, runnable: () -> Unit, time: DateTime)
    fun schedule(key: String, runnable: () -> Unit, time: DateTime, interval: Long, intervalUnit: TimeUnit)
}