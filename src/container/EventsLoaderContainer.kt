package container

import general.CsvEventLoader
import general.Quote
import org.apache.commons.csv.CSVRecord
import org.joda.time.DateTime
import scheduler.IClock

class EventsLoaderContainer(private val clock: IClock) {
    var marketDataFilePath: String? = null
    var positionFilePath: String? = null

    fun init() {
        marketDataFilePath?.let {
            load(it, {rec ->
                // csv record: time, ric, bidQty, bid, ask, askQty
                val q = Quote(rec[1], rec[2].toLong(), rec[3].toDouble(), rec[4].toDouble(), rec[5].toLong(), DateTime.parse(rec[0]))
                clock.schedule(q.id, {
                }, q.lastUpdateTime.millis)
                q
            })
        }

        positionFilePath?.let {

        }
    }

    private fun load(path: String, producer: ((CSVRecord)->Unit)) {
        CsvEventLoader.loadAsList(path, producer)
    }

}

fun main(args: Array<String>) {
}