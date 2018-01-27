package base.thread.scheduler

import org.apache.logging.log4j.core.util.Clock

class LoggerClock : Clock {
    companion object {
        var referenceClock: IClock? = null

        fun setClock(clock: IClock) {
            referenceClock = clock
        }
    }


    override fun currentTimeMillis() = if (null != referenceClock) referenceClock!!.getTime().millis else System.currentTimeMillis()

}