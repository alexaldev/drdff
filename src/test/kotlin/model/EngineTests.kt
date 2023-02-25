package model

import domain.ComputationProgress
import domain.DrdffEngine
import domain.DrdffResult
import domain.EngineConfig
import domain.UserInput
import utils.isOdd
import utils.oneThousand
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class EngineTests {

    private lateinit var fakeEngine: DrdffEngine
    private lateinit var engineConfig: EngineConfig
    private lateinit var fakeUserInput: UserInput

    @BeforeTest
    fun setUp() {
        val engineSetup = EngineConfig.default()
        fakeEngine = DrdffEngine.from(engineSetup)
        fakeUserInput = UserInput("src/test/resources/search_for_files_from", "src/test/resources/search_for_files_in")
    }

    @Test
    fun `Drdff#compute function is a blocking one that  produces a non-null DifferenceResult object`() {

        val testResult: DrdffResult = fakeEngine.compute(fakeUserInput)

        assertNotNull(testResult)
    }

    @Test
    fun `Drdff#compute can have a receiver callback with DifferenceResult as a parameter`() {

        fakeEngine.compute(fakeUserInput) { result: DrdffResult ->
            assertNotNull(result)
        }
    }

    @Test
    fun `engine is in indle state when initialized`() {
        fakeEngine = DrdffEngine.default()
       assertEquals(DrdffEngine.State.Idle, fakeEngine.state)
    }

    @Test
    fun `engine gets in computing state without progression  when a computation is requested`() {
        fakeEngine = DrdffEngine.default()
        assertEquals(DrdffEngine.State.Idle, fakeEngine.state)
        fakeEngine.compute(fakeUserInput)
        assertEquals(DrdffEngine.State.Computing(ComputationProgress(0)), fakeEngine.state)
    }

    @Test
    fun `engine computation result contains all the files missing and percentage based on the size`() {

        val expectedFilesMissing = oneThousand()
            .filter { it.isOdd }
            .map { "src/test/resources/search_for_files_from/$it" }

        fakeEngine.compute(fakeUserInput) {

        }
    }
}