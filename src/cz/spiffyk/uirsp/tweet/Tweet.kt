package cz.spiffyk.uirsp.tweet

import java.math.BigInteger
import java.time.ZonedDateTime
import java.util.stream.Collectors

/**
 * A data class representing a tweet.
 *
 * @property id ID of the tweet
 * @property timestamp the time of the tweet's posting
 * @property langCode code of the language the tweet is written in
 * @property eventTopic the topic of the event; tweet has not been event-annotated if this is `null`
 * @property body the body of the tweet
 */
data class Tweet(val id: BigInteger,
                 val timestamp: ZonedDateTime,
                 val langCode: String,
                 val eventTopic: EventTopic? = null,
                 val body: String) {

    fun splitWords(): List<String> {
        return this.body.split(Regex("""[,.!?;\s]+""")).stream()
                .filter { return@filter !it.isEmpty() }
                .collect(Collectors.toList())
    }
}