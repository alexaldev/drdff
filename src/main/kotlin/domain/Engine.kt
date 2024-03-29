package domain

import utils.HasObservers
import utils.ListBackedObservable
import utils.requireState
import kotlin.time.ExperimentalTime
import kotlin.time.TimedValue
import kotlin.time.measureTimedValue

typealias ResultHandler = (DrdffResult) -> Unit
typealias EngineComputableArg = Pair<ResolverResult, ResolverResult>
sealed class EngineArguments {
    class PureStringArgs(val s1: Set<String>, val s2: Set<String>) : EngineArguments()
}

class DrdffEngine private constructor(
    private val config: EngineConfig,
    private val listBackedObservable: ListBackedObservable<State, (State) -> Unit> = ListBackedObservable()
) : HasObservers<(State) -> Unit> by listBackedObservable {

    companion object {

        fun noConfig() = DrdffEngine(EngineConfig.default())

        fun with(config: EngineConfig): DrdffEngine {
            return DrdffEngine(config)
        }
    }

    private val directoryResolver = config.directoryResolver
    private val setsOperator: SetsOperations = config.setsOperations
    private val postFiltersAggregator = ListOrFilter(config.postFilters)

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

    fun compute(args: EngineArguments, resultHandler: ResultHandler) {
        // TODO()
    }

    @OptIn(ExperimentalTime::class)
    fun compute(input: UserInput, resultHandler: (DrdffResult) -> Unit) {

        requireEngineIdleness()

        val (resultSetAndCheckSized, elapsed) = computeDifferences(input)

        val (resultSet, checkedSize) = resultSetAndCheckSized

        resultHandler(
            DrdffResult(
                missingFilenames = resultSet.sorted().toSet(),
                percentageMissing = ((resultSet.size.toFloat() / checkedSize.toFloat()) * 100),
                duration = elapsed.inWholeMilliseconds,
                directoriesCompared = input.toString()
            )
        )
        updateStateTo(State.Idle)
    }

    @OptIn(ExperimentalTime::class)
    private fun computeDifferences(userInput: UserInput): TimedValue<Pair<Set<String>, Int>> {
        return measureTimedValue {
            val (searchFor, searchIn) = extractSearchPairFrom(userInput)

            updateStateTo(State.ResolvingDifferences)

            val output =
                setsOperator.includedOnlyInSelf(searchFor.namesToAbsolutePath.keys, searchIn.namesToAbsolutePath.keys)

            Pair(output.map { searchFor.namesToAbsolutePath[it]!! }.toSet(), searchFor.namesToAbsolutePath.size)
        }
    }

    private fun extractSearchPairFrom(input: UserInput): EngineComputableArg {

        updateStateTo(State.ResolvingDirectories(input.d1))

        val searchFor = applyPostFiltersOn(directoryResolver.getContents(input.d1, config.resolverProgressListener))
        val searchIn = applyPostFiltersOn(directoryResolver.getContents(input.d2, config.resolverProgressListener))

        return Pair(searchFor, searchIn)
    }

    private fun applyPostFiltersOn(resolverResult: ResolverResult): ResolverResult {

        if (postFiltersAggregator.noFiltersAttached()) return resolverResult

        return resolverResult.copy(
            namesToAbsolutePath = resolverResult.namesToAbsolutePath.filterKeys { postFiltersAggregator.evaluates(it) }
        )
    }

    fun shutdown() {
        updateStateTo(State.Idle)
    }

    private fun updateStateTo(state: State) {
        this.state = state
    }

    private fun requireEngineIdleness() {
        requireState(this.state == State.Idle) { "An operation is already running, cannot execute more." }
    }
}
