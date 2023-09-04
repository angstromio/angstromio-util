package angstromio.util.reflect

import angstromio.util.AncestorWithAnnotations
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
import angstromio.util.DontCreateMe
import angstromio.util.GenericTestClass
import angstromio.util.GenericTestClassWithMultipleArgs
import angstromio.util.MarkerAnnotation
import angstromio.util.MyObject
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

class AnnotationsTest : FunSpec() {

    private fun getAnnotations(clazz: Class<*> = ClassOneTwoThreeFour::class.java): List<Annotation> {
        val annotationMap: Map<String, List<Annotation>> = Annotations.getConstructorAnnotations(clazz)
        annotationMap.isNotEmpty() should be(true)
        return annotationMap.flatMap { (_, annotations) -> annotations.toList() }
    }

    init {
        test("Annotations#filterIfAnnotationPresent") {
            val annotations = getAnnotations(ClassOneTwo::class.java)

            val found = annotations.filter<MarkerAnnotation>()
            found.size should be(1)
            found.first().annotationClass shouldBeEqual Annotation2::class
        }

        test("Annotations#filterAnnotations") {
            val annotations = getAnnotations(ClassThreeFour::class.java)

            val found = annotations.filter(setOf(Annotation4::class.java))
            found.size should be(1)
            found.first().annotationClass shouldBeEqual Annotation4::class
        }

        test("Annotations#findAnnotation") {
            val annotations = getAnnotations(ClassThreeFour::class.java)

            annotations.find(Annotation1::class.java) should beNull() // not found
            val found = annotations.find(Annotation3::class.java)
            found shouldNot beNull()
            found!!.annotationClass shouldBeEqual Annotation3::class
        }

        test("Annotations#findAnnotation by type") {
            val annotations = getAnnotations(ClassOneTwoThreeFour::class.java)
            annotations.find<MarkerAnnotation>() should beNull() // not found
            annotations.find<Annotation1>() shouldNot beNull()
            annotations.find<Annotation2>() shouldNot beNull()
            annotations.find<Annotation3>() shouldNot beNull()
            annotations.find<Annotation4>() shouldNot beNull()
        }

        test("Annotations#equals") {
            val annotations = getAnnotations(ClassOneTwoThreeFour::class.java)
            val found = annotations.find<Annotation1>()
            found shouldNot beNull()
            found!!.eq<Annotation1>() should be(true)
        }

        test("Annotations#isAnnotationPresent") {
            val annotations = getAnnotations(ClassOneTwoThreeFour::class.java)
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
            var found: Map<String, List<Annotation>> = WithThings::class.java.getConstructorAnnotations()
            found.isEmpty() should be(false)
            var annotations = found.flatMap { (_, annotations) -> annotations.toList() }
            annotations.size shouldBeEqual 4

            found = WithWidgets::class.java.getConstructorAnnotations()
            found.isEmpty() should be(false)
            annotations = found.flatMap { (_, annotations) -> annotations.toList() }
            annotations.size shouldBeEqual 4

            found = ClassOneTwo::class.java.getConstructorAnnotations()
            found.isEmpty() should be(false)
            annotations = found.flatMap { (_, annotations) -> annotations.toList() }
            annotations.size shouldBeEqual 2

            found = ClassThreeFour::class.java.getConstructorAnnotations()
            found.isEmpty() should be(false)
            annotations = found.flatMap { (_, annotations) -> annotations.toList() }
            annotations.size shouldBeEqual 2

            found = ClassOneTwoThreeFour::class.java.getConstructorAnnotations()
            found.isEmpty() should be(false)
            annotations = found.flatMap { (_, annotations) -> annotations.toList() }
            annotations.size shouldBeEqual 4

            found = ClassOneTwoWithFields::class.java.getConstructorAnnotations()
            found.isEmpty() should be(false)
            annotations = found.flatMap { (_, annotations) -> annotations.toList() }
            annotations.size shouldBeEqual 2

            found = ClassOneTwoWithAnnotatedField::class.java.getConstructorAnnotations()
            found.isEmpty() should be(false)
            annotations = found.flatMap { (_, annotations) -> annotations.toList() }
            annotations.size shouldBeEqual 3

            found = ClassThreeFourAncestorOneTwo::class.java.getConstructorAnnotations()
            found.isEmpty() should be(false)
            annotations = found.flatMap { (_, annotations) -> annotations.toList() }
            annotations.size shouldBeEqual 4

            found = ClassAncestorOneTwo::class.java.getConstructorAnnotations()
            found.isEmpty() should be(false)
            annotations = found.flatMap { (_, annotations) -> annotations.toList() }
            annotations.size shouldBeEqual 3
        }

        test("Annotations.findAnnotations error") {
            assertThrows<IllegalArgumentException> {
                ClassOneTwoWithFields::class.java.getConstructorAnnotations(
                    arrayOf(
                        Boolean::class.java,
                        Double::class.java
                    )
                )
            }

            val annotationMap: Map<String, List<Annotation>> =
                ClassOneTwoWithFields::class.java.getConstructorAnnotations(
                    arrayOf(
                        String::class.java,
                        String::class.java
                    )
                )

            annotationMap.isEmpty() should be(false)
            annotationMap.size shouldBeEqual 2
            annotationMap["one"]!!.size shouldBeEqual 1
            annotationMap["one"]!!.first().annotationClass shouldBeEqual Annotation1::class
            annotationMap["two"]!!.size shouldBeEqual 1
            annotationMap["two"]!!.first().annotationClass shouldBeEqual Annotation2::class
        }

        test("Annotations#getValueIfAnnotatedWith") {
            val annotationsMap: Map<String, List<Annotation>> =
                WithThings::class.java.getConstructorAnnotations()

            annotationsMap.isEmpty() should be(false)
            val annotations = annotationsMap.flatMap { (_, annotations) -> annotations.toList() }
            val annotation1 = annotations.find<Annotation1>()!!
            val annotation2 = annotations.find<Annotation2>()!!

            // @Annotation1 is not annotated with @MarkerAnnotation
            annotation1.getValueIfAnnotatedWith<MarkerAnnotation>() should beNull()
            Annotations.getValueIfAnnotatedWith<MarkerAnnotation>(annotation1) should beNull()
            // @Annotation2 is annotated with @MarkerAnnotation but does not define a value() method
            annotation2.getValueIfAnnotatedWith<MarkerAnnotation>() should beNull()
            Annotations.getValueIfAnnotatedWith<MarkerAnnotation>(annotation2) should beNull()

            val things = annotations.filter(setOf(Thing::class.java))
            things.forEach { thing ->
                thing.getValueIfAnnotatedWith<MarkerAnnotation>() shouldNot beNull()
                Annotations.getValueIfAnnotatedWith<MarkerAnnotation>(thing) shouldNot beNull()
            }

            val fooThingAnnotation = Things.named("foo")
            fooThingAnnotation.getValueIfAnnotatedWith<MarkerAnnotation>() should be("foo")
            Annotations.getValueIfAnnotatedWith<MarkerAnnotation>(fooThingAnnotation) should be("foo")

            // incorrect value function name
            Annotations.getValueIfAnnotatedWith<MarkerAnnotation>(fooThingAnnotation, "values") should beNull()
        }

        test("Annotations#getValue") {
            val annotationsMap: Map<String, List<Annotation>> =
                ClassThreeFour::class.java.getConstructorAnnotations()

            annotationsMap.isEmpty() should be(false)
            val annotations = annotationsMap.flatMap { (_, annotations) -> annotations.toList() }
            val annotation3 = annotations.find<Annotation3>()!!
            val annotation4 = annotations.find<Annotation4>()!!

            val annotation3Value = annotation3.getValue()
            annotation3Value shouldNot beNull()
            annotation3Value!! shouldBeEqual "annotation3"

            // Annotation4 has no value
            Annotations.getValue(annotation4) should beNull()

            val clazzFiveAnnotationsMap = ClassFive::class.java.getConstructorAnnotations()
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
                WithSecondaryConstructor::class.java.getConstructorAnnotations(arrayOf(String::class.java, String::class.java))

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
                WithSecondaryConstructor::class.java.getConstructorAnnotations(arrayOf(Int::class.java, Int::class.java))

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

        test("Annotations#secondaryConstructor 3") {
            val annotationMap: Map<String, List<Annotation>> =
                StaticSecondaryConstructor::class.java.getConstructorAnnotations(
                    arrayOf(
                        Int::class.java,
                        Int::class.java
                    )
                )

            annotationMap.isEmpty() should be(false)
            annotationMap.size shouldBeEqual 2
            annotationMap["one"]!!.size shouldBeEqual 1
            annotationMap["one"]!!.first().annotationClass shouldBeEqual Annotation1::class
            annotationMap["two"]!!.size shouldBeEqual 1
            annotationMap["two"]!!.first().annotationClass shouldBeEqual Annotation2::class
        }

        test("Annotations#secondaryConstructor 4") {
            val annotationMap: Map<String, List<Annotation>> =
                StaticSecondaryConstructor::class.java.getConstructorAnnotations()

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
                Annotations.getConstructorAnnotations(StaticSecondaryConstructorWithMethodAnnotation::class.java)
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
                Annotations.getConstructorAnnotations(StaticSecondaryConstructorWithMethodAnnotation::class.java)

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
            // we expressly want these to no-op rather than fail outright
            NoConstructor::class.java.getConstructorAnnotations().isEmpty() should be(true)
            DontCreateMe::class.java.getConstructorAnnotations().isEmpty() should be(true)
        }

        test("Annotations#getConstructorAnnotations - with incorrect parameter types") {
            assertThrows<IllegalArgumentException> {
                DontCreateMe::class.java.getConstructorAnnotations(arrayOf(String::class.java, String::class.java))
                    .isEmpty() should be(
                    true
                )
            }
            assertThrows<IllegalArgumentException> {
                NoConstructor::class.java.getConstructorAnnotations(arrayOf(Int::class.java))
            }
        }

        test("Annotations#getConstructorAnnotations - no constructor") {
            val e = assertThrows<IllegalArgumentException> {
                AncestorWithAnnotations::class.java.getConstructorAnnotations()
            }
            e.message should be("Unable to locate a primary no-arg constructor for class '${AncestorWithAnnotations::class.qualifiedName}'.")

            val e1 = assertThrows<IllegalArgumentException> {
                AncestorWithAnnotations::class.java.getConstructorAnnotations(
                    arrayOf(
                        String::class.java,
                        Int::class.java
                    )
                )
            }
            e1.message should be(
                "Unable to locate a constructor for '${AncestorWithAnnotations::class.qualifiedName}' with parameter types: [${
                    listOf(String::class.java, Int::class.java).joinToString(
                        ", "
                    )
                }]"
            )

            assertThrows<IllegalArgumentException> {
                MyObject::class.java.getConstructorAnnotations()
            }

            assertThrows<IllegalArgumentException> {
                MyObject::class.java.getConstructorAnnotations(arrayOf(Double::class.java))
            }
        }

        test("Annotations#generic types") {
            val annotationMap: Map<String, List<Annotation>> =
                GenericTestClass::class.java.getConstructorAnnotations(arrayOf(Object::class.java)) // generic types resolve to Object

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
                GenericTestClassWithMultipleArgs::class.java.getConstructorAnnotations(
                    arrayOf(
                        Object::class.java,
                        Int::class.java
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