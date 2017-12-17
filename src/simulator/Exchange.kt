package simulator

import base.Execution
import base.spec.IExecution
import base.spec.IOrder
import base.thread.IDispatcher
import base.thread.scheduler.IClock
import org.jetbrains.kotlin.backend.common.pop
import java.math.RoundingMode

class Exchange {
    private data class PriceList(private val orders: MutableList<IOrder> = mutableListOf()) {
        operator fun plusAssign(order: IOrder) {
            orders.add(order)
        }

        operator fun iterator() = orders.iterator()

        fun cleanFilled() {
            while (orders.isNotEmpty() && orders[0].remQty() <= 0) {
                println("${orders.pop()} is fully filled.")
            }
        }

        fun isNotEmpty() = orders.isNotEmpty()
    }

    private data class PriceLists(private val scale: Int, private val asc: Boolean, private val prcMap: MutableMap<Double, PriceList> = mutableMapOf()) {
        operator fun get(prc: Double): PriceList {
            val rounded = prc.toBigDecimal().setScale(scale, RoundingMode.HALF_UP).toDouble()
            return prcMap.getOrPut(rounded, { PriceList() })
        }

        fun cross(order: IOrder): IOrder? {
            val prcList = if (asc) prcMap.keys.sorted() else prcMap.keys.sortedDescending()
            var remQty = order.remQty()

            val factor = if (asc) 1 else -1
            val tmpOrdPrc = factor * order.prc
            for (p in prcList) {
                val tmpQuePrc = factor * p

                if (remQty <= 0 || tmpOrdPrc < tmpQuePrc) {
                    break
                }

                val prcList = prcMap[p]

                if (null != prcList && prcList.isNotEmpty()) {
                    for (o in prcList) {
                        if (remQty <= 0) {
                            break
                        }

                        println("canCross")
                        val targetQty = Math.min(remQty, o.qty)
                        o.fills.add(Execution(o.sym, p, targetQty))
                        order.fills.add(Execution(o.sym, p, targetQty))

                        remQty = order.remQty()
                    }
                    prcList.cleanFilled()
                }
            }

            if (order.remQty() > 0) {
                return order
            }

            return null
        }
    }

    private data class OrderBook(private val scale: Int, private val bids: PriceLists = PriceLists(scale, false),
                                 private val asks: PriceLists = PriceLists(scale, true)) {
        operator fun get(side: IOrder.Side): PriceLists = if (IOrder.Side.Buy == side) bids else asks
    }

    private data class OrderBooks(private val scale: Int, private val orderBooks: MutableMap<String, OrderBook> = mutableMapOf()) {
        operator fun get(id: String): OrderBook = orderBooks.getOrPut(id, { OrderBook(scale) })
    }


    private val dispatcher: IDispatcher
    private val clock: IClock
    private val orderBooks: OrderBooks

    constructor(dispatcher: IDispatcher, clock: IClock, scale: Int) {
        this.dispatcher = dispatcher
        this.clock = clock
        this.orderBooks = OrderBooks(scale)
    }

    fun onNewOrder(order: IOrder) {
        // TODO: register sender address

        dispatcher.dispatch(order.sym, {
            var remainder = orderBooks[order.sym][order.side.opposite()].cross(order)
            remainder?.let {
                orderBooks[it.sym][it.side][it.prc] += it
            }
        })

    }

    fun onFill(execution: IExecution) {

        dispatcher.dispatch(execution.sym, {
            // TODO: send back to sender address
            println(execution)
        })

    }
}
