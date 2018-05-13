package cz.spiffyk.uirsp.preprocess

import cz.spiffyk.uirsp.tweet.TweetWithVector

/**
 * Result of pre-processing.
 */
data class PreprocessResult(val tweets: List<TweetWithVector>,
                            val allKeys: Set<String>)