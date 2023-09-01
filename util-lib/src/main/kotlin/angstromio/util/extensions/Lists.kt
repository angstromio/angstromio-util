package angstromio.util.extensions

object Lists {

    /**
     * Returns the first element. Different from [List.first] in that it will not
     * throw an [Exception] for an empty or null list but instead return null. In this way
     * it acts more like the Scala Collections#headOption
     */
    fun <T : Any> List<T>?.head(): T? {
        return if (this.isNullOrEmpty()) {
            null
        } else {
            this.first()
        }
    }

    /**
     * Returns the last elements of a list. This is similar to the Scala Collections#tail
     * except for symmetry is it more akin to a `tailOption` in that for an empty or null
     * list instead of throwing an [Exception] a null is returned.
     */
    fun <T : Any> List<T>?.tail(): List<T>? {
        return if (this.isNullOrEmpty()) {
            null
        } else {
            this.drop(1)
        }
    }
}