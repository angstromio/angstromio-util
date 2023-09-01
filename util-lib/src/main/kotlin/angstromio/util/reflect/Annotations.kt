package angstromio.util.reflect

import angstromio.util.extensions.getConstructor
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import java.lang.reflect.Field
import java.lang.reflect.Method
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor

object Annotations {
    private data class ConstructorAnnotationCacheKey(val clazz: KClass<*>, val parameterTypes: List<KClass<*>>)

    private const val DEFAULT_CACHE_SIZE: Long = 1024

    // simple memoization of the constructor annotations for a KClass
    private val ConstructorAnnotationCache: Cache<ConstructorAnnotationCacheKey, Map<String, List<Annotation>>> =
        Caffeine
            .newBuilder()
            .maximumSize(DEFAULT_CACHE_SIZE)
            .build()

    /**
     * Determines if the given [Annotation] is annotated by an [Annotation] of the given
     * type param.
     * @param ToFindAnnotation the type of the [Annotation] to determine if is annotated on the passed [Annotation].
     * @param annotation the [Annotation] to match.
     *
     * @return true if the given [Annotation] is annotated with an [Annotation] of type A,
     *         false otherwise.
     */
    inline fun <reified ToFindAnnotation : Annotation> isAnnotationPresent(annotation: Annotation): Boolean {
        val toInspect = annotation.annotationClass.java
        val toFindClazz = ToFindAnnotation::class.java
        return toInspect.isAnnotationPresent(toFindClazz)
    }

    /**
     * Determines if the given [A] is annotated by an [Annotation] of the given
     * type param [ToFindAnnotation].
     * @param A the type of the [Annotation] to determine if is annotated with an annotation of type [ToFindAnnotation].
     * @param ToFindAnnotation the [Annotation] to match.
     *
     *
     * @return true if the given [Annotation] of type [A] is annotated with an [Annotation] of type [ToFindAnnotation],
     *         false otherwise.
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified ToFindAnnotation : Annotation, reified A : Annotation> isAnnotationPresent(): Boolean =
        A::class.java.isAnnotationPresent(ToFindAnnotation::class.java as Class<Annotation>)

    /**
     * Filters a list of annotations discriminated on whether they are annotated with any from the set of given annotations.
     * @param predicate the Set of [Annotation] classes by which to filter.
     * @param annotations the list [Annotation] instances to filter.
     *
     * @return the filtered list of matching annotations.
     */
    fun filter(
        annotations: List<Annotation>,
        predicate: Set<KClass<out Annotation>>
    ): List<Annotation> =
        annotations.filter { annotation: Annotation -> predicate.contains(annotation.annotationClass) }

    /**
     * Filter a list of annotations discriminated on whether they are annotated with the annotation denoted by type [A].
     * @param A the type of the annotation by which to filter.
     * @param annotations the list of [Annotation] instances to filter.
     *
     * @return the filtered list of matching annotations.
     */
    inline fun <reified A : Annotation> filter(annotations: List<Annotation>): List<Annotation> =
        annotations.filter { ann -> isAnnotationPresent<A>(ann) }

    /**
     * Attempts to return the result of invoking `method()` of a given [Annotation] which SHOULD be
     * annotated by an [Annotation] of type [A].
     *
     * If the given [Annotation] is NOT annotated by an [Annotation] of type [A] or if the
     * `Annotation#method` cannot be invoked then null is returned.
     * @param A the [Annotation] type to match against before trying to invoke `annotation#method`.
     * @param annotation the [Annotation] to process.
     * @param method the name of the method in the [Annotation] to invoke to obtain a value. Default = 'value'.
     *
     * @return the result of invoking `annotation#method()` or null.
     */
    inline fun <reified A : Annotation> getValueIfAnnotatedWith(
        annotation: Annotation,
        method: String = "value"
    ): String? = if (isAnnotationPresent<A>(annotation)) {
        getValue(annotation, method)
    } else null

    /**
     * Attempts to return the result of invoking `method()` of a given [Annotation]. If the
     * `Annotation#method` cannot be invoked then null is returned.
     * @param annotation the [Annotation] to process.
     * @param method the name of the method in the [Annotation] to invoke to obtain a value. Default is 'value'
     *
     * @return the result of invoking `annotation#method()` or None.
     */
    fun getValue(
        annotation: Annotation,
        method: String = "value"
    ): String? =
        annotation
            .annotationClass
            .members
            .find { it.name == method }?.let { located: KCallable<*> ->
                try {
                    val result = located.call(annotation)
                    result as String
                } catch (e: Exception) {
                    return null
                }
            }

    /**
     * Attempts to map fields to array of annotations. This method scans the constructor and
     * then all declared fields in order to build up the mapping of field name to [List<java.lang.Annotation>].
     * When the given class has a single constructor, that constructor will be used. Otherwise, the
     * given parameter types are used to locate a constructor.
     * Steps:
     *   - walk the constructor and collect annotations on all parameters.
     *   - for each declared field in the class, collect declared annotations. If a constructor parameter
     *     is overridden as a declared field, the annotations on the declared field take precedence.
     *   - for each super interface, walk each declared method, collect declared method annotations IF
     *     the declared method is the same name of a declared field from step two. That is find only
     *     super interface methods which are implemented by declared fields in the given class in order
     *     to locate inherited annotations for the declared field.
     * @param clazz the `KClass` to inspect. This should represent a Kotlin data class.
     * @param parameterTypes an optional list of parameter class types in order to locate the appropriate
     *                       data class constructor when the class has multiple constructors. If a class
     *                       has multiple constructors and parameter types are not specified, an
     *                       [IllegalArgumentException] is thrown. Likewise, if a suitable constructor
     *                       cannot be located by the given parameter types an [IllegalArgumentException]
     *                       is thrown.
     *
     * @return a map of field name to associated annotations. An entry in the map only occurs if the
     *         associated annotations are non-empty. That is fields without any found annotations are
     *         not returned.
     * @note this will ALSO search companion objects for an appropriate 'invoke' method if a suitable constructor cannot
     *       be found on the class.
     */
    fun getConstructorAnnotations(
        clazz: KClass<*>,
        parameterTypes: List<KClass<*>> = emptyList()
    ): Map<String, List<Annotation>> =
        ConstructorAnnotationCache.get(ConstructorAnnotationCacheKey(clazz, parameterTypes)) { key ->
            findConstructorAnnotations(key.clazz, key.parameterTypes)
        }

    /**
     * Find the given target [Annotation] within a given list of annotations.
     * @param target the class of the [Annotation] to find.
     * @param annotations the list of [Annotation] instances to search.
     *
     * @return the matching [Annotation] instance if found, otherwise null.
     */
    fun findAnnotation(
        annotations: List<Annotation>,
        target: KClass<out Annotation>
    ): Annotation? {
        var found: Annotation? = null
        var index = 0
        while (index < annotations.size && found == null) {
            val annotation = annotations[index]
            if (annotation.annotationClass == target) found = annotation
            index += 1
        }
        return found
    }

    /**
     * Find the given target [Annotation] denoted by type [A] within a given list of annotations.
     * @param A the type of the [Annotation] to find.
     * @param annotations the list of [Annotation] instances to search.
     *
     * @return the matching [Annotation] instance if found, otherwise null.
     */
    inline fun <reified A : Annotation> findAnnotation(annotations: List<Annotation>): A? {
        val size = annotations.size
        val annotationType = A::class.java
        var found: A? = null
        var index = 0
        while (found == null && index < size) {
            val annotation = annotations[index]
            if (annotation.annotationClass.java == annotationType) found = annotation as A
            index += 1
        }
        return found
    }

    /**
     * Determines if the given [Annotation] is an annotation type of the given type param.
     * @param A the type to match against.
     * @param annotation the [Annotation] to match.
     *
     * @return true if the given [Annotation] is of type [A], false otherwise.
     */
    inline fun <reified A : Annotation> eq(annotation: Annotation): Boolean =
        annotation.annotationClass.java == A::class.java

    private fun findConstructorAnnotations(
        clazz: KClass<*>,
        parameterTypes: List<KClass<*>>
    ): Map<String, List<Annotation>> {
        val clazzAnnotations = hashMapOf<String, List<Annotation>>()
        val constructor = if (parameterTypes.isEmpty()) {
            clazz.primaryConstructor
        } else {
            clazz.getConstructor(parameterTypes)
        }
        if (constructor == null) {
            val message = if (parameterTypes.isEmpty()) {
                "Unable to locate a primary no-arg constructor for class '${clazz.qualifiedName}'."
            } else {
                "Unable to locate a constructor for '${clazz.qualifiedName}' with parameter types: [${
                    parameterTypes.joinToString(
                        ", "
                    )
                }]"
            }
            throw IllegalArgumentException(message)
        }
        val fields: List<KParameter> = constructor.parameters
        val clazzConstructorAnnotations: List<List<Annotation>> =
            constructor.parameters.map { it.annotations }
        var i = 0
        while (i < fields.size) {
            val field = fields[i]
            val fieldAnnotations = clazzConstructorAnnotations[i]
            if (fieldAnnotations.isNotEmpty()) {
                clazzAnnotations[field.name!!] = fieldAnnotations
            }
            i += 1
        }

        val declaredFields: Array<Field> = clazz.java.declaredFields
        var j = 0
        while (j < declaredFields.size) {
            val field = declaredFields[j]
            if (field.annotations.isNotEmpty()) {
                val existing = clazzAnnotations[field.name]
                if (existing != null) {
                    // prefer field annotations over constructor annotation
                    mergeAnnotations(field.annotations.toList(), existing)
                } else {
                    clazzAnnotations[field.name] = field.annotations.toList()
                }
            }

            j += 1
        }

        // find inherited annotations for declared fields
        return findDeclaredAnnotations(
            clazz,
            declaredFields.map { it.name },
            clazzAnnotations
        )
    }

    private fun findDeclaredAnnotations(
        clazz: KClass<*>,
        declaredFields: List<String>,
        acc: HashMap<String, List<Annotation>>
    ): Map<String, List<Annotation>> {
        val methods = clazz.java.declaredMethods.toList()
        var i = 0
        while (i < methods.size) {
            val method = methods[i]
            val methodAnnotations = method.annotations
            val methodNameAsField = mkFieldName(method)
            if (methodAnnotations.isNotEmpty() && declaredFields.contains(methodNameAsField)) {
                val result = acc[method.name]
                if (result != null) {
                    acc[methodNameAsField] = mergeAnnotations(result, methodAnnotations.toList())
                } else {
                    acc[methodNameAsField] = methodAnnotations.toList()
                }
            }
            i += 1
        }

        val interfaces = clazz.java.interfaces
        var j = 0
        while (j < interfaces.size) {
            val intF = interfaces[j]
            findDeclaredAnnotations(intF.kotlin, declaredFields, acc)
            j += 1
        }

        return acc
    }

    /** prefer annotations in 'a' over annotations in 'b' */
    private fun mergeAnnotations(
        a: List<Annotation>,
        b: List<Annotation>
    ): List<Annotation> =
        a + b.filterNot { bAnnotation -> a.any { it.annotationClass == bAnnotation.annotationClass } }

    private fun mkFieldName(method: Method): String = method.name.replace("get", "").replaceFirstChar { it.lowercase() }
}