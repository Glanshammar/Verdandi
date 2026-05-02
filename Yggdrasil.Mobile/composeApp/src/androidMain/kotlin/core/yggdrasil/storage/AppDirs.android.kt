package core.yggdrasil.storage

import android.content.Context
import android.os.Environment
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

    override val allStorageDirs: List<Path> by lazy {
        val dirs = mutableListOf<Path>()
        
        dirs.add(downloadsDir)
        
        context.getExternalFilesDirs(null).forEach { file ->
            if (file != null) {
                dirs.add(Path(file.absolutePath))
            }
        }

        val publicDownloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (publicDownloads.exists()) dirs.add(Path(publicDownloads.absolutePath))

        val publicMovies = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
        if (publicMovies.exists()) dirs.add(Path(publicMovies.absolutePath))

        val publicMusic = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
        if (publicMusic.exists()) dirs.add(Path(publicMusic.absolutePath))

        dirs.distinct()
    }
}
