package kt

import general.spec.AlgoSpec
import general.spec.IAlgo
import general.spec.IAlgoFramework
import java.io.File
import java.lang.reflect.Method
import java.net.URL
import java.net.URLClassLoader
import java.nio.charset.Charset
import java.nio.file.Files
import kotlin.properties.Delegates

class KotlinAlgoLoader : IAlgo {

    private var _code: String by Delegates.notNull()
    private var mappedFunction: Map<String, Method> by Delegates.notNull()

    private constructor()

    constructor(code: String) : this() {
        _code = code
        val path = Files.createTempDirectory("kotlin_algo_")
        path.toFile().deleteOnExit()
        val tmpPath = path.toString()

        val inpPath = tmpPath + File.separator + "inp" + File.separator
        val outPath = tmpPath + File.separator + "out" + File.separator

        with(File(outPath)) {
            if (!exists())
                mkdirs()
            deleteOnExit()
        }

        with(File(inpPath)) {
            if (!exists())
                mkdirs()
            deleteOnExit()

            val algoFile = inpPath + "Algo.kt"
            with(File(algoFile)) {
                createNewFile()
                writeText(code, Charset.defaultCharset())

                deleteOnExit()

                KotlinCompiler.compile(algoFile, outPath)
                val clazz = URLClassLoader(listOf(URL("file://$outPath")).toTypedArray()).loadClass("AlgoKt")
                mappedFunction = clazz.declaredMethods.associateBy({ it.name }, { it })
            }
        }
    }

    override fun onStart(framework: IAlgoFramework): Boolean {
        try {
            mappedFunction["algoMain"]?.invoke(null, framework)
            return true
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        return false
    }

    override fun onShutdown(framework: IAlgoFramework) {

    }

    override fun handleTick(framework: IAlgoFramework, bid: Double, ask: Double) {
        AlgoSpec.handleTick?.invoke(framework)
    }
}