package base.spec

interface IOutbound {
    fun onNewOrder(order: IOrder)
    fun onCancel(order: IOrder)
    fun onAmend(order: IOrder, prc: Double, qty: Long)
}