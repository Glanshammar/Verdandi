package core.yggdrasil.viewmodels

import androidx.lifecycle.ViewModel
import core.yggdrasil.storage.AppDirs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.io.files.Path

class MediaPlayerViewModel(private val appDirs: AppDirs) : ViewModel() {
    private val _mediaUrl = MutableStateFlow("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
    val mediaUrl = _mediaUrl.asStateFlow()

    fun setMedia(url: String) {
        _mediaUrl.value = url
    }

    /**
     * Sets a local file from the app's downloads directory as the media source.
     * @param fileName The name of the file in the downloads folder.
     */
    fun setLocalMedia(fileName: String) {
        val filePath = Path(appDirs.downloadsDir.toString(), fileName).toString()
        // Most media players require the file:// prefix for local paths
        _mediaUrl.value = if (!filePath.startsWith("file://")) "file://$filePath" else filePath
    }
    
    /**
     * Sets a local file path as the media source.
     * @param path The absolute path to the local file.
     */
    fun setLocalPath(path: String) {
        val uri = when {
            path.startsWith("content://") -> path
            path.startsWith("file://") -> path
            else -> "file://$path"
        }
        _mediaUrl.value = uri
    }
}
