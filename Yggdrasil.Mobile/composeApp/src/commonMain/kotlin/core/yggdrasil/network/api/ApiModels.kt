package core.yggdrasil.network.api

import kotlinx.serialization.Serializable

@Serializable
data class ApiStatus(
    val status: String? = null,
    val message: String? = null,
    val timestamp: String? = null
)

@Serializable
data class ApiFile(
    val id: Int,
    val name: String,
    val filePath: String,
    val fileType: String? = null,
    val timeCreated: String? = null
)

@Serializable
data class AddFileRequest(
    val filePath: String
)

@Serializable
data class DownloadRequest(
    val ids: List<Int>
)

@Serializable
data class DownloadErrorResponse(
    val error: String,
    val missingFiles: List<ApiFile> = emptyList()
)