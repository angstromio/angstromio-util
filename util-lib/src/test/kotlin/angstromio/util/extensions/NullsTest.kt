package angstromio.util.extensions

import angstromio.util.extensions.Nulls.mapNotNull
import angstromio.util.extensions.Nulls.whenNotNull
import angstromio.util.extensions.Nulls.whenNull
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.be
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should

class NullsTest : FunSpec({

    test("NullOps#whenNotNull") {
        var index = 0
        val s = null
        s.whenNotNull {
            index += 1
        }
        index should be(0)

        val s1 = "hello"
        s1.whenNotNull {
            index += 1
        }
        index should be(1)
    }

    test("Nulls#mapNotNull") {
        val s = null
        s.mapNotNull { "hello, world" } should beNull()

        val s1 = 42
        s1.mapNotNull { "hello, world" } should be("hello, world")
    }

    test("NullOps#whenNull") {
        var index = 0
        val s = null
        s.whenNull {
            index += 1
        }
        index should be(1)

        val s1 = "hello"
        s1.whenNull {
            index += 1
        }
        index should be(1)
    }
})