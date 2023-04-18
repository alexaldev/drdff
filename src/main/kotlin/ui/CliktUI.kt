package ui

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.options.split
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.int
import domain.*
import utils.INTRO_MESSAGE
import utils.Project
import utils.logger
import kotlin.io.path.Path

class CommandLineUI : CliktCommand(
    name = Project.name
) {

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
        help = "NOT IMPLEMENTED Specify the number of threads to run the application"
    ).int()
        .default(1)

    private val filenameExtensions by option(
        "-x",
        help = "NOT IMPLEMENTED Specify the extensions of the files to be searched separated by comma(,)"
    ).split(",")

    private val setsOperator by option(
        "-s",
        help = "Algorithm used to compute differences between two sets"
    ).enum<SetOperation>().default(SetOperation.Intersect)

    private val directoryResolver by option(
        "-ds",
        help = "Algorithm used to extract the directory tree from the provided arguments"
    ).enum<Resolver>(ignoreCase = true).default(Resolver.TreeWalk)

    override fun run() {

        logger.info { INTRO_MESSAGE }

        DrdffEngine
            .with(engineConfigFromArgs())
            .compute(userInputFromArgs()) { computeResult ->
                createResultPrinterBasedOnOptions().printResult(computeResult)
            }
    }

    private fun engineConfigFromArgs(): EngineConfig {
        return EngineConfig.builder {
            this.directoryResolver = this@CommandLineUI.directoryResolver.resolver
            this.setsOperations = setsOperator.operation
            this.resolverProgressListener = ProgressListener {
                if (it % 10 == 0) logger.info { "Progress: ${it}%" }
            }
        }
    }

    private fun userInputFromArgs(): UserInput {
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