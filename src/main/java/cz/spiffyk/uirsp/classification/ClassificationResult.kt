package cz.spiffyk.uirsp.classification

import cz.spiffyk.uirsp.tweet.Topic

/**
 * Result of a classification.
 */
data class ClassificationResult(val resultGroups: Map<Topic, ClassificationGroup>)