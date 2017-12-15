package base.container

import base.CsvEventLoader
import base.Quote
import base.thread.scheduler.IClock
import org.apache.commons.csv.CSVRecord
import org.apache.logging.log4j.LogManager
import org.joda.time.DateTime

class EventsLoaderContainer(private val clock: IClock) {
    companion object {
        private val logger = LogManager.getLogger(EventsLoaderContainer::class)!!
    }

    var marketDataFilePath: String? = null
    var tradesFilePath: String? = null
    var positionFilePath: String? = null

    fun init() {
        marketDataFilePath?.let {
            load(it, { rec ->
                // csv record: time, ric, bidQty, bid, ask, askQty
                val q = Quote(
                        rec[2],
                        rec[3].toLong(),
                        rec[4].toDouble(),
                        rec[5].toDouble(),
                        rec[6].toLong(),
                        DateTime.parse(rec[0].replace("/", "-") + "T" + rec[1]) // ISO Datetime format
                )
                clock.schedule(q.id, {
                }, q.lastUpdateTime.millis)
                q
            }, Quote::class.simpleName!!)
        }

        positionFilePath?.let {
        }
    }

    private fun <T> load(path: String, producer: (CSVRecord) -> T, name: String) {
        val msg = "${CsvEventLoader.loadAsList(path, producer).size} $name records loaded."
        logger.info(msg)
    }


}

fun main(args: Array<String>) {
    val d = DateTime.parse("2017-12-12T09:31:45.123")
    println(d.millis)
}