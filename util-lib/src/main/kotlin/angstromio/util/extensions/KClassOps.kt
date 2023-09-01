@file:kotlin.jvm.JvmMultifileClass

package angstromio.util.extensions

import angstromio.util.reflect.Annotations
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.javaType
import kotlin.reflect.jvm.kotlinFunction

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
 *
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
 * @see [Annotations.getConstructorAnnotations]]
 */
fun KClass<*>.getConstructorAnnotations(parameterTypes: List<KClass<*>> = emptyList()): Map<String, List<Annotation>> =
    Annotations.getConstructorAnnotations(this, parameterTypes)

/**
 * Returns a [KFunction] object that reflects the specified public constructor of the class represented by this [KClass].
 * The parameterTypes parameter is a vararg of [KClass] objects that identify the constructor's formal parameter types,
 * in declared order.
 *
 * @note this will ALSO search companion objects for an appropriate 'invoke' method if a suitable constructor cannot
 *       be found on the class.
 */
fun KClass<*>.getConstructor(vararg parameterTypes: KClass<*>): KFunction<*>? =
    KClassOps.getConstructor(this, listOf(*parameterTypes))

/**
 * Returns a [KFunction] object that reflects the specified public constructor of the class represented by this [KClass].
 * The parameterTypes parameter is a vararg of [KClass] objects that identify the constructor's formal parameter types,
 * in declared order.
 *
 * @note this will ALSO search companion objects for an appropriate 'invoke' method if a suitable constructor cannot
 *       be found on the class.
 */
fun KClass<*>.getConstructor(parameterTypes: List<KClass<*>>): KFunction<*>? =
    KClassOps.getConstructor(this, parameterTypes)

object KClassOps {

    fun getConstructor(clazz: KClass<*>, parameterTypes: List<KClass<*>>): KFunction<*>? {
        return try {
            clazz.java.getConstructor(*(parameterTypes.map { it.java }).toTypedArray()).kotlinFunction
        } catch (e: NoSuchMethodException) {
            // try to find an 'invoke' function on companion
            clazz.companionObject?.declaredFunctions?.find { kFunction ->
                kFunction.name == "invoke" && matchParameters(kFunction.parameters, parameterTypes)
            }
        }
    }

    // match the VALUE KParameter types in order to the given Array of KClass types
    private fun matchParameters(kParameters: List<KParameter>, parameterTypes: List<KClass<*>>): Boolean {
        val parameters = kParameters.filter { it.kind == KParameter.Kind.VALUE }.map { it.type.javaType }
        return if (parameters.size != parameterTypes.size) {
            false
        } else {
            parameters.mapIndexed { index, parameter ->
                val parameterType = parameterTypes[index]
                parameter.typeName == parameterType.starProjectedType.javaType.typeName
            }.all { it }
        }
    }
}