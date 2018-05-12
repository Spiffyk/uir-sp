package cz.spiffyk.uirsp.preprocess.preprocessors

import cz.spiffyk.uirsp.preprocess.PreprocessResult
import cz.spiffyk.uirsp.tweet.TextVector
import cz.spiffyk.uirsp.tweet.Tweet
import cz.spiffyk.uirsp.tweet.TweetWithVector

/**
 * A bag-of-words generator object.
 */
object BagOfWordsPreprocessor {

    fun preprocess(tweet: Tweet): TweetWithVector {
        val split = tweet.splitWords()
        val builder = TextVector.Builder()
        split.forEach {
            builder.add(it)
        }
        return TweetWithVector(tweet, builder.build())
    }

    fun preprocess(tweets: Iterable<Tweet>): PreprocessResult {
        val resultTweets = ArrayList<TweetWithVector>()
        val resultWords = HashSet<String>()
        tweets.forEach {
            val tweet = preprocess(it)
            resultTweets.add(tweet)
            resultWords.addAll(tweet.vector.keys)
        }
        return PreprocessResult(resultTweets, resultWords)
    }

}