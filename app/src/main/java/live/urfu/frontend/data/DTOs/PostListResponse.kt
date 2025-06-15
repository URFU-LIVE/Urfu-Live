package live.urfu.frontend.data.DTOs

import live.urfu.frontend.data.model.Post
import kotlinx.serialization.Serializable

@Serializable
data class PostListResponse (
    val posts: Post
)