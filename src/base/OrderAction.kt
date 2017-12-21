package base

import base.spec.IOrder
import base.spec.IOrderAction
import base.spec.IOutbound

class OrderAction(private val outbound: IOutbound, private val order: IOrder) : IOrderAction {
    var submitSent = false
    var cancelSent = false

    override fun submit() {
        if (!submitSent) {
            outbound.onNewOrder(order)
            submitSent = true
        }
    }

    override fun cancel() {
        if (!cancelSent && submitSent) {
            outbound.onCancel(order)
            cancelSent = true
        }
    }

    override fun amend(prc: Double, qty: Long) {
        if (!cancelSent && submitSent) {
            outbound.onAmend(order, prc, qty)
        }
    }

    override fun forceCancel() {
        outbound.onCancel(order)
        cancelSent = true
    }
}


