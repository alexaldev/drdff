package domain

import utils.HasObservers
import utils.ListBackedObservable
import utils.requireState

class DrdffEngine private constructor(
    private val config: EngineConfig,
    private val listBackedObservable: ListBackedObservable<State, (State) -> Unit> = ListBackedObservable()
) : HasObservers<(DrdffEngine.State) -> Unit> by listBackedObservable {

    sealed class State {
        object Idle : State()
        data class Computing(val progress: ComputationProgress) : State()
    }

    companion object {

        fun default() = DrdffEngine(EngineConfig.default())

        fun from(config: EngineConfig): DrdffEngine {
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

    fun compute(input: UserInput): DrdffResult {

        requireEngineIdleness()

        updateStateTo(State.Computing(ComputationProgress(0)))

        val result = DrdffResult()

        return result
    }

    fun compute(input: UserInput, result: (DrdffResult) -> Unit) {
        result(compute(input))
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

data class ComputationProgress(val percentage: Int)
class DrdffResult
class EngineConfig private constructor() {
    companion object {
        fun default() = EngineConfig()
    }
}