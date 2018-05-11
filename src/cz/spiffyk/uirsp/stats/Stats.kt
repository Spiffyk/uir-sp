package cz.spiffyk.uirsp.stats

import cz.spiffyk.uirsp.tweet.EventTopic

data class Stats(val topics: Map<EventTopic, StatsUnit>,
                 val mean: StatsUnit) {

    data class StatsUnit(val precision: Double,
                         val recall: Double,
                         val fMeasure: Double)
}

