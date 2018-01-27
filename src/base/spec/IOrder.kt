package base.spec

interface IOrder {

    enum class Side {
        Buy, Sell;

        fun opposite(): Side {
            if (Buy == this) {
                return Sell
            }
            return Buy
        }
    }

    val symbol: String
    val side: Side
    val prc: Double
    val qty: Long
    val destination: String
    val orderId: String

    val fills: MutableList<IExecution>
    val response: IOrderResponse
    val action: IOrderAction
    val outbound: IOutbound

    fun remQty(): Long
    fun clone(): IOrder
}