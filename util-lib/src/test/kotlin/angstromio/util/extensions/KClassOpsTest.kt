package angstromio.util.extensions

import angstromio.util.Annotation1
import angstromio.util.Annotation2
import angstromio.util.Annotation3
import angstromio.util.Annotation4
import angstromio.util.StaticSecondaryConstructor
import angstromio.util.WithSecondaryConstructor
import angstromio.util.WithThings
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.be
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.javaType

@OptIn(ExperimentalStdlibApi::class)
class KClassOpsTest : FunSpec({

    test("KClassOps#getConstructor") {
        val constructor: KFunction<*>? = StaticSecondaryConstructor::class.getConstructor(Int::class, Int::class)
        constructor!! shouldNot beNull()

        // should be constructor (Int, Int)
        val parameters = constructor.parameters.filter { it.kind == KParameter.Kind.VALUE }
        parameters.all { parameter ->
            parameter.type.javaType == Int::class.javaObjectType
        }
    }

    test("KClassOps#getConstructor - find secondary constructor") {
        val constructor: KFunction<*>? = StaticSecondaryConstructor::class.getConstructor(String::class, String::class)
        constructor!! shouldNot beNull()

        // should be constructor (String, String)
        val parameters = constructor.parameters.filter { it.kind == KParameter.Kind.VALUE }
        parameters.size shouldBeEqual 2
        parameters.all { parameter ->
            parameter.type.javaType == String::class.javaObjectType
        }
    }

    test("KClassOps#getConstructor 2") {
        val constructor: KFunction<*>? = WithSecondaryConstructor::class.getConstructor(Int::class, Int::class)
        constructor!! shouldNot beNull()

        // should be constructor (Int, Int)
        val parameters = constructor.parameters.filter { it.kind == KParameter.Kind.VALUE }
        parameters.all { parameter ->
            parameter.type.javaType == Int::class.javaObjectType
        }
    }

    test("KClassOps#getConstructor - find secondary constructor 2") {
        val constructor: KFunction<*>? = WithSecondaryConstructor::class.getConstructor(String::class, String::class)
        constructor!! shouldNot beNull()

        // should be constructor (String, String)
        val parameters = constructor.parameters.filter { it.kind == KParameter.Kind.VALUE }
        parameters.all { parameter ->
            parameter.type.javaType == String::class.javaObjectType
        }
    }

    test("KClassOps#getConstructorAnnotations") {
        val found: Map<String, List<Annotation>> = WithThings::class.getConstructorAnnotations()
        found.isEmpty() should be(false)
        val annotations = found.flatMap { (_, annotations) -> annotations.toList() }
        annotations.size shouldBeEqual 4
    }

    test("KClassOps#getConstructorAnnotations 2") {
        val annotationMap: Map<String, List<Annotation>> =
            StaticSecondaryConstructor::class.getConstructorAnnotations(listOf(String::class, String::class))

        annotationMap.isEmpty() should be(false)
        annotationMap.size shouldBeEqual 2
        annotationMap["three"]!!.size shouldBeEqual 1
        annotationMap["three"]!!.first().annotationClass shouldBeEqual Annotation3::class
        annotationMap["four"]!!.size shouldBeEqual 1
        annotationMap["four"]!!.first().annotationClass shouldBeEqual Annotation4::class
    }

    test("KClassOps#getConstructorAnnotations 3") {
        val annotationMap: Map<String, List<Annotation>> =
            StaticSecondaryConstructor::class.getConstructorAnnotations(listOf(Int::class, Int::class))

        annotationMap.isEmpty() should be(false)
        annotationMap.size shouldBeEqual 2
        annotationMap["one"]!!.size shouldBeEqual 1
        annotationMap["one"]!!.first().annotationClass shouldBeEqual Annotation1::class
        annotationMap["two"]!!.size shouldBeEqual 1
        annotationMap["two"]!!.first().annotationClass shouldBeEqual Annotation2::class
    }

    test("KClassOps#getConstructorAnnotations 4") {
        val annotationMap: Map<String, List<Annotation>> =
            WithSecondaryConstructor::class.getConstructorAnnotations(listOf(String::class, String::class))

        annotationMap.isEmpty() should be(false)
        annotationMap.size shouldBeEqual 2
        annotationMap["three"]!!.size shouldBeEqual 1
        annotationMap["three"]!!.first().annotationClass shouldBeEqual Annotation3::class
        annotationMap["four"]!!.size shouldBeEqual 1
        annotationMap["four"]!!.first().annotationClass shouldBeEqual Annotation4::class
    }
})