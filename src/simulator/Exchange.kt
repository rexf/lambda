package simulator

import base.Execution
import base.spec.IExecution
import base.spec.IOrder
import base.spec.IOutbound
import base.thread.IDispatcher
import base.thread.scheduler.IClock
import org.jetbrains.kotlin.backend.common.pop
import java.math.RoundingMode

class Exchange(private val dispatcher: IDispatcher, private val clock: IClock,
               private val name: String, scale: Int) : IOutbound {

    private data class PriceList(private val orders: MutableList<IOrder> = mutableListOf()) {
        operator fun plusAssign(order: IOrder?) {
            if (null != order) {
                orders.add(order)
            }
        }

        operator fun iterator() = orders.iterator()

        fun cleanFilled() {
            while (orders.isNotEmpty() && orders[0].remQty() <= 0) {
                println("${orders.pop()} is fully filled.")
            }
        }

        fun isNotEmpty() = orders.isNotEmpty()
    }

    private data class PriceLists(private val scale: Int,
                                  private val asc: Boolean,
                                  private val onFill: (IExecution, IExecution) -> Unit,
                                  private val prcMap: MutableMap<Double, PriceList> = mutableMapOf()) {
        operator fun get(prc: Double): PriceList {
            val rounded = prc.toBigDecimal().setScale(scale, RoundingMode.HALF_UP).toDouble()
            return prcMap.getOrPut(rounded, { PriceList() })
        }

        private fun nonEmptyMap() = prcMap.filter { (_, q) -> q.isNotEmpty() }

        fun cross(order: IOrder): IOrder? {
            val nonEmptyMap = nonEmptyMap()
            val (prcLists, factor) = if (asc) Pair(nonEmptyMap.keys.sorted(), 1)
            else Pair(nonEmptyMap.keys.sortedDescending(), -1)

            var remQty = order.remQty()

            val tmpOrdPrc = factor * order.prc
            for (p in prcLists) {
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

                        val targetQty = Math.min(remQty, o.qty)

                        val e1 = Execution(o, p, targetQty)
                        o.fills.add(e1)

                        val e2 = Execution(order, p, targetQty)
                        order.fills.add(e2)

                        onFill(e1, e2)

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

    private data class OrderBook(private val scale: Int,
                                 private val onFill: (IExecution, IExecution) -> Unit,
                                 private val bids: PriceLists = PriceLists(scale, false, onFill),
                                 private val asks: PriceLists = PriceLists(scale, true, onFill)) {
        operator fun get(side: IOrder.Side): PriceLists = if (IOrder.Side.Buy == side) bids else asks
    }

    private data class OrderBooks(private val scale: Int,
                                  private val onFill: (IExecution, IExecution) -> Unit,
                                  private val orderBooks: MutableMap<String, OrderBook> = mutableMapOf()) {
        operator fun get(id: String): OrderBook = orderBooks.getOrPut(id, { OrderBook(scale, onFill) })
    }


    private val orderBooks: OrderBooks
    override fun getName(): String = name
    override fun toString(): String {
        return "Exchange@${Exchange::class.simpleName}(name=$name)"
    }

    override fun onNewOrder(order: IOrder) {
        dispatcher.dispatch(order.symbol, {
            try {
                val remOrder = orderBooks[order.symbol][order.side.opposite()].cross(order)
                if (null != remOrder) {
                    if (remOrder.remQty() == remOrder.qty) {
                        order.response.ack()
                    }

                    orderBooks[order.symbol][order.side][order.prc] += remOrder
                }
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    override fun onCancel(order: IOrder) {
        dispatcher.dispatch(order.symbol, {
            try {
                val remOrder = orderBooks[order.symbol][order.side.opposite()].cross(order)
                if (null != remOrder) {
                    if (remOrder.remQty() == remOrder.qty) {
                        order.response.ack()
                    }

                    orderBooks[order.symbol][order.side][order.prc] += remOrder
                }
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        })

    }

    override fun onAmend(order: IOrder, prc: Double, qty: Long) {

    }

    private fun onFill(ex1: IExecution, ex2: IExecution) {
        dispatcher.dispatch(ex1.order.symbol, {
            ex1.order.response.onFill(ex1)
            ex2.order.response.onFill(ex2)
        })

    }

    init {
        orderBooks = OrderBooks(scale, this::onFill)
    }

}