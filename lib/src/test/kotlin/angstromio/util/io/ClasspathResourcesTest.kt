package angstromio.util.io

import io.kotest.assertions.fail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan

class ClasspathResourcesTest : FunSpec({
    test("ClasspathResource#load absolute path") {
        // loads from /test.txt
        when (val inputStream = ClasspathResource.load("/test.txt")) {
            null -> fail("")
            else ->
                inputStream.use { ins ->
                    ins.available() shouldBeGreaterThan (0)
                }
        }
    }

    test("ClasspathResource#load path interpreted as absolute") {
        // loads from test.txt
        when (val inputStream = ClasspathResource.load("test.txt")) {
            null -> fail("")
            else ->
                inputStream.use { ins ->
                    ins.available() shouldBeGreaterThan (0)
                }
        }
    }

    test("ClasspathResource#load absolute path multiple directories 1") {
        // loads from /io/angstrom/util/io/resource-test-file.txt
        when (val inputStream = ClasspathResource.load("/io/angstrom/util/io/resource-test-file.txt")) {
            null -> fail("")
            else ->
                inputStream.use { ins ->
                    ins.available() shouldBeGreaterThan (0)
                }
        }
    }

    test("ClasspathResource#load absolute path multiple directories 2") {
        // loads from /foo/bar/test-file.txt
        when (val inputStream = ClasspathResource.load("/foo/bar/test-file.txt")) {
            null -> fail("")
            else ->
                inputStream.use { ins ->
                    ins.available() shouldBeGreaterThan (0)
                }
        }
    }

    test("ClasspathResource#load path interpreted as absolute multiple directories 1") {
        // loads from io/angstrom/util/io/resource-test-file.txt
        when (val inputStream = ClasspathResource.load("io/angstrom/util/io/resource-test-file.txt")) {
            null -> fail("")
            else ->
                inputStream.use { ins ->
                    ins.available() shouldBeGreaterThan (0)
                }
        }
    }

    test("ClasspathResource#load path interpreted as absolute multiple directories 2") {
        // loads from foo/bar/test-file.txt
        when (val inputStream = ClasspathResource.load("foo/bar/test-file.txt")) {
            null -> fail("")
            else ->
                inputStream.use { ins ->
                    ins.available() shouldBeGreaterThan (0)
                }
        }
    }

    test("ClasspathResource#load does not exist 1") {
        // loads from /does-not-exist.txt
        when (ClasspathResource.load("/does-not-exist.txt")) {
            null -> Unit // should not return an InputStream
            else -> fail("")
        }
    }

    test("ClasspathResource#load does not exist 2") {
        // loads from does-not-exist.txt
        when (ClasspathResource.load("does-not-exist.txt")) {
            null -> Unit // should not return an InputStream
            else -> fail("")
        }
    }

    test("ClasspathResource#load empty file 1") {
        // loads from /empty-file.txt
        when (ClasspathResource.load("/empty-file.txt")) {
            null -> Unit // should not return an InputStream
            else -> fail("")
        }
    }

    test("ClasspathResource#load empty file 2") {
        // loads from empty-file.txt
        when (ClasspathResource.load("empty-file.txt")) {
            null -> Unit // should not return an InputStream
            else -> fail("")
        }
    }
})