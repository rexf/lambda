package base

import base.spec.*
import main.AppMain
import org.jetbrains.kotlin.utils.addToStdlib.sumByLong
import java.util.*


data class Order(override val symbol: String,
                 override val side: IOrder.Side,
                 override val prc: Double,
                 override val qty: Long,
                 override val destination: String,
                 override val orderId: String = UUID.randomUUID().toString()) : IOrder {

    override val action: IOrderAction by lazy {
        OrderAction(outbound, this@Order)
    }

    override val response: IOrderResponse by lazy {
        OrderResponse(this@Order)
    }

    override val outbound: IOutbound by lazy {
        val outMap = AppMain.context.getBean("outbound.map", Map::class.java) as Map<String, IOutbound>
        outMap[destination]!!
    }

    override val fills: MutableList<IExecution> = mutableListOf()

    override fun remQty(): Long = (qty - fills.sumByLong { it.lastQty })

    override fun clone(): IOrder = Order(symbol, side, prc, qty, destination)
}