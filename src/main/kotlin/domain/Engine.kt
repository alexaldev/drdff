package domain

import includedOnlyInSelf
import kotlinx.coroutines.*
import utils.HasObservers
import utils.ListBackedObservable
import utils.requireState
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

typealias ResultHandler = (DrdffResult) -> Unit

sealed class EngineArguments {
    class PureStringArgs(val s1: Set<String>, val s2: Set<String>) : EngineArguments()
}

class DrdffEngine private constructor(
    private val config: EngineConfig,
    private val listBackedObservable: ListBackedObservable<State, (State) -> Unit> = ListBackedObservable()
) : HasObservers<(State) -> Unit> by listBackedObservable {

    private val directoryResolver = config.directoryResolver

    companion object {

        fun default() = DrdffEngine(EngineConfig.default())

        fun with(config: EngineConfig): DrdffEngine {
            return DrdffEngine(config)
        }
    }

    var state: State = State.Idle
        set(value) {
            field = value
            listBackedObservable.triggerObservers(value)
        }

    init {
        state = State.Idle
    }

    fun registerStateObserver(obs: (State) -> Unit) {
        listBackedObservable.subscribe(obs)
    }

    fun unregisterStateObserver(obs: (State) -> Unit) {
        listBackedObservable.unsubscribe(obs)
    }

    @Deprecated("Use compute(UserInput, (Result) -> Unit) instead. Does nothing.")
    fun compute(input: UserInput): DrdffResult {
        TODO()
    }

    private suspend fun computeDifferences(d1: Set<String>, d2: Set<String>): Set<String> {
        return d1.includedOnlyInSelf(d2)
    }

    fun compute(args: EngineArguments, resultHandler: ResultHandler) {
        // TODO()
    }

    @OptIn(ExperimentalTime::class)
    fun compute(input: UserInput, resultHandler: (DrdffResult) -> Unit) {

        requireEngineIdleness()

        var searchForSize: Int

        val (res, elapsed) = measureTimedValue {

            val (searchFor, searchIn) = searchPair(input)
            searchForSize = searchFor.size

            updateStateTo(State.ResolvingDifferences)
            searchFor.includedOnlyInSelf(searchIn)
        }

        resultHandler(
            DrdffResult(
                missingFilenames = res.sorted().toSet(),
                percentageMissing = ((res.size.toFloat() / searchForSize.toFloat()) * 100),
                duration = elapsed.inWholeMilliseconds,
                directoriesCompared = input.toString()
            )
        )
        updateStateTo(State.Idle)
    }

    private fun searchPair(input: UserInput): Pair<Set<String>, Set<String>> {
        updateStateTo(State.ResolvingDirectories(input.d1))
        val searchFor = directoryResolver.getContents(input.d1)
        val searchIn = directoryResolver.getContents(input.d2)
        return Pair(searchFor, searchIn)
    }

    fun shutdown() {
        updateStateTo(State.Idle)
    }

    private fun extractComputationContents(userInput: UserInput): List<Set<String>> {
        return listOf(
            directoryResolver.getContents(userInput.d1),
            directoryResolver.getContents(userInput.d2)
        )
    }

    private fun updateStateTo(state: State) {
        this.state = state
    }

    private fun requireEngineIdleness() {
        requireState(this.state == State.Idle) { "An operation is already running, cannot execute more." }
    }
}

data class ComputationProgress(val percentage: Int)
data class DrdffResult(
    val directoriesCompared: String,
    val missingFilenames: Set<String>,
    val percentageMissing: Float,
    val duration: Long
) {
    override fun toString() = "$missingFilenames\n| " +
            "%.2f".format(percentageMissing) + "%\n" +
            "${duration}ms"
}

class EngineConfig private constructor(
    val directoryResolver: DirectoryResolver = NativeDirectoryResolver()
) {
    companion object {
        fun default() = EngineConfig()
        fun withResolver(resolver: DirectoryResolver) = EngineConfig(resolver)
    }
}