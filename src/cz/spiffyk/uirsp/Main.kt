package cz.spiffyk.uirsp

import cz.spiffyk.uirsp.tweet.TweetsCsvParser
import cz.spiffyk.uirsp.util.CommandLineArgs
import java.io.File
import kotlin.system.exitProcess

const val STATUS_HELP: Int = 255
const val STATUS_INVALID_ARGS: Int = 1

const val HELP_TEXT =
        "Input files can be specified either as CLI arguments or, when none supplied, in stdin.\n\n" +
        "Supported switches:\n" +
        "-h --help      - shows this help"

/**
 * Program entry point.
 *
 * @param args Command line arguments (parsed by [CommandLineArgs])
 */
fun main(args: Array<String>) {
    val parser = TweetsCsvParser()
    try {
        val argsDto = CommandLineArgs(args)

        if (argsDto.help) {
            println(HELP_TEXT)
            exitProcess(STATUS_HELP)
        }

        val file = determineFile(argsDto)
        val tweets = parser.parseFile(file)

        println("Parsed ${tweets.size} tweets.")
    } catch (e: CommandLineArgs.InvalidArgsException) {
        System.err.println(e.message)
        println(HELP_TEXT)
        exitProcess(STATUS_INVALID_ARGS)
    }
}

/**
 * Gets file from CLI arguments.
 */
fun determineFile(argsDto: CommandLineArgs): File = when (argsDto.file) {
    null -> {
        print("File: ")
        File(readLine())
    }
    else -> File(argsDto.file)
}
