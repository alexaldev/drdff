package ui

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int
import domain.*
import utils.INTRO_MESSAGE
import utils.Project
import utils.logger
import kotlin.io.path.Path

class CommandLineUI : CliktCommand() {

    private val logger by logger(Project.name)

    private val toSearchDirName by option(
        "-d",
        "--directory",
        help = "Directory containing all the files to be searched"
    ).required()

    private val searchInDirName by option(
        "-i",
        "--search_in",
        help = "Directory to search the files found in specified directory"
    ).required()

    private val resultsFileName by option("-o", "--results", help = "Filename to store the results")

    private val threadsCount by option(
        "-t",
        "--threads",
        help = "Specify the number of threads to run the application"
    ).int()
        .default(1)

    private val fileExtension by option("-x", "--extension", help = "Specify the extension of the files to be searched")

    override fun run() {

        logger.info { INTRO_MESSAGE }

        DrdffEngine.with(EngineConfig.withResolver(NativeDirectoryResolver()))
            .compute(mapInputToUserInput()) { computeResult ->
                createResultPrinterBasedOnOptions().printResult(computeResult)
            }
    }

    private fun mapInputToUserInput(): UserInput {
        return UserInput(toSearchDirName, searchInDirName)
    }

    fun createResultPrinterBasedOnOptions(): ResultPrinter = resultsFileName?.let {
        return ResultPrinter.FileResultPrinter(
            resultFilePath = Path(it),
        )
    } ?: kotlin.run {
        return ResultPrinter.StdOutResultPrinter(logger)
    }
}