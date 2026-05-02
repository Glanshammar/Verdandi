package core.yggdrasil.storage

import kotlinx.io.files.Path
import java.io.File

actual object FileScanner {
    private val mediaExtensions = listOf(
        "mp4", "mkv", "avi", "webm", "mov",
        "mp3", "m4a", "aac", "ogg", "flac", "wav"
    )

    actual fun scanMedia(dirs: List<Path>): List<ScannedFile> {
        val result = mutableListOf<ScannedFile>()
        dirs.forEach { path ->
            val root = File(path.toString())
            if (root.exists() && root.isDirectory) {
                scanRecursive(root, result, depth = 0)
            }
        }
        return result
    }

    private fun scanRecursive(dir: File, result: MutableList<ScannedFile>, depth: Int) {
        if (depth > 3) return // Prevent too deep scans
        
        val files = dir.listFiles() ?: return
        for (file in files) {
            if (file.isDirectory) {
                scanRecursive(file, result, depth + 1)
            } else {
                val ext = file.extension.lowercase()
                if (mediaExtensions.contains(ext)) {
                    result.add(ScannedFile(file.name, file.absolutePath))
                }
            }
        }
    }
}
