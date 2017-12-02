package container

import spec.IEventLoader
import io.vertx.core.AbstractVerticle

class FileEventLoader : AbstractVerticle(), IEventLoader {
    override fun loadAll() {

    }
}