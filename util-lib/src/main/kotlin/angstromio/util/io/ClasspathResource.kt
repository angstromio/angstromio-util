package angstromio.util.io

import java.io.BufferedInputStream
import java.io.InputStream

object ClasspathResource {

    /**
     * Loads the named resource from the classpath to return an optional [InputStream].
     * If the resolved resource exists and has a non-zero number of readable bytes an
     * [InputStream] will be returned otherwise a None is returned.
     *
     * Resolution of the path to the named resource is ALWAYS assumed to be absolute, that is,
     * if the given `name` does not begin with a `/` (`\u002f`), one will be prepended to the given name.
     * E.g., `load("foo.txt")` will attempt to load the resource with the absolute name `/foo.txt`.
     *
     * ==Usage==
     *
     * load("foo.txt") // loads the classpath resource `/foo.txt`
     * load("/foo.txt") // loads the classpath resource `/foo.txt`
     *
     * load("foo/bar/file.txt") // loads the classpath resource `/foo/bar/file.txt`
     * load("/foo/bar/file.txt") // loads the classpath resource `/foo/bar/file.txt`
     *
     *
     * @note The caller is responsible for managing the closing of any returned [InputStream].
     * @see [java.lang.Class#getResourceAsStream]
     */
    fun load(name: String): InputStream? = when (val inputStream = javaClass.getResourceAsStream(absolutePath(name))) {
        null -> null
        else -> {
            val bufferedInputStream = BufferedInputStream(inputStream)
            if (bufferedInputStream.available() > 0) {
                bufferedInputStream
            } else {
                // close the opened resource since we will not be returning the input stream
                bufferedInputStream.close()
                null
            }
        }
    }

    // ensure the given name is prepended with a "/" to load as an absolute path.
    private fun absolutePath(name: String): String = if (name.startsWith("/")) name else "/" + name
}