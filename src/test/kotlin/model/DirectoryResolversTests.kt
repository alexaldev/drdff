package model

import domain.DirectoryResolver
import fakeUserInput
import java.io.File
import java.nio.file.Paths
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
    }
}

