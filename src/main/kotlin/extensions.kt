import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors
import kotlin.io.path.name

fun <T : Any> Set<T>.includedOnlyInSelf(other: Set<T>): Set<T> {
    return this - (this.intersect(other))
}

fun File.includedOnlyInSelf(other: File): Set<String> {
    val thisFileDir = this.directoryTreeSet
    val otherFileDir = other.directoryTreeSet
    return thisFileDir.includedOnlyInSelf(otherFileDir)
}

fun Path.includedOnlyInSelf(other: Path): Set<String> {
    return this.directoryTreeSet().includedOnlyInSelf(other.directoryTreeSet())
}

fun Path.directoryTreeSet(): Set<String> {
    return Files.walk(this)
        .map { it.name }
        .collect(Collectors.toSet())
}

val File.directoryTreeSet: MutableSet<String>
    get() = Files.walk(Paths.get(this.name))
        .map { it.name }
        .collect(Collectors.toSet())


fun <T : Any> File.fillWith(things: Iterable<T>, transform: (T) -> String = { it.toString() }) {
    this.bufferedWriter().use { out ->
        things.map(transform).forEach { out.write(it); out.newLine() }
    }
}
