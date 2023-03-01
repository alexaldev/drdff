package domain

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

fun interface DirectoryResolver {
    fun getContents(directory: String): Set<String>
}

class NativeDirectoryResolver : DirectoryResolver {
    override fun getContents(directory: String): Set<String> {
        return Files.walk(Paths.get(directory))
            .map { it.name }
            .collect(Collectors.toSet())
    }
}

class KotlinDirectoryResolver : DirectoryResolver {
    override fun getContents(directory: String): Set<String> {
        return File(directory)
            .walkBottomUp()
            .map { it.name }
            .toCollection(mutableSetOf())
    }
}

class KotlinPathListDirectoriesResolver : DirectoryResolver {
    override fun getContents(directory: String): Set<String> {
        return Paths.get(directory)
            .listDirectoryEntries()
            .map { it.name }
            .toSet()
    }
}
