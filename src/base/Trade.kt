package base

import base.spec.ITrade
import org.joda.time.DateTime

data class Trade(
        override val id: String,
        override val prc: Double,
        override val qty: Long,
        override val time: DateTime) : ITrade