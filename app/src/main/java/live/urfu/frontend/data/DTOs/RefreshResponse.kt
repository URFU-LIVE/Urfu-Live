package live.urfu.frontend.data.DTOs

import kotlinx.serialization.Serializable

@Serializable
data class RefreshResponse (
    val accessToken: String
)