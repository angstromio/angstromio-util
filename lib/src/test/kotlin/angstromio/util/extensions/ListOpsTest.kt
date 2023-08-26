package angstromio.util.extensions

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe

class ListOpsTest : FunSpec({
    test("ListOps#head") {
        val l1 = listOf(1, 2, 3)
        l1.head()!! shouldBeEqual 1

        val l2: List<String>? = null
        l2.head() should beNull()

        val l3 = emptyList<String>()
        l3.head() should beNull()
    }

    test("ListOps#tail") {
        val l1 = listOf(1, 2, 3)
        l1.tail() shouldBe listOf(2, 3)

        val l2: List<String>? = null
        l2.tail() should beNull()

        val l3 = emptyList<String>()
        l3.tail() should beNull()
    }
})