package thread

import java.util.*
import java.util.function.Consumer

class MultiplexInbox(private val name: String, private val dispatcher: IDispatcher) {
    private val mapper = mutableMapOf<Class<Any>, Consumer<Any>>()

    constructor(dispatcher: IDispatcher) : this(UUID.randomUUID().toString(), dispatcher)

    fun register(clazz: Class<Any>, consumer: Consumer<Any>) {
        mapper.putIfAbsent(clazz, consumer)
    }

    fun deliver(o: Any) {
        val c = mapper[o.javaClass]
        if (null != c) {
            dispatcher.dispatch(name) {
                c.accept(o)
            }
        }
    }
}
