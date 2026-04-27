package core.yggdrasil.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import core.yggdrasil.data.ItemRepository
import core.yggdrasil.network.FileDownloader
import core.yggdrasil.storage.AppDirs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.io.files.Path

sealed class DownloadState {
    data object Idle : DownloadState()
    data class Downloading(val progress: Float) : DownloadState()
    data class Completed(val path: String) : DownloadState()
    data class Error(val message: String) : DownloadState()
}

class DownloadViewModel(
    private val appDirs: AppDirs,
    private val downloader: FileDownloader
) : ViewModel() {

    private val _downloadState = MutableStateFlow<DownloadState>(DownloadState.Idle)
    val downloadState = _downloadState.asStateFlow()

    fun startDownload(url: String, fileName: String) {
        if (url.isBlank() || fileName.isBlank()) {
            _downloadState.value = DownloadState.Error("URL and File Name cannot be empty.")
            return
        }

        val sanitizedFileName = fileName.replace(Regex("[^a-zA-Z0-9._-]"), "_")
        
        viewModelScope.launch {
            _downloadState.value = DownloadState.Downloading(0f)
            val destination = Path(appDirs.downloadsDir.toString(), sanitizedFileName)
            
            val result = downloader.downloadFile(url, destination) { progress ->
                _downloadState.value = DownloadState.Downloading(progress.progress)
            }

            result.fold(
                onSuccess = {
                    _downloadState.value = DownloadState.Completed(destination.toString())
                    ItemRepository.addItem(
                        title = "Downloaded: $sanitizedFileName",
                        content = "Location: ${destination.toString()}"
                    )
                },
                onFailure = { error ->
                    _downloadState.value = DownloadState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }
}
