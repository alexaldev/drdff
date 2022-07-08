import files.ListSearcher
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.Executors
import java.util.stream.Collectors
import kotlin.io.path.name

sealed class DiffFinder(open val filenames: Pair<String, String>) {
    abstract fun findDiffs(): Set<String>

    class WithSetsDiffFinder(override val filenames: Pair<String, String>) : DiffFinder(filenames) {
        override fun findDiffs(): Set<String> {
            val (f1, f2) = filenames
            println("Constructing directory tree for $f1")
            val p1 = Paths.get(f1).directoryTreeSet()
            println("Completed. Files found: ${p1.size}")
            println("Constructing directory tree for $f2")
            val p2 = Paths.get(f2).directoryTreeSet()
            println("Completed. Files found: ${p2.size}")
            println("Computing differences...")
            val resultFilenames = p1.includedOnlyInSelf(p2).sorted().toSet()
            val p1FullPaths: Map<String, String> = Files.walk(Paths.get(f1)).collect(Collectors.toList()).associate { it.name to it.toString() }
            return resultFilenames.map { p1FullPaths[it] ?: it }.toSet()
        }
    }

    class MultithreadedDiffFinder(
        override val filenames: Pair<String, String>,
        val config: Config
    ) : DiffFinder(filenames) {
        override fun findDiffs(): Set<String> {

            val (f1, f2) = filenames
            println("Constructing the directory trees...")
            val thingsToFind = Paths.get(f1).directoryTreeSet().toList()
            val searchIn = Paths.get(f2).directoryTreeSet().toList()

            println("Creating caches...")
            val sublistsSize = thingsToFind.size / config.numOfThreads

            val thingsToFindSubLists: List<List<String>> = List(config.numOfThreads) {
                thingsToFind.subList(it * sublistsSize, (it * sublistsSize) + sublistsSize)
            }

            val executor = Executors.newFixedThreadPool(config.numOfThreads)

            println("Computing differences...")
            val futures = executor.invokeAll(
                thingsToFindSubLists.map {
                    ListSearcher(it, searchIn)
                }
            )

            val result = mutableSetOf<String>()

            futures.forEach {
                result.addAll(it.get())
            }

            executor.shutdown()
            return result
        }

        class Config(
            val numOfThreads: Int
        )
    }
}

enum class DiffFinderType {
    Sets, Threads
}

abstract class DiffFinderCreator {
    abstract fun createDiffFinder(type: DiffFinderType): DiffFinder
}

class BruteforceDiffFinderCreator(
    private val filenames: Pair<String, String>
) : DiffFinderCreator() {
    override fun createDiffFinder(type: DiffFinderType): DiffFinder {
        return when (type) {
            DiffFinderType.Sets -> DiffFinder.WithSetsDiffFinder(filenames)
            DiffFinderType.Threads -> DiffFinder.MultithreadedDiffFinder(
                filenames,
                DiffFinder.MultithreadedDiffFinder.Config(8)
            )
        }
    }
}
