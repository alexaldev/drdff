package files

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