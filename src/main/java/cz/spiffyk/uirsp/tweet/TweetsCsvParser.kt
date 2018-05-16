package cz.spiffyk.uirsp.tweet

import java.io.File
import java.math.BigInteger
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList

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
     * @return a list of [Tweet]s parsed from the file
     */
    fun parseFile(file: File): List<Tweet> {
        if (!file.exists()) {
            throw ParserException("'${file.path}' not found!")
        }
        if (!file.isFile) {
            throw ParserException("'${file.path}' is not a file!")
        }

        val result = ArrayList<Tweet>()
        var lineNo = 0

        file.forEachLine { line ->
            lineNo++
            val trimmedLine = line.trim()
            if (trimmedLine.isNotBlank()) {
                try {
                    result.add(parseLine(trimmedLine))
                } catch (e: Exception) {
                    throw ParserException("Error on line $lineNo", e)
                }
            }
        }

        return result
    }



    /**
     * Parses a CSV line and creates a [Tweet] out of it.
     *
     * @param line the line to parse
     * @return the resulting [Tweet]
     */
    private fun parseLine(line: String): Tweet {
        val separatorIndices = ArrayList<Int>()
        line.forEachIndexed { i, c ->
            if (c == ';') {
                separatorIndices.add(i)
            }
        }

        if (separatorIndices.size < (ANNOTATED_TWEET_VALUE_COUNT - 1)) {
            throw ParserException("Insufficient number of values in CSV file (${separatorIndices.size})!")
        }

        return Tweet(
                topic = getTopic(line, separatorIndices),
                id = getId(line, separatorIndices),
                langCode = getLangCode(line, separatorIndices),
                timestamp = getTimestamp(line, separatorIndices),
                body = getBody(line, separatorIndices))
    }

    /**
     * Parses the first and second value in the CSV line to determine the topic of the tweet.
     */
    private fun getTopic(line: String, separatorIndices: List<Int>): Topic {
        val isTopicStr = line.substring(0, separatorIndices[0])
        val topicCode = line.substring(separatorIndices[0] + 1, separatorIndices[1])
        val topic = Topic.ofStrict(topicCode)

        when {
            (isTopicStr == "0") -> {
                if (topic !== Topic.NONE) {
                    throw ParserException("Tweet is marked as a non-event but a topic ($topicCode) is set!")
                }
                return Topic.NONE
            }
            (isTopicStr != "1") -> throw ParserException("The first value on a line must be either 1 or 0!")
            (topic === Topic.NONE) -> throw ParserException("Tweet is marked as an event but no topic is set!")
        }

        return topic
    }

    /**
     * Parses the third value in the CSV line to determine the ID of the tweet.
     */
    private fun getId(line: String, separatorIndices: List<Int>): BigInteger {
        val str = line.substring(separatorIndices[1] + 1, separatorIndices[2])
        return BigInteger(str)
    }

    /**
     * Uses the fourth value in the CSV line as the language code of the tweet.
     */
    private fun getLangCode(line: String, separatorIndices: List<Int>): String {
        return line.substring(separatorIndices[2] + 1, separatorIndices[3])
    }

    /**
     * Parses the fifth value in the CSV line to determine the timestamp of the tweet.
     */
    private fun getTimestamp(line: String, separatorIndices: List<Int>): ZonedDateTime {
        val str = line.substring(separatorIndices[3] + 1, separatorIndices[4])
        return ZonedDateTime.parse(str, DATE_TIME_FORMATTER)
    }

    /**
     * Uses the sixth value in the CSV line as the body of the tweet.
     */
    private fun getBody(line: String, separatorIndices: List<Int>): String {
        return line.substring(separatorIndices[4] + 1)
    }



    /**
     * Thrown when parser fails.
     */
    class ParserException(override var message: String? = null, override var cause: Throwable? = null):
            Exception(message, cause)
}