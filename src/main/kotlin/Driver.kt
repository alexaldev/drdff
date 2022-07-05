import java.io.File
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main(args: Array<String>) {

    measureTime {
        val secondDirFiles = File(args[1]).directorySet()
        File(args[2]).fillWith((secondDirFiles - (secondDirFiles.intersect(File(args[0]).directorySet()))))
    }.also {
        println(
            "Difference computation between directories completed.\n" +
                    "Results stored in: ${args[2]} \n" +
                    "Computation lasted: $it"
        )
    }
}

fun <T : Any> File.fillWith(things: Iterable<T>, transform: (T) -> String = { it.toString() }) {
    this.bufferedWriter().use { out ->
        things.map(transform).forEach { out.write(it); out.newLine() }
    }
}

fun File.directorySet() =
    walk()
        .map { it.name }
        .toSet()