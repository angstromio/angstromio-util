package angstromio.util.extensions

import kotlin.random.Random

object Strings {
    private val SnakeCaseRegexFirstPass = """([A-Z]+)([A-Z][a-z])""".toRegex()
    private val SnakeCaseRegexSecondPass = """([a-z\d])([A-Z])""".toRegex()
    private val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    /** Returns a random alphanumeric string of the given size */
    fun randomAlphanumericString(size: Int = 16): String {
        assert(size > 0)
        return (1..size)
            .map { Random.nextInt(0, charPool.size).let { charPool[it] } }
            .joinToString("")
    }

    /**
     * Turn a string of format "FooBar" into snake case "foo_bar"
     * @note toSnakeCase is not reversible, i.e. in general the following will _not_ be true:
     *
     *      s == toCamelCase(toSnakeCase(s))
     *
     */
    fun String?.toSnakeCase(): String? {
        return if (this == null) { null
        } else {
            SnakeCaseRegexSecondPass
                .replace(SnakeCaseRegexFirstPass.replace(this, "$1_$2"), "$1_$2")
                .lowercase()
        }
    }

    /**
     * Turns a string of format "foo_bar" into PascalCase "FooBar"
     */
    fun String?.toPascalCase(): String? {
        return if (this == null) {
            null
        } else {
            if (this.isEmpty()) {
                ""
            } else {
                val sb = StringBuilder()
                val nameList: List<Char> = this.toList()
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
    }

    /**
     * Turn a string of format "foo_bar" into camelCase with the first letter in lower case: "fooBar"
     */
    fun String?.toCamelCase(): String? {
        return if (this == null) {
            null
        } else {
            val tmp: String = this.toPascalCase()!!
            return if (tmp.isEmpty()) {
                ""
            } else {
                tmp.substring(0, 1).lowercase() + tmp.substring(1)
            }
        }
    }
}