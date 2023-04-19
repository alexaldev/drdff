package domain

import java.io.File

enum class Resolver(val resolver: DirectoryResolver) {
    TreeWalk(KotlinTreeWalkResolver()),
}
fun interface DirectoryResolver {
    fun getContents(directory: String, progressListener: ProgressListener?): ResolverResult
}

fun interface ProgressListener {
    fun onProgress(current: Int)
}

class KotlinTreeWalkResolver : DirectoryResolver {
    override fun getContents(directory: String, progressListener: ProgressListener?): ResolverResult {

        val f = File(directory)
        val treeWalk = f.walkBottomUp().filterNot { it.isDirectory }

        val total = treeWalk.count()

        val result =
            treeWalk
                .withIndex()
                .map { indexValue ->
                    (indexValue.value).also { progressListener?.onProgress(percentage((indexValue.index) + 1, total)) }
                }
                .associate { it.name to it.absolutePath }

        return ResolverResult(result)
    }

    fun percentage(current: Int, total: Int): Int {
        return ((current.toFloat() * 100).div(total.toFloat())).toInt()
    }
}
