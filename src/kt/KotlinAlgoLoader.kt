package kt

import general.AbstractAlgo
import general.AlgoState
import general.spec.IAlgoFramework
import java.io.File
import java.lang.reflect.Method
import java.net.URL
import java.net.URLClassLoader
import java.nio.charset.Charset
import java.nio.file.Files
import kotlin.properties.Delegates

class KotlinAlgoLoader : AbstractAlgo {

    //    private var _code: String by Delegates.notNull()
    private var mappedFunction: Map<String, Method> by Delegates.notNull()

    private constructor() : super()

    constructor(fw: IAlgoFramework, code: String) : this() {
        fw.algo = this
        state = AlgoState.Initializing

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
                val initFunc = mappedFunction["algoMain"]
                try {
                    initFunc!!.invoke(null, fw)
                } catch (t: Throwable) {
                    t.printStackTrace()
                }
            }
        }
    }
}