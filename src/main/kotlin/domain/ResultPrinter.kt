package domain

import mu.KLogger
import utils.Project

interface ResultPrinter {
    fun print(result: DrdffResult)
}

class StdOutWriter(
    private val logger: KLogger
) : ResultPrinter {

    override fun print(result: DrdffResult) {
        logger.debug { Project.version }
    }
}