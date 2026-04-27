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
        if (!url.startsWith("https://", ignoreCase = true)) {
            return@withContext Result.failure(Exception("Only HTTPS URLs are allowed for security."))
        }

        try {
            val response = httpClient.get(url)
            
            if (!response.status.isSuccess()) {
                return@withContext Result.failure(Exception("HTTP error: ${response.status}"))
            }

            val contentType = response.contentType()
            if (contentType?.match(ContentType.Text.Html) == true) {
                return@withContext Result.failure(Exception("The URL points to a web page (HTML) instead of a downloadable file."))
            }

            val contentLength = response.contentLength() ?: -1L
            if (contentLength == 0L) {
                return@withContext Result.failure(Exception("The file on the server is empty (0 bytes)."))
            }

            val channel = response.bodyAsChannel()
            
            try {
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
                    
                    if (contentLength > 0 && bytesRead < contentLength) {
                        throw Exception("Download incomplete: received $bytesRead of $contentLength bytes.")
                    }
                    
                    if (bytesRead == 0L) {
                        throw Exception("The downloaded file is empty.")
                    }

                    onProgress(DownloadProgress(bytesRead, contentLength, isFinished = true))
                }
            } catch (e: Exception) {
                // Clean up partial file on failure
                try {
                    if (SystemFileSystem.exists(destination)) {
                        SystemFileSystem.delete(destination)
                    }
                } catch (cleanupError: Exception) {
                    // Ignore cleanup errors
                }
                throw e
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
