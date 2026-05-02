package core.yggdrasil.storage

import kotlinx.io.files.Path
import platform.Foundation.*

actual object FileScanner {
    private val mediaExtensions = listOf(
        "mp4", "mkv", "avi", "webm", "mov",
        "mp3", "m4a", "aac", "ogg", "flac", "wav"
    )

    actual fun scanMedia(dirs: List<Path>): List<ScannedFile> {
        val result = mutableListOf<ScannedFile>()
        val fileManager = NSFileManager.defaultManager
        
        dirs.forEach { path ->
            val stringPath = path.toString()
            val enumerator = fileManager.enumeratorAtPath(stringPath) ?: return@forEach
            
            while (true) {
                val relativePath = enumerator.nextObject() as? String ?: break
                val fullPath = "$stringPath/$relativePath"
                
                val ext = fullPath.substringAfterLast(".").lowercase()
                if (mediaExtensions.contains(ext)) {
                    val name = fullPath.substringAfterLast("/")
                    result.add(ScannedFile(name, fullPath))
                }
            }
        }
        return result
    }
}
