package core.yggdrasil.data

import core.yggdrasil.network.api.ApiFile
import core.yggdrasil.network.api.ApiStatus
import core.yggdrasil.network.api.YggdrasilApi
import io.ktor.client.statement.HttpStatement

class ApiRepository(private val api: YggdrasilApi) {
    suspend fun getOnlineStatus(): ApiStatus = api.onlineStatus()

    suspend fun listFiles(
        search: String? = null,
        fileType: String? = null,
        minCreated: String? = null
    ): List<ApiFile> = api.listFiles(search, fileType, minCreated)

    suspend fun addFile(filePath: String): ApiFile = api.addFile(filePath)

    suspend fun getFile(id: Int): ApiFile = api.getFile(id)

    suspend fun deleteFile(id: Int) = api.deleteFile(id)

    suspend fun downloadFiles(ids: List<Int>): HttpStatement = api.downloadFiles(ids)
}
