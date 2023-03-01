package output

import domain.DrdffResult
import domain.StdOutWriter
import mu.KotlinLogging
import kotlin.test.Test

class ResultWritersTests {

    @Test
    fun `FileWriter writes results to a file with a header, computation date, percentage missing, directories compared and all the result set`() {

    }

    @Test
    fun `StdoutWriter writes result on the standard output, with a header, computation date, percentage missing, directories compared and all the result set`() {
        val testWriter = StdOutWriter(KotlinLogging.logger {})
        testWriter.print(fakeResult())
    }

    private fun fakeResult() = DrdffResult(
        missingFilenames = setOf(),
        percentageMissing = 40f,
        duration = 2_000
    )
}