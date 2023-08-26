package angstromio.util.control

object NonFatal {

    fun isNonFatal(t: Throwable): Boolean {
        return when(t) {
            is VirtualMachineError -> false
            is ThreadDeath -> false
            is InterruptedException -> false
            is LinkageError -> false
            else -> true
        }
    }
}