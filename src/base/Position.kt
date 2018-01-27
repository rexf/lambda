package base

import base.spec.IPosition
import org.joda.time.DateTime

data class Position(override val sym: String, override val qty: Long, override val time: DateTime) : IPosition