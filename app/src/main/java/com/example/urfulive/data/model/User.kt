package com.example.urfulive.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val username: String,
    val email: String,
    val role: UserRole,
    val token: String? = null
)

enum class UserRole {
    USER,
    WRITER,
    ADMIN
}