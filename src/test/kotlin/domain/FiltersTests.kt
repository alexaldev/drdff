package domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FiltersTests {

    @Test
    fun `listOr aggregator works as expected`() {

        val greaterThan10 = Filter<Int> { it >= 10 }
        val isOdd = Filter<Int> { it % 2 == 0 }

        val t1 = ListOrFilter<Int>(emptyList())
        val t2 = ListOrFilter(listOf(isOdd))
        val t3 = ListOrFilter(listOf(greaterThan10, isOdd))

        val testSet = listOf(1, 30, 22, 10, 12354, 2, 9)

        assertTrue(testSet.associateWith { t1.evaluates(it) }
            .entries.fold(false) { acc, entry -> acc or entry.value })

        assertEquals(listOf(30, 22, 10, 12354, 2), testSet.filter { t2.evaluates(it) })
        assertEquals(listOf(30, 22, 10, 12354, 2), testSet.filter { t3.evaluates(it) })
    }
}