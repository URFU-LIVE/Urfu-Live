package com.example.urfulive.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Post (
    val id: Long,
    val title: String,
    val text: String,
    val author: User,
    val tags: List<Tag>,
    val time: String,
    val comments: Int,
    var likes: Int
)
