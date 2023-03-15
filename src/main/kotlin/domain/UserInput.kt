package domain

import java.io.File

private fun String.isDirectoryAndRequired() {
    require(this.isDirectoryAndExists()) { "$this must correspond to existing directory"  }
}

private fun String.isDirectoryAndExists(): Boolean {
    return File(this).isDirectory
}

data class UserInput(val d1: String, val d2: String) {
    init {
        d1.isDirectoryAndRequired()
        d2.isDirectoryAndRequired()
    }

    override fun toString(): String {
        return "For every file in $d1, I tried to find it in $d2"
    }
}