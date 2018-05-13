package cz.spiffyk.uirsp.classification.classifiers

import cz.spiffyk.uirsp.classification.ClassificationTopicStats
import cz.spiffyk.uirsp.tweet.Topic
import cz.spiffyk.uirsp.tweet.TweetWithVector
import java.util.*

/**
 * A utility class for [KMeansClassifier] and [KNNClassifier].
 */
object ClassifierUtils {

    /**
     * Calculates topic statistics for the specified tweet group.
     */
    fun calculateTopicStats(tweets: List<TweetWithVector>): TreeSet<ClassificationTopicStats> {
        val topicCountMap = HashMap<Topic, Int>()
        tweets.forEach { tweetWithVector ->
            val topic = tweetWithVector.tweet.topic
            topicCountMap[topic] = ((topicCountMap[topic] ?: 0) + 1)
        }

        val topicStats = TreeSet<ClassificationTopicStats>()
        topicCountMap.forEach { topicEntry ->
            topicStats.add(
                    ClassificationTopicStats(
                            topic = topicEntry.key,
                            percentage = topicEntry.value.toDouble() / tweets.size.toDouble(),
                            count = topicEntry.value))
        }

        return topicStats
    }
}