package cz.spiffyk.uirsp.preprocess.preprocessors

import cz.spiffyk.uirsp.preprocess.PreprocessResult
import cz.spiffyk.uirsp.tweet.Tweet
import cz.spiffyk.uirsp.tweet.TextVector
import cz.spiffyk.uirsp.tweet.TweetWithVector

/**
 * A tf-idf pre-processor object.
 */
object TfIdfPreprocessor {

    /**
     * Preprocesses a [Collection] of [Tweet]s and creates a new [PreprocessResult].
     *
     * @param tweets the [Tweet]s to pre-process
     */
    fun preprocess(tweets: Collection<Tweet>): PreprocessResult {
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

        val idfVector = idf(overallCountVectorBuilder.build(), tweets.size)
        val resultTweets = ArrayList<TweetWithVector>()
        tfVectors.forEach {
            resultTweets.add(TweetWithVector(it.tweet, it.vector.hadamard(idfVector)))
        }
        return PreprocessResult(resultTweets, overallCountVectorBuilder.map.keys)
    }

    /**
     * Calculates an idf value [TextVector] from an overall-count [TextVector].
     *
     * @param ocVector the vector with counts of documents containing each of the words
     * @param tweetCount the count of preprocessed tweets
     *
     * @return the calculated idf value [TextVector]
     */
    private fun idf(ocVector: TextVector, tweetCount: Int): TextVector {
        val result = HashMap<String, Double>()
        ocVector.forEach {
            result[it.key] = Math.log10(tweetCount / (1.0 + it.value))
        }
        return TextVector(result)
    }

}
