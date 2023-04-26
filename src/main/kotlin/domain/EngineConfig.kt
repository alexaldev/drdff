package domain

class EngineConfig private constructor() {

    var directoryResolver: DirectoryResolver = KotlinTreeWalkResolver()
    var setsOperations: SetsOperations = ByIntersectOperation()
    var resolverProgressListener: ProgressListener = ProgressListener {}
    val postFilters = mutableListOf<ResultFileNameFilter>()

    companion object {
        fun default() = EngineConfig()

        @Deprecated("Use config() builder function instead.")
        fun withResolver(resolver: DirectoryResolver) = builder { directoryResolver = resolver }

        /**
         * Builder function to create a new [EngineConfig].
         * You can specify the following properties:
         * - [directoryResolver] : The [DirectoryResolver] to use, default [KotlinTreeWalkResolver]
         * - [setsOperations]: The [SetsOperations] to use, default [ByIntersectOperation]
         * - [resolverProgressListener]: The [ProgressListener] which reports resolution progress, default null
         * - [postFilters]: You can populate a list of [ResultFileNameFilter], default is emptyList
         */
        fun builder(init: EngineConfig.() -> Unit): EngineConfig {
            val result = EngineConfig()
            result.init()
            return result
        }
    }
}