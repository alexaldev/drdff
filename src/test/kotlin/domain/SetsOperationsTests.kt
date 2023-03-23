package domain

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

class SetsOperationsTests {

    private lateinit var testAlgorithm: SetsOperations

    @BeforeTest
    fun setUp() {
        testAlgorithm = ByIntersectOperation()
    }

    @TestFactory
    @DisplayName("Non overlapping sets results in the whole first set")
    fun nonOverlappingSets() = listOf(
        setOf(1, 2) to setOf(3, 4),
        K1000_2000 to K3000_4000
    ).map { (first, second) ->
        DynamicTest.dynamicTest("Set of $first and set of $second do not overlap") {
            assertEquals(first, testAlgorithm.includedOnlyInSelf(first, second))
        }
    }

    private val K1000_2000 = oneThousandToTwoThousand()
    private val K3000_4000 = threeThousandToFourThousand()

    fun oneThousandToTwoThousand() =
        (1_000..2_000).toSet()

    fun threeThousandToFourThousand() = (3_000..4_000 step 2).toSet()
}