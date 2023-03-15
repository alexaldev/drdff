package output

import domain.*
import utils.oneThousand
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class ResultWritersTests {

    private lateinit var testFilePrinter: ResultPrinter.FileResultPrinter
    private val outputFilePath = "src/test/resources/testOutput.txt"

    @BeforeTest
    fun setUp() {
        testFilePrinter = ResultPrinter.FileResultPrinter(
            resultFilePath = Path(outputFilePath),
            dateProvider = DateProvider(),
            headerProvider = HeaderProvider(
                PropertiesVersionProvider()
            )
        )
    }

    @Test
    fun `FileWriter writes results to a file with a header, computation date, percentage missing, directories compared and all the result set`() {
        testFilePrinter.printResult(fakeResult)
        assertTrue("Output file not created") { Path(outputFilePath).exists() }
    }

    @Test
    fun `StdoutWriter writes result on the standard output, with a header, computation date, percentage missing, directories compared and all the result set`() {

    }

    private val fakeResult = DrdffResult(
        missingFilenames = oneThousand().map { it.toString() }.toSet(),
        directoriesCompared = "d1 | d2",
        percentageMissing = 40f,
        duration = 8
    )
}