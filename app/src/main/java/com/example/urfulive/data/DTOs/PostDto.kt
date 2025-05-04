package com.example.urfulive.data.DTOs

import kotlinx.serialization.Serializable

@Serializable
data class PostDto(
    val id: Long,
    val title: String? = null,
    val text: String,
    val author: UserDto,
    val createdAt: List<Int>,
    val tags: List<TagDto>,
    val likedBy: List<UserIdDto>,
    val comments: List<String>
)
