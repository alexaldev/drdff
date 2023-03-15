package utils

import java.util.*

object Project {

    private val versionProperties = Properties()

    val version: String
        get() {
            return versionProperties.getProperty("version") ?: "unknown"
        }

    val name = "Drdff"

    val nameAndVersion = "$name v.$version"

    init {
        val versionPropertiesFile = this.javaClass.getResourceAsStream("/version.properties")
        versionProperties.load(versionPropertiesFile)
    }
}
