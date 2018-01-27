package base

import base.spec.IQuote
import org.joda.time.DateTime

data class Quote(override val sym: String,
                 override val bidQty: Long,
                 override val bid: Double,
                 override val ask: Double,
                 override val askQty: Long,
                 override val lastUpdateTime: DateTime = DateTime()) : IQuote