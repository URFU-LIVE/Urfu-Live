package com.example.urfulive.data.api

import com.example.urfulive.data.DTOs.CommentDto
import com.example.urfulive.data.DTOs.DefaultResponse
import com.example.urfulive.data.DTOs.NotificationDto
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class CommentApiService {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = false
            })
        }
    }

    // todo надо вынести в одну переменную
    private val baseUrl = "http://45.144.53.244:7070"

    suspend fun create(id: Long, text: String): Result<CommentDto> {
        return try {
            val tokenValue = TokenManagerInstance.getInstance().getAccessTokenBlocking()

            val response = client.post("$baseUrl/posts/$id/comments") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $tokenValue")
                }
                contentType(ContentType.Application.Json)
                setBody(
                    mapOf(
                        "text" to text
                    )
                )
            }

            if (response.status.isSuccess()) {
                println("Успешно")
                val notificationList = Json.decodeFromString<CommentDto>(response.bodyAsText())
                Result.success(notificationList)
            } else {
                println("Ошибка")
                Result.failure(Exception("HTTP Error: ${response.status}"))
            }
        } catch (e: Exception) {
            println(e.message)
            Result.failure(e)
        }
    }

    suspend fun getAll(id: Long): Result<List<CommentDto>> {
        return try {
            val tokenValue = TokenManagerInstance.getInstance().getAccessTokenBlocking()

            val response = client.get("$baseUrl/posts/$id/comments") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $tokenValue")
                }
            }

            if (response.status.isSuccess()) {
                val notificationList = Json.decodeFromString<List<CommentDto>>(response.bodyAsText())
                Result.success(notificationList)
            } else {
                Result.failure(Exception("HTTP Error: ${response.status}"))
            }
        } catch (e: Exception) {
            println(e.message)
            Result.failure(e)
        }
    }
}