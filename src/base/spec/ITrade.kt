package base.spec

import org.joda.time.DateTime

interface ITrade {
    val time: DateTime
    val id: String
    val prc: Double
    val qty: Long
}