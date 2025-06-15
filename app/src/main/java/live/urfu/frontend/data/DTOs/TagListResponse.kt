package live.urfu.frontend.data.DTOs

import live.urfu.frontend.data.model.Tag
import kotlinx.serialization.Serializable

@Serializable
data class TagListResponse (
    val tags: List<Tag>
)