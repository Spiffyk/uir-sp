package cz.spiffyk.uirsp.stats

import cz.spiffyk.uirsp.tweet.Topic

/**
 * Classification result statistics data.
 */
data class Stats(val topics: Map<Topic, StatsUnit>,
                 val mean: StatsUnit) {

    /**
     * Data class containing precision, recall and f-measure.
     */
    data class StatsUnit(val precision: Double,
                         val recall: Double,
                         val fMeasure: Double)
}

