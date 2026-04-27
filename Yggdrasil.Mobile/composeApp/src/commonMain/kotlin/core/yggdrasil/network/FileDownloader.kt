package core.yggdrasil.network

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

data class DownloadProgress(
    val bytesRead: Long,
    val contentLength: Long,
    val isFinished: Boolean = false
) {
    val progress: Float
        get() = if (contentLength > 0) bytesRead.toFloat() / contentLength else 0f
}

class FileDownloader(private val httpClient: HttpClient) {

    suspend fun downloadFile(
        url: String,
        destination: Path,
        onProgress: (DownloadProgress) -> Unit
    ): Result<Unit> = withContext(Dispatchers.Default) {
        try {
            val response = httpClient.get(url)
            
            if (!response.status.isSuccess()) {
                return@withContext Result.failure(Exception("HTTP error: ${response.status}"))
            }

            val contentLength = response.contentLength() ?: -1L
            val channel = response.bodyAsChannel()
            
            SystemFileSystem.sink(destination).buffered().use { sink ->
                var bytesRead = 0L
                val buffer = ByteArray(8192)
                
                while (!channel.isClosedForRead) {
                    val read = channel.readAvailable(buffer)
                    if (read == -1) break
                    
                    sink.write(buffer, 0, read)
                    bytesRead += read
                    
                    onProgress(DownloadProgress(bytesRead, contentLength))
                }
                onProgress(DownloadProgress(bytesRead, contentLength, isFinished = true))
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
