package angstromio.util.extensions

object Nulls {

    /**
     * Map a function returning Unit when this T is not null.
     */
    fun <T : Any> T?.whenNotNull(f: (it: T) -> Unit) {
        if (this != null) f(this)
    }

    /**
     * Execute a Unit function when this T is null.
     */
    fun <T : Any> T?.whenNull(f: () -> Unit) {
        if (this == null) f()
    }
}