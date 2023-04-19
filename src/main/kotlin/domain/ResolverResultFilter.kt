package domain

interface ResolverResultFilter {
    fun apply(resolverResult: ResolverResult): ResolverResult
}
