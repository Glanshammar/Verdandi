package core.yggdrasil.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import core.yggdrasil.data.ApiRepository
import core.yggdrasil.network.FileDownloader
import core.yggdrasil.storage.AppDirs
import core.yggdrasil.storage.ZipUtils
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

sealed class ApiState {
    data object Idle : ApiState()
    data object Loading : ApiState()
    data class Success(val data: Any) : ApiState()
    data class Error(val message: String) : ApiState()
}

class ApiViewModel(
    private val repository: ApiRepository,
    private val downloader: FileDownloader,
    private val appDirs: AppDirs
) : ViewModel() {
    private val _state = MutableStateFlow<ApiState>(ApiState.Idle)
    val state = _state.asStateFlow()

    fun callOnlineStatus() {
        performApiCall { repository.getOnlineStatus() }
    }

    fun callListFiles() {
        performApiCall { repository.listFiles() }
    }

    fun callAddFile(filePath: String) {
        performApiCall { repository.addFile(filePath) }
    }

    fun callGetFile(id: Int) {
        performApiCall { repository.getFile(id) }
    }

    fun callDeleteFile(id: Int) {
        performApiCall { repository.deleteFile(id) }
    }

    fun callDownloadFiles(ids: List<Int>) {
        viewModelScope.launch {
            _state.value = ApiState.Loading
            try {
                val response: HttpResponse = repository.downloadFiles(ids)
                val contentType = response.contentType()
                val isZip = contentType?.contentType == ContentType.Application.Zip.contentType && 
                            contentType.contentSubtype == ContentType.Application.Zip.contentSubtype
                
                val fileName = if (isZip) "debug_download.zip" else "debug_download_file"
                val destination = Path(appDirs.cacheDir.toString(), fileName)
                
                val result = downloader.downloadFromResponse(response, destination) { _ ->
                    // Progress tracking in state if needed
                }

                result.fold(
                    onSuccess = {
                        if (isZip) {
                            val unzipDir = Path(appDirs.downloadsDir.toString(), "extracted_${ids.joinToString("_")}")
                            ZipUtils.unzip(destination, unzipDir)
                            _state.value = ApiState.Success("ZIP Downloaded and unzipped to: $unzipDir")
                        } else {
                            _state.value = ApiState.Success("File Downloaded to: $destination")
                        }
                    },
                    onFailure = { error ->
                        _state.value = ApiState.Error(error.message ?: "Download failed")
                    }
                )
            } catch (e: Exception) {
                _state.value = ApiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun performApiCall(call: suspend () -> Any) {
        viewModelScope.launch {
            _state.value = ApiState.Loading
            try {
                val result = call()
                _state.value = ApiState.Success(result)
            } catch (e: Exception) {
                _state.value = ApiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
