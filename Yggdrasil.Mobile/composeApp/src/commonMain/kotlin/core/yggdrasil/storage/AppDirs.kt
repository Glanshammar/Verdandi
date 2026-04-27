package core.yggdrasil.storage

import kotlinx.io.files.Path

interface AppDirs {
    val downloadsDir: Path
    val cacheDir: Path
}
