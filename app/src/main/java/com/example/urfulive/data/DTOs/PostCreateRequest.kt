package com.example.urfulive.data.DTOs

import kotlinx.serialization.Serializable

@Serializable
data class PostCreateRequest(
    var title: String,
    val text: String,
    val tagIds: List<Int>
)