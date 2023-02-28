package input

import domain.UserInput
import java.io.File
import kotlin.test.Test
import kotlin.test.assertFailsWith

class UserInputTests {

    @Test
    fun `The user can provide 2 directory strings`() {
        validTestDirectories().windowed(size = 2, step = 2).map { UserInput(it.first(), it[1]) }
    }

    @Test
    fun `The provided inputs from the user must be directory strings, otherwise IAE is thrown`() {
        validTestDirectories().windowed(size = 2, step = 2).map { UserInput(it.first(), it[1]) }
        assertFailsWith(IllegalArgumentException::class) {
            invalidTestDirectories().windowed(size = 2, step = 2).map { UserInput(it.first(), it[1]) }
        }
    }

    private fun validTestDirectories() = listOf(
        "/",
        "/bin",
        "/home"
    )

    private fun invalidTestDirectories() = listOf(
        "/bash",
        "/bin/beep",
        "/bap/bop/bam"
    )
}

private fun fileAsDirectoryExists(s: String): Boolean {
    return File(s).isDirectory
}
