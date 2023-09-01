package angstromio.util.io

import java.io.File

/**
 * Initialized [TempFolder] for use.
 *
 * @see [TempFolder]
 */
class InitializedTempFolder : TempFolder {
    override val _folderName: ThreadLocal<File> = ThreadLocal<File>()
}