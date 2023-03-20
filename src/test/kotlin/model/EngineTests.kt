package model

import domain.*
import utils.isOdd
import utils.oneThousand
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class EngineTests {

    private lateinit var testEngine: DrdffEngine
    private lateinit var engineConfig: EngineConfig
    private lateinit var fakeUserInput: UserInput

    @BeforeTest
    fun setUp() {
        val engineSetup = EngineConfig.default()
        testEngine = DrdffEngine.with(engineSetup)
        fakeUserInput = UserInput("src/test/resources/search_for_files_from", "src/test/resources/search_for_files_in")
        testEngine.shutdown()
    }

    @Test
    fun `Drdff#compute can have a receiver callback with DifferenceResult as a parameter`() {

        testEngine.compute(fakeUserInput) { result: DrdffResult ->
            assertNotNull(result)
        }
    }

    @Test
    fun `engine can receive EngineRunArguments in its compute interface`() {

        val fakeArguments = EngineArguments.PureStringArgs(setOf("f1, f2, f3, f4"), setOf("f1, f4, f5, f6"))

        testEngine.compute(fakeArguments) {
            // Ignore
        }
    }

    @Test
    fun `engine is in indle state when initialized`() {
        testEngine = DrdffEngine.default()
        assertEquals(State.Idle, testEngine.state)
    }

    @Test
    fun `engine computation result contains all the files missing and percentage based on the size`() {

        val expectedFilesMissing =
            oneThousand()
                .filter { it.isOdd }
                .map { "$it" }
                .sorted()
                .toMutableSet()

        expectedFilesMissing += "search_for_files_from" // Also root directory

        testEngine.compute(fakeUserInput) {

            assertEquals(50f, it.percentageMissing)
            assertEquals(expectedFilesMissing, it.missingFilenames)
        }
    }

    private val keepOnlyOdds : (Set<Int>) -> List<Int> = {
        it.filter { n -> n.isOdd }
    }
}