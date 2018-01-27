package base.thread

import com.google.common.collect.Queues
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.properties.Delegates

class Dispatcher(poolSize: Int = 4) : IDispatcher {
    private class Recycler {
        private val getCount = AtomicInteger(0)
        private val collectCount = AtomicInteger(0)
        private val q = Queues.newArrayDeque<RecyclableTask>()!!
        fun get(tq: TaskQueue, exe: Executor, runnable: () -> Unit): RecyclableTask {
            getCount.incrementAndGet()
            return (q.poll() ?: RecyclableTask()).init(tq, exe, runnable, this)
        }

        fun collect(task: RecyclableTask): Boolean {
            collectCount.incrementAndGet()
            return q.add(task)
        }

        override fun toString(): String {
            return "Recycler(get=$getCount, collect=$collectCount, q.size=${q.size})"
        }
    }

    private class TaskQueue(private val executor: Executor, private val recycler: Recycler) {
        private val queue = ConcurrentLinkedDeque<RecyclableTask>()
        private val counter = AtomicInteger(0)

        fun add(runnable: () -> Unit) {
            queue.add(recycler.get(this, executor, runnable))
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

        override fun toString(): String {
            return "TaskQueue(pending=${queue.size})"
        }
    }

    private class RecyclableTask : Runnable {
        private var queue: TaskQueue by Delegates.notNull()
        private var executor: Executor by Delegates.notNull()
        private var task: () -> Unit by Delegates.notNull()
        private var recycler: Recycler by Delegates.notNull()

        fun init(queue_: TaskQueue, executor_: Executor, task_: () -> Unit, recycler_: Recycler): RecyclableTask {
            this.queue = queue_
            this.executor = executor_
            this.task = task_
            this.recycler = recycler_
            return this
        }

        override fun run() {
            try {
                task()
            } catch (t: Throwable) {
                t.printStackTrace()
            }

            if (queue.decrementAndGet() > 0) {
                executor.execute(queue.poll())
            }

            recycler.collect(this)
        }
    }

    //    private val TASK_POOL_SIZE = poolSize * 1000
    private val recyler = Recycler()
    private val executor: Executor = Executors.newWorkStealingPool(poolSize)
    private val executorTaskPool = ConcurrentHashMap<String, TaskQueue>()


    override fun dispatch(key: String, runnable: () -> Unit) {
        executorTaskPool.computeIfAbsent(key, { _ -> TaskQueue(executor, recyler) }).add(runnable)
    }

    override fun toString(): String {
        return "pool=$executorTaskPool, recycler=$recyler"
    }

}

