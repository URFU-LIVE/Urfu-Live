package com.example.urfulive.data.DTOs

import com.example.urfulive.data.model.Tag
import kotlinx.serialization.Serializable

@Serializable
data class TagListResponse (
    val tags: List<Tag>
)