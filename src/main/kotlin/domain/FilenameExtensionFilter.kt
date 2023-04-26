package domain

class FilenameExtensionFilter(
    private val ext: String
) : ResultFileNameFilter {
    override fun evaluates(t: String): Boolean {
        return t.substringAfterLast(".").lowercase() == ext
    }
}