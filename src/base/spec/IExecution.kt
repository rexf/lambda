package base.spec

import org.joda.time.DateTime

interface IExecution {
    val order: IOrder
    val lastPx: Double
    val lastQty: Long
    val lastUpdateTime: DateTime

}
