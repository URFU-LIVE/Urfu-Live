package live.urfu.frontend.data.DTOs

import kotlinx.serialization.Serializable

@Serializable
data class CheckerResponse (
    val available: Boolean,
    val message: String
)