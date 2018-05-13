package cz.spiffyk.uirsp.classification

import cz.spiffyk.uirsp.tweet.Topic

/**
 * A topic statistics data class.
 */
data class ClassificationTopicStats(val topic: Topic,
                                    val percentage: Double,
                                    val count: Int) : Comparable<ClassificationTopicStats> {

    override fun compareTo(other: ClassificationTopicStats): Int {
        return (- this.percentage.compareTo(other.percentage))
    }
}