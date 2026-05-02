package core.yggdrasil.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import core.yggdrasil.data.ApiRepository
import core.yggdrasil.data.ItemRepository
import core.yggdrasil.network.api.ApiConfig
import core.yggdrasil.storage.AppDirs
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.io.files.Path

data class MediaItem(
    val id: String,
    val title: String,
    val url: String,
    val type: String, // "Remote" or "Local"
    val isDownloaded: Boolean = false
)

class MediaPlayerViewModel(
    private val appDirs: AppDirs,
    private val apiRepository: ApiRepository
) : ViewModel() {
    private val _mediaUrl = MutableStateFlow("")
    val mediaUrl = _mediaUrl.asStateFlow()

    private val _remoteMedia = MutableStateFlow<List<MediaItem>>(emptyList())
    
    val availableMedia: StateFlow<List<MediaItem>> = combine(
        _remoteMedia,
        ItemRepository.items
    ) { remote, localItems ->
        val mediaExtensions = listOf(
            ".mp4", ".mkv", ".avi", ".webm", ".mov", // Video
            ".mp3", ".m4a", ".aac", ".ogg", ".flac", ".wav" // Audio
        )

        val localMedia = localItems.filter { item ->
            val path = item.content.removePrefix("Location: ").lowercase()
            mediaExtensions.any { ext -> path.endsWith(ext) }
        }.map { item ->
            val path = item.content.removePrefix("Location: ")
            MediaItem(
                id = "local_${item.id}",
                title = item.title,
                url = if (path.startsWith("content://") || path.startsWith("file://")) path else "file://$path",
                type = "Local",
                isDownloaded = true
            )
        }
        remote + localMedia
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        refreshRemoteMedia()
        scanLocalMedia()
    }

    fun scanLocalMedia() {
        ItemRepository.scanStorage(appDirs)
    }

    fun refreshRemoteMedia() {
        viewModelScope.launch {
            try {
                val files = apiRepository.listFiles(fileType = "audio,video")
                _remoteMedia.value = files.map { file ->
                    MediaItem(
                        id = "remote_${file.id}",
                        title = file.name,
                        url = "${ApiConfig.URL}/files/${file.id}/download",
                        type = "Remote"
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setMedia(url: String) {
        _mediaUrl.value = url
    }

    fun setLocalPath(path: String) {
        val uri = when {
            path.startsWith("content://") -> path
            path.startsWith("file://") -> path
            else -> "file://$path"
        }
        _mediaUrl.value = uri
    }
}
