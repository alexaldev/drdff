package domain

import kotlin.test.Test
import kotlin.test.assertEquals

class DirectoryResolversTests {

    private val testRootDirectory = "src/test/resources/"

    @Test
    fun `KotlinTreeWalkResolver produces all the filenames paired with their absolutePath except for the directories names`() {

        val testResolver = KotlinTreeWalkResolver()

        val testResult = testResolver.getContents("${testRootDirectory}testSearchFor")

        assertEquals(
            ResolverResult(
                mapOf(
                    "2.txt" to "/home/pesimatik/IdeaProjects/drdf/src/test/resources/testSearchFor/f1/2.txt",
                    "4.txt" to "/home/pesimatik/IdeaProjects/drdf/src/test/resources/testSearchFor/f1/4.txt",
                    "5.txt" to "/home/pesimatik/IdeaProjects/drdf/src/test/resources/testSearchFor/f1/5.txt",
                    "1.txt" to "/home/pesimatik/IdeaProjects/drdf/src/test/resources/testSearchFor/1.txt",
                    "10.txt" to "/home/pesimatik/IdeaProjects/drdf/src/test/resources/testSearchFor/10.txt"
                )
            ).namesToAbsolutePath.values.sorted(),
            testResult.namesToAbsolutePath.values.sorted()
        )
    }

    @Test
    fun `KotlinPathListResolver produces all the filenames paired with their absolutePath except for the directories names`() {

        val testResolver = KotlinPathListEntriesResolver()

        val testResult = testResolver.getContents("${testRootDirectory}testSearchFor")

        assertEquals(
            ResolverResult(
                mapOf(
                    "2.txt" to "/home/pesimatik/IdeaProjects/drdf/src/test/resources/testSearchFor/f1/2.txt",
                    "4.txt" to "/home/pesimatik/IdeaProjects/drdf/src/test/resources/testSearchFor/f1/4.txt",
                    "5.txt" to "/home/pesimatik/IdeaProjects/drdf/src/test/resources/testSearchFor/f1/5.txt",
                    "1.txt" to "/home/pesimatik/IdeaProjects/drdf/src/test/resources/testSearchFor/1.txt",
                    "10.txt" to "/home/pesimatik/IdeaProjects/drdf/src/test/resources/testSearchFor/10.txt"
                )
            ).namesToAbsolutePath.values.sorted(),
            testResult.namesToAbsolutePath.values.sorted()
        )
    }
}