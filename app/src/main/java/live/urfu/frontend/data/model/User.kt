package live.urfu.frontend.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val username: String,
    val name: String? = null,
    val surname: String? = null,
    val email: String,
    val birthDate: String? = null,
    val role: UserRole,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val description: String? = null,
    val token: String? = null,
    val followers: List<Int>,
    val avatarUrl: String?,
    val backgroundUrl: String?
)

enum class UserRole {
    USER,
    WRITER,
    MODER,
    ADMIN
}