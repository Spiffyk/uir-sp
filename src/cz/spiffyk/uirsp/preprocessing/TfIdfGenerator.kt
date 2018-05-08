package cz.spiffyk.uirsp.preprocessing

import cz.spiffyk.uirsp.tweet.Tweet
import cz.spiffyk.uirsp.tweet.TextVector
import cz.spiffyk.uirsp.tweet.TweetWithVector

object TfIdfGenerator {

    fun generate(tweets: List<Tweet>): List<TweetWithVector> {
        val ocVectorBuilder = TextVector.Builder()
        val tfVectors = ArrayList<TweetWithVector>()
        tweets.forEach {
            val tfVectorBuilder = TextVector.Builder()
            val split = it.splitWords()
            split.forEach {
                tfVectorBuilder.add(it)
            }
            tfVectorBuilder.map.forEach {
                ocVectorBuilder.add(it.key)
            }
            tfVectors.add(TweetWithVector(it, tfVectorBuilder.build() / split.size))
        }

        val idfVector = generateIdf(ocVectorBuilder.build(), tweets.size)
        val result = ArrayList<TweetWithVector>()
        tfVectors.forEach {
            result.add(TweetWithVector(it.tweet, it.vector.hadamard(idfVector)))
        }
        return result
    }

    private fun generateIdf(ocVector: TextVector, tweetCount: Int): TextVector {
        val result = HashMap<String, Double>()
        ocVector.forEach {
            result[it.key] = Math.log10(tweetCount / (1.0 + it.value))
        }
        return TextVector(result)
    }

}
