package cz.spiffyk.uirsp.classification

import cz.spiffyk.uirsp.tweet.Topic
import cz.spiffyk.uirsp.tweet.Tweet
import java.util.*

/**
 * A group of processed tweets.
 */
data class ClassificationGroup(val tweets: List<Tweet>,
                               val topics: SortedSet<ClassificationTopicStats>,
                               val presetTopic: Topic? = null)