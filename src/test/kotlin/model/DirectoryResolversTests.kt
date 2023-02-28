package model

import fakeUserInput
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name
import kotlin.test.BeforeTest
import kotlin.test.Test

class DirectoryResolversTests {

    private lateinit var testResolver: DirectoryResolver

    @BeforeTest
    fun setUp() {

    }

    @Test
    fun `A directoryResolver can parse a UserInput`() {
        val fakeInput = fakeUserInput
        val result = testResolver.getContents(fakeInput.d1)
    }
}

fun interface DirectoryResolver {
    fun getContents(directory: String): Set<String>
}

class NativeDirectoryResolver : DirectoryResolver {
    override fun getContents(directory: String): Set<String> {
        return Files.walk(Path.of(directory))
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
        return Path.of(directory)
            .listDirectoryEntries()
            .map { it.name }
            .toSet()
    }
}
