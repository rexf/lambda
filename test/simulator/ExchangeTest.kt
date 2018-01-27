package simulator

import base.Order
import base.spec.IOrder
import base.thread.Dispatcher
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ExchangeTest {

    @Test
    fun crossOrAdd() {
        val d = Dispatcher()
        Order("0005.HK", IOrder.Side.Buy, 77.1, 1000, "SIMULATOR").action.submit()
        Order("0005.HK", IOrder.Side.Buy, 77.2, 1000, "SIMULATOR").action.submit()
        Order("0005.HK", IOrder.Side.Sell, 77.3, 1000, "SIMULATOR").action.submit()
        Order("0005.HK", IOrder.Side.Sell, 77.1, 2000, "SIMULATOR").action.submit()

        Thread.sleep(2000L)
    }

    @Test
    fun expectNoOutbound() {
        assertThrows<NullPointerException> {
            Order("0005.HK", IOrder.Side.Sell, 77.1, 2000, "UNKNOWN").action.submit()
        }
    }
}