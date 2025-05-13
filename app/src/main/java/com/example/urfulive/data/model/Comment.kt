package com.example.urfulive.data.model

data class Comment(
    val id: Long,
    val text: String,
    val createdAt: String,
    val postId: Long,
    val author: User
)