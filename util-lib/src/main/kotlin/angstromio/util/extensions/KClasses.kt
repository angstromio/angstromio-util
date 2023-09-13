package angstromio.util.extensions

import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.javaType
import kotlin.reflect.jvm.kotlinFunction

object KClasses {

    /**
     * Returns a [KFunction] object that reflects the specified public constructor of the class represented by this [KClass].
     * The parameterTypes parameter is a vararg of [KClass] objects that identify the constructor's formal parameter types,
     * in declared order.
     *
     * @note this will ALSO search companion objects for an appropriate 'invoke' method if a suitable constructor cannot
     *       be found on the class.
     */
    fun KClass<*>.getConstructor(vararg parameterTypes: KClass<*>): KFunction<*>? =
        this.getConstructor(parameterTypes.toList())

    /**
     * Returns a [KFunction] object that reflects the specified public constructor of the class represented by this [KClass].
     * The parameterTypes parameter is a vararg of [KClass] objects that identify the constructor's formal parameter types,
     * in declared order.
     *
     * @note this will ALSO search companion objects for an appropriate 'invoke' method if a suitable constructor cannot
     *       be found on the class.
     */
    fun KClass<*>.getConstructor(parameterTypes: List<KClass<*>>): KFunction<*>? {
        return try {
            if (parameterTypes.isEmpty()) { this.primaryConstructor } else {
                this.java.getConstructor(*(parameterTypes. map { it.java }).toTypedArray()).kotlinFunction
            }
        } catch (e: NoSuchMethodException) {
            // try to find an 'invoke' function on companion
            this.companionObject?.declaredFunctions?.find { kFunction ->
                kFunction.name == "invoke" && matchParameters(kFunction.parameters, parameterTypes)
            }
        }
    }

    private fun matchParameters(kParameters: List<KParameter>, parameterTypes: List<KClass<*>>): Boolean {
        val parameters = kParameters.filter { it.kind == KParameter.Kind.VALUE }.map { it.type.javaType }
        return if (parameters.size != parameterTypes.size) {
            false
        } else {
            parameters.mapIndexed { index, parameter ->
                val parameterType = parameterTypes[index]
                when (parameter) {
                    is ParameterizedType -> // compare erased types only
                        parameter.rawType == parameterType.java
                    else ->
                        parameter == parameterType.java
                }
            }.all { it }
        }
    }
}