package base.spec

import org.joda.time.DateTime

interface IExecution {
    val sym: String
    val lastPx: Double
    val lastQty: Long
    val lastUpdateTime: DateTime

}
