package base

import base.spec.IExecution
import org.joda.time.DateTime

data class Execution(override val sym: String, override val lastPx: Double, override val lastQty: Long, override val lastUpdateTime: DateTime = DateTime()) : IExecution {

}