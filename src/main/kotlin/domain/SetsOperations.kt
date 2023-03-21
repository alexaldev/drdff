package domain

interface SetsOperations {
    /**
     * Contract for a useful operation on sets required in this system.
     * The desired output should be as shown here:
     * e.g. val firstSet = setOf(1, 2, 3, 4, 5)
     *      val secondSet = setOf(1, 2, 3, 6)
     *      firstSet.includedOnlyInSelf(secondSet) -> (4, 5)
     */
    fun <T> includedOnlyInSelf(thisSet: Set<T>, other: Set<T>): Set<T>
}

class ByIntersectOperation : SetsOperations {

    override fun <T> includedOnlyInSelf(thisSet: Set<T>, other: Set<T>): Set<T> {
        return thisSet - (thisSet.intersect(other))
    }
}

class ByDistinctOperation : SetsOperations {
    override fun <T> includedOnlyInSelf(thisSet: Set<T>, other: Set<T>): Set<T> {
        return thisSet.distinctBy { it !in other }.toSet()
    }
}