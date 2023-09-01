@file:kotlin.jvm.JvmMultifileClass
@file:kotlin.jvm.JvmName("AnnotationsKt")

package angstromio.util.extensions

import angstromio.util.reflect.Annotations
import kotlin.reflect.KClass

/**
 * Determines if this [Annotation] is an annotation type of the given type param.
 * @param A the type to match against.
 *
 * @return true if this [Annotation] is of type [A], false otherwise.
 */
inline fun <reified A : Annotation> Annotation.eq(): Boolean = Annotations.eq<A>(this)

/**
 * Determines if this [Annotation] is NOT an annotation type of the given type param.
 * @param A the type to match against.
 *
 * @return true if this [Annotation] is of type [A], false otherwise.
 */
inline fun <reified A : Annotation> Annotation.notEq(): Boolean = !Annotations.eq<A>(this)

/**
 * Attempts to return the result of invoking `method()` of this [Annotation] which SHOULD be
 * annotated by an [Annotation] of type [A].
 *
 * If this [Annotation] is NOT annotated by an [Annotation] of type [A] or if the
 * `Annotation#method` cannot be invoked then null is returned.
 * @param A the [Annotation] type to match against before trying to invoke `annotation#method`.
 * @param method the name of thw method in this [Annotation] to invoke to obtain a value. Default = 'value'.
 *
 * @return the result of invoking `annotation#method()` or null.
 */
inline fun <reified A : Annotation> Annotation.getValueIfAnnotatedWith(method: String = "value"): String? =
    Annotations.getValueIfAnnotatedWith<A>(this, method)

/**
 * Attempts to return the result of invoking `method()` of this [Annotation]. If the
 * `Annotation#method` cannot be invoked then null is returned.
 * @param method the name of the method in this [Annotation] to invoke to obtain a value. Default is 'value'.
 *
 * @return the result of invoking `annotation#method()` or None.
 */
fun Annotation.getValue(method: String = "value"): String? = Annotations.getValue(this, method)

/**
 * Determines if this [Annotation] is annotated by an [Annotation] of the given
 * type param.
 * @param ToFindAnnotation the type of the [Annotation] to determine if is annotated on the passed [Annotation].
 *
 * @return true if this [Annotation] is annotated with an [Annotation] of type A,
 *         false otherwise.
 */
inline fun <reified ToFindAnnotation : Annotation> Annotation.isAnnotationPresent() =
    Annotations.isAnnotationPresent<ToFindAnnotation>(this)

/**
 * Find the given target [Annotation] within this of annotations.
 * @param target the class of the [Annotation] to find.
 *
 * @return the matching [Annotation] instance if found, otherwise null.
 */
fun List<Annotation>.find(target: KClass<out Annotation>): Annotation? = Annotations.findAnnotation(this, target)

/**
 * Find the given target [Annotation] denoted by type [A] within this list of annotations.
 * @param A the type of the [Annotation] to find.
 *
 * @return the matching [Annotation] instance if found, otherwise null.
 */
inline fun <reified A : Annotation> List<Annotation>.find(): Annotation? = Annotations.findAnnotation<A>(this)

/**
 * Filter this list of annotations discriminated on whether they are annotated with the annotation denoted by type [A].
 * @param A the type of the annotation by which to filter.
 *
 * @return the filtered list of matching annotations.
 */
inline fun <reified A : Annotation> List<Annotation>.filter(): List<Annotation> =
    Annotations.filter<A>(this)

/**
 * Filters this list of annotations discriminated on whether they are annotated with any from the set of given annotations.
 * @param predicate the Set of [Annotation] classes by which to filter.
 *
 * @return the filtered list of matching annotations.
 */
fun List<Annotation>.filter(predicate: Set<KClass<out Annotation>>): List<Annotation> =
    Annotations.filter(this, predicate)
