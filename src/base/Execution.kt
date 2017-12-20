package base

import base.spec.IExecution
import base.spec.IOrder
import org.joda.time.DateTime

data class Execution(override val order: IOrder,
                     override val lastPx: Double,
                     override val lastQty: Long,
                     override val lastUpdateTime: DateTime = DateTime()) : IExecution
