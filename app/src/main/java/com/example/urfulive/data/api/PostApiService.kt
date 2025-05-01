package com.example.urfulive.data.api

import com.example.urfulive.data.DTOs.AuthResponse
import com.example.urfulive.data.DTOs.DefaultResponse
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
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

class PostApiService {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = false
            })
        }
    }

    private val baseUrl = "http://10.0.2.2:7070"

    suspend fun create(title: String, text: String): Result<DefaultResponse> {
        return try {
            println("Начинаем")
            val tokenManager = TokenManagerInstance.getInstance()
            println("Между")
            val tokenValue = tokenManager.getAccessTokenBlocking()
            val arr = IntArray(1) { 1 }
            println(tokenValue)
            val response = client.post("$baseUrl/posts") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $tokenValue")
                }
                contentType(ContentType.Application.Json)
                setBody(
                    mapOf(
                        "title" to title,
                        "text" to text,
                        "tagIds" to arr
                    )
                )
            }

            if (response.status.isSuccess()) {
                println("Success")
                println(response.bodyAsText())
                val defaultResponse = Json.decodeFromString<DefaultResponse>(response.bodyAsText())
                Result.success(defaultResponse)
            } else {
                println(response.status)
                println("Ошибка")
                Result.failure(Exception("HTTP Error: ${response.status}"))
            }
        } catch (e: Exception) {
            print(e.message)
            Result.failure(e)
        }
    }
}