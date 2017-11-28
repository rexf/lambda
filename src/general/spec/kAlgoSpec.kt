package general.spec


class kAlgoSpec {
    companion object {
        var handleTick: ((framework: IAlgoFramework, bid: Double, ask: Double) -> Unit)? = null
    }
}