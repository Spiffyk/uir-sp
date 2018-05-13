package cz.spiffyk.uirsp.preprocess.preprocessors

import cz.spiffyk.uirsp.preprocess.PreprocessResult
import cz.spiffyk.uirsp.tweet.Tweet
import cz.spiffyk.uirsp.tweet.TextVector
import cz.spiffyk.uirsp.tweet.TweetWithVector

/**
 * An n-gram pre-processor object.
 */
object NGramPreprocessor {

    /**
     * Preprocesses an [Iterable] of [Tweet]s and creates a new [PreprocessResult].
     *
     * @param tweets the [Tweet]s to pre-process
     * @param n the `n` in n-gram
     */
    fun preprocess(tweets: Iterable<Tweet>, n: Int): PreprocessResult {
        if (n <= 0) {
            throw IllegalArgumentException("n must be a positive integer")
        }

        val resultTweets = ArrayList<TweetWithVector>()
        val resultWords = HashSet<String>()
        tweets.forEach {
            val tweet = preprocessTweet(it, n)
            resultTweets.add(tweet)
            resultWords.addAll(tweet.vector.keys)
        }
        return PreprocessResult(resultTweets, resultWords)
    }

    /**
     * Generates an n-gram [TextVector] from the specified [Tweet] and wraps these in a [TweetWithVector].
     *
     * @param tweet the [Tweet] to generate a [TextVector] from
     * @return a [TweetWithVector] referencing the newly created [TextVector] and the input `tweet`
     */
    private fun preprocessTweet(tweet: Tweet, n: Int): TweetWithVector {
        val split = tweet.splitWords(applyFilter = false)
        val builder = TextVector.Builder()
        if (split.size >= n) {
            for (i in 0..(split.size - n)) {
                val sb = StringBuilder(split[i])
                for (j in 1..(n - 1)) {
                    sb.append(" ${split[i + j]}")
                }
                builder.add(sb.toString())
            }
        }
        return TweetWithVector(tweet, builder.build())
    }
}