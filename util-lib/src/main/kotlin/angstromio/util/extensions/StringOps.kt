@file:kotlin.jvm.JvmMultifileClass

package angstromio.util.extensions

/**
 * Turn a string of format "FooBar" into snake case "foo_bar"
 * @note toSnakeCase is not reversible, i.e. in general the following will _not_ be true:
 *
 *      s == toCamelCase(toSnakeCase(s))
 *
 */
fun String?.toSnakeCase(): String = StringOps.toSnakeCase(this ?: "")

/**
 * Turns a string of format "foo_bar" into PascalCase "FooBar"
 */
fun String?.toPascalCase(): String = StringOps.toPascalCase(this ?: "")

/**
 * Turn a string of format "foo_bar" into camelCase with the first letter in lower case: "fooBar"
 */
fun String?.toCamelCase(): String = StringOps.toCamelCase(this ?: "")

object StringOps {
    private val SnakeCaseRegexFirstPass = """([A-Z]+)([A-Z][a-z])""".toRegex()
    private val SnakeCaseRegexSecondPass = """([a-z\d])([A-Z])""".toRegex()

    fun toSnakeCase(name: String): String =
        SnakeCaseRegexSecondPass
            .replace(SnakeCaseRegexFirstPass.replace(name, "$1_$2"), "$1_$2")
            .lowercase()

    fun toPascalCase(s: String): String {
        return if (s.isEmpty()) {
            ""
        } else {
            val sb = StringBuilder()
            val nameList: List<Char> = s.toList()
            var i = 0
            var previousCharWasUnderscore = false
            while (i < nameList.size) {
                val char = nameList[i]
                previousCharWasUnderscore = if (char == '_') { // skip
                    true
                } else {
                    if (sb.isEmpty() || previousCharWasUnderscore) sb.append(char.uppercaseChar())
                    else sb.append(char)
                    false
                }
                i += 1
            }
            sb.toString()
        }
    }

    fun toCamelCase(name: String): String {
        val tmp: String = toPascalCase(name)
        return if (tmp.isEmpty()) {
            ""
        } else {
            tmp.substring(0, 1).lowercase() + tmp.substring(1)
        }
    }
}