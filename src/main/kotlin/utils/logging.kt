package utils

import mu.KLogger
import mu.KotlinLogging

fun <T: Any> logger(forClass: Class<T>): KLogger {
    return KotlinLogging.logger(forClass.name)
}

fun <R : Any> R.logger(): Lazy<KLogger> {
    return lazy { logger(this.javaClass) }
}