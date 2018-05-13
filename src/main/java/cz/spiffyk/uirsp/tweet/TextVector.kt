package cz.spiffyk.uirsp.tweet

import cz.spiffyk.uirsp.util.InstanceCache

/**
 * A text vector representation class. Any non-existent members in this vector are treated as zero.
 */
class TextVector(inputMap: MutableMap<String, Double>) : Iterable<Map.Entry<String, Double>> {

    companion object {
        val wordCache = InstanceCache<String>()

        /**
         * Calculates the distance between two word vectors.
         *
         * @param v1 a word vector
         * @param v2 a word vector
         * @return the distance between the word vectors
         */
        fun dist(v1: TextVector, v2: TextVector): Double {
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
     * Keys of the vector.
     *
     * @see Map.keys
     */
    val keys
        get() = map.keys

    /**
     * Values of the vector.
     *
     * @see Map.values
     */
    val values
        get() = map.values

    /**
     * Key-value entries of the vector.
     *
     * @see Map.entries
     */
    val entries
        get() = map.entries



    /**
     * Calculates the distance between this word vector and another word vector.
     *
     * @param v the other word vector
     * @return the distance between the word vectors
     */
    fun dist(v: TextVector): Double {
        return dist(this, v)
    }

    /**
     * Calculates the [Hadamard product](https://en.wikipedia.org/wiki/Hadamard_product_%28matrices%29) of this word
     * vector and the specified word vector.
     *
     * @param v the other word vector
     *
     * @return the Hadamard product of this word vector and the specified word vector
     */
    fun hadamard(v: TextVector): TextVector {
        val newMap = HashMap<String, Double>()
        getKeySet(v).forEach {
            val result = this[it] * v[it]
            if (result != 0.0) {
                newMap[it] = result
            }
        }
        return TextVector(newMap)
    }


    override fun iterator(): Iterator<Map.Entry<String, Double>> {
        return map.iterator()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TextVector

        if (map != other.map) return false

        return true
    }

    override fun hashCode(): Int {
        return map.hashCode()
    }

    override fun toString(): String {
        return map.toString()
    }



    /**
     * Combines keys of the word vector and another word vector and returns them in a [Set].
     *
     * @return [Set] of keys
     */
    private fun getKeySet(v: TextVector): Set<String> {
        val keySet = mutableSetOf<String>()
        keySet.addAll(map.keys)
        keySet.addAll(v.map.keys)
        return keySet
    }



    operator fun get(word: String): Double {
        return map[word] ?: 0.0
    }

    operator fun unaryPlus() = this

    operator fun unaryMinus(): TextVector {
        val newMap = HashMap<String, Double>()
        map.forEach {
            newMap.put(it.key, -it.value)
        }
        return TextVector(newMap)
    }

    operator fun plus(v: TextVector): TextVector {
        val newMap = HashMap<String, Double>()
        getKeySet(v).forEach {
            newMap[it] = this[it] + v[it]
        }
        return TextVector(newMap)
    }

    operator fun minus(v: TextVector): TextVector {
        val newMap = HashMap<String, Double>()
        getKeySet(v).forEach {
            newMap[it] = this[it] - v[it]
        }
        return TextVector(newMap)
    }

    operator fun times(x: Double): TextVector {
        val newMap = HashMap<String, Double>()
        map.forEach {
            newMap[it.key] = it.value * x
        }
        return TextVector(newMap)
    }

    operator fun div(x: Double): TextVector {
        val newMap = HashMap<String, Double>()
        map.forEach {
            newMap[it.key] = it.value / x
        }
        return TextVector(newMap)
    }

    operator fun times(x: Int): TextVector = this * x.toDouble()

    operator fun div(x: Int): TextVector = this / x.toDouble()



    /**
     * Builder class of [TextVector].
     */
    class Builder {
        val map = HashMap<String, Double>()

        /**
         * Adds the specified `x` to the `string` member of the [TextVector] to be built.
         *
         * @param string the ID of the member
         * @param x the value to add (default: 1.0)
         */
        fun add(string: String, x: Double = 1.0): Builder {
            map[string] = (map[string] ?: 0.0) + x
            return this
        }

        /**
         * Adds the specified text vector to the [TextVector] to be built.
         *
         * @param textVector the text vector to add
         */
        fun add(textVector: TextVector): Builder {
            textVector.forEach {
                add(it.key, it.value)
            }
            return this
        }

        /**
         * Builds the [TextVector].
         */
        fun build(): TextVector {
            return TextVector(map)
        }
    }
}
