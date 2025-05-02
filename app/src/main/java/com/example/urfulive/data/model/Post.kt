package com.example.urfulive.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Post (
    val id: Long,
    val title: String,
    val text: String,
    val author: User,
    val tags: List<Tag>
)
