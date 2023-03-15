import mu.KotlinLogging
import ui.RunConfig

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) = RunConfig().main(args)
//
//    val input = UserInput("src/test/resources/search_for_files_from", "src/test/resources/search_for_files_in")
//
//    val config = EngineConfig.with(KotlinPathListDirectoriesResolver())
//    val testEngine = DrdffEngine.with(config)
//
//    testEngine.registerStateObserver {
//        when (it) {
//            is State.Idle -> {
//                logger.info { "Engine idle" }
//            }
//
//            is State.ResolvingDifferences -> {
//                logger.info { "Resolving differences" }
//            }
//
//            is State.ResolvingDirectories -> {
//                logger.info { "Resolving directory: ${it.directory}" }
//            }
//        }
//    }
//
//    testEngine.compute(input) {
//        logger.info { it.toString() }
//    }
//}