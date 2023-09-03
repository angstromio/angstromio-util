package angstromio.util.extensions

import angstromio.util.NoConstructor
import angstromio.util.extensions.Anys.isInstanceOf
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.be
import io.kotest.matchers.should

class AnysTest : FunSpec({

    test("Anys#null") {
        null.isInstanceOf<String>() should be(false)
        null.isInstanceOf<Short>() should be(false)
        null.isInstanceOf<Byte>() should be(false)
        null.isInstanceOf<Int>() should be(false)
        null.isInstanceOf<Long>() should be(false)
        null.isInstanceOf<Float>() should be(false)
        null.isInstanceOf<Double>() should be(false)
        null.isInstanceOf<Boolean>() should be(false)
        null.isInstanceOf<Any>() should be(false)
        null.isInstanceOf<Void>() should be(false)
        null.isInstanceOf<Unit>() should be(false)
        null.isInstanceOf<NoConstructor>() should be(false)
    }

    test("Anys#String") {
        val s = "hello"
        s.isInstanceOf<String>() should be(true)
        s.isInstanceOf<Short>() should be(false)
        s.isInstanceOf<Byte>() should be(false)
        s.isInstanceOf<Int>() should be(false)
        s.isInstanceOf<Long>() should be(false)
        s.isInstanceOf<Float>() should be(false)
        s.isInstanceOf<Double>() should be(false)
        s.isInstanceOf<Boolean>() should be(false)
        s.isInstanceOf<Any>() should be(true)
        s.isInstanceOf<Void>() should be(false)
        s.isInstanceOf<Unit>() should be(false)
        s.isInstanceOf<NoConstructor>() should be(false)
    }

    test("Anys#Short") {
        val s = 1.toShort()
        s.isInstanceOf<String>() should be(false)
        s.isInstanceOf<Short>() should be(true)
        s.isInstanceOf<Byte>() should be(false)
        s.isInstanceOf<Int>() should be(false)
        s.isInstanceOf<Long>() should be(false)
        s.isInstanceOf<Float>() should be(false)
        s.isInstanceOf<Double>() should be(false)
        s.isInstanceOf<Boolean>() should be(false)
        s.isInstanceOf<Any>() should be(true)
        s.isInstanceOf<Void>() should be(false)
        s.isInstanceOf<Unit>() should be(false)
        s.isInstanceOf<NoConstructor>() should be(false)
    }

    test("Anys#Byte") {
        val s = 0.toByte()
        s.isInstanceOf<String>() should be(false)
        s.isInstanceOf<Short>() should be(false)
        s.isInstanceOf<Byte>() should be(true)
        s.isInstanceOf<Int>() should be(false)
        s.isInstanceOf<Long>() should be(false)
        s.isInstanceOf<Float>() should be(false)
        s.isInstanceOf<Double>() should be(false)
        s.isInstanceOf<Boolean>() should be(false)
        s.isInstanceOf<Any>() should be(true)
        s.isInstanceOf<Void>() should be(false)
        s.isInstanceOf<Unit>() should be(false)
        s.isInstanceOf<NoConstructor>() should be(false)
    }

    test("Anys#Int") {
        val s = 1
        s.isInstanceOf<String>() should be(false)
        s.isInstanceOf<Short>() should be(false)
        s.isInstanceOf<Byte>() should be(false)
        s.isInstanceOf<Int>() should be(true)
        s.isInstanceOf<Long>() should be(false)
        s.isInstanceOf<Float>() should be(false)
        s.isInstanceOf<Double>() should be(false)
        s.isInstanceOf<Boolean>() should be(false)
        s.isInstanceOf<Any>() should be(true)
        s.isInstanceOf<Void>() should be(false)
        s.isInstanceOf<Unit>() should be(false)
        s.isInstanceOf<NoConstructor>() should be(false)
    }

    test("Anys#Long") {
        val s = 1L
        s.isInstanceOf<String>() should be(false)
        s.isInstanceOf<Short>() should be(false)
        s.isInstanceOf<Byte>() should be(false)
        s.isInstanceOf<Int>() should be(false)
        s.isInstanceOf<Long>() should be(true)
        s.isInstanceOf<Float>() should be(false)
        s.isInstanceOf<Double>() should be(false)
        s.isInstanceOf<Boolean>() should be(false)
        s.isInstanceOf<Any>() should be(true)
        s.isInstanceOf<Void>() should be(false)
        s.isInstanceOf<Unit>() should be(false)
        s.isInstanceOf<NoConstructor>() should be(false)
    }

    test("Anys#Float") {
        val s = 1.toFloat()
        s.isInstanceOf<String>() should be(false)
        s.isInstanceOf<Short>() should be(false)
        s.isInstanceOf<Byte>() should be(false)
        s.isInstanceOf<Int>() should be(false)
        s.isInstanceOf<Long>() should be(false)
        s.isInstanceOf<Float>() should be(true)
        s.isInstanceOf<Double>() should be(false)
        s.isInstanceOf<Boolean>() should be(false)
        s.isInstanceOf<Any>() should be(true)
        s.isInstanceOf<Void>() should be(false)
        s.isInstanceOf<Unit>() should be(false)
        s.isInstanceOf<NoConstructor>() should be(false)
    }

    test("Anys#Double") {
        val s = 1.toDouble()
        s.isInstanceOf<String>() should be(false)
        s.isInstanceOf<Short>() should be(false)
        s.isInstanceOf<Byte>() should be(false)
        s.isInstanceOf<Int>() should be(false)
        s.isInstanceOf<Long>() should be(false)
        s.isInstanceOf<Float>() should be(false)
        s.isInstanceOf<Double>() should be(true)
        s.isInstanceOf<Boolean>() should be(false)
        s.isInstanceOf<Any>() should be(true)
        s.isInstanceOf<Void>() should be(false)
        s.isInstanceOf<Unit>() should be(false)
        s.isInstanceOf<NoConstructor>() should be(false)
    }

    test("Anys#Boolean") {
        val s = true
        s.isInstanceOf<String>() should be(false)
        s.isInstanceOf<Short>() should be(false)
        s.isInstanceOf<Byte>() should be(false)
        s.isInstanceOf<Int>() should be(false)
        s.isInstanceOf<Long>() should be(false)
        s.isInstanceOf<Float>() should be(false)
        s.isInstanceOf<Double>() should be(false)
        s.isInstanceOf<Boolean>() should be(true)
        s.isInstanceOf<Any>() should be(true)
        s.isInstanceOf<Void>() should be(false)
        s.isInstanceOf<Unit>() should be(false)
        s.isInstanceOf<NoConstructor>() should be(false)
    }

    test("Anys#Any") {
        val s = Any()
        s.isInstanceOf<String>() should be(false)
        s.isInstanceOf<Short>() should be(false)
        s.isInstanceOf<Byte>() should be(false)
        s.isInstanceOf<Int>() should be(false)
        s.isInstanceOf<Long>() should be(false)
        s.isInstanceOf<Float>() should be(false)
        s.isInstanceOf<Double>() should be(false)
        s.isInstanceOf<Boolean>() should be(false)
        s.isInstanceOf<Any>() should be(true)
        s.isInstanceOf<Void>() should be(false)
        s.isInstanceOf<Unit>() should be(false)
        s.isInstanceOf<NoConstructor>() should be(false)
    }

    test("Anys#Unit") {
        val s = Unit
        s.isInstanceOf<String>() should be(false)
        s.isInstanceOf<Short>() should be(false)
        s.isInstanceOf<Byte>() should be(false)
        s.isInstanceOf<Int>() should be(false)
        s.isInstanceOf<Long>() should be(false)
        s.isInstanceOf<Float>() should be(false)
        s.isInstanceOf<Double>() should be(false)
        s.isInstanceOf<Boolean>() should be(false)
        s.isInstanceOf<Any>() should be(true)
        s.isInstanceOf<Void>() should be(false)
        s.isInstanceOf<Unit>() should be(true)
        s.isInstanceOf<NoConstructor>() should be(false)
    }

    test("Anys#Object") {
        val s = NoConstructor()
        s.isInstanceOf<String>() should be(false)
        s.isInstanceOf<Short>() should be(false)
        s.isInstanceOf<Byte>() should be(false)
        s.isInstanceOf<Int>() should be(false)
        s.isInstanceOf<Long>() should be(false)
        s.isInstanceOf<Float>() should be(false)
        s.isInstanceOf<Double>() should be(false)
        s.isInstanceOf<Boolean>() should be(false)
        s.isInstanceOf<Any>() should be(true)
        s.isInstanceOf<Void>() should be(false)
        s.isInstanceOf<Unit>() should be(false)
        s.isInstanceOf<NoConstructor>() should be(true)
    }
})