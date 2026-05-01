package core.yggdrasil.network.api

import kotlinx.serialization.Serializable

@Serializable
data class ApiStatus(
    val status: String? = null,
    val message: String? = null,
    val timestamp: String? = null
)
