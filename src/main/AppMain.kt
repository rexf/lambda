package main

import base.thread.scheduler.IClock
import base.thread.scheduler.LoggerClock
import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import org.apache.logging.log4j.LogManager
import org.springframework.context.annotation.AnnotationConfigApplicationContext

fun main(args: Array<String>) {
    val logger = LogManager.getLogger("main")!!
    logger.info("Initializing beans...")

    val vertx = Vertx.vertx()
    val ctx = AnnotationConfigApplicationContext(AppConfig::class.java)
    if (ctx.containsBean("referenceClock")) {
        LoggerClock.setClock(ctx.getBean("referenceClock", IClock::class.java))
        logger.info("Reference clock loaded.")
    }

    val beans = ctx.getBeansOfType(AbstractVerticle::class.java)

    val opts = DeploymentOptions()
    opts.isWorker = true
    beans.entries.forEach { (n, v) ->
        logger.info("Deploying verticle [$n]")
        vertx.deployVerticle(v, opts)
    }
}

