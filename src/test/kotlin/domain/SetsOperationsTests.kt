package domain

import utils.isEven
import utils.isOdd

class SetsOperationsTests {

    fun setUp() {
        TODO()
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