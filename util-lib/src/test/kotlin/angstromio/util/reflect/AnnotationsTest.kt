package angstromio.util.reflect

import angstromio.util.Annotation1
import angstromio.util.Annotation2
import angstromio.util.Annotation3
import angstromio.util.Annotation4
import angstromio.util.Annotation5
import angstromio.util.ClassAncestorOneTwo
import angstromio.util.ClassFive
import angstromio.util.ClassOneTwo
import angstromio.util.ClassOneTwoThreeFour
import angstromio.util.ClassOneTwoWithAnnotatedField
import angstromio.util.ClassOneTwoWithFields
import angstromio.util.ClassThreeFour
import angstromio.util.ClassThreeFourAncestorOneTwo
import angstromio.util.GenericTestClass
import angstromio.util.GenericTestClassWithMultipleArgs
import angstromio.util.MarkerAnnotation
import angstromio.util.NoConstructor
import angstromio.util.StaticSecondaryConstructor
import angstromio.util.StaticSecondaryConstructorWithMethodAnnotation
import angstromio.util.Thing
import angstromio.util.Things
import angstromio.util.Widget
import angstromio.util.WithSecondaryConstructor
import angstromio.util.WithThings
import angstromio.util.WithWidgets
import angstromio.util.extensions.eq
import angstromio.util.extensions.filter
import angstromio.util.extensions.find
import angstromio.util.extensions.getConstructorAnnotations
import angstromio.util.extensions.getValue
import angstromio.util.extensions.getValueIfAnnotatedWith
import angstromio.util.extensions.isAnnotationPresent
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.be
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import org.junit.jupiter.api.assertThrows
import kotlin.reflect.KClass

class AnnotationsTest : FunSpec() {

    private fun getAnnotations(clazz: KClass<*> = ClassOneTwoThreeFour::class): List<Annotation> {
        val annotationMap: Map<String, List<Annotation>> = Annotations.getConstructorAnnotations(clazz)
        annotationMap.isNotEmpty() should be(true)
        return annotationMap.flatMap { (_, annotations) -> annotations.toList() }
    }

    init {
        test("Annotations#filterIfAnnotationPresent") {
            val annotations = getAnnotations(ClassOneTwo::class)

            val found = annotations.filter<MarkerAnnotation>()
            found.size should be(1)
            found.first().annotationClass shouldBeEqual Annotation2::class
        }

        test("Annotations#filterAnnotations") {
            val annotations = getAnnotations(ClassThreeFour::class)

            val found = annotations.filter(setOf(Annotation4::class))
            found.size should be(1)
            found.first().annotationClass shouldBeEqual Annotation4::class
        }

        test("Annotations#findAnnotation") {
            val annotations = getAnnotations(ClassThreeFour::class)

            annotations.find(Annotation1::class) should beNull() // not found
            val found = annotations.find(Annotation3::class)
            found shouldNot beNull()
            found!!.annotationClass shouldBeEqual Annotation3::class
        }

        test("Annotations#findAnnotation by type") {
            val annotations = getAnnotations(ClassOneTwoThreeFour::class)
            annotations.find<MarkerAnnotation>() should beNull() // not found
            annotations.find<Annotation1>() shouldNot beNull()
            annotations.find<Annotation2>() shouldNot beNull()
            annotations.find<Annotation3>() shouldNot beNull()
            annotations.find<Annotation4>() shouldNot beNull()
        }

        test("Annotations#equals") {
            val annotations = getAnnotations(ClassOneTwoThreeFour::class)
            val found = annotations.find<Annotation1>()
            found shouldNot beNull()
            found!!.eq<Annotation1>() should be(true)
        }

        test("Annotations#isAnnotationPresent") {
            val annotations = getAnnotations(ClassOneTwoThreeFour::class)
            val annotation1 = annotations.find<Annotation1>()!!
            val annotation2 = annotations.find<Annotation2>()!!
            val annotation3 = annotations.find<Annotation3>()!!
            val annotation4 = annotations.find<Annotation4>()!!

            annotation1.isAnnotationPresent<MarkerAnnotation>() should be(false)
            annotation2.isAnnotationPresent<MarkerAnnotation>() should be(true)
            annotation3.isAnnotationPresent<MarkerAnnotation>() should be(true)
            annotation4.isAnnotationPresent<MarkerAnnotation>() should be(false)
        }

        test("Annotations#isAnnotationPresent 2") {
            Annotations.isAnnotationPresent<MarkerAnnotation, Annotation1>() should be(false)
            Annotations.isAnnotationPresent<MarkerAnnotation, Annotation2>() should be(true)
            Annotations.isAnnotationPresent<MarkerAnnotation, Annotation3>() should be(true)
            Annotations.isAnnotationPresent<MarkerAnnotation, Annotation4>() should be(false)
        }

        test("Annotations#findAnnotations") {
            var found: Map<String, List<Annotation>> = WithThings::class.getConstructorAnnotations()
            found.isEmpty() should be(false)
            var annotations = found.flatMap { (_, annotations) -> annotations.toList() }
            annotations.size shouldBeEqual 4

            found = WithWidgets::class.getConstructorAnnotations()
            found.isEmpty() should be(false)
            annotations = found.flatMap { (_, annotations) -> annotations.toList() }
            annotations.size shouldBeEqual 4

            found = ClassOneTwo::class.getConstructorAnnotations()
            found.isEmpty() should be(false)
            annotations = found.flatMap { (_, annotations) -> annotations.toList() }
            annotations.size shouldBeEqual 2

            found = ClassThreeFour::class.getConstructorAnnotations()
            found.isEmpty() should be(false)
            annotations = found.flatMap { (_, annotations) -> annotations.toList() }
            annotations.size shouldBeEqual 2

            found = ClassOneTwoThreeFour::class.getConstructorAnnotations()
            found.isEmpty() should be(false)
            annotations = found.flatMap { (_, annotations) -> annotations.toList() }
            annotations.size shouldBeEqual 4

            found = ClassOneTwoWithFields::class.getConstructorAnnotations()
            found.isEmpty() should be(false)
            annotations = found.flatMap { (_, annotations) -> annotations.toList() }
            annotations.size shouldBeEqual 2

            found = ClassOneTwoWithAnnotatedField::class.getConstructorAnnotations()
            found.isEmpty() should be(false)
            annotations = found.flatMap { (_, annotations) -> annotations.toList() }
            annotations.size shouldBeEqual 3

            found = ClassThreeFourAncestorOneTwo::class.getConstructorAnnotations()
            found.isEmpty() should be(false)
            annotations = found.flatMap { (_, annotations) -> annotations.toList() }
            annotations.size shouldBeEqual 4

            found = ClassAncestorOneTwo::class.getConstructorAnnotations()
            found.isEmpty() should be(false)
            annotations = found.flatMap { (_, annotations) -> annotations.toList() }
            annotations.size shouldBeEqual 3
        }

        test("Annotations.findAnnotations error") {
            assertThrows<IllegalArgumentException> {
                ClassOneTwoWithFields::class.getConstructorAnnotations(listOf(Boolean::class, Double::class))
            }

            val annotationMap: Map<String, List<Annotation>> =
                ClassOneTwoWithFields::class.getConstructorAnnotations(listOf(String::class, String::class))

            annotationMap.isEmpty() should be(false)
            annotationMap.size shouldBeEqual 2
            annotationMap["one"]!!.size shouldBeEqual 1
            annotationMap["one"]!!.first().annotationClass shouldBeEqual Annotation1::class
            annotationMap["two"]!!.size shouldBeEqual 1
            annotationMap["two"]!!.first().annotationClass shouldBeEqual Annotation2::class
        }

        test("Annotations#getValueIfAnnotatedWith") {
            val annotationsMap: Map<String, List<Annotation>> =
                WithThings::class.getConstructorAnnotations()

            annotationsMap.isEmpty() should be(false)
            val annotations = annotationsMap.flatMap { (_, annotations) -> annotations.toList() }
            val annotation1 = annotations.find<Annotation1>()!!
            val annotation2 = annotations.find<Annotation2>()!!

            // @Annotation1 is not annotated with @MarkerAnnotation
            annotation1.getValueIfAnnotatedWith<MarkerAnnotation>() should beNull()
            // @Annotation2 is annotated with @MarkerAnnotation but does not define a value() method
            annotation2.getValueIfAnnotatedWith<MarkerAnnotation>() should beNull()

            val things = annotations.filter(setOf(Thing::class))
            things.forEach { thing ->
                thing.getValueIfAnnotatedWith<MarkerAnnotation>() shouldNot beNull()
            }

            val fooThingAnnotation = Things.named("foo")
            val result = fooThingAnnotation.getValueIfAnnotatedWith<MarkerAnnotation>()
            result shouldNot beNull()
            result!! shouldBeEqual "foo"
        }

        test("Annotations#getValue") {
            val annotationsMap: Map<String, List<Annotation>> =
                ClassThreeFour::class.getConstructorAnnotations()

            annotationsMap.isEmpty() should be(false)
            val annotations = annotationsMap.flatMap { (_, annotations) -> annotations.toList() }
            val annotation3 = annotations.find<Annotation3>()!!
            val annotation4 = annotations.find<Annotation4>()!!

            val annotation3Value = annotation3.getValue()
            annotation3Value shouldNot beNull()
            annotation3Value!! shouldBeEqual "annotation3"

            // Annotation4 has no value
            Annotations.getValue(annotation4) should beNull()

            val clazzFiveAnnotationsMap = ClassFive::class.getConstructorAnnotations()
            val clazzFiveAnnotations = clazzFiveAnnotationsMap.flatMap { (_, annotations) ->
                annotations.toList()
            }
            val annotation5 =
                clazzFiveAnnotations.find<Annotation5>()!!
            // Annotation5 has a uniquely named method
            val annotation5Value = annotation5.getValue("discriminator")
            annotation5Value shouldNot beNull()
            annotation5Value!! shouldBeEqual "annotation5"

            // directly look up a value on an annotation
            val fooThingAnnotation = Things.named("foo")
            val fooThingAnnotationValue = fooThingAnnotation.getValue()
            fooThingAnnotationValue shouldNot beNull()
            fooThingAnnotationValue!! shouldBeEqual "foo"
        }

        test("Annotations#secondaryConstructor") {
            val annotationMap: Map<String, List<Annotation>> =
                WithSecondaryConstructor::class.getConstructorAnnotations(listOf(String::class, String::class))

            annotationMap.isEmpty() should be(false)
            annotationMap.size shouldBeEqual 2
            annotationMap["three"]!!.size shouldBeEqual 1
            annotationMap["three"]!!.first().annotationClass shouldBeEqual Annotation3::class
            annotationMap["four"]!!.size shouldBeEqual 1
            annotationMap["four"]!!.first().annotationClass shouldBeEqual Annotation4::class
            annotationMap["one"] should beNull() // not found
            annotationMap["two"] should beNull() // not found

            val annotations = annotationMap.flatMap { (_, annotations) -> annotations.toList() }
            annotations.find<MarkerAnnotation>() should beNull() // not found
            annotations.find<Annotation1>() should beNull() // not found
            annotations.find<Annotation2>() should beNull() // not found
            annotations.find<Annotation3>() shouldNot beNull()
            annotations.find<Annotation4>() shouldNot beNull()
        }

        test("Annotations#secondaryConstructor 1") {
            val annotationMap: Map<String, List<Annotation>> =
                WithSecondaryConstructor::class.getConstructorAnnotations(listOf(Int::class, Int::class))

            annotationMap.isEmpty() should be(false)
            annotationMap.size shouldBeEqual 2
            annotationMap["one"]!!.size shouldBeEqual 1
            annotationMap["one"]!!.first().annotationClass shouldBeEqual Annotation1::class
            annotationMap["two"]!!.size shouldBeEqual 1
            annotationMap["two"]!!.first().annotationClass shouldBeEqual Annotation2::class
            annotationMap["three"] should beNull() // not found
            annotationMap["four"] should beNull() // not found

            val annotations = annotationMap.flatMap { (_, annotations) -> annotations.toList() }
            annotations.find<MarkerAnnotation>() should beNull() // not found
            annotations.find<Annotation1>() shouldNot beNull()
            annotations.find<Annotation2>() shouldNot beNull()
            annotations.find<Annotation3>() should beNull() // not found
            annotations.find<Annotation4>() should beNull() // not found
        }

        test("Annotations#secondaryConstructor 2") {
            val annotationMap: Map<String, List<Annotation>> =
                StaticSecondaryConstructor::class.getConstructorAnnotations(listOf(String::class, String::class))

            annotationMap.isEmpty() should be(false)
            annotationMap.size shouldBeEqual 2
            annotationMap["three"]!!.size shouldBeEqual 1
            annotationMap["three"]!!.first().annotationClass shouldBeEqual Annotation3::class
            annotationMap["four"]!!.size shouldBeEqual 1
            annotationMap["four"]!!.first().annotationClass shouldBeEqual Annotation4::class
        }

        test("Annotations#secondaryConstructor 3") {
            val annotationMap: Map<String, List<Annotation>> =
                StaticSecondaryConstructor::class.getConstructorAnnotations(listOf(Int::class, Int::class))

            annotationMap.isEmpty() should be(false)
            annotationMap.size shouldBeEqual 2
            annotationMap["one"]!!.size shouldBeEqual 1
            annotationMap["one"]!!.first().annotationClass shouldBeEqual Annotation1::class
            annotationMap["two"]!!.size shouldBeEqual 1
            annotationMap["two"]!!.first().annotationClass shouldBeEqual Annotation2::class
        }

        test("Annotations#secondaryConstructor 4") {
            val annotationMap: Map<String, List<Annotation>> =
                StaticSecondaryConstructor::class.getConstructorAnnotations()

            annotationMap.isEmpty() should be(false)
            annotationMap.size shouldBeEqual 2
            annotationMap["one"]!!.size shouldBeEqual 1
            annotationMap["one"]!!.first().annotationClass shouldBeEqual Annotation1::class
            annotationMap["two"]!!.size shouldBeEqual 1
            annotationMap["two"]!!.first().annotationClass shouldBeEqual Annotation2::class

            val annotations = annotationMap.flatMap { (_, annotations) -> annotations.toList() }
            annotations.find<MarkerAnnotation>() should beNull() // not found
            annotations.find<Annotation1>() shouldNot beNull()
            annotations.find<Annotation2>() shouldNot beNull()
            annotations.find<Annotation3>() should beNull() // not found
            annotations.find<Annotation4>() should beNull() // not found
        }

        test("Annotations#secondaryConstructor 5") {
            val annotationMap: Map<String, List<Annotation>> =
                Annotations.getConstructorAnnotations(StaticSecondaryConstructorWithMethodAnnotation::class)
            annotationMap.isEmpty() should be(false)
            annotationMap.size shouldBeEqual 2
            annotationMap["one"]!!.size shouldBeEqual 1
            annotationMap["one"]!!.first().annotationClass shouldBeEqual Annotation1::class
            annotationMap["two"]!!.size shouldBeEqual 1
            annotationMap["two"]!!.first().annotationClass shouldBeEqual Annotation2::class
            annotationMap["widget1"] should beNull()

            val annotations = annotationMap.flatMap { (_, annotations) -> annotations.toList() }
            val annotation1 = annotations.find<Annotation1>()!!
            val annotation2 = annotations.find<Annotation2>()!!

            annotations.find<MarkerAnnotation>() should beNull() // not found
            annotations.find<Annotation1>()!! shouldBeEqual annotation1
            annotations.find<Annotation2>()!! shouldBeEqual annotation2
            annotations.find<Annotation3>() should beNull() // not found
            annotations.find<Annotation4>() should beNull() // not found
            val widgetAnnotationOpt = annotations.find<Widget>()
            widgetAnnotationOpt should beNull()
        }

        test("Annotations#secondaryConstructor 6") {
            val annotationMap: Map<String, List<Annotation>> =
                Annotations.getConstructorAnnotations(StaticSecondaryConstructorWithMethodAnnotation::class)

            annotationMap.isEmpty() should be(false)
            annotationMap.size shouldBeEqual 2
            annotationMap["one"]!!.size shouldBeEqual 1
            annotationMap["one"]!!.first().annotationClass shouldBeEqual Annotation1::class
            annotationMap["two"]!!.size shouldBeEqual 1
            annotationMap["two"]!!.first().annotationClass shouldBeEqual Annotation2::class
            annotationMap["widget1"] should beNull()

            val annotations = annotationMap.flatMap { (_, annotations) -> annotations.toList() }
            annotations.find<MarkerAnnotation>() should beNull() // not found
            annotations.find<Annotation1>() shouldNot beNull()
            annotations.find<Annotation2>() shouldNot beNull()
            annotations.find<Annotation3>() should beNull() // not found
            annotations.find<Annotation4>() should beNull() // not found
            val widgetAnnotationOpt = annotations.find<Widget>()
            widgetAnnotationOpt should beNull()
        }

        test("Annotations#no constructor should not fail") {
            // we expressly want this to no-op rather than fail outright
            val annotationMap: Map<String, List<Annotation>> =
                NoConstructor::class.getConstructorAnnotations()

            annotationMap.isEmpty() should be(true)
        }

        test("Annotations#generic types") {
            val annotationMap: Map<String, List<Annotation>> =
                GenericTestClass::class.getConstructorAnnotations(listOf(Object::class)) // generic types resolve to Object

            annotationMap.isEmpty() should be(false)
            annotationMap.size shouldBeEqual 1
            annotationMap["one"]!!.size shouldBeEqual 1
            annotationMap["one"]!!.first().annotationClass shouldBeEqual Annotation1::class

            val annotations = annotationMap.flatMap { (_, annotations) -> annotations.toList() }
            annotations.find<MarkerAnnotation>() should beNull() // not found
            annotations.find<Annotation1>() shouldNot beNull()
        }

        test("Annotations#generic types 1") {
            val annotationMap: Map<String, List<Annotation>> =
                GenericTestClassWithMultipleArgs::class.getConstructorAnnotations(
                    listOf(
                        Object::class,
                        Int::class
                    )
                ) // generic types resolve to Object

            annotationMap.isEmpty() should be(false)
            annotationMap.size shouldBeEqual 2
            annotationMap["one"]!!.size shouldBeEqual 1
            annotationMap["one"]!!.first().annotationClass shouldBeEqual Annotation1::class
            annotationMap["two"]!!.size shouldBeEqual 1
            annotationMap["two"]!!.first().annotationClass shouldBeEqual Annotation2::class

            val annotations = annotationMap.flatMap { (_, annotations) -> annotations.toList() }
            annotations.find<MarkerAnnotation>() should beNull() // not found
            annotations.find<Annotation1>() shouldNot beNull()
            annotations.find<Annotation2>() shouldNot beNull()
        }
    }
}