package domain

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.test.Test
import kotlin.test.assertEquals

class DirectoryResolversTests {

    private val testRootDirectory = "src/test/resources/"

    @TestFactory
    fun `Percentage computer works as expected`() {

        val testResolver = KotlinTreeWalkResolver()

        listOf(
            Pair(0, 100) to 0,
            (20 to 100) to 20,
            (30 to 100) to 30,
            (100 to 100) to 100,
            (30 to 60) to 50,
            (34 to 58) to 58
        ).map { (pairs, expected) ->
            DynamicTest.dynamicTest("Current index of ${pairs.first} in total of ${pairs.second} results in ${expected}%") {
                assertEquals(expected, testResolver.percentage(pairs.first, pairs.second))
            }
        }
    }

    @Test
    fun `KotlinTreeWalkResolver produces all the filenames paired with their absolutePath except for the directories names`() {

        val testResolver = KotlinTreeWalkResolver()

        val testResult = testResolver.getContents("${testRootDirectory}testSearchFor", null)

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
    fun `Progress listener of KotlinTreeWalkResolver reports 100 after the resolution of the filenames`() {
        var testProgress = 0
        val testProgressListener = ProgressListener {
            testProgress = it
        }
        val testResolver = KotlinTreeWalkResolver()

        testResolver.getContents("${testRootDirectory}testSearchFor", testProgressListener)

        assertEquals(100, testProgress)
    }
}