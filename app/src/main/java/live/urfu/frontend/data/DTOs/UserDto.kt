package live.urfu.frontend.data.DTOs

import kotlinx.serialization.SerialName
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
    @SerialName("avatar_url")
    val avatarUrl: String? = null,
    @SerialName("background_url")
    val backgroundUrl: String? = null,
    val followers: List<Int> = emptyList(),
    val following: List<Int> = emptyList(),
    val description: String? = null
)
