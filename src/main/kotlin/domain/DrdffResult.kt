package domain

data class DrdffResult(
    val directoriesCompared: String,
    val missingFilenames: Set<String>,
    val percentageMissing: Float,
    val duration: Long
) {
    override fun toString() = "$missingFilenames\n| " +
            "%.2f".format(percentageMissing) + "%\n" +
            "${duration}ms"
}