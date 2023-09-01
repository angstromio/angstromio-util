package angstromio.util.extensions

import angstromio.util.extensions.Strings.toCamelCase
import angstromio.util.extensions.Strings.toPascalCase
import angstromio.util.extensions.Strings.toSnakeCase
import io.kotest.core.spec.style.FunSpec
import java.util.*

class StringsTest : FunSpec({

    test("StringOps#snakeCase") {
        // return a null if given null
        assert(null.toSnakeCase() == null)

        assert("FooBar".toSnakeCase() == "foo_bar")

        assert("MyCamelCase".toSnakeCase() == "my_camel_case")
        assert("CamelCase".toSnakeCase() == "camel_case")
        assert("Camel".toSnakeCase() == "camel")
        assert("MyCamel12Case".toSnakeCase() == "my_camel12_case")
        assert("CamelCase12".toSnakeCase() == "camel_case12")
        assert("Camel12".toSnakeCase() == "camel12")
        assert("Foobar".toSnakeCase() == "foobar")
        // not modify existing snake case strings
        assert("my_snake_case".toSnakeCase() == "my_snake_case")
        assert("snake".toSnakeCase() == "snake")
        // handle abbreviations
        assert("ABCD".toSnakeCase() == "abcd")
        assert("HTML".toSnakeCase() == "html")
        assert("HTMLEditor".toSnakeCase() == "html_editor")
        assert("EditorTOC".toSnakeCase() == "editor_toc")
        assert("HTMLEditorTOC".toSnakeCase() == "html_editor_toc")

        assert("HTML5".toSnakeCase() == "html5")
        assert("HTML5Editor".toSnakeCase() == "html5_editor")
        assert("Editor2TOC".toSnakeCase() == "editor2_toc")
        assert("HTML5Editor2TOC".toSnakeCase() == "html5_editor2_toc")
    }

    test("StringOps#pascalCase") {
        assert("foo_bar".toPascalCase() == "FooBar")
        assert("foo__bar".toPascalCase() == "FooBar")
        assert("foo___bar".toPascalCase() == "FooBar")
        assert("_foo_bar".toPascalCase() == "FooBar")
        assert("foo_bar_".toPascalCase() == "FooBar")
        assert("_foo_bar_".toPascalCase() == "FooBar")
        assert("a_b_c_d".toPascalCase() == "ABCD")

        // return a null if given null
        assert(null.toPascalCase() == null)
        // leave a CamelCased name untouched
        assert("NeatFeet".toPascalCase() == "NeatFeet")
        assert("FooBar".toPascalCase() == "FooBar")
        assert("HTML".toPascalCase() == "HTML")
        assert("HTML5".toPascalCase() == "HTML5")
        assert("Editor2TOC".toPascalCase() == "Editor2TOC")
    }

    test("StringOps#camelCase") {
        assert("foo_bar".toCamelCase() == "fooBar")
        assert("foo__bar".toCamelCase() == "fooBar")
        assert("foo___bar".toCamelCase() == "fooBar")
        assert("_foo_bar".toCamelCase() == "fooBar")
        assert("foo_bar_".toCamelCase() == "fooBar")
        assert("_foo_bar_".toCamelCase() == "fooBar")
        assert("a_b_c_d".toCamelCase() == "aBCD")

        // return a null  if given null
        assert(null.toCamelCase() == null)
        assert("GetTweets".toCamelCase() == "getTweets")
        assert("FooBar".toCamelCase() == "fooBar")
        assert("HTML".toCamelCase() == "hTML")
        assert("HTML5".toCamelCase() == "hTML5")
        assert("Editor2TOC".toCamelCase() == "editor2TOC")
    }

    test("StringOps#toPascalCase & toCamelCase Method function") {
        val name = "emperor_norton"
        // test that the first letter for toCamelCase is lower-cased
        name.toCamelCase()?.toList()?.get(0)?.isLowerCase()?.let { assert(it) }
        // test that toPascalCase and toCamelCase.capitalize are the same
        assert(name.toPascalCase() ==
                name.toCamelCase()?.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                })
    }
})