package domain

import domain.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Execution(ExecutionMode.CONCURRENT)
class EngineTests {

    private lateinit var testEngine: DrdffEngine

    @Deprecated("Will be replaced with a mock object")
    private lateinit var fakeUserInput: UserInput

    private val aValidDirectoryToSearchIn = "src/test/resources/testSearchFor"
    private val aValidDirectoryToSearchFrom = "src/test/resources/testSearchIn"

    @BeforeTest
    fun setUp() {
        val engineSetup = EngineConfig.default()
        testEngine = DrdffEngine.with(engineSetup)

        fakeUserInput = UserInput(aValidDirectoryToSearchFrom, aValidDirectoryToSearchIn)
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

        }
    }

    @Test
    fun `engine is in indle state when initialized`() {
        testEngine = DrdffEngine.default()
        assertEquals(State.Idle, testEngine.state)
    }

    @Test
    fun `engine can have its directory resolution algorithm injected`() {

        val fakeUserInput = UserInput(aValidDirectoryToSearchFrom, aValidDirectoryToSearchIn)
        val mockResolver: DirectoryResolver = mockk()
        val fakeConfig = EngineConfig.builder {
            this.directoryResolver = mockResolver
        }

        every { mockResolver.getContents(any()) } returns ResolverResult(emptyMap())

        DrdffEngine
            .with(fakeConfig)
            .compute(fakeUserInput) {}

        verify {
            mockResolver.getContents(aValidDirectoryToSearchFrom)
        }
    }

    @Test
    fun `engine can have its sets difference algorithm injected`() {
        val fakeUserInput = UserInput(aValidDirectoryToSearchFrom, aValidDirectoryToSearchIn)
        val mockSetsOperations: SetsOperations = mockk<ByIntersectOperation>()
        val fakeConfig = EngineConfig.builder {
            this.setsOperations = mockSetsOperations
        }

        every { mockSetsOperations.includedOnlyInSelf(ofType(Set::class), ofType(Set::class)) } returns emptySet()

        DrdffEngine
            .with(fakeConfig)
            .compute(fakeUserInput) {}

        verify {
            mockSetsOperations.includedOnlyInSelf(ofType(Set::class), ofType(Set::class))
        }
    }

    @Test
    fun `engine can be configured to search specific file extensions`() {

        val fakeUserInput = UserInput(aValidDirectoryToSearchFrom, aValidDirectoryToSearchIn)
        val fakeExtensions = listOf("jpg", "pdf")
        val directoryResolver: DirectoryResolver = mockk<KotlinTreeWalkResolver>()

        every { directoryResolver.getContents(aValidDirectoryToSearchFrom) } returns
                ResolverResult(
                    mapOf(
                        "1.jpg" to "1.jpg",
                        "2.pdf" to "2.pdf",
                        "3" to "3",
                        "4" to "4",
                        "5" to "5",
                        "happy" to "happy",
                        "6.pdf" to "6.pdf"
                    )
                )
        every { directoryResolver.getContents(aValidDirectoryToSearchIn) } returns
                ResolverResult(
                    mapOf(
                        "1.jpg" to "1.jpg",
                        "4" to "4",
                        "3" to "3"
                    )
                )

        val fakeConfig = EngineConfig.builder {
            setExtensions(fakeExtensions)
            this.directoryResolver = directoryResolver
        }

        testEngine = DrdffEngine.with(fakeConfig)

        testEngine.compute(fakeUserInput) {
            assertEquals(setOf("2.pdf", "6.pdf"), it.missingFilenames)
        }

        verify {
            directoryResolver.getContents(aValidDirectoryToSearchFrom)
            directoryResolver.getContents(aValidDirectoryToSearchIn)
        }
    }
}