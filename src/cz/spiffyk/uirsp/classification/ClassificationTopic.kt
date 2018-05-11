package cz.spiffyk.uirsp.classification

import cz.spiffyk.uirsp.tweet.EventTopic

data class ClassificationTopic(val eventTopic: EventTopic,
                               val percentage: Double,
                               val count: Int) : Comparable<ClassificationTopic> {

    override fun compareTo(other: ClassificationTopic): Int {
        return (- this.percentage.compareTo(other.percentage))
    }
}