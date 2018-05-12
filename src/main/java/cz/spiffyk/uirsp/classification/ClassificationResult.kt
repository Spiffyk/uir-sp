package cz.spiffyk.uirsp.classification

import cz.spiffyk.uirsp.tweet.Topic

data class ClassificationResult(val resultGroups: Map<Topic, ClassificationGroup>)