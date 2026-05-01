package core.yggdrasil.data

import core.yggdrasil.network.api.ApiStatus
import core.yggdrasil.network.api.YggdrasilApi

class ApiRepository(private val api: YggdrasilApi) {
    suspend fun getOnlineStatus(): ApiStatus {
        return api.onlineStatus()
    }
}
