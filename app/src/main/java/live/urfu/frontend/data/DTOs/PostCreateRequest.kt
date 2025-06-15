package live.urfu.frontend.data.DTOs

import kotlinx.serialization.Serializable

@Serializable
data class PostCreateRequest(
    var title: String,
    val text: String,
    val tags: List<String>
)