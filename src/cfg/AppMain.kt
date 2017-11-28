package cfg

import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import org.apache.logging.log4j.LogManager
import org.springframework.context.annotation.AnnotationConfigApplicationContext

fun main(args: Array<String>) {
    val logger = LogManager.getLogger("main")

    val ctx = AnnotationConfigApplicationContext(AppConfig::class.java)
    val beans  = ctx.getBeansOfType(AbstractVerticle::class.java)


    val opts = DeploymentOptions()
    opts.setWorker(true)
    beans.entries.forEach{ (n, v) ->
        logger.info("Deploying verticle [$n]")
        Vertx.vertx().deployVerticle(v, opts)
    }
}

