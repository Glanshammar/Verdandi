package core.yggdrasil.storage

import kotlinx.io.files.Path
import java.io.File
import java.io.FileInputStream
import java.util.zip.ZipInputStream

actual object ZipUtils {
    actual fun unzip(zipFile: Path, targetDir: Path) {
        val targetDirectory = File(targetDir.toString())
        if (!targetDirectory.exists()) {
            targetDirectory.mkdirs()
        }

        ZipInputStream(FileInputStream(zipFile.toString())).use { zis ->
            var entry = zis.nextEntry
            while (entry != null) {
                val newFile = File(targetDirectory, entry.name)
                if (entry.isDirectory) {
                    newFile.mkdirs()
                } else {
                    newFile.parentFile?.mkdirs()
                    newFile.outputStream().use { fos ->
                        zis.copyTo(fos)
                    }
                }
                zis.closeEntry()
                entry = zis.nextEntry
            }
        }
    }
}