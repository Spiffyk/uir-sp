package cz.spiffyk.uirsp.preprocessing

import cz.spiffyk.uirsp.tweet.Tweet
import cz.spiffyk.uirsp.tweet.TextVector
import cz.spiffyk.uirsp.tweet.TweetWithVector

/**
 * An N-gram generator object.
 */
object NGramPreprocessor {

    fun generate(tweet: Tweet, n: Int): TweetWithVector {
        if (n <= 0) {
            throw IllegalArgumentException("n must be a positive integer")
        }
        if (n == 1) {
            return BagOfWordsPreprocessor.generate(tweet)
        }

        return generateNgram(tweet, n)
    }

    fun generate(tweets: Iterable<Tweet>, n: Int): PreprocessResult {
        if (n <= 0) {
            throw IllegalArgumentException("n must be a positive integer")
        }
        if (n == 1) {
            return BagOfWordsPreprocessor.generate(tweets)
        }

        val resultTweets = ArrayList<TweetWithVector>()
        val resultWords = HashSet<String>()
        tweets.forEach {
            val tweet = generateNgram(it, n)
            resultTweets.add(tweet)
            resultWords.addAll(tweet.vector.keys)
        }
        return PreprocessResult(resultTweets, resultWords)
    }

    private fun generateNgram(tweet: Tweet, n: Int): TweetWithVector {
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