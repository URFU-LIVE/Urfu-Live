package live.urfu.frontend.data.DTOs

import kotlinx.serialization.Serializable

@Serializable
data class TagDto(
    val id: Long,
    val name: String
)
