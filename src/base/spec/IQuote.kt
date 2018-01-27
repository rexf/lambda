package base.spec

import org.joda.time.DateTime

interface IQuote {

    val sym: String

    val bidQty: Long

    val bid: Double

    val ask: Double

    val askQty: Long

    val lastUpdateTime: DateTime
}
