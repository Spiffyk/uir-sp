package cz.spiffyk.uirsp.preprocessing

import cz.spiffyk.uirsp.tweet.Tweet
import cz.spiffyk.uirsp.tweet.TweetVector

object TfIdfGenerator {

    fun generate(tweets: List<Tweet>): List<TweetVector> {
        val ocVectorBuilder = TweetVector.Builder()
        val tfVectors = ArrayList<TweetVector>()
        tweets.forEach {
            val tfVectorBuilder = TweetVector.Builder()
            val split = it.splitWords()
            split.forEach {
                tfVectorBuilder.add(it)
            }
            tfVectorBuilder.map.forEach {
                ocVectorBuilder.add(it.key)
            }
            tfVectors.add(tfVectorBuilder.build() / split.size)
        }

        val idfVector = generateIdf(ocVectorBuilder.build(), tweets.size)
        val result = ArrayList<TweetVector>()
        tfVectors.forEach {
            result.add(it.hadamard(idfVector))
        }
        return result
    }

    private fun generateIdf(ocVector: TweetVector, tweetCount: Int): TweetVector {
        val result = HashMap<String, Double>()
        ocVector.forEach {
            result[it.key] = Math.log10(tweetCount / (1.0 + it.value))
        }
        return TweetVector(result)
    }

}
