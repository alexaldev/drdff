import java.util.concurrent.Callable

class Searcher<V>(
    val v: V,
    val values: List<V>
) : Callable<Boolean> {
    override fun call(): Boolean {
        return values.any { v == it }
    }
}

class ListSearcher<V>(
    val lv: Collection<V>,
    val searchIn: Collection<V>
): Callable<Collection<V>> {
    override fun call(): Collection<V> {
        return lv.filter {
            it !in searchIn
        }
    }
}

class SetDiffFinder<V: Any>(
    val toSearch: Set<V>,
    val searchIn: Set<V>
) : Callable<Set<V>> {
    override fun call(): Set<V> {
        return toSearch.includedOnlyInSelf(searchIn)
    }
}