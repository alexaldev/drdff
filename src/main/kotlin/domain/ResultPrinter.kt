package domain

import mu.KLogger
import utils.Project
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.io.path.bufferedWriter

class HeaderProvider(
    private val versionProvider: VersionProvider
) {
    fun createHeader(): String {
        return "------------ DRDFF - Directories diff finder ----------- \n" +
                versionProvider.getAppVersion()
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
        return SimpleDateFormat("dd/MM/YYYY").format(Date())
    }
}

// --------------------------------------------------------------------------------------

sealed class ResultPrinter {
    abstract fun printResult(result: DrdffResult)

    class FileResultPrinter(
        private val resultFilePath: Path,
        private val versionProvider: VersionProvider,
        private val dateProvider: DateProvider) : ResultPrinter() {
        override fun printResult(result: DrdffResult) {
            Files.createFile(resultFilePath).bufferedWriter().use {
                it.appendLine(versionProvider.getAppVersion())
                it.newLine()
                it.appendLine(dateProvider.createDate())
                it.newLine()
                it.appendLine("Directories compared: ${result.directoriesCompared}")
                it.appendLine("------------------------------------")
                result.missingFilenames.forEach { missingFile -> it.appendLine(missingFile) }
            }
        }
    }

    class StdOutResultPrinter(val logger: KLogger) : ResultPrinter() {
        override fun printResult(result: DrdffResult) {

        }
    }
}