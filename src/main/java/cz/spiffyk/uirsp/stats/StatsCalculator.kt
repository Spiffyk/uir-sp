package cz.spiffyk.uirsp.stats

import cz.spiffyk.uirsp.classification.ClassificationResult
import cz.spiffyk.uirsp.tweet.EventTopic
import kotlin.math.absoluteValue

object StatsCalculator {

    private val EVENT_TOPICS = EventTopic.values()

    fun calculate(classificationResult: ClassificationResult): Stats {
        val relevantMap = HashMap<EventTopic, Int>()
        val retrievedMap = HashMap<EventTopic, Int>()
        val actualMap = HashMap<EventTopic, Int>()

        classificationResult.resultGroups.forEach { group ->
            group.value.topics.forEach { topic ->
                relevantMap[topic.eventTopic] = (relevantMap[topic.eventTopic] ?: 0) + topic.count
                retrievedMap[group.key] = (retrievedMap[group.key] ?: 0) + topic.count

                if (topic.eventTopic === group.key) {
                    actualMap[topic.eventTopic] = (actualMap[topic.eventTopic] ?: 0) + topic.count
                }
            }
        }

        val topicStats = HashMap<EventTopic, Stats.StatsUnit>()
        EVENT_TOPICS.forEach { topic ->
            val relevant = relevantMap[topic] ?: 0
            val retrieved = retrievedMap[topic] ?: 0
            val actual = actualMap[topic] ?: 0

            val precision = when (retrieved) {
                actual -> 1.0
                0 -> 0.0
                else -> actual.toDouble() / retrieved.toDouble()
            }
            val recall = when (relevant) {
                actual -> 1.0
                0 -> 0.0
                else -> actual.toDouble() / relevant.toDouble()
            }
            val fMeasureDenominator = precision + recall
            val fMeasure = when (fMeasureDenominator) {
                0.0 -> 0.0
                else -> (2 * precision * recall) / (fMeasureDenominator)
            }

            topicStats[topic] = Stats.StatsUnit(precision, recall, fMeasure)
        }

        var mPrecision = 0.0
        var mRecall = 0.0
        var mFMeasure = 0.0
        topicStats.values.forEach { unit ->
            mPrecision += unit.precision
            mRecall += unit.recall
            mFMeasure += unit.fMeasure
        }

        val meanStats = Stats.StatsUnit(
                precision = mPrecision / EVENT_TOPICS.size,
                recall = mRecall / EVENT_TOPICS.size,
                fMeasure = mFMeasure / EVENT_TOPICS.size)

        return Stats(topicStats, meanStats)
    }

}