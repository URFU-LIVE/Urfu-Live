package live.urfu.frontend.data.DTOs

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentDto(
    val id: Long,
    val text: String,
    val createdAt: List<Int>,
    @SerialName("post_id")
    val postId: Long,

    @SerialName("author")
    val userDto: UserDto
)
