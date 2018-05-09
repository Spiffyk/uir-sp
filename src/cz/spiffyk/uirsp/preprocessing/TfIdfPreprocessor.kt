package cz.spiffyk.uirsp.preprocessing

import cz.spiffyk.uirsp.tweet.Tweet
import cz.spiffyk.uirsp.tweet.TextVector
import cz.spiffyk.uirsp.tweet.TweetWithVector

object TfIdfPreprocessor {

    fun generate(tweets: List<Tweet>): PreprocessResult {
        val overallCountVectorBuilder = TextVector.Builder()
        val tfVectors = ArrayList<TweetWithVector>()
        tweets.forEach {
            val tfVectorBuilder = TextVector.Builder()
            val split = it.splitWords()
            // Increment tf value for each word
            split.forEach {
                tfVectorBuilder.add(it)
            }
            // Increment overall count for each unique word
            tfVectorBuilder.map.keys.forEach {
                overallCountVectorBuilder.add(it)
            }
            tfVectors.add(TweetWithVector(it, tfVectorBuilder.build() / split.size))
        }

        val idfVector = generateIdf(overallCountVectorBuilder.build(), tweets.size)
        val resultTweets = ArrayList<TweetWithVector>()
        tfVectors.forEach {
            resultTweets.add(TweetWithVector(it.tweet, it.vector.hadamard(idfVector)))
        }
        return PreprocessResult(resultTweets, overallCountVectorBuilder.map.keys)
    }

    private fun generateIdf(ocVector: TextVector, tweetCount: Int): TextVector {
        val result = HashMap<String, Double>()
        ocVector.forEach {
            result[it.key] = Math.log10(tweetCount / (1.0 + it.value))
        }
        return TextVector(result)
    }

}
