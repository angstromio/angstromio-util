@file:kotlin.jvm.JvmMultifileClass

package angstromio.util.extensions

fun <T : Any> List<T>?.head(): T? = ListOps.head(this)

fun <T : Any> List<T>?.tail(): List<T>? = ListOps.tail(this)

object ListOps {

    /**
     * Returns the first element. Different from [List.first] in that it will not
     * throw an [Exception] for an empty or null list but instead return null. In this way
     * it acts more like the Scala Collections#headOption
     */
    fun <T : Any> head(l: List<T>?): T? {
        return if (l.isNullOrEmpty()) {
            null
        } else {
            l.first()
        }
    }

    /**
     * Returns the last elements of a list. This is similar to the Scala Collections#tail
     * except for symmetry is it more akin to a `tailOption` in that for an empty or null
     * list instead of throwing an [Exception] a null is returned.
     */
    fun <T : Any> tail(l: List<T>?): List<T>? {
        return if (l.isNullOrEmpty()) {
            null
        } else {
            l.drop(1)
        }
    }
}