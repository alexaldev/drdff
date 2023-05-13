package domain

import mu.KLogger
import utils.Project
import java.nio.file.Path
import java.text.SimpleDateFormat
import java.util.*
import kotlin.io.path.*

class HeaderProvider(
    private val versionProvider: VersionProvider
) {
    fun createHeader(): String {
        return "------------ DRDFF v.${versionProvider.getAppVersion()} - Directories diff finder -----------"
    }
}

interface VersionProvider {
    fun getAppVersion(): String
}

class PropertiesVersionProvider : VersionProvider {
    override fun getAppVersion(): String {
        return Project.version
    }

}

class DateProvider {
    fun createDate(): String {
        return SimpleDateFormat("dd/MM/YYYY HH:mm").format(Date())
    }
}

// --------------------------------------------------------------------------------------

sealed class ResultPrinter {
    abstract fun printResult(result: DrdffResult)

    class FileResultPrinter(
        private val resultFilePath: Path,
        private val dateProvider: DateProvider = DateProvider(),
        private val headerProvider: HeaderProvider = HeaderProvider(PropertiesVersionProvider())
    ) : ResultPrinter() {
        override fun printResult(result: DrdffResult) {

            Path(resultFilePath.pathString).deleteIfExists()

            Path(resultFilePath.pathString)
                .createFile()
                .bufferedWriter()
                .use {
                    it.appendLine(headerProvider.createHeader())
                    it.appendLine("${dateProvider.createDate()}, Total duration: ${result.duration} ms")
                    it.appendLine(result.directoriesCompared)
                    it.appendLine("Percentage of files missing: ${"%.2f".format(result.percentageMissing)}%")
                    it.appendLine("Here are the files not found")
                    it.appendLine("------------------------------------")
                    result.missingFilenames.forEach { missingFile -> it.appendLine(missingFile) }
                }
        }
    }

    class StdOutResultPrinter(private val logger: KLogger) : ResultPrinter() {

        private val dateProvider = DateProvider()
        override fun printResult(result: DrdffResult) {
            logger.info {
                with(StringBuilder()) {
                    this.appendLine("${dateProvider.createDate()}, Total duration: ${result.duration} ms")
                    this.appendLine(result.directoriesCompared)
                    this.appendLine("Percentage of files missing: ${"%.2f".format(result.percentageMissing)}%")
                    this.appendLine("Here are the files not found")
                    this.appendLine("------------------------------------")
                    result.missingFilenames.forEach { missingFile -> this.appendLine(missingFile) }
                }.toString()
            }
        }
    }
}