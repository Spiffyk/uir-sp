package cz.spiffyk.uirsp.tweet

import java.math.BigInteger
import java.time.ZonedDateTime

/**
 * A data class representing a tweet.
 *
 * @property id ID of the tweet
 * @property timestamp the time of the tweet's posting
 * @property langCode code of the language the tweet is written in
 * @property topic the topic of the event; tweet has not been event-annotated if this is `null`
 * @property body the body of the tweet
 */
data class Tweet(val id: BigInteger,
                 val timestamp: ZonedDateTime,
                 val langCode: String,
                 val topic: Topic,
                 val body: String) {

    companion object {
        const val GLOBAL_FILTER_DISABLE: Boolean = false

        private const val TRIMMED_CHARS = """[^\wáéíýóúůžščřďťňě#@]*"""

        private val FILTERED_WORDS = setOf(
                /*
                 * Czech prepositions
                 *     (source: https://cs.wiktionary.org/wiki/Kategorie:%C4%8Cesk%C3%A9_p%C5%99edlo%C5%BEky)
                 */
                "à", "bez", "beze", "blízko", "dle", "do", "k", "ke", "kol", "krom", "kromě", "ku", "kvůli", "mezi",
                "mimo", "na", "nad", "nade", "naproti", "navzdory", "nedaleko", "o", "ob", "od", "ode", "ohledně",
                "okolo", "oproti", "po", "poblíž", "pod", "pode", "podél", "podle", "podlevá", "podlivá", "pomocí",
                "před", "přede", "přes", "přese", "při", "pro", "prostřednictvím", "proti", "s", "se", "skrz", "skrze",
                "stran", "u", "uprostřed", "v", "včetně", "ve", "vedle", "versus", "vinou", "vis-à-vis", "vně", "vo",
                "vod", "vstříc", "vůči", "vůkol", "vz", "vzdor", "vzhledem", "z", "za", "ze", "zkraje", "zpod", "zpoza",

                /*
                 * Czech pronouns
                 *     (source: https://cs.wiktionary.org/wiki/Kategorie:%C4%8Cesk%C3%A1_z%C3%A1jmena)
                 *     (edited, non-exhaustive)
                 */
                "an", "ana", "ano", "any", "bůhvíco", "bůhvíkdo", "čí", "čísi", "co", "cokoli", "cokoliv", "copak",
                "cos", "cosi", "což", "cože", "já", "jakkoli", "jakový", "jaký", "jaká", "jaké", "jací", "jakýkoli",
                "jakákoli", "jakékoli", "jacíkoli", "jakýkoliv", "jakákoliv", "jakékoliv", "jacíkoliv", "jakýsi",
                "jakási", "jakési", "jacísi", "jeho", "jehož", "jejíž", "její", "jejich", "jejichž", "jejíž", "jenž",
                "jíž", "jež", "jich", "každý", "kdekoli", "kdekoliv", "kdo", "kdokoli", "kdokoliv", "kdosi", "kdy",
                "kdykoli", "kdykoliv", "kterej", "který", "která", "které", "kteří", "kterýkoliv", "ký", "lecco",
                "leckdo", "leckterý", "leckterá", "leckteré", "leckteří", "málokdo", "mé", "můj", "moje", "my", "mý",
                "mí", "ňáký", "ňáká", "ňáké", "ňácí", "náš", "naše", "naši", "něčí", "něco", "nějaký", "nějaká",
                "nějaké", "nějací", "někdo", "některý", "některá", "některé", "někteří", "nesvůj", "nesvá", "nesvé",
                "nesví", "nic", "ničí", "nikdo", "nižádný", "nižádná", "nižádné", "nižádní", "odkdy", "on", "ona",
                "onano", "onen", "oni", "ono", "ony", "pranic", "pražádný", "pražádná", "pražádné", "pražádní", "sa",
                "sám", "sama", "samo", "sami", "samý", "samá", "samé", "samí", "se", "svůj", "svá", "své", "sví",
                "svoje", "svoji", "ta", "tenhle", "tahle", "tohle", "tihle", "takový", "taková", "takové", "takoví",
                "takovýhle", "takováhle", "takovéhle", "takovíhle", "takovýto", "takováto", "takovéto", "takovíto",
                "taký", "taká", "také", "tací", "tato", "ten", "tenhle", "tento", "tentýž", "ti", "tito", "to", "tohle",
                "toto", "tuten", "tvůj", "ty", "tyto", "týž", "váš", "vešker", "veškerý", "von", "vše", "všecek",
                "všechen", "všechno", "všeliký", "vy", "žádný")

        private val wordFilter: (String) -> Boolean = {
            when {
                (it.length <= 1) -> {
                    false
                }
                it in FILTERED_WORDS -> false
                else -> true
            }
        }
    }



    fun splitWords(applyFilter: Boolean = true): List<String> {
        val firstColon = this.body.indexOf(':')
        val rawBody = when {
            this.body.startsWith("RT") && firstColon != -1 && this.body.indexOf('@') < firstColon -> {
                this.body.substring(firstColon + 1).trim()
            }
            else -> this.body
        }

        val splitStream = rawBody.split(Regex("""[,.!?;]*[\s]+""")).filter {
            !it.isEmpty() && it.matches(Regex(""".*\w+.*"""))
        }.map {
            it.replace(Regex("""^$TRIMMED_CHARS"""), "")
                    .replace(Regex("""$TRIMMED_CHARS$"""), "")
        }

        return when (!GLOBAL_FILTER_DISABLE && applyFilter) {
            true -> splitStream.filter(wordFilter)
            false -> splitStream
        }
    }

    fun toCsv(): String {
        val isEvent = when (topic) {
            Topic.NONE -> 0
            else -> 1
        }
        val date = TweetsCsvParser.DATE_TIME_FORMATTER.format(timestamp)

        return "$isEvent;${topic.code};$id;$langCode;$date;$body"
    }
}