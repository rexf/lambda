package general

import general.spec.IAlgo
import org.joda.time.DateTime

abstract class AbstractAlgo: IAlgo {
    override val code: String
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val lastUpdated: DateTime
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override var state: AlgoState
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}
    override var handleEvent: (() -> Unit)?
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}
}