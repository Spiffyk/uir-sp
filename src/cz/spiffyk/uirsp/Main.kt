package cz.spiffyk.uirsp

import cz.spiffyk.uirsp.preprocessing.NGramPreprocessor
import cz.spiffyk.uirsp.preprocessing.TfIdfPreprocessor
import cz.spiffyk.uirsp.tweet.TweetsCsvParser
import cz.spiffyk.uirsp.util.ArgsDto
import kotlin.system.exitProcess

const val STATUS_HELP: Int = 255
const val STATUS_INVALID_ARGS: Int = 1

/**
 * Program entry point.
 *
 * @param rawArgs Command line arguments (parsed by [ArgsDto])
 */
fun main(rawArgs: Array<String>) {
    try {
        val args = ArgsDto.of(rawArgs)

        if (args === null) {
            println(ArgsDto.HELP_TEXT)
            exitProcess(STATUS_HELP)
        }

        val tweets = TweetsCsvParser.parseFile(args.inputFile)

        println("Parsed ${tweets.size} tweets.\n\n")

        val bowStart = System.currentTimeMillis()
        val bowResult = NGramPreprocessor.generate(tweets)
        val bowEnd = System.currentTimeMillis()
        println("bag-of-words:   ${bowEnd - bowStart} ms")

        val nGramStart = System.currentTimeMillis()
        val nGramResult = NGramPreprocessor.generate(tweets, 3)
        val nGramEnd = System.currentTimeMillis()
        println("3-gram:         ${nGramEnd - nGramStart} ms")

        val tfIdfStart = System.currentTimeMillis()
        val tfIdfResult = TfIdfPreprocessor.generate(tweets)
        val tfIdfEnd = System.currentTimeMillis()
        println("tf-idf:         ${tfIdfEnd - tfIdfStart} ms")
    } catch (e: ArgsDto.InvalidArgsException) {
        println("${e.javaClass.simpleName}: ${e.message}")
        println(ArgsDto.HELP_TEXT)
        exitProcess(STATUS_INVALID_ARGS)
    }
}
