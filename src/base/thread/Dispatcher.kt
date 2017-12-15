package base.thread

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

class Dispatcher(poolSize: Int = 4) : IDispatcher {
    //    private val TASK_POOL_SIZE = poolSize * 1000
    private val executor: Executor = Executors.newWorkStealingPool(poolSize)
    private val executorTaskPool = ConcurrentHashMap<String, TaskQueue>()
//    private val taskPool = ConcurrentLinkedDeque<RecyclableTask>()


    override fun dispatch(key: String, runnable: () -> Unit) {
        executorTaskPool.computeIfAbsent(key, { _ -> TaskQueue(executor) }).add(runnable)
    }

}

class TaskQueue(private val executor: Executor) {
    private val queue = ConcurrentLinkedDeque<RecyclableTask>()
    private val counter = AtomicInteger(0)

    fun add(runnable: () -> Unit) {
        queue.add(RecyclableTask(this, executor, runnable))
        if (1 == counter.incrementAndGet()) {
            executor.execute(queue.poll())
        }
    }

    fun poll(): Runnable {
        return queue.poll()
    }

    fun decrementAndGet(): Int {
        return counter.decrementAndGet()
    }
}

class RecyclableTask(private val queue: TaskQueue, private val executor: Executor, private val task: () -> Unit) : Runnable {
    override fun run() {
        try {
            task()
        } catch (t: Throwable) {
            t.printStackTrace()
        }

        if (queue.decrementAndGet() > 0) {
            executor.execute(queue.poll())
        }

        // recycle here?
    }
}

