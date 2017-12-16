package base

import base.spec.IOrder

data class Order(override val sym: String, override val side: IOrder.Side, override val prc: Double, override val qty: Long) : IOrder