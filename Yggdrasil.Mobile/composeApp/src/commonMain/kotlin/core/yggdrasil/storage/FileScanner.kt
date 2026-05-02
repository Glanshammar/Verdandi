package core.yggdrasil.storage

import kotlinx.io.files.Path

data class ScannedFile(
    val name: String,
    val path: String
)

expect object FileScanner {
    fun scanMedia(dirs: List<Path>): List<ScannedFile>
}
