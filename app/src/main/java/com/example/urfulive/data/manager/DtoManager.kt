package com.example.urfulive.data.manager

import com.example.urfulive.data.DTOs.PostDto
import com.example.urfulive.data.DTOs.UserDto
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

    fun UserDto.toUser(): User {
        // Convert birthDate List<Int> to a formatted string (DD.MM.YYYY format)
        val dateString = try {
            val year = birthDate.getOrNull(0) ?: 0
            val month = birthDate.getOrNull(1) ?: 0
            val day = birthDate.getOrNull(2) ?: 0
            "%02d.%02d.%04d".format(day, month, year)
        } catch (e: Exception) {
            "N/A"
        }

        // Convert role string to UserRole enum
        val userRole = when (role) {
            "WRITER" -> UserRole.WRITER
            "ADMIN" -> UserRole.ADMIN
            else -> UserRole.USER
        }

        // Get counts instead of full lists
        val followersCount = followers.size
        val followingCount = following.size

        return User(
            id = this.id.toString(),
            username = this.username,
            name = this.name,
            surname = this.surname,
            email = this.email,
            birthDate = dateString,
            role = userRole,
            followersCount = followersCount,
            followingCount = followingCount,
            description = this.description
        )
    }
}