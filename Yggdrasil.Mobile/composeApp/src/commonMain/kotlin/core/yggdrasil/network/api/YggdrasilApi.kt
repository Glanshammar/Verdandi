package core.yggdrasil.network.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import core.yggdrasil.BuildKonfig

class YggdrasilApi(private val client: HttpClient) {
    suspend fun onlineStatus(): ApiStatus {
        return client.get("${BuildKonfig.API_URL}/status").body()
        val response = client.get("${ApiConfig.URL}/status")
        println(response.status)
        println(response.bodyAsText())
        return response.body()
    }

    suspend fun listFiles(
        search: String? = null,
        fileType: String? = null,
        minCreated: String? = null
    ): List<ApiFile> {
        return client.get("${ApiConfig.URL}/files") {
            search?.let { parameter("search", it) }
            fileType?.let { parameter("fileType", it) }
            minCreated?.let { parameter("minCreated", it) }
        }.body()
    }

    suspend fun addFile(filePath: String): ApiFile {
        return client.post("${ApiConfig.URL}/files") {
            contentType(ContentType.Application.Json)
            setBody(AddFileRequest(filePath))
        }.body()
    }

    suspend fun getFile(id: Int): ApiFile {
        return client.get("${ApiConfig.URL}/files/$id").body()
    }

    suspend fun deleteFile(id: Int) {
        client.delete("${ApiConfig.URL}/files/$id")
    }
    }
}
