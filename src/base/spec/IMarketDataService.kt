package base.spec

interface IMarketDataService {
    fun subscribe(symbol: String,  k) : String
    fun unsubscribe(subId: String)
}