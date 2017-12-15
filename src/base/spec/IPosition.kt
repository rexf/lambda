package base.spec

import org.joda.time.DateTime

interface IPosition {
    val id: String
    val qty: Long
    val time: DateTime
}
