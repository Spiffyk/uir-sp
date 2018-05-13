package cz.spiffyk.uirsp.preprocess.preprocessors

import cz.spiffyk.uirsp.preprocess.PreprocessResult
import cz.spiffyk.uirsp.tweet.TextVector
import cz.spiffyk.uirsp.tweet.Tweet
import cz.spiffyk.uirsp.tweet.TweetWithVector

/**
 * A bag-of-words pre-processor object.
 */
object BagOfWordsPreprocessor {

    /**
     * Preprocesses an [Iterable] of [Tweet]s and creates a new [PreprocessResult].
     *
     * @param tweets the [Tweet]s to pre-process
     */
    fun preprocess(tweets: Iterable<Tweet>): PreprocessResult {
        val resultTweets = ArrayList<TweetWithVector>()
        val resultWords = HashSet<String>()
        tweets.forEach {
            val tweet = preprocessTweet(it)
            resultTweets.add(tweet)
            resultWords.addAll(tweet.vector.keys)
        }
        return PreprocessResult(resultTweets, resultWords)
    }

    /**
     * Generates a bag-of-words [TextVector] from the specified [Tweet] and wraps these in a [TweetWithVector].
     *
     * @param tweet the [Tweet] to generate a [TextVector] from
     * @return a [TweetWithVector] referencing the newly created [TextVector] and the input `tweet`
     */
    private fun preprocessTweet(tweet: Tweet): TweetWithVector {
        val split = tweet.splitWords()
        val builder = TextVector.Builder()
        split.forEach {
            builder.add(it)
        }
        return TweetWithVector(tweet, builder.build())
    }

}