package angstromio.util.io

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import org.junit.jupiter.api.assertThrows
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.NullPointerException
import java.nio.file.FileSystems
import java.nio.file.Files

class TempFolderTest : FunSpec(), TempFolder by InitializedTempFolder() {

    private fun writeStringToFile(
        directory: String,
        name: String,
        ext: String,
        text: String
    ): File {

        val file = Files.createTempFile(FileSystems.getDefault().getPath(directory), name, ext).toFile()
        return try {
            val out: OutputStream = FileOutputStream(file, false)
            out.write(text.toByteArray(Charsets.UTF_8))
            file
        } finally {
            file.deleteOnExit()
        }
    }

    init {
        test("TempFolder#withTempFolder") {
            // cannot access the current thread's active temporary folder name before the 'withTempFolder' closure has been invoked
            assertThrows<NullPointerException> {
                canonicalFolderName()
            }

            withTempFolder {
                canonicalFolderName() shouldNot beNull()

                val file: File = writeStringToFile(folderName(), "test-file", ".json", """{"id": "999999999"}""")
                val text = File(file.absolutePath).readLines().joinToString("\n")
                text shouldBeEqual """{"id": "999999999"}"""
            }

            // current thread's active temporary folder is available after closure
            canonicalFolderName() shouldNot beNull()
        }
    }
}