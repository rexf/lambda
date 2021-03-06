package simulator

import base.Order
import base.spec.IOrder
import base.thread.Dispatcher
import base.thread.scheduler.RealtimeClock
import org.junit.jupiter.api.Test

internal class ExchangeTest {

    @Test
    fun crossOrAdd() {
        val d = Dispatcher()
        val e = Exchange(dispatcher = d, clock = RealtimeClock(d), scale = 2)
        e.onNewOrder(Order("0005.HK", IOrder.Side.Buy, 77.1, 1000, {println(it)}))
        e.onNewOrder(Order("0005.HK", IOrder.Side.Buy, 77.2, 1000, {println(it)}))
        e.onNewOrder(Order("0005.HK", IOrder.Side.Sell, 77.3, 1000, {println(it)}))
        e.onNewOrder(Order("0005.HK", IOrder.Side.Sell, 77.1, 100, { println(it)}))
    }
}