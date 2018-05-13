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

    /**
     * Annotated tweet value count.
     */
    private const val ANNOTATED_TWEET_VALUE_COUNT: Int = 6

    /**
     * The datetime pattern used in the CSV files.
     */
    private const val DATE_TIME_PATTERN: String = "E MMM d HH:mm:ss zz y"

    /**
     * The datetime formatter for parsing/formatting dates in tweets CSVs.
     */
    val DATE_TIME_FORMATTER: DateTimeFormatter =
            DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)


    /**
     * Parses the specified file into a list of [Tweet]s.
     *
     * @param file the file to parse
     * @param valueSeparator separator of individual values
     * @return a list of [Tweet]s parsed from the file
     */
    fun parseFile(file: File,
                  valueSeparator: Char = ';'): List<Tweet> {
        if (!file.exists()) {
            throw ParserException("'${file.path}' not found!")
        }
        if (!file.isFile) {
            throw ParserException("'${file.path}' is not a file!")
        }

        val result = ArrayList<Tweet>()
        var lineNo = 0

        file.forEachLine {
            lineNo++
            if (it.isNotBlank()) {
                val values = it.split(valueSeparator)

                try {
                    when (values.size) {
                        ANNOTATED_TWEET_VALUE_COUNT -> {
                            result.add(Tweet(
                                    topic = Topic.ofStrict(values[1]),
                                    id = BigInteger(values[2]),
                                    langCode = values[3],
                                    timestamp = ZonedDateTime.parse(values[4], DATE_TIME_FORMATTER),
                                    body = values[5].trim()))
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