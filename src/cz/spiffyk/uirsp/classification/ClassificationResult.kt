package cz.spiffyk.uirsp.classification

import cz.spiffyk.uirsp.tweet.EventTopic

data class ClassificationResult(val resultGroups: Map<EventTopic, ClassificationGroup>)