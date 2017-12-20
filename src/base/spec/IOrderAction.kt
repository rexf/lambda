package base.spec

interface IOrderAction {
    fun submit()
    fun cancel()
    fun forceCancel()
    fun amend(prc: Double, qty: Long)
}