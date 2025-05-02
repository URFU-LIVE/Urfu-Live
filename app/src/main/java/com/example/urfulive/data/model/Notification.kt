package com.example.urfulive.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Notification (
    val id: Int,
    val title: String,
    val message: String,
    val time: String,
    val isRead: Boolean = false
)

val Notifications = listOf(
    Notification(
        id = 1,
        title = "Ваша заявка принята",
        message = "Ваша заявка на участие в мероприятии была одобрена",
        time = "10 мин назад",
        isRead = false
    ),
    Notification(
        id = 2,
        title = "Новый подарок",
        message = "Вам доступен новый бонус за активность",
        time = "1 час назад",
        isRead = true
    ),
)