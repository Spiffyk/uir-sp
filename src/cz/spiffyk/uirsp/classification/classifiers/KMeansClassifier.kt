package cz.spiffyk.uirsp.classification.classifiers

import cz.spiffyk.uirsp.classification.ClassificationGroup
import cz.spiffyk.uirsp.classification.ClassificationResult
import cz.spiffyk.uirsp.classification.ClassificationTopic
import cz.spiffyk.uirsp.preprocess.PreprocessResult
import cz.spiffyk.uirsp.tweet.EventTopic
import cz.spiffyk.uirsp.tweet.TextVector
import cz.spiffyk.uirsp.tweet.TweetWithVector
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object KMeansClassifier {

    private val TOPIC_COUNT = EventTopic.values().size

    fun classify(preprocessResult: PreprocessResult): ClassificationResult {

        val means = MutableList(TOPIC_COUNT, { randomMean(preprocessResult.allKeys) })
        var tweetGroups: List<List<TweetWithVector>>

        do {
            tweetGroups = List(TOPIC_COUNT, { ArrayList<TweetWithVector>() })
            preprocessResult.tweets.forEach {
                var closestMean = -1
                var closestDist = Double.MAX_VALUE

                means.forEachIndexed { index, mean ->
                    val dist = TextVector.dist(it.vector, mean)
                    if (dist < closestDist) {
                        closestMean = index
                        closestDist = dist
                    }
                }

                tweetGroups[closestMean].add(it)
            }

            var meansChanged = false
            tweetGroups.forEachIndexed { index, tweet ->
                val newMean = meanOf(tweet)
                if (newMean != means[index]) {
                    means[index] = newMean
                    meansChanged = true
                }
            }
        } while (meansChanged)

        val resultGroups = ArrayList<ClassificationGroup>()
        tweetGroups.forEach { tweets ->
            val topicCountMap = HashMap<EventTopic, Int>()

            tweets.forEach { tweetWithVector ->
                val topic = tweetWithVector.tweet.eventTopic ?: throw NullPointerException()
                topicCountMap[topic] = ((topicCountMap[topic] ?: 0) + 1)
            }

            val topicProbabilityMap = TreeSet<ClassificationTopic>()
            topicCountMap.forEach { topicEntry ->
                topicProbabilityMap.add(
                        ClassificationTopic(
                                eventTopic = topicEntry.key,
                                probability = topicEntry.value.toDouble() / tweets.size.toDouble(),
                                count = topicEntry.value))
            }

            resultGroups.add(ClassificationGroup(tweets.map { it.tweet }, topicProbabilityMap))
        }

        return ClassificationResult(resultGroups)
    }

    private fun randomMean(keySet: Set<String>,
                           random: Random = Random(),
                           eccentricity: Double = 1.0): TextVector {
        val map = HashMap<String, Double>()
        keySet.forEach {
            map[it] = (random.nextDouble() * eccentricity * 2) - eccentricity
        }
        return TextVector(map)
    }

    private fun meanOf(tweetGroup: List<TweetWithVector>): TextVector {
        val builder = TextVector.Builder()
        tweetGroup.forEach {
            builder.add(it.vector)
        }
        return builder.build() / tweetGroup.size
    }

}