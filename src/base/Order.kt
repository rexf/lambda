package base

import base.spec.IExecution
import base.spec.IOrder
import org.jetbrains.kotlin.utils.addToStdlib.sumByLong

data class Order(override val sym: String,
                 override val side: IOrder.Side,
                 override val prc: Double,
                 override val qty: Long,
                 override val onResponse: (IExecution) -> Unit) : IOrder {
    override val fills: MutableList<IExecution> = mutableListOf()

    override fun remQty(): Long = (qty - fills.sumByLong{ it.lastQty })
}