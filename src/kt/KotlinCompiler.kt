package kt

import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.config.Services
import java.nio.file.Files
import java.nio.file.Paths


class KotlinCompiler {
    companion object {
        fun compile(inputFile: String, outputPath: String): Boolean = K2JVMCompiler().run {
            val args = K2JVMCompilerArguments().apply {
                freeArgs = mutableListOf(inputFile)
                loadBuiltInsFromDependencies = true
                destination = outputPath
                classpath = System.getProperty("java.class.path")
                        .split(System.getProperty("path.separator"))
                        .filter {
                            val path = Paths.get(it)
                            Files.exists(path) && Files.isReadable(path)
                        }.joinToString(":")
                noStdlib = true
                noReflect = true
                skipRuntimeVersionCheck = true
                reportPerf = false
                suppressWarnings = true

            }
            execImpl(PrintingMessageCollector(System.out, MessageRenderer.WITHOUT_PATHS, false),
                    Services.EMPTY,
                    args)
        }.code == 0


    }
}


