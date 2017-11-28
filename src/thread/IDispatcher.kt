package thread

interface IDispatcher {
    fun dispatch(key: String, runnable: () -> Unit)
}