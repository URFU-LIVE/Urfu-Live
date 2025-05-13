package com.example.urfulive.data.DTOs

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentDto(
    val id: Long,
    val text: String,
    val createdAt: List<Int>,
    val post_id: Long,

    @SerialName("author")
    val userDto: UserDto
)
