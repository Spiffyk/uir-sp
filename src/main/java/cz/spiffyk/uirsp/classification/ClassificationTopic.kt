package cz.spiffyk.uirsp.classification

import cz.spiffyk.uirsp.tweet.Topic

data class ClassificationTopic(val topic: Topic,
                               val percentage: Double,
                               val count: Int) : Comparable<ClassificationTopic> {

    override fun compareTo(other: ClassificationTopic): Int {
        return (- this.percentage.compareTo(other.percentage))
    }
}