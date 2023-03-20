package model

import includedOnlyInSelf
import utils.isEven
import utils.isOdd
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SetsOperationsTests {

    @Test
    fun `isEven ext property of Int reports if the integer is even`() {
        val allEvens = listOf(2, 44, 22, 126, 450, 420).fold(true) { acc, i -> acc && i.isEven }
        assertTrue(allEvens)
    }

    @Test
    fun `includedOnlyInSelf reports elements found in first argument exclusively`() {
        val firstFakeTest = setOf(1, 2, 3, 4, 5, 6, 7, 8)
        val secondFakeTest = setOf(5, 6, 7, 8)

        val testResult = firstFakeTest.includedOnlyInSelf(secondFakeTest)

        assertEquals(setOf(1, 2, 3, 4), testResult)
    }

    @Test
    fun `includedOnlyInSelf works on big collections`() {
        val testResult = oneMillion().includedOnlyInSelf(evensUpToOneMillion())

        assertEquals(oddsUpToOneMillion(), testResult)
    }

    fun oneMillion(): Set<Int> {
        return (0..1_000_000)
            .fold(mutableSetOf()) { acc, i -> acc.add(i); acc }
    }

    fun evensUpToOneMillion(): Set<Int> {
        return (0..1_000_000)
            .filter { it.isEven }
            .fold(mutableSetOf()) { acc, i -> acc.addAndReturnSelf(i) }
    }

    fun oddsUpToOneMillion(): Set<Int> {
        return (0..1_000_000)
            .filter { it.isOdd }
            .fold(mutableSetOf()) { acc, i -> acc.addAndReturnSelf(i) }
    }

    fun <T : Any> MutableSet<T>.addAndReturnSelf(t: T): MutableSet<T> {
        this += t
        return this
    }
}