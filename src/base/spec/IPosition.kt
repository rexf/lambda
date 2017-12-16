package base.spec

import org.joda.time.DateTime

interface IPosition {
    val sym: String
    val qty: Long
    val time: DateTime
}

