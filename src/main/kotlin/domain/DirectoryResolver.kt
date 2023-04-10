package domain

import java.io.File
import java.nio.file.Paths
import kotlin.io.path.absolutePathString
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

enum class Resolver(val resolver: DirectoryResolver) {
    TreeWalk(KotlinTreeWalkResolver()),

    @Deprecated("Underlying algo not working")
    PathList(KotlinPathListEntriesResolver())
}

fun interface DirectoryResolver {
    fun getContents(directory: String): ResolverResult
}

class KotlinTreeWalkResolver : DirectoryResolver {
    override fun getContents(directory: String): ResolverResult {
        return ResolverResult(File(directory)
            .walkBottomUp()
            .filterNot { it.isDirectory }
            .associate { it.name to it.absolutePath })
    }
}

@Deprecated("Does not compute. Will be removed")
class KotlinPathListEntriesResolver : DirectoryResolver {
    override fun getContents(directory: String): ResolverResult {
        return ResolverResult(Paths.get(directory)

            .listDirectoryEntries()
            .associate { it.name to it.absolutePathString() })
    }
}
