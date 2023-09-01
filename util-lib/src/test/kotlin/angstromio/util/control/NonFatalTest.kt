package angstromio.util.control

import io.kotest.assertions.fail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.be
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

class NonFatalTest : FunSpec({

    test("NonFatal#correctly identifies fatal exceptions") {
        NonFatal.isNonFatal(OutOfMemoryError()) should be(false) // VirtualMachineError
        NonFatal.isNonFatal(ThreadDeath()) should be(false)
        NonFatal.isNonFatal(InterruptedException()) should be(false)
        NonFatal.isNonFatal(LinkageError()) should be(false)

        NonFatal.isNonFatal(IllegalArgumentException()) should be(true)
        NonFatal.isNonFatal(NullPointerException()) should be(true)
    }

    test("NonFatal#tryOrNull") {
        // catch NonFatal and return null
        val f = NonFatal.tryOrNull { throw IllegalArgumentException("FORCED TEST EXCEPTION") }
        f should beNull()

        // fatal escapes and is thrown
        try {
            NonFatal.tryOrNull { throw OutOfMemoryError("FORCED TEST EXCEPTION") }
            fail("Test should not reach here")
        } catch (t: Throwable) {
            t::class should be(OutOfMemoryError::class)
        }

        // successful invocation result is returned
        val s: String? = NonFatal.tryOrNull { "Hello, world." }
        s shouldNot beNull()
        s!! shouldBeEqual "Hello, world."
    }
})