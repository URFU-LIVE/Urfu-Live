package live.urfu.frontend.data.DTOs

import kotlinx.serialization.Serializable

@Serializable
data class DefaultResponse(
    val message: String,
)