package core.yggdrasil.storage

import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import platform.Foundation.*

class IosAppDirs : AppDirs {
    override val downloadsDir: Path by lazy {
        val paths = NSFileManager.defaultManager.URLsForDirectory(NSDocumentDirectory, NSUserDomainMask)
        val documentsDirectory = paths.first() as NSURL
        val dir = Path(documentsDirectory.path!!, "downloads")
        if (!SystemFileSystem.exists(dir)) {
            SystemFileSystem.createDirectories(dir)
        }
        dir
    }

    override val cacheDir: Path by lazy {
        val paths = NSFileManager.defaultManager.URLsForDirectory(NSCachesDirectory, NSUserDomainMask)
        val cacheDirectory = paths.first() as NSURL
        val dir = Path(cacheDirectory.path!!, "downloads")
        if (!SystemFileSystem.exists(dir)) {
            SystemFileSystem.createDirectories(dir)
        }
        dir
    }
}
