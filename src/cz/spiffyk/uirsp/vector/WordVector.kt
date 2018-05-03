package cz.spiffyk.uirsp.vector

import cz.spiffyk.uirsp.util.InstanceCache

/**
 * A word vector class.
 */
class WordVector(inputMap: MutableMap<String, Double>) {

    companion object {
        val wordCache = InstanceCache<String>()

        /**
         * Calculates the distance between two word vectors.
         *
         * @param v1 a word vector
         * @param v2 a word vector
         * @return the distance between the word vectors
         */
        fun dist(v1: WordVector, v2: WordVector): Double {
            return (v1 - v2).length
        }
    }


    /**
     * The inner map representing the word vector.
     */
    private val map: Map<String, Double>

    /**
     * Creates a word vector.
     */
    init {
        map = HashMap()
        inputMap.forEach {
            map[wordCache.cache(it.key)] = it.value
        }
    }

    /**
     * The length of the vector.
     */
    val length: Double by lazy {
        var result = 0.0
        map.forEach {
            result += it.value * it.value
        }
        return@lazy Math.sqrt(result)
    }



    /**
     * Calculates the distance between this word vector and another word vector.
     *
     * @param v the other word vector
     * @return the distance between the word vectors
     */
    fun dist(v: WordVector): Double {
        return WordVector.dist(this, v)
    }



    /**
     * Gets a value for the specified word.
     */
    operator fun get(word: String): Double {
        return map[word] ?: 0.0
    }

    operator fun unaryPlus() = this

    operator fun unaryMinus(): WordVector {
        val newMap = HashMap<String, Double>()
        map.forEach {
            newMap.put(it.key, -it.value)
        }
        return WordVector(newMap)
    }

    operator fun plus(v: WordVector): WordVector {
        val newMap = HashMap<String, Double>()
        getKeySet(v).forEach {
            newMap[it] = this[it] + v[it]
        }
        return WordVector(newMap)
    }

    operator fun minus(v: WordVector): WordVector {
        val newMap = HashMap<String, Double>()
        getKeySet(v).forEach {
            newMap[it] = this[it] - v[it]
        }
        return WordVector(newMap)
    }



    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WordVector

        if (map != other.map) return false

        return true
    }

    override fun hashCode(): Int {
        return map.hashCode()
    }



    /**
     * Combines keys of the word vector and another word vector and returns them in a [Set].
     *
     * @return [Set] of keys
     */
    private fun getKeySet(v: WordVector): Set<String> {
        val keySet = mutableSetOf<String>()
        keySet.addAll(map.keys)
        keySet.addAll(v.map.keys)
        return keySet
    }

}