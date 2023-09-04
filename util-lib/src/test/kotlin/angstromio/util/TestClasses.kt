@file:kotlin.jvm.JvmMultifileClass
@file:kotlin.jvm.JvmName("TestClassesKt")

package angstromio.util

object MyObject

class NoConstructor()

class DontCreateMe private constructor() {}

data class ClassOneTwo(@Annotation1 val one: String, @Annotation2 val two: String)
data class ClassOneTwoWithFields(@Annotation1 val one: String, @Annotation2 val two: String) {
    val city: String = "San Francisco"
    val state: String = "California"
}

data class NoSecondaryInvokeFunction(val one: String, val two: String) {
    companion object {
        fun create(three: Int, four: Int): NoSecondaryInvokeFunction =
            NoSecondaryInvokeFunction(three.toString(), four.toString())
    }
}

data class ClassOneTwoWithAnnotatedField(@Annotation1 val one: String, @Annotation2 val two: String) {
    @Annotation3
    val three: String = "three"
}

data class ClassThreeFour(@Annotation3 val three: String, @Annotation4 val four: String)
data class ClassFive(@Annotation5 val five: String)
data class ClassOneTwoThreeFour(
    @Annotation1 val one: String,
    @Annotation2 val two: String,
    @Annotation3 val three: String,
    @Annotation4 val four: String
)

data class WithThings(
    @Annotation1 @Thing("thing1") val thing1: String,
    @Annotation2 @Thing("thing2") val thing2: String
)

data class WithWidgets(
    @Annotation3 @Widget("widget1") val widget1: String,
    @Annotation4 @Widget("widget2") val widget2: String
)


data class GenericTestClass<T : Any>(@Annotation1 val one: T)
data class GenericTestClassWithMultipleArgs<T : Any>(@Annotation1 val one: T, @Annotation2 val two: Int)

data class WithSecondaryConstructor(
    @Annotation1 val one: Int,
    @Annotation2 val two: Int
) {
    constructor(@Annotation3 three: String, @Annotation4 four: String) : this(three.toInt(), four.toInt())
}

data class StaticSecondaryConstructor(@Annotation1 val one: Int, @Annotation2 val two: Int) {
    companion object {
        // NOTE: this is a factory method and not a constructor, so annotations will not be picked up
        operator fun invoke(@Annotation3 three: String, @Annotation4 four: String): StaticSecondaryConstructor =
            StaticSecondaryConstructor(three.toInt(), four.toInt())
    }
}

data class StaticSecondaryConstructorWithMethodAnnotation(
    @Annotation1 val one: Int,
    @Annotation2 val two: Int
) {
    // will not be found as Annotations only scans for declared field annotations, this is a method
    @Widget("widget1")
    fun widget1(): String = "this is widget 1 method"
}

interface AncestorWithAnnotations {
    @get:Annotation1
    val one: String
    @get:Annotation2
    val two: String
}

data class ClassThreeFourAncestorOneTwo(
    override val one: String,
    override val two: String,
    @Annotation3 val three: String,
    @Annotation4 val four: String
) : AncestorWithAnnotations

data class ClassAncestorOneTwo(@Annotation5 val five: String) : AncestorWithAnnotations {
    override val one: String = "one"
    override val two: String = "two"
}