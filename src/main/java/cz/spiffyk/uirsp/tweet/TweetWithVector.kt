package cz.spiffyk.uirsp.tweet

/**
 * A pair of a [Tweet] and a [TextVector] generated from it.
 */
data class TweetWithVector(val tweet: Tweet,
                           val vector: TextVector)