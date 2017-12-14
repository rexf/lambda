package base.spec

import base.enumerate.AlgoState
import org.joda.time.DateTime

interface IAlgo {
    val code: String
    val lastUpdated: DateTime
    var state: AlgoState

    var handleEvent: (() -> Unit)?

    fun init(framework: IAlgoFramework)
}
