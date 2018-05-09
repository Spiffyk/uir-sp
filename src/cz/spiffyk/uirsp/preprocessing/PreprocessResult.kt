package cz.spiffyk.uirsp.preprocessing

import cz.spiffyk.uirsp.tweet.TweetWithVector

data class PreprocessResult(val tweets: List<TweetWithVector>,
                            val allKeys: Set<String>)