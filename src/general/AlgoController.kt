package general

import general.spec.IAlgo
import general.spec.IAlgoFramework
import org.joda.time.DateTime

class AlgoController(private val framework: IAlgoFramework, var lastModified: DateTime? = null, var clazzName: String? = null) {
    var algo: IAlgo? = null
        set(value) {
            field = value
            lastModified = DateTime.now()
        }

    var state = AlgoState.NoAlgo
    var code: String = ""


    fun handleEvent() {

    }

    fun start() {
        algo?.onStart(framework)
    }
}
