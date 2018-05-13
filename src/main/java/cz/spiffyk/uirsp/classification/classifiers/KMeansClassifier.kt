package cz.spiffyk.uirsp.classification.classifiers

import cz.spiffyk.uirsp.classification.ClassificationGroup
import cz.spiffyk.uirsp.classification.ClassificationResult
import cz.spiffyk.uirsp.classification.ClassificationTopicStats
import cz.spiffyk.uirsp.preprocess.PreprocessResult
import cz.spiffyk.uirsp.tweet.Topic
import cz.spiffyk.uirsp.tweet.TextVector
import cz.spiffyk.uirsp.tweet.TweetWithVector
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object KMeansClassifier {

    private val RANDOM = Random()

    /**
     * Classifies the pre-processed tweets.
     *
     * @param preprocessResult the result of a pre-process
     * @param teacherRatio the ratio of tweets to be used as a teacher
     */
    fun classify(preprocessResult: PreprocessResult,
                 teacherRatio: Double): ClassificationResult {
        val means = generateMeans(preprocessResult, teacherRatio)
        var tweetGroups: List<List<TweetWithVector>>

        do {
            tweetGroups = List(Topic.values().size, { ArrayList<TweetWithVector>() })
            preprocessResult.tweets.forEach {
                var closestMean = -1
                var closestDist = Double.MAX_VALUE

                means.forEachIndexed { index, mean ->
                    val dist = TextVector.dist(it.vector, mean.vector)
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
                val oldMean = means[index]
                if (newMean != oldMean.vector) {
                    means[index] = TopicMean(newMean, oldMean.topic)
                    meansChanged = true
                }
            }
        } while (meansChanged)

        val resultGroups = ArrayList<ClassificationGroup>()
        tweetGroups.forEachIndexed { index, tweets ->
            val topicStats = ClassifierUtils.calculateTopicStats(tweets)
            val presetTopic = means[index].topic
            resultGroups.add(when (presetTopic) {
                null -> ClassificationGroup(tweets.map { it.tweet }, topicStats)
                else -> ClassificationGroup(tweets.map { it.tweet }, topicStats, presetTopic)
            })
        }

        return postProcessResults(resultGroups)
    }

    /**
     * Generates starting means. If the teacher ratio is zero, starting means are picked randomly, otherwise
     * they are determined by the teaching data.
     */
    private fun generateMeans(preprocessResult: PreprocessResult,
                              teacherRatio: Double,
                              random: Random = RANDOM): MutableList<TopicMean> {
        if (teacherRatio == 0.0) {
            return MutableList(Topic.values().size, { randomMean(preprocessResult.allKeys, random) })
        }

        val topicMap = HashMap<Topic, ArrayList<TweetWithVector>>()
        val shuffledTweets = preprocessResult.tweets.shuffled(RANDOM)
        val noOfTeachers = (shuffledTweets.size * teacherRatio).toInt()
        var i = 0

        for (tweet in shuffledTweets) {
            if (i >= noOfTeachers) {
                break
            }

            var topicTweets = topicMap[tweet.tweet.topic]
            if (topicTweets === null) {
                topicTweets = ArrayList()
                topicMap[tweet.tweet.topic] = topicTweets
            }
            topicTweets.add(tweet)

            i++
        }

        val result = ArrayList<TopicMean>()
        topicMap.forEach { entry ->
            result.add(TopicMean(meanOf(entry.value), entry.key))
        }

        for (x in 1..(Topic.values().size - result.size)) {
            result.add(randomMean(preprocessResult.allKeys, random))
        }

        return result
    }

    /**
     * Estimates, which group is of which [Topic].
     */
    private fun postProcessResults(resultGroups: List<ClassificationGroup>): ClassificationResult {
        val groupQueue = ArrayDeque<ClassificationGroup>(resultGroups)
        val resultMap = HashMap<Topic, Occupier>()

        queue@ while (!groupQueue.isEmpty()) {
            val head = groupQueue.poll()

            if (head.presetTopic !== null) {
                val occupier = resultMap[head.presetTopic]
                if (occupier !== null) {
                    groupQueue.add(occupier.group)
                }
                resultMap[head.presetTopic] = Occupier.undeniable(head, head.presetTopic)
                continue@queue
            }

            for (topic in head.topics) {
                val occupier = resultMap[topic.topic]
                if (occupier === null) {
                    resultMap[topic.topic] = Occupier(head, topic)
                    continue@queue
                }

                if ((occupier.on?.percentage ?: 0.0) < topic.percentage) {
                    groupQueue.add(occupier.group)
                    resultMap[topic.topic] = Occupier(head, topic)
                    continue@queue
                }
            }

            for (topic in Topic.values()) {
                if (resultMap[topic] === null) {
                    resultMap[topic] = Occupier(head, null)
                    continue@queue
                }
            }

            throw IllegalStateException("Did not fit the map???")
        }

        return ClassificationResult(resultMap.mapValues { it.value.group })
    }

    /**
     * Generates a random text vector for the specified key set.
     */
    private fun randomMean(keySet: Set<String>,
                           random: Random = RANDOM,
                           eccentricity: Double = 1.0): TopicMean {
        val map = HashMap<String, Double>()
        keySet.forEach {
            map[it] = (random.nextDouble() * eccentricity * 2) - eccentricity
        }
        return TopicMean(TextVector(map))
    }

    /**
     * Calculates a mean vector of a group of tweet vectors.
     */
    private fun meanOf(tweetGroup: List<TweetWithVector>): TextVector {
        val builder = TextVector.Builder()
        tweetGroup.forEach {
            builder.add(it.vector)
        }
        return builder.build() / tweetGroup.size
    }



    private data class Occupier(val group: ClassificationGroup,
                                val on: ClassificationTopicStats?) {
        companion object {
            fun undeniable(group: ClassificationGroup,
                           topic: Topic): Occupier {
                return Occupier(
                        group = group,
                        on = ClassificationTopicStats(
                                topic = topic,
                                percentage = Double.MAX_VALUE,
                                count = -1))
            }
        }
    }

    private data class TopicMean(val vector: TextVector,
                                 val topic: Topic? = null)
}