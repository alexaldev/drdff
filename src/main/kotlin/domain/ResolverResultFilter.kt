package domain

interface Filter<in T> {
    fun evaluates(t: T): Boolean
}

class ListOrFilter<T>(
    private val filters: List<Filter<T>>
) : Filter<T> {
    override fun evaluates(t: T): Boolean {
        return filters.fold(
            false
        ) { acc, filter -> acc or filter.evaluates(t) }
    }
}

interface ResultFileNameFilter : Filter<String>