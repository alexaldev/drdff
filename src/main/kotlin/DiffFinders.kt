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

            val p1 = extractDirectorySetFromFilename(f1)
            val p2 = extractDirectorySetFromFilename(f2)

            logger.info(LOG_COMPUTING_DIFFERENCES)
            val resultFilenames = p1.includedOnlyInSelf(p2).sorted().toSet()
            val p1FullPaths = Paths.get(f1).filenamesToFullPath
            return DiffResult(
                resultFilenames.size,
                (resultFilenames.size.toFloat() / p2.size.toFloat()) * 10000,
                resultFilenames.map { p1FullPaths[it] ?: it }.toSet()
            )
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

            val p1FullPaths = Paths.get(f1).filenamesToFullPath

            return DiffResult(
                result.size,
                (result.size.toFloat() / searchIn.size.toFloat()) * 10000,
                result.map { p1FullPaths[it] ?: it }.toSet()
            )
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
)

enum class DiffFinderType {
    Sets, Threads
}

enum class MultithreadedChunkSizeCalculationPolicy {
    ThreadsNumber
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
                DiffFinder.MultithreadedDiffFinder.Config(8, MultithreadedChunkSizeCalculationPolicy.ThreadsNumber)
            )
        }
    }
}
