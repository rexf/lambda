package simulator

import base.Order
import base.spec.IOrder
import base.thread.Dispatcher
import base.thread.IDispatcher
import base.thread.scheduler.IClock
import base.thread.scheduler.RealtimeClock
import java.math.RoundingMode

class Exchange {
    private data class PriceList(private val orders: MutableList<IOrder> = mutableListOf()) {
        operator fun plusAssign(order: IOrder) {
            orders.add(order)
        }
    }
    private data class PriceLists(private val scale: Int, private val asc: Boolean, private val prcMap: MutableMap<Double, PriceList> = mutableMapOf()) {
        operator fun get(prc: Double): PriceList {
            val rounded = prc.toBigDecimal().setScale(scale, RoundingMode.HALF_UP).toDouble()
            return prcMap.getOrPut(rounded, { PriceList() })
        }

        fun cross(order: IOrder): IOrder {
            for (p in prcMap.keys.sorted()) {
                println(p)
            }
            return order
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

    fun crossOrAdd(order: IOrder) {
        var remainder: IOrder?
        remainder = orderBooks[order.sym][order.side.opposite()].cross(order)
        remainder?.let {
            orderBooks[it.sym][it.side][it.prc] += it
        }
        print(orderBooks)


    }
}


fun main(args: Array<String>) {
    val d = Dispatcher()
    val e = Exchange(dispatcher = d, clock = RealtimeClock(d), scale = 2)
    e.crossOrAdd(Order("0005.HK", IOrder.Side.Buy, 77.1, 1000))
    e.crossOrAdd(Order("0005.HK", IOrder.Side.Buy, 77.2, 1000))
    e.crossOrAdd(Order("0005.HK", IOrder.Side.Sell, 77.3, 1000))
    e.crossOrAdd(Order("0005.HK", IOrder.Side.Sell, 77.1, 100))
}