package base.spec

import base.enumerate.AlgoState
import org.joda.time.DateTime

interface IAlgo {
    val code: String
    val lastUpdated: DateTime
    var state: AlgoState

    var handleEvent: (() -> Unit)?
    var handleTimerEvent: ((String) -> Unit)?

    fun init(framework: IAlgoFramework)
}
