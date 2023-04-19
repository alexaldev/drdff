package domain

class FilenameExtensionFilter(
    private val ext: String
) : ResolverResultFilter {
    override fun apply(resolverResult: ResolverResult): ResolverResult {
        return resolverResult.copy(
            namesToAbsolutePath = resolverResult
                .namesToAbsolutePath
                .filterKeys { it.substringAfterLast(".") == ext }
        )
    }

}
