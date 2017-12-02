package main

import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import org.apache.logging.log4j.LogManager
import org.springframework.context.annotation.AnnotationConfigApplicationContext

val logger = LogManager.getLogger("main")!!
fun main(args: Array<String>) {

    val vertx = Vertx.vertx()
    val ctx = AnnotationConfigApplicationContext(AppConfig::class.java)
    val beans = ctx.getBeansOfType(AbstractVerticle::class.java)

    val opts = DeploymentOptions()
    opts.isWorker = true
    beans.entries.forEach { (n, v) ->
        logger.info("Deploying verticle [$n]")
        vertx.deployVerticle(v, opts)
    }
}

