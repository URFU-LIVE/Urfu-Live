package com.example.urfulive.data.DTOs

import kotlinx.serialization.Serializable

@Serializable
data class TagDto(
    val id: Long,
    val name: String
)
