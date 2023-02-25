package domain

import utils.HasObservers
import utils.ListBackedObservable

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
        state = State.Computing(ComputationProgress(0))
        return DrdffResult()
    }

    fun compute(input: UserInput, result: (DrdffResult) -> Unit) {
        result(compute(input))
    }
}

data class ComputationProgress(val percentage: Int)
class DrdffResult
class EngineConfig private constructor() {
    companion object {
        fun default() = EngineConfig()
    }
}