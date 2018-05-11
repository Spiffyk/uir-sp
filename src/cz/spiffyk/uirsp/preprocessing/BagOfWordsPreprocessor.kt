package cz.spiffyk.uirsp.preprocessing

import cz.spiffyk.uirsp.tweet.TextVector
import cz.spiffyk.uirsp.tweet.Tweet
import cz.spiffyk.uirsp.tweet.TweetWithVector

/**
 * A bag-of-words generator object.
 */
object BagOfWordsPreprocessor {

    fun generate(tweet: Tweet): TweetWithVector {
        val split = tweet.splitWords()
        val builder = TextVector.Builder()
        split.forEach {
            builder.add(it)
        }
        return TweetWithVector(tweet, builder.build())
    }

    fun generate(tweets: Iterable<Tweet>): PreprocessResult {
        val resultTweets = ArrayList<TweetWithVector>()
        val resultWords = HashSet<String>()
        tweets.forEach {
            val tweet = generate(it)
            resultTweets.add(tweet)
            resultWords.addAll(tweet.vector.keys)
        }
        return PreprocessResult(resultTweets, resultWords)
    }

}