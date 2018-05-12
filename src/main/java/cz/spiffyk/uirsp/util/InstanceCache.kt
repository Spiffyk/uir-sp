package cz.spiffyk.uirsp.util

/**
 * A [HashMap]-based cache for duplicate object mitigation.
 */
class InstanceCache<T> {

    /**
     * Cache map.
     */
    private val map = HashMap<T, T>()

    /**
     * Checks whether an object equal to `t` is already in the cache. If it is, returns the cached instance; if not,
     * stores it in the cache and returns it.
     *
     * @param t the object to check
     * @return cached object equal to `t` or `t` if no such object is cached
     *
     * @see Object.equals
     */
    fun cache(t: T): T {
        val cached = map[t]
        if (cached === null) {
            map[t] = t
            return t
        }

        return cached
    }

}