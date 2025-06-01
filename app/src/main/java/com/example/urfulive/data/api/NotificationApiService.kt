package com.example.urfulive.data.api

import com.example.urfulive.data.DTOs.NotificationDto
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class NotificationApiService : BaseApiService() {

    suspend fun getAll(): Result<List<NotificationDto>> {
        val token = getToken()
        return authorizedRequest {
            client.get("$baseUrl/notifications") {
                headers { append(HttpHeaders.Authorization, "Bearer $token")}
            }
        }
    }
}