package angstromio.util.extensions

import angstromio.util.Annotation1
import angstromio.util.Annotation2
import angstromio.util.Annotation3
import angstromio.util.Annotation4
import angstromio.util.NoSecondaryInvokeFunction
import angstromio.util.StaticSecondaryConstructor
import angstromio.util.StaticSecondaryWithParameterizedTypes
import angstromio.util.WithSecondaryConstructor
import angstromio.util.WithThings
import angstromio.util.extensions.Annotations.getConstructorAnnotations
import angstromio.util.extensions.KClasses.getConstructor
import angstromio.util.extensions.KClasses.getConstructors
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.be
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KTypeProjection
import kotlin.reflect.KVariance
import kotlin.reflect.full.createType
import kotlin.reflect.javaType
import kotlin.reflect.jvm.jvmErasure

@OptIn(ExperimentalStdlibApi::class)
class KClassesTest : FunSpec({

    test(" KClasses#getConstructor") {
        val constructor: KFunction<*>? = StaticSecondaryConstructor::class.getConstructor(Int::class, Int::class)
        constructor!! shouldNot beNull()

        // should be constructor (Int, Int)
        val parameters = constructor.parameters.filter { it.kind == KParameter.Kind.VALUE }
        parameters.all { parameter ->
            parameter.type.javaType == Int::class.javaPrimitiveType
        } should be(true)
    }

    test("KClasses#getConstructor - find static secondary constructor using list of parameter types") {
        val constructor = StaticSecondaryConstructor::class.getConstructor(listOf(String::class, String::class))
        assert(constructor != null)
        // should be constructor (String, String)
        val parameters = constructor!!.parameters.filter { it.kind == KParameter.Kind.VALUE }
        parameters.size shouldBeEqual 2
        parameters.all { parameter ->
            parameter.type.javaType == String::class.javaObjectType
        } should be(true)

        // try with incorrect parameter types list
        StaticSecondaryConstructor::class.getConstructor(
            listOf(
                String::class,
                String::class,
                String::class
            )
        ) should beNull()
    }

    test(" KClasses#getConstructor - find static secondary constructor using vararg parameter types") {
        val constructor: KFunction<*>? = StaticSecondaryConstructor::class.getConstructor(String::class, String::class)
        assert(constructor != null)

        // should be constructor (String, String)
        val parameters = constructor!!.parameters.filter { it.kind == KParameter.Kind.VALUE }
        parameters.size shouldBeEqual 2
        parameters.all { parameter ->
            parameter.type.javaType == String::class.javaObjectType
        } should be(true)
    }

    test(" KClasses#getConstructor - find primary constructor") {
        val constructor: KFunction<*>? = WithSecondaryConstructor::class.getConstructor(Int::class, Int::class)
        assert(constructor != null)

        // should be constructor (Int, Int)
        val parameters = constructor!!.parameters.filter { it.kind == KParameter.Kind.VALUE }
        parameters.all { parameter ->
            parameter.type.javaType == Int::class.javaPrimitiveType
        } should be(true)

        // try with incorrect parameter types varargs
        WithSecondaryConstructor::class.getConstructor(Int::class, Int::class, Int::class) should beNull()
    }

    test(" KClasses#getConstructor - find secondary constructor 2") {
        val constructor: KFunction<*>? = WithSecondaryConstructor::class.getConstructor(String::class, String::class)
        assert(constructor != null)

        // should be constructor (String, String)
        val parameters = constructor!!.parameters.filter { it.kind == KParameter.Kind.VALUE }
        parameters.all { parameter ->
            parameter.type.javaType == String::class.javaObjectType
        } should be(true)
    }

    test("KClasses#getConstructor - no static secondary invoke operator function") {
        // can't locate a secondary invoke function
        NoSecondaryInvokeFunction::class.getConstructor(Int::class, Int::class) should beNull()

        // can locate the primary constructor
        NoSecondaryInvokeFunction::class.getConstructor(String::class, String::class) shouldNot beNull()
    }

    test(" KClasses#getConstructorAnnotations") {
        val found: Map<String, Array<Annotation>> = WithThings::class.java.getConstructorAnnotations()
        found.isEmpty() should be(false)
        val annotations = found.flatMap { (_, annotations) -> annotations.toList() }
        annotations.size shouldBeEqual 4
    }

    test(" KClasses#getConstructorAnnotations 2") {
        val annotationMap: Map<String, Array<Annotation>> =
            StaticSecondaryConstructor::class.java.getConstructorAnnotations(
                arrayOf(
                    String::class.java,
                    String::class.java
                )
            )

        annotationMap.isEmpty() should be(false)
        annotationMap.size shouldBeEqual 2
        annotationMap["three"]!!.size shouldBeEqual 1
        annotationMap["three"]!!.first().annotationClass shouldBeEqual Annotation3::class
        annotationMap["four"]!!.size shouldBeEqual 1
        annotationMap["four"]!!.first().annotationClass shouldBeEqual Annotation4::class
    }

    test(" KClasses#getConstructorAnnotations 3") {
        val annotationMap: Map<String, Array<Annotation>> =
            StaticSecondaryConstructor::class.java.getConstructorAnnotations(arrayOf(Int::class.java, Int::class.java))

        annotationMap.isEmpty() should be(false)
        annotationMap.size shouldBeEqual 2
        annotationMap["one"]!!.size shouldBeEqual 1
        annotationMap["one"]!!.first().annotationClass shouldBeEqual Annotation1::class
        annotationMap["two"]!!.size shouldBeEqual 1
        annotationMap["two"]!!.first().annotationClass shouldBeEqual Annotation2::class
    }

    test(" KClasses#getConstructorAnnotations 4") {
        val annotationMap: Map<String, Array<Annotation>> =
            WithSecondaryConstructor::class.java.getConstructorAnnotations(
                arrayOf(
                    String::class.java,
                    String::class.java
                )
            )

        annotationMap.isEmpty() should be(false)
        annotationMap.size shouldBeEqual 2
        annotationMap["three"]!!.size shouldBeEqual 1
        annotationMap["three"]!!.first().annotationClass shouldBeEqual Annotation3::class
        annotationMap["four"]!!.size shouldBeEqual 1
        annotationMap["four"]!!.first().annotationClass shouldBeEqual Annotation4::class
    }

    test("KClasses#getConstructors") {
        val withStaticSecondaryConstructor = StaticSecondaryConstructor::class.getConstructors()
        withStaticSecondaryConstructor.size shouldBeEqual 2
        withStaticSecondaryConstructor.first().name should be("<init>")
        withStaticSecondaryConstructor.last().name should be("invoke")
    }

    test("KClasses#getConstructor with parameterized members") {
        val kClazz = List::class.createType(
            arguments =
            listOf(
                KTypeProjection(
                    KVariance.INVARIANT,
                    String::class.createType()
                )
            )
        ).jvmErasure
        val constructor = StaticSecondaryWithParameterizedTypes::class.getConstructor(kClazz)
        constructor shouldNot beNull()
    }

    test("KClasses#getConstructors 2") {
        val withStaticSecondaryConstructor = StaticSecondaryWithParameterizedTypes::class.getConstructors()
        withStaticSecondaryConstructor.size shouldBeEqual 3
        withStaticSecondaryConstructor[0].name should be("<init>")

        val constructor1 = withStaticSecondaryConstructor[1]
        constructor1.name should be("invoke")
        val constructor1Params = withStaticSecondaryConstructor[1].parameters.filter { it.kind == KParameter.Kind.VALUE }
        constructor1Params.size shouldBeEqual 1
        constructor1Params[0].type should be(IntArray::class.createType())

        val constructor2 = withStaticSecondaryConstructor[2]
        constructor2.name should be("invoke")
        val constructor2Params = withStaticSecondaryConstructor[2].parameters.filter { it.kind == KParameter.Kind.VALUE }
        constructor2Params.size shouldBeEqual 1
        constructor2Params[0].type should be(
            List::class.createType(
                arguments =
                listOf(
                    KTypeProjection(
                        KVariance.INVARIANT,
                        String::class.createType()
                    )
                )
            )
        )
    }
})