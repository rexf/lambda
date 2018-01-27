package base.thread

import org.apache.logging.log4j.LogManager


internal class DispatcherTest {
    companion object {
        private val logger = LogManager.getLogger(Dispatcher::class)!!
    }

    @org.junit.jupiter.api.Test
    fun dispatch() {
        val d = Dispatcher()
        var stats: String
        val mapCount = mutableMapOf<String, Int>()
        run {
            var k = "A"
            d.dispatch(k, { mapCount[k] = (mapCount.get(k) ?: 0) + 1; logger.info(k + "1 " + System.currentTimeMillis()); Thread.sleep(500) })
            d.dispatch(k, { mapCount[k] = (mapCount.get(k) ?: 0) + 1; logger.info(k + "2 " + System.currentTimeMillis()); Thread.sleep(500) })
            d.dispatch(k, { mapCount[k] = (mapCount.get(k) ?: 0) + 1; logger.info(k + "3 " + System.currentTimeMillis()); Thread.sleep(500) })
            d.dispatch(k, { mapCount[k] = (mapCount.get(k) ?: 0) + 1; logger.info(k + "4 " + System.currentTimeMillis()); Thread.sleep(500) })
        }
        Thread.sleep(700L)

        stats = d.toString()
        logger.info(stats)
        assert("pool={A=TaskQueue(pending=2)}, recycler=Recycler(get=4, collect=1, q.size=1)".equals(stats))

        run {
            var k = "B"
            d.dispatch(k, { mapCount[k] = (mapCount.get(k) ?: 0) + 1; logger.info(k + "1 " + System.currentTimeMillis()); Thread.sleep(500) })
            d.dispatch(k, { mapCount[k] = (mapCount.get(k) ?: 0) + 1; logger.info(k + "2 " + System.currentTimeMillis()); Thread.sleep(500) })
        }
        Thread.sleep(700L)

        stats = d.toString()
        logger.info(stats)
        assert("pool={A=TaskQueue(pending=1), B=TaskQueue(pending=0)}, recycler=Recycler(get=6, collect=3, q.size=2)".equals(stats))

        run {
            var k = "C"
            d.dispatch(k, { mapCount[k] = (mapCount.get(k) ?: 0) + 1; logger.info(k + "1 " + System.currentTimeMillis()); Thread.sleep(500) })

        }
        Thread.sleep(2000L)
        logger.info(mapCount)

        stats = d.toString()
        logger.info(stats)
        assert("pool={A=TaskQueue(pending=0), B=TaskQueue(pending=0), C=TaskQueue(pending=0)}, recycler=Recycler(get=7, collect=7, q.size=5)".equals(stats))
    }
}