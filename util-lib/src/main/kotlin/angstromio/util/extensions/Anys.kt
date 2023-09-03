package angstromio.util.extensions

object Anys {
    /**
     * Returns true if the given T type class is assignable from this instance type class.
     * E.g., T::class.java.isAssignableFrom(this::class.java)
     */
    inline fun <reified T : Any> Any?.isInstanceOf(): Boolean =
        (this != null) && T::class.java.isAssignableFrom(this::class.java)
}