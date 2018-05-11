package cz.spiffyk.uirsp.preprocess.preprocessors

import cz.spiffyk.uirsp.preprocess.PreprocessResult
import cz.spiffyk.uirsp.tweet.Tweet
import cz.spiffyk.uirsp.tweet.TextVector
import cz.spiffyk.uirsp.tweet.TweetWithVector

/**
 * An N-gram generator object.
 */
object NGramPreprocessor {

    fun preprocess(tweet: Tweet, n: Int): TweetWithVector {
        if (n <= 0) {
            throw IllegalArgumentException("n must be a positive integer")
        }
        if (n == 1) {
            return BagOfWordsPreprocessor.preprocess(tweet)
        }

        return nGram(tweet, n)
    }

    fun preprocess(tweets: Iterable<Tweet>, n: Int): PreprocessResult {
        if (n <= 0) {
            throw IllegalArgumentException("n must be a positive integer")
        }
        if (n == 1) {
            return BagOfWordsPreprocessor.preprocess(tweets)
        }

        val resultTweets = ArrayList<TweetWithVector>()
        val resultWords = HashSet<String>()
        tweets.forEach {
            val tweet = nGram(it, n)
            resultTweets.add(tweet)
            resultWords.addAll(tweet.vector.keys)
        }
        return PreprocessResult(resultTweets, resultWords)
    }

    private fun nGram(tweet: Tweet, n: Int): TweetWithVector {
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