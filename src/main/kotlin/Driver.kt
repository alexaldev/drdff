import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors
import kotlin.io.path.name
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main(args: Array<String>) {

    val diffAlgo = DiffFinderType.Sets

    val diffFinderCreator: DiffFinderCreator = BruteforceDiffFinderCreator(Pair(args[0], args[1]))
    val result: Set<String> = diffFinderCreator.createDiffFinder(diffAlgo).findDiffs()

    print("Stored results in file: ${args[2]}")
    File(args[2]).fillWith(result)
}
