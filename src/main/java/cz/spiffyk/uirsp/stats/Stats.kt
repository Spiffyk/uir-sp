package cz.spiffyk.uirsp.stats

import cz.spiffyk.uirsp.tweet.Topic

data class Stats(val topics: Map<Topic, StatsUnit>,
                 val mean: StatsUnit) {

    data class StatsUnit(val precision: Double,
                         val recall: Double,
                         val fMeasure: Double)
}

