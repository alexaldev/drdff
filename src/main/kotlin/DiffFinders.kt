import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.Executors
import java.util.logging.Logger

sealed class DiffFinder(open val filenames: Pair<String, String>) {
    abstract fun findDiffs(): DiffResult
    protected val logger: Logger = Logger.getLogger(className)
    protected abstract val className: String

    class WithSetsDiffFinder(override val filenames: Pair<String, String>) : DiffFinder(filenames) {
        override fun findDiffs(): DiffResult {
            val (f1, f2) = filenames

            val p1: Set<String> = extractDirectorySetFromFilename(f1)
            val p2: Set<String> = extractDirectorySetFromFilename(f2)

            logger.info(LOG_COMPUTING_DIFFERENCES)
            val resultFilenames = p1.includedOnlyInSelf(p2).sorted().toSet()

            return DiffResult.from(resultFilenames, Paths.get(f1), p1)
        }

        private fun extractDirectorySetFromFilename(f: String): Set<String> {
            logger.info(LOG_CONSTRUCT_DIRECTORY_TREE + f)
            val p = Paths.get(f).directoryTreeSet()
            logger.info(LOG_CONSTRUCTION_COMPLETED + p.size)
            return p
        }

        override val className: String
            get() = WithSetsDiffFinder::class.simpleName!!
    }

    class MultithreadedDiffFinder(
        override val filenames: Pair<String, String>,
        private val config: Config
    ) : DiffFinder(filenames) {
        override fun findDiffs(): DiffResult {

            val (f1, f2) = filenames
            logger.info(LOG_CONSTRUCTION_DIRECTORY_TREES)
            val thingsToFind = Paths.get(f1).directoryTreeSet().toList()
            val searchIn = Paths.get(f2).directoryTreeSet()

            val thingsToFindSubLists = chunkSearchCollection(thingsToFind).map { it.toSet() }

            val executor = Executors.newFixedThreadPool(config.numOfThreads)

            logger.info(LOG_COMPUTING_DIFFERENCES)
            val futures = executor.invokeAll(
                thingsToFindSubLists.map {
                    SetDiffFinder(it, searchIn)
                }
            )

            val result = mutableSetOf<String>()

            futures.forEach {
                result.addAll(it.get())
            }

            executor.shutdown()

            return DiffResult.from(result, Paths.get(f1), thingsToFind.toSet())
        }

        override val className: String
            get() = MultithreadedDiffFinder::class.simpleName!!


        private fun <T> chunkSearchCollection(c: List<T>): List<List<T>> {
            val sublistSize = when (config.chunkSizeCalculationPolicy) {
                MultithreadedChunkSizeCalculationPolicy.ThreadsNumber -> c.size / config.numOfThreads
            }
            return c.chunked(sublistSize)
        }

        class Config(
            val numOfThreads: Int,
            val chunkSizeCalculationPolicy: MultithreadedChunkSizeCalculationPolicy
        )
    }
}

data class DiffResult(
    val missingFilesCount: Int,
    val missingPercentage: Float,
    val missingFiles: Set<String>
) {
    companion object {
        fun from(
            resultFilenames: Set<String>,
            pathToSearch: Path,
            pathToSearchDirectorySet: Set<String>
        ): DiffResult {

            val pathsToSearchFilenamesToFullPath = pathToSearch.filenamesToFullPath

            return DiffResult(
                resultFilenames.size,
                (resultFilenames.size.toFloat() / pathToSearchDirectorySet.size) * 100,
                resultFilenames.map { pathsToSearchFilenamesToFullPath[it] ?: it }.toSet()
            )
        }
    }
}

enum class DiffFinderType {
    Sets, Threads
}

enum class MultithreadedChunkSizeCalculationPolicy {
    /**
     * Splits the list to be searched in equal sized lists.
     * The number of the lists is equal to the provided thread number,
     * so that each thread will work on equal sized lists.
     */
    ThreadsNumber
}

fun createDiffFinder(filenames: Pair<String, String>, type: DiffFinderType, threads: Int = 1): DiffFinder {
    return when (type) {
        DiffFinderType.Sets -> DiffFinder.WithSetsDiffFinder(filenames)
        DiffFinderType.Threads -> DiffFinder.MultithreadedDiffFinder(filenames,
            DiffFinder.MultithreadedDiffFinder.Config(threads, MultithreadedChunkSizeCalculationPolicy.ThreadsNumber))
    }
}