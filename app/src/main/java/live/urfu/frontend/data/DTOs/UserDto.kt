package live.urfu.frontend.data.DTOs

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Long,
    val username: String,
    val name: String?,
    val surname: String?,
    val email: String,
    val birthDate: List<Int>,
    val role: String,
    val avatar_url: String?,
    val background_url: String?,
    val followers: List<Int> = emptyList(),
    val following: List<Int> = emptyList(),
    val description: String? = null
)