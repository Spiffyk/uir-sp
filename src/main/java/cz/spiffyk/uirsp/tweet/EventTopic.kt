package cz.spiffyk.uirsp.tweet

/**
 * The topic of an event.
 */
enum class EventTopic(val code: String) {
    POLITICS("po"),
    INDUSTRY("pr"),
    AGRICULTURE("ze"),
    SPORT("sp"),
    CULTURE("ku"),
    CRIME("kr"),
    WEATHER("pc"),
    OTHER("ji"),
    NONE("-");

    companion object {
        /**
         * Gets a topic by the specified code. Returns `null` if no topic with such code exists.
         */
        fun of(code: String): EventTopic? {
            for (v in values()) {
                if (v.code == code) {
                    return v
                }
            }

            return null
        }

        /**
         * Gets a topic by the specified code. Throws an [IllegalArgumentException] if no topic with such code exists.
         */
        fun ofStrict(code: String): EventTopic {
            return of(code) ?: throw IllegalArgumentException("Invalid topic code!")
        }
    }
}