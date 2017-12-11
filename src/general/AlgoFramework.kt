package general

import container.LambdaContainer
import io.vertx.core.json.JsonObject
import org.apache.logging.log4j.LogManager
import scheduler.IClock
import spec.IAlgo
import spec.IAlgoFramework
import kotlin.properties.Delegates

class AlgoFramework(private val lambda: LambdaContainer) : IAlgoFramework {
    companion object {
        private val logger = LogManager.getLogger(AlgoFramework::class)!!
    }

    override val clock: IClock
        get() = lambda.clock

    override var algo: IAlgo by Delegates.notNull()

    override fun subscribeMarketData(vararg rics: String) {

    }

    override fun subscribePosition(vararg books: String) {

    }

    override fun subscribeSignals(vararg types: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun subscribeExecutions() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setPeriodic(eventId: String, delayInMs: Long, intervalInMs: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setOnce(eventId: String, delayInMs: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun sendText(message: String) {
        val json = JsonObject()
        json.put("Algo Message", message)
        lambda.vertx.eventBus().publish(JsonObject::class.qualifiedName, json)
    }

    override fun log(message: String) {
        logger.info(message)
//        println(message)
    }

}