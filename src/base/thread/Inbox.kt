package base.thread

import java.util.*

class Inbox<T>(private val name: String = UUID.randomUUID().toString(), private val task: (t: T) -> Unit, private val dispatcher: IDispatcher) {

    fun deliver(msg: T) {
        dispatcher.dispatch(name, { task(msg) })
    }
}