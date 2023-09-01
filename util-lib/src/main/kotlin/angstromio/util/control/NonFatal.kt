package angstromio.util.control

object NonFatal {

    fun isNonFatal(t: Throwable): Boolean {
        return when (t) {
            is VirtualMachineError -> false
            is ThreadDeath -> false
            is InterruptedException -> false
            is LinkageError -> false
            else -> true
        }
    }

    inline fun <reified T : Any> tryOrNull(f: () -> T?) =
        try {
            f()
        } catch (e: Exception) {
            when {
                isNonFatal(e) -> null
                else -> throw e
            }
        }
}