package cz.spiffyk.uirsp

import cz.spiffyk.uirsp.tweet.TweetsCsvParser
import cz.spiffyk.uirsp.util.CommandLineArgs
import java.io.File

const val STATUS_HELP: Int = 255

/**
 * Program entry point.
 *
 * @param args Command line arguments (parsed by [CommandLineArgs])
 */
fun main(args: Array<String>) {
    val parser = TweetsCsvParser()
    val commandLineArgs = CommandLineArgs(args)

    if (commandLineArgs.help) {
        printHelp()
        System.exit(STATUS_HELP)
    }

    print("File: ")
    val file = File(readLine())
    val tweets = parser.parseFile(file)

    println("Parsed ${tweets.size} tweets.")
}

/**
 * Prints help into `stdout`.
 */
fun printHelp() {
    println("This program reads files from stdin.\n\n" +
            "Supported switches:\n" +
            "-h --help      - shows this help")
}
