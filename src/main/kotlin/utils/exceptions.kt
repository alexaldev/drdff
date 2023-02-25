package utils

inline fun requireState(value: Boolean, lazyMessage: () -> Any) {
    if (!value) {
        val msg = lazyMessage()
        throw IllegalStateException(msg.toString())
    }
}