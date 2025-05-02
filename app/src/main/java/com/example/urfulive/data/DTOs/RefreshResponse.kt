package com.example.urfulive.data.DTOs

import kotlinx.serialization.Serializable

@Serializable
data class RefreshResponse (
    val refreshToken: String
)