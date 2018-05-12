package cz.spiffyk.uirsp.classification.classifiers

import cz.spiffyk.uirsp.classification.ClassificationTopic
import cz.spiffyk.uirsp.tweet.Topic
import cz.spiffyk.uirsp.tweet.TweetWithVector
import java.util.*

object ClassifierUtils {

    fun calculateTopicStats(tweets: List<TweetWithVector>): TreeSet<ClassificationTopic> {
        val topicCountMap = HashMap<Topic, Int>()
        tweets.forEach { tweetWithVector ->
            val topic = tweetWithVector.tweet.topic
            topicCountMap[topic] = ((topicCountMap[topic] ?: 0) + 1)
        }

        val topicStats = TreeSet<ClassificationTopic>()
        topicCountMap.forEach { topicEntry ->
            topicStats.add(
                    ClassificationTopic(
                            topic = topicEntry.key,
                            percentage = topicEntry.value.toDouble() / tweets.size.toDouble(),
                            count = topicEntry.value))
        }

        return topicStats
    }
}