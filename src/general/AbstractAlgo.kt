package general

import general.spec.IAlgo
import org.joda.time.DateTime
import kotlin.properties.Delegates

abstract class AbstractAlgo : IAlgo {
    override val code: String by Delegates.notNull()
    override val lastUpdated = DateTime()
    override var state: AlgoState by Delegates.notNull()
    override var handleEvent: (() -> Unit)? = null
}