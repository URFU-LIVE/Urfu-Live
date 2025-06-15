package live.urfu.frontend.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Notification (
    val id: Int,
    val title: String,
    val message: String,
    val time: String,
    val isRead: Boolean = false
)
