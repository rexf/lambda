package base

import base.spec.IExecution
import base.spec.IOrder
import base.spec.IOrderResponse
import org.apache.logging.log4j.LogManager

class OrderResponse(private val order: IOrder) : IOrderResponse {
    companion object {
        private val logger = LogManager.getLogger(IOrder::class)
    }

    override var onAck: ((IOrder) -> Unit) = {
        logger.info("OrderAcked: $it.")
    }

    override var onFill: ((IExecution) -> Unit) = {
        logger.info("OrderFill: ${it.order.orderId} - [$it]. #fills: ${it.order.fills.size}")
    }

    override var onCancel: ((IOrder) -> Unit) = {
        logger.info("OrderCxl: $it")
    }

    override var onReject: ((order: IOrder, reason: String) -> Unit) = { o, r ->
        logger.info("OrderRej: ${o.orderId} reason: $r")
    }

    override fun ack() = onAck(order)
    override fun fill(execution: IExecution) = onFill(execution)
    override fun cancel() = onCancel(order)
    override fun reject(reason: String) = onReject(order, reason)


}
