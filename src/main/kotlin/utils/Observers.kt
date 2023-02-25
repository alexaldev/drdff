package utils

class ListBackedObservable<R: Any, T : (R) -> Unit> : HasObservers<T> {
    protected val observers = mutableListOf<T>()

    fun subscribe(obs: T) {
        observers += obs
    }

    fun unsubscribe(obs: T) {
        observers -= obs
    }

    fun removeAll() {
        observers.clear()
    }

    fun triggerObservers(newValue: R) {
        observers.forEach { it.invoke(newValue) }
    }
}

interface HasObservers<T> {

}
