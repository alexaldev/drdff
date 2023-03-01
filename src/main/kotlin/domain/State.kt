package domain

sealed class State {
    object Idle : State()
    data class ResolvingDirectories(val directory: String): State()
    object ResolvingDifferences : State()
}