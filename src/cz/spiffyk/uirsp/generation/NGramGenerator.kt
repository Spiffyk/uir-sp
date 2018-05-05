package cz.spiffyk.uirsp.generation

import cz.spiffyk.uirsp.tweet.Tweet
import cz.spiffyk.uirsp.tweet.TweetVector
import java.util.stream.Collectors

/**
 * A Bag-of-words / N-gram generator object.
 */
object NGramGenerator {

    fun generate(tweet: Tweet, n: Int = 1): TweetVector {
        if (n <= 0) {
            throw IllegalArgumentException("n must be a positive integer")
        }

        if (n == 1) {
            return generateBagOfWords(tweet)
        } else {
            return generateNGram(tweet, n)
        }
    }

    fun generate(tweets: Iterable<Tweet>, n: Int = 1): List<TweetVector> {
        if (n <= 0) {
            throw IllegalArgumentException("n must be a positive integer")
        }

        val result = ArrayList<TweetVector>()
        tweets.forEach {
            result.add(generate(it, n))
        }
        return result
    }



    private fun generateBagOfWords(tweet: Tweet): TweetVector {
        val split = splitWords(tweet)
        val builder = TweetVector.Builder()
        split.forEach {
            builder.add(it)
        }
        return builder.build()
    }

    private fun generateNGram(tweet: Tweet, n: Int): TweetVector {
        val split = splitWords(tweet)
        val builder = TweetVector.Builder()
        for (i in 0..(split.size - n)) {
            val sb = StringBuilder(split[i])
            for (j in 1..(n - 1)) {
                sb.append(" ${split[i + j]}")
            }
            builder.add(sb.toString())
        }
        return builder.build()
    }

    private fun splitWords(tweet: Tweet): List<String> {
        return tweet.body.split(Regex("""[,.!?;\s]+""")).stream()
                .filter { !it.isEmpty() }
                .collect(Collectors.toList())
    }
}