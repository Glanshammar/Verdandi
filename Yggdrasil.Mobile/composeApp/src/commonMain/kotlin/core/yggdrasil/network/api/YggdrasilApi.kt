package core.yggdrasil.network.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.HttpStatement
import io.ktor.http.*
import core.yggdrasil.BuildKonfig
import io.ktor.client.statement.bodyAsText

object ApiConfig {
    val URL = BuildKonfig.API_URL.trim('"') + "/api"
}

class YggdrasilApi(private val client: HttpClient) {
    suspend fun onlineStatus(): ApiStatus {
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

    suspend fun downloadFiles(ids: List<Int>): HttpStatement {
        return if (ids.size == 1) {
            client.prepareGet("${ApiConfig.URL}/files/${ids[0]}/download")
        } else {
            client.preparePost("${ApiConfig.URL}/files/download") {
                contentType(ContentType.Application.Json)
                setBody(DownloadRequest(ids))
            }
        }
    }
}
