package live.urfu.frontend.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Tag (
    val id: Long,
    val name: String
)