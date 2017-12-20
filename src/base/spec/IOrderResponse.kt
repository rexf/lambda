package base.spec

interface IOrderResponse {
    var onAck: ((IOrder) -> Unit)
    var onFill: ((IExecution) -> Unit)
    var onCancel: ((IOrder) -> Unit)
    var onReject: ((order: IOrder, reason: String) -> Unit)

    fun ack()
    fun fill(execution: IExecution)
    fun cancel()
    fun reject(reason: String)
}