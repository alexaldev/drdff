package domain

import domain.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

//@Execution(ExecutionMode.CONCURRENT)
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
    fun `Engine can be configured with extra post-filtering properties for the ResolverResults`() {
        val fakeConfig = EngineConfig.builder {
            this.postFilters += FilenameExtensionFilter("jpg")
            this.postFilters += FilenameExtensionFilter("pdf")
        }
        DrdffEngine.with(fakeConfig)
    }

    @Test
    fun `Engine reports filenames with specific extensions when there are configured ones`() {

        val fakeInput = UserInput(aValidDirectoryToSearchFrom, aValidDirectoryToSearchIn)
//        every { mockInput.d1 } returns aValidDirectoryToSearchIn
//        every { mockInput.d2 } returns aValidDirectoryToSearchFrom

        val mockResolver: DirectoryResolver = mockk(relaxed = true)
        every { mockResolver.getContents(fakeInput.d1, any()) } returns
                ResolverResult(
                    mapOf(
                        "1.jpg" to "1.jpg",
                        "2.txt" to "2.txt",
                        "3" to "3",
                        "4.pdf" to "4.pdf",
                        "5" to "5"
                    )
                )
        every { mockResolver.getContents(fakeInput.d2, any()) } returns
                ResolverResult(
                    mapOf(
                        "1.jpg" to "full/1.jpg",
                        "2" to "full/2",
                        "5.jpg" to "full/5.jpg"
                    )
                )
        val fakeJpegFilter = FilenameExtensionFilter("jpg")
        val fakeTxtFilter = FilenameExtensionFilter("txt")

        val fakeConfig = EngineConfig.builder {
            this.directoryResolver = mockResolver
            this.postFilters += fakeJpegFilter
            this.postFilters += fakeTxtFilter
        }
        val testEngine = DrdffEngine.with(fakeConfig)

        testEngine.compute(fakeInput) {
            assertEquals(
                setOf(
                    "2.txt",
                    "4.pdf"
                ), it.missingFilenames
            )
        }
    }

    @Test
    fun `Engine can have a ProgressListener for filenames resolution injected via its configuration`() {
        val testResolverListener = ProgressListener { it }

        val fakeConfig = EngineConfig.builder {
            this.resolverProgressListener = testResolverListener
        }

        DrdffEngine.with(fakeConfig)
    }

    @Test
    fun `DirectoryResolver ProgressListener reports the running percentage progress as an Integer`() {

        val fakeConfig: EngineConfig = mockk()
        var testProgress = 0
        every { fakeConfig.resolverProgressListener } returns ProgressListener { testProgress = it }
        every { fakeConfig.directoryResolver } returns KotlinTreeWalkResolver()
        every { fakeConfig.setsOperations } returns ByIntersectOperation()
        every { fakeConfig.postFilters } returns mutableListOf()

        val testEngine = DrdffEngine.with(fakeConfig)
        testEngine.compute(fakeUserInput) {}

        assertEquals(100, testProgress)
    }
}