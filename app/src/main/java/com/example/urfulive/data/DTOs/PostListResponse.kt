package com.example.urfulive.data.DTOs

import com.example.urfulive.data.model.Post
import kotlinx.serialization.Serializable

@Serializable
data class PostListResponse (
    val posts: Post
)