package cz.spiffyk.uirsp.preprocessing

import cz.spiffyk.uirsp.tweet.Tweet
import cz.spiffyk.uirsp.tweet.TextVector
import cz.spiffyk.uirsp.tweet.TweetWithVector

/**
 * A Bag-of-words / N-gram generator object.
 */
object NGramPreprocessor {

    fun generate(tweet: Tweet, n: Int = 1): TweetWithVector {
        if (n <= 0) {
            throw IllegalArgumentException("n must be a positive integer")
        }

        if (n == 1) {
            return generateBagOfWords(tweet)
        } else {
            return generateNGram(tweet, n)
        }
    }

    fun generate(tweets: Iterable<Tweet>, n: Int = 1): PreprocessResult {
        if (n <= 0) {
            throw IllegalArgumentException("n must be a positive integer")
        }

        val resultTweets = ArrayList<TweetWithVector>()
        val resultWords = HashSet<String>()
        tweets.forEach {
            val tweet = generate(it, n)
            resultTweets.add(tweet)
            resultWords.addAll(tweet.vector.keys)
        }
        return PreprocessResult(resultTweets, resultWords)
    }



    private fun generateBagOfWords(tweet: Tweet): TweetWithVector {
        val split = tweet.splitWords()
        val builder = TextVector.Builder()
        split.forEach {
            builder.add(it)
        }
        return TweetWithVector(tweet, builder.build())
    }

    private fun generateNGram(tweet: Tweet, n: Int): TweetWithVector {
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