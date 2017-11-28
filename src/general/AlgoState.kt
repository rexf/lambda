package general

enum class AlgoState(val description: String) {
    NoAlgo("No algo loaded."),
    Initializing("Algo is initializing"),
    Initialized("Algo has been initialized"),
    InitializationFailed("Algo is failed to initialize"),
    Running("Algo is running"),
    Paused("Algo has been paused."),
    Exception("Uncaught exception from algo"),
    Terminated("Algo is terminated")
}
