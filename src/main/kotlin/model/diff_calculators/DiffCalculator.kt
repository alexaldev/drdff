package model.diff_calculators

interface DiffCalculator {
    fun calculateDiffs(): Set<String>
}

class WithSetsCalculator(innerCalculator: DiffCalculator) : DiffCalculator by innerCalculator {
    override fun calculateDiffs(): Set<String> {
        TODO("Not yet implemented")
    }
}

class MultithreadedCalculator(innerCalculator: DiffCalculator) : DiffCalculator by innerCalculator {
    override fun calculateDiffs(): Set<String> {
        TODO()
    }
}