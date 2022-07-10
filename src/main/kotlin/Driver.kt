import java.io.File
import java.util.logging.Logger
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main(args: Array<String>) {

    val logger = Logger.getLogger("Driver")

    val diffFinderCreator: DiffFinderCreator = BruteforceDiffFinderCreator(Pair(args[0], args[1]))
    val diffFinder = diffFinderCreator.createDiffFinder(DiffFinderType.Sets)

    val setsTime = measureTime {
        val result: DiffResult = diffFinder.findDiffs()
        logger.info("Diff find with sets completed. Missing files from ${args[0]} in ${args[1]}: ${result.missingFilesCount}, " + "%.2f".format(result.missingPercentage) + "%")
        logger.info("Storing results in: ${args[2]}")
        File(args[2]).fillWith(result.missingFiles)
    }

    logger.info("Process completed at $setsTime")
}