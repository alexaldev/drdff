import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int
import java.io.File
import java.util.logging.Logger
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
class RunConfig : CliktCommand() {

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

    private val threadsCount by option("-t", "--threads", help = "Specify the number of threads to run the application").int()
        .default(1)

    override fun run() {

        val logger = Logger.getLogger("Driver")

        val diffFinder = createDiffFinder(
            Pair(toSearchDirName, searchInDirName),
            if (threadsCount > 1) DiffFinderType.Threads else DiffFinderType.Sets,
            threadsCount
        )

        val setsTime = measureTime {
            val result: DiffResult = diffFinder.findDiffs()
            logger.info(
                "Diff find with sets completed. Missing files from $toSearchDirName in $searchInDirName: ${result.missingFilesCount}, " + "%.2f".format(
                    result.missingPercentage
                ) + "%"
            )
            logger.info("Storing results in: $resultsFileName")

            resultsFileName?.let {
                File(it).fillWith(result.missingFiles)
            } ?: run {
                print(result.missingFiles)
            }
        }

        logger.info("Process completed at $setsTime")
    }
}

fun main(args: Array<String>) = RunConfig().main(args)