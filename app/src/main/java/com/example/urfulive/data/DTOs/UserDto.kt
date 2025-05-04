package com.example.urfulive.data.DTOs

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Long,
    val username: String,
    val name: String?,
    val surname: String?,
    val email: String,
    val birthDate: List<Int>,
    val role: String,
    val description: String? = null
)
