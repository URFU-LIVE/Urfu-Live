package com.example.urfulive.data.DTOs

import com.example.urfulive.data.model.Tag
import kotlinx.serialization.Serializable

@Serializable
data class TagDto(
    val id: Long,
    val name: String
)

fun TagDto.toModel(): Tag = Tag(
    id = this.id,
    name = this.name
)