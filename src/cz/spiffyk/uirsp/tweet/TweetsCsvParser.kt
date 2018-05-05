package cz.spiffyk.uirsp.tweet

import java.io.File
import java.math.BigInteger
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * A class for parsing CSV files containing tweets.
 */
object TweetsCsvParser {

    private const val BARE_TWEET_VALUE_COUNT: Int = 4
    private const val ANNOTATED_TWEET_VALUE_COUNT: Int = 6

    private const val DATE_TIME_PATTERN: String = "E MMM d HH:mm:ss zz y"
    private val DATE_TIME_FORMATTER: DateTimeFormatter =
            DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)

    fun parseFile(file: File,
                  valueSeparator: Char = ';'): List<Tweet> {
        println("Parsing file '${file.absolutePath}' ...")

        val result = ArrayList<Tweet>()
        var lineNo = 0

        file.forEachLine {
            lineNo++
            if (it.isNotBlank()) {
                val values = it.split(valueSeparator)

                try {
                    when (values.size) {
                        BARE_TWEET_VALUE_COUNT -> {
                            result.add(Tweet(
                                    id = BigInteger(values[0]),
                                    langCode = values[1],
                                    timestamp = ZonedDateTime.parse(values[2], DATE_TIME_FORMATTER), // values[2] // TODO - timestamp formatter
                                    body = values[3]))
                        }
                        ANNOTATED_TWEET_VALUE_COUNT -> {
                            result.add(Tweet(
                                    eventTopic = EventTopic.ofStrict(values[1]),
                                    id = BigInteger(values[2]),
                                    langCode = values[3],
                                    timestamp = ZonedDateTime.parse(values[4], DATE_TIME_FORMATTER), // values[4] // TODO - timestamp formatter
                                    body = values[5]))
                        }
                        else -> {
                            throw ParserException("Unrecognized number of tweet values (${values.size})!")
                        }
                    }
                } catch (e: Exception) {
                    System.err.println("Error on line $lineNo:\n$it\n")
                    throw e
                }
            }
        }

        return result
    }

    /**
     * Thrown when parser fails.
     */
    class ParserException(override var message: String? = null, override var cause: Throwable? = null):
            Exception(message, cause)
}