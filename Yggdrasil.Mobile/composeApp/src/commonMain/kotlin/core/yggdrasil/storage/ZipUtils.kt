package core.yggdrasil.storage

import kotlinx.io.files.Path

expect object ZipUtils {
    fun unzip(zipFile: Path, targetDir: Path)
}