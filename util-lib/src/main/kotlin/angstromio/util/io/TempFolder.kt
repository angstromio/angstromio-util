package angstromio.util.io

import java.io.File
import java.nio.file.Files
import java.nio.file.Path

/**
 * Test mixin that creates a temporary thread-local folder for a block of code to execute in.
 * The folder is recursively deleted after the test.
 *
 * Note that multiple uses of [TempFolder] cannot be nested, because the temporary directory
 * is effectively a thread-local global.
 *
 * Usage is typically through the [InitializedTempFolder] delegate (which initializes the ThreadLocal), e.g.
 *
 *      class MyClass : SomeParent(), TempFolder by InitializedTempFolder() {
 *          ...
 *      }
 *
 * @note based on [com.twitter.io.TempFolder](https://github.com/twitter/util/blob/develop/util-core/src/main/scala/com/twitter/io/TempFolder.scala)
 */
interface TempFolder {

    val _folderName: ThreadLocal<File>
        get() = ThreadLocal<File>()

    /**
     * Runs the given block of code with the presence of a temporary folder whose name can be
     * obtained from within the code block by calling folderName.
     *
     * Use of this function may not be nested.
     */
    fun withTempFolder(f: () -> Any) {
        val tempFolder = System.getProperty("java.io.tmpdir")
        var folder = File(tempFolder, "angstomio-test-" + System.currentTimeMillis())
        while (!folder.mkdir()) {
            folder = File(tempFolder, "angstomio-test-" + System.currentTimeMillis())
        }
        _folderName.set(folder)

        try {
            f.invoke()
        } finally {
            Files
                .walk(folder.toPath())
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete)
        }
    }

    /**
     * @return The current thread's active temporary folder.
     * @note Throws `RuntimeException` if not running within a withTempFolder block
     */
    fun folderName(): String = _folderName.get().path

    /**
     * @return The canonical path of the current thread's active temporary folder.
     * @note Throws `RuntimeException` if not running within a withTempFolder block
     */
    fun canonicalFolderName(): String = _folderName.get().canonicalPath
}