package live.urfu.frontend.data.manager

import live.urfu.frontend.data.DTOs.CommentDto
import live.urfu.frontend.data.DTOs.NotificationDto
import live.urfu.frontend.data.DTOs.PostDto
import live.urfu.frontend.data.DTOs.UserDto
import live.urfu.frontend.data.model.Comment
import live.urfu.frontend.data.model.Notification
import live.urfu.frontend.data.model.Post
import live.urfu.frontend.data.model.Tag
import live.urfu.frontend.data.model.User
import live.urfu.frontend.data.model.UserRole

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
                },
                followers = author.followers,
                // todo Хардкод в проде надо поменять
                avatarUrl = author.avatar_url?.replace("localhost", "45.144.53.244"),
                backgroundUrl = author.background_url?.replace("localhost", "45.144.53.244")
            ),
            tags = this.tags.map { Tag(it.id, it.name) },
            time = formattedTime,
            comments = this.comments.size,
            likes = this.likedBy.size,
            likedBy = this.likedBy
        )
    }

    fun UserDto.toUser(): User {
        val dateString = try {
            val year = birthDate.getOrNull(0) ?: 0
            val month = birthDate.getOrNull(1) ?: 0
            val day = birthDate.getOrNull(2) ?: 0
            "%02d.%02d.%04d".format(day, month, year)
        } catch (e: Exception) {
            "N/A"
        }

        val userRole = when (role) {
            "MODERATOR" -> UserRole.MODER
            "WRITER" -> UserRole.WRITER
            "ADMIN" -> UserRole.ADMIN
            else -> UserRole.USER
        }

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
            description = this.description,
            followers = this.followers,
            avatarUrl = this.avatar_url?.replace("localhost", "45.144.53.244"),
            backgroundUrl = this.background_url?.replace("localhost", "45.144.53.244")
        )
    }

    fun NotificationDto.toNotification(): Notification {
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

        return Notification(
            id = this.id,
            title = "${user.name} ${user.surname}",
            message = this.message,
            time = formattedTime,
            isRead = this.read
        )
    }

    fun CommentDto.toComment(): Comment {
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

        return Comment(
            id = this.id,
            text = this.text,
            createdAt = formattedTime,
            postId = this.post_id,
            author = User(
                id = this.userDto.id.toString(),
                username = this.userDto.username,
                email = this.userDto.email,
                role = when (this.userDto.role) {
                    "WRITER" -> UserRole.WRITER
                    "ADMIN" -> UserRole.ADMIN
                    else -> UserRole.USER
                },
                followers = this.userDto.followers,
                // todo Хардкод в проде надо поменять
                avatarUrl = this.userDto.avatar_url?.replace("localhost", "45.144.53.244"),
                backgroundUrl = this.userDto.background_url?.replace("localhost", "45.144.53.244")
            )
        )
    }
}
