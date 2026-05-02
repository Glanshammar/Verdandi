package core.yggdrasil.storage

import kotlinx.io.files.Path

actual object ZipUtils {
    actual fun unzip(zipFile: Path, targetDir: Path) {
        // Implementation for iOS would typically use a library like ZipArchive (SSZipArchive)
        // or a platform API if available in newer versions.
        // For now, this is a placeholder.
        println("Unzipping on iOS is not yet implemented.")
    }
}
