package general.spec

interface IAlgo {
    fun onStart(framework: IAlgoFramework): Boolean
    fun onShutdown(framework: IAlgoFramework)
    fun handleTick(framework: IAlgoFramework, bid: Double, ask: Double)
}
