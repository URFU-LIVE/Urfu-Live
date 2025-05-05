package com.example.urfulive.data.DTOs

import kotlinx.serialization.Serializable

@Serializable
data class NotificationDto(
    val id: Int,
    val user: UserDto,
    val message: String,
    val createdAt: List<Int>,
    val read: Boolean
)