import domain.DrdffEngine
import domain.UserInput
import mu.KotlinLogging

fun main(args: Array<String>) {

    val logger = KotlinLogging.logger("main")

    val input = UserInput("src/test/resources/search_for_files_from", "src/test/resources/search_for_files_in")
    val testEngine = DrdffEngine.default()
    testEngine.registerStateObserver {
        println("I am getting triggered")
    }
    testEngine.compute(input)
}