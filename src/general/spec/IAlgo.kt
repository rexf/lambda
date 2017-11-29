package general.spec

import general.AlgoState
import org.joda.time.DateTime

interface IAlgo {
    val code: String
    val lastUpdated: DateTime
    var state: AlgoState

    var handleEvent: (() -> Unit)?
}
