package base.template

import base.enumerate.AlgoState
import base.spec.IAlgo
import base.spec.IAlgoFramework
import org.joda.time.DateTime
import java.lang.reflect.Method
import kotlin.properties.Delegates

abstract class AbstractAlgo : IAlgo {
    override val code: String by Delegates.notNull()
    override val lastUpdated = DateTime()
    override lateinit var state: AlgoState
    override var handleEvent: (() -> Unit)? = null
    override var handleTimerEvent: ((String) -> Unit)? = null
    protected var mappedFunction: Map<String, Method> by Delegates.notNull()

    override fun init(framework: IAlgoFramework) {

        try {
            val initFunc = mappedFunction["algoMain"]
            initFunc!!.invoke(null, framework)
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }
}