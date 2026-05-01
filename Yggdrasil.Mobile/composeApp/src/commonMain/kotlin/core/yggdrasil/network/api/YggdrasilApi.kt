package core.yggdrasil.network.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import core.yggdrasil.BuildKonfig

class YggdrasilApi(private val client: HttpClient) {
    suspend fun onlineStatus(): ApiStatus {
        return client.get("${BuildKonfig.API_URL}/status").body()
    }
}
