package ui

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int
import utils.logger

class RunConfig : CliktCommand() {

    private val logger by logger()

    private val toSearchDirName by option(
        "-d",
        "--directory",
        help = "Directory containing all the files to be searched"
    ).required()

    private val searchInDirName by option(
        "-i",
        "--search_in",
        help = "Directory to search the files found in specified directory"
    ).required()

    private val resultsFileName by option("-o", "--results", help = "Filename to store the results")

    private val threadsCount by option("-t", "--threads", help = "Specify the number of threads to run the application").int()
        .default(1)

    override fun run() {

    }
}