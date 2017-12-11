package container

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.apache.logging.log4j.LogManager
import spec.IEventLoader
import java.io.BufferedReader
import java.io.FileReader


class CsvEventLoader : AbstractVerticle(), IEventLoader<CSVRecord> {

    companion object {
        private val logger = LogManager.getLogger(CsvEventLoader::class)!!
    }

    var path: String? = null

    override fun <T> loadAsList(producer: (CSVRecord) -> T, comparator: Comparator<in T>?, skipIfFailed: Boolean): List<T> {
        val listOfRecords = mutableListOf<T>()
        path?.let {
            val parser = CSVParser(BufferedReader(FileReader(it)), CSVFormat.DEFAULT)
            logger.info(String.format("number of records: %d", parser.recordNumber))
            parser.forEach {
                try {
                    val r = producer.invoke(it)
                    listOfRecords.add(r)
                } catch (t: Throwable) {
                    t.printStackTrace()
                    if (!skipIfFailed) {
                        return@forEach
                    }
                    throw t
                }

            }
        }

        if (null != comparator) {
            return listOfRecords.sortedWith(comparator)
        }
        return listOfRecords
    }

    override fun <K, V> loadAsMap(producer: (CSVRecord) -> IEventLoader.MapEntry<K, V>, skipIfFailed: Boolean): Map<K, V> {
        val mapOfRecords = mutableMapOf<K, V>()

        path?.let {
            val parser = CSVParser(BufferedReader(FileReader(it)), CSVFormat.DEFAULT)
            logger.info(String.format("number of records: %d", parser.recordNumber))
            parser.forEach {
                try {
                    val r = producer.invoke(it)
                    mapOfRecords.put(r.key, r.value)
                } catch (t: Throwable) {
                    t.printStackTrace()
                    if (!skipIfFailed) {
                        return@forEach
                    }
                    throw t
                }

            }
        }
        return mapOfRecords

    }

    override fun start() {
        super.start()

        vertx.executeBlocking({ event: Future<String> ->
            val listOfRecord = loadAsList( {rec ->

            })


        },
                {})


    }
}