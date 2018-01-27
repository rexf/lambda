package base.spec

interface IOutbound {
    fun getName() : String
    fun onNewOrder(order: IOrder)
    fun onCancel(order: IOrder)
    fun onAmend(order: IOrder, prc: Double, qty: Long)
}