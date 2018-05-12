package cz.spiffyk.uirsp.classification.classifiers

import cz.spiffyk.uirsp.classification.ClassificationGroup
import cz.spiffyk.uirsp.classification.ClassificationResult
import cz.spiffyk.uirsp.preprocess.PreprocessResult
import cz.spiffyk.uirsp.tweet.Topic
import cz.spiffyk.uirsp.tweet.TextVector
import cz.spiffyk.uirsp.tweet.TweetWithVector
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

object KNNClassifier {

    private val TOPIC_COUNT = Topic.values().size
    private val RANDOM = Random()

    fun classify(preprocessResult: PreprocessResult,
                 teacherRatio: Double,
                 k: Int): ClassificationResult {
        if ((preprocessResult.tweets.size * teacherRatio) < TOPIC_COUNT) {
            throw IllegalStateException("Too small teacher ratio!")
        }

        val tweets = HashSet(preprocessResult.tweets)
        val resultMap = teach(tweets, teacherRatio)

        var done = 0
        tweets.forEach { tweetWithVector ->
            val neighbors = TreeSet<Neighbor>()

            resultMap.forEach { topic, potentialNeighbors ->
                potentialNeighbors.forEach { potentialNeighbor ->
                    neighbors.add(Neighbor(
                            neighbor = potentialNeighbor,
                            topic = topic,
                            distance = TextVector.dist(tweetWithVector.vector, potentialNeighbor.vector)))
                }
            }

            val topicCounts = HashMap<Topic, Int>()
            var i = 0
            for (neighbor in neighbors) {
                if (i >= k) {
                    break
                }
                topicCounts[neighbor.topic] = (topicCounts[neighbor.topic] ?: 0) + 1
                i++
            }

            var maxCount = -1
            var maxTopic: Topic? = null
            topicCounts.forEach { topic, count ->
                if (count >= maxCount) {
                    if (count > maxCount || RANDOM.nextBoolean()) {
                        maxCount = count
                        maxTopic = topic
                    }
                }
            }

            resultMap[maxTopic]!!.add(tweetWithVector)
            done++
        }

        return ClassificationResult(resultMap.mapValues { entry ->
            ClassificationGroup(
                    tweets = entry.value.map {
                                tweetWithVector -> tweetWithVector.tweet
                            },
                    topics = ClassifierUtils.calculateTopicStats(entry.value))
        })
    }

    private fun teach(tweets: MutableSet<TweetWithVector>,
                      teacherRatio: Double,
                      random: Random = RANDOM): Map<Topic, ArrayList<TweetWithVector>> {
        val resultMap = HashMap<Topic, ArrayList<TweetWithVector>>()
        val intermediateMap = HashMap<Topic, ArrayList<TweetWithVector>>()
        Topic.values().forEach { topic ->
            resultMap[topic] = ArrayList()
            intermediateMap[topic] = ArrayList()
        }

        tweets.forEach { tweet ->
            intermediateMap[tweet.tweet.topic]!!.add(tweet)
        }

        var remainingTeachers = (tweets.size * teacherRatio).toInt()
        var remainingTopics = TOPIC_COUNT

        for (i in 0..(TOPIC_COUNT - 1)) {
            val topic = Topic.values()[i]

            remainingTopics--
            val noOfTeachers = when (i) {
                (TOPIC_COUNT - 1) -> remainingTeachers
                else -> 1 + random.nextInt(remainingTeachers - remainingTopics)
            }

            val list = intermediateMap[topic]!!.shuffled(random)
            var j = 0
            for (tweet in list) {
                if (j >= noOfTeachers) {
                    break
                }

                remainingTeachers--

                resultMap[topic]!!.add(tweet)
                tweets.remove(tweet)
                j++
            }
        }

        return resultMap
    }

    private class Neighbor(val neighbor: TweetWithVector,
                           val topic: Topic,
                           val distance: Double): Comparable<Neighbor> {

        override fun compareTo(other: Neighbor): Int = this.distance.compareTo(other.distance)
    }

}