package cz.spiffyk.uirsp.classification

import cz.spiffyk.uirsp.tweet.EventTopic

data class TopicWithProbability(val eventTopic: EventTopic,
                                val probability: Double) : Comparable<TopicWithProbability> {

    override fun compareTo(other: TopicWithProbability): Int {
        return (- this.probability.compareTo(other.probability))
    }
}