package domain

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.test.BeforeTest
import kotlin.test.Test

class DirectoryResolversTests {

    private val testRootDirectory = "src/test/resources/"

    @BeforeTest
    fun setUp() {
        Paths.get(testRootDirectory)
    }

    @Test
    fun `KotlinDirectoryResolver produces all the filenames paired with their absolutePath except for the directories names`() {

    }
}

fun Files.createFile(s: String) {
    Files.createFile(Path.of(s))
}

operator fun File.plusAssign(another: File) {

    require(this.isDirectory) { "Must be a directory" }

    Files.createDirectory(Path.of("${this.absolutePath}/${another.name}"))
}

operator fun File.plusAssign(anotherFilename: String) {
    this += Files.createFile(Path.of(anotherFilename)).toFile()
}