package base

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.apache.logging.log4j.LogManager
import java.io.BufferedReader
import java.io.FileReader

class CsvEventLoader {
    data class MapEntry<K, V>(val k: K, val v: V)

    companion object {
        private val logger = LogManager.getLogger(CsvEventLoader::class)!!

        fun <T> loadAsList(path: String, producer: (CSVRecord) -> T, comparator: Comparator<in T>? = null, skipIfFailed: Boolean = true): List<T> {
            val records = mutableListOf<T>()
            val parser = CSVParser(BufferedReader(FileReader(path)), CSVFormat.DEFAULT)
            logger.info(String.format("number of records: %d", parser.recordNumber))
            run parserLoop@ {
                parser.forEach {
                    try {
                        val r = producer(it)
                        records.add(r)
                    } catch (t: Throwable) {
                        t.printStackTrace()
                        if (!skipIfFailed) {
                            return@parserLoop
                        }
                        throw t
                    }
                }
            }

            if (null != comparator) {
                return records.sortedWith(comparator)
            }
            return records
        }

        fun <K, V> loadAsMap(path: String, producer: (CSVRecord) -> MapEntry<K, V>, skipIfFailed: Boolean = true): Map<K, V> {
            val records = mutableMapOf<K, V>()

            val parser = CSVParser(BufferedReader(FileReader(path)), CSVFormat.DEFAULT)
            logger.info(String.format("number of records: %d", parser.recordNumber))
            run parserLoop@ {
                parser.forEach {
                    try {
                        val (k, v) = producer(it)
                        records.put(k, v)
                    } catch (t: Throwable) {
                        t.printStackTrace()
                        if (!skipIfFailed) {
                            return@parserLoop
                        }
                        throw t
                    }

                }
            }
            return records

        }

    }

}