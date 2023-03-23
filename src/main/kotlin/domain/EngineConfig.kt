package domain

typealias FileExtensions = MutableList<String>

class EngineConfig private constructor() {

    private val extensions: FileExtensions = mutableListOf()
    var directoryResolver: DirectoryResolver = NativeDirectoryResolver()
    var setsOperations: SetsOperations = ByIntersectOperation()


    fun setExtensions(extensions: Collection<String>) {
        this.extensions.clear()
        this.extensions.addAll(extensions)
    }

    companion object {
        fun default() = EngineConfig()

        @Deprecated("Use config() builder function instead.")
        fun withResolver(resolver: DirectoryResolver) = builder { directoryResolver = resolver }

        fun builder(init: EngineConfig.() -> Unit): EngineConfig {
            val result = EngineConfig()
            result.init()
            return result
        }
    }
}