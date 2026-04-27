package core.yggdrasil.storage

import android.content.Context
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

class AndroidAppDirs(private val context: Context) : AppDirs {
    override val downloadsDir: Path by lazy {
        val dir = Path(context.filesDir.absolutePath, "downloads")
        if (!SystemFileSystem.exists(dir)) {
            SystemFileSystem.createDirectories(dir)
        }
        dir
    }
    
    override val cacheDir: Path by lazy {
        val dir = Path(context.cacheDir.absolutePath, "downloads")
        if (!SystemFileSystem.exists(dir)) {
            SystemFileSystem.createDirectories(dir)
        }
        dir
    }
}
