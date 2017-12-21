package main

import base.thread.scheduler.IClock
import base.thread.scheduler.LoggerClock
import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import org.apache.logging.log4j.LogManager
import org.springframework.context.support.FileSystemXmlApplicationContext

val logger = LogManager.getLogger("main")!!
class AppMain {
    companion object {
        val context = FileSystemXmlApplicationContext("resources/config.xml")
        init {
            if (context.containsBean("referenceClock")) {
                LoggerClock.setClock(context.getBean("referenceClock", IClock::class.java))
                logger.info("Reference clock loaded.")
            }
        }
    }
}

fun main(args: Array<String>) {
    logger.info("Initializing beans...")

    val vertx = Vertx.vertx()
    val ctx = AppMain.context
    val beans = ctx.getBeansOfType(AbstractVerticle::class.java)

    val opts = DeploymentOptions()
    opts.isWorker = true
    beans.entries.forEach { (n, v) ->
        logger.info("Deploying verticle [$n]")
        vertx.deployVerticle(v, opts)
    }
}
