package com.example.urfulive.data.manager

import com.example.urfulive.data.DTOs.PostDto
import com.example.urfulive.data.model.Post
import com.example.urfulive.data.model.Tag
import com.example.urfulive.data.model.User
import com.example.urfulive.data.model.UserRole

class DtoManager {
    fun PostDto.toPost(): Post {
        val formattedTime = try {
            val year = createdAt.getOrNull(0) ?: 0
            val month = createdAt.getOrNull(1) ?: 0
            val day = createdAt.getOrNull(2) ?: 0
            val hour = createdAt.getOrNull(3) ?: 0
            val minute = createdAt.getOrNull(4) ?: 0
            val second = createdAt.getOrNull(5) ?: 0
            "%02d.%02d.%04d %02d:%02d:%02d".format(day, month, year, hour, minute, second)
        } catch (e: Exception) {
            "N/A"
        }

        return Post(
            id = this.id,
            title = this.title ?: "",
            text = this.text,
            author = User(
                id = author.id.toString(),
                username = author.username,
                email = author.email,
                role = when (author.role) {
                    "WRITER" -> UserRole.WRITER
                    "ADMIN" -> UserRole.ADMIN
                    else -> UserRole.USER
                }
            ),
            tags = this.tags.map { Tag(it.id, it.name) },
            time = formattedTime,
            comments = this.comments.size,
            likes = this.likedBy.size
        )
    }

}