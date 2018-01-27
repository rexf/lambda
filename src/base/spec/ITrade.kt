package base.spec

import org.joda.time.DateTime

interface ITrade {
    val time: DateTime
    val sym: String
    val prc: Double
    val qty: Long
}