package base.spec

interface IOrder {
    enum class Side {
        Buy, Sell;
        fun opposite() : Side {
            if (Buy == this) {
                return Sell
            }
            return Buy
        }
    }

    val sym: String
    val side: Side
    val prc: Double
    val qty: Long

    val fills: MutableList<IExecution>
    val onResponse: (IExecution)->Unit

    fun remQty() : Long
}