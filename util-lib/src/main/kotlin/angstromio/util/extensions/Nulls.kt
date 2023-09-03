package angstromio.util.extensions

object Nulls {

    /**
     * Map a function returning Unit when this T is not null.
     */
    fun <T : Any> T?.whenNotNull(f: (it: T) -> Unit) {
        if (this != null) f(this)
    }

    /**
     * Map a function over T returning R when this T is not null.
     */
    fun <T : Any, R : Any> T?.mapNotNull(f: (it: T) -> R?): R? {
        return when (this) {
            null -> null
            else -> f(this)
        }
    }

    /**
     * Execute a Unit function when this T is null.
     */
    fun <T : Any> T?.whenNull(f: () -> Unit) {
        if (this == null) f()
    }
}