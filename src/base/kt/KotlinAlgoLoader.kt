package base.kt

import base.enumerate.AlgoState
import base.template.AbstractAlgo
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.nio.charset.Charset
import java.nio.file.Files

class KotlinAlgoLoader : AbstractAlgo {


    private constructor() : super()

    constructor(code: String) : this() {
        state = AlgoState.Initializing

        val path = Files.createTempDirectory("kotlin_algo_")
        path.toFile().deleteOnExit()
        val tmpPath = path.toString()

        val inpPath = tmpPath + File.separator + "inp" + File.separator
        val outPath = tmpPath + File.separator + "out" + File.separator

        with(File(outPath)) {
            if (!exists())
                mkdirs()
        }

        with(File(inpPath)) {
            if (!exists())
                mkdirs()

            val algoFile = inpPath + "Algo.base.kt"
            with(File(algoFile)) {
                createNewFile()
                writeText(code, Charset.defaultCharset())

                if (KotlinCompiler.compile(algoFile, outPath)) {
                    val clazz = URLClassLoader(listOf(URL("file://$outPath")).toTypedArray()).loadClass("AlgoKt")
                    mappedFunction = clazz.declaredMethods.associateBy({ it.name }, { it })
                }
            }
        }
    }
}