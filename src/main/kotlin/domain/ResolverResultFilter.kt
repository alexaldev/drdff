package domain

fun interface Filter<in T> {
    fun evaluates(t: T): Boolean
}

class ListOrFilter<T>(
    private val filters: List<Filter<T>>
) : Filter<T> {

    fun noFiltersAttached() = filters.isEmpty()
    override fun evaluates(t: T): Boolean {

        return if (filters.isEmpty()) true
        else {
            filters.fold(
                false
            ) { acc, filter -> acc or filter.evaluates(t) }
        }
    }
}

interface ResultFileNameFilter : Filter<String>