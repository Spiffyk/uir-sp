package cz.spiffyk.uirsp.classification

import cz.spiffyk.uirsp.tweet.Tweet
import java.util.*

data class ClassificationGroup(val tweets: List<Tweet>,
                               val likelyTopics: SortedSet<TopicWithProbability>)