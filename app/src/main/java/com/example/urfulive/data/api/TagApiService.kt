package com.example.urfulive.data.api

import com.example.urfulive.data.DTOs.DefaultResponse
import com.example.urfulive.data.DTOs.PostCreateRequest
import com.example.urfulive.data.DTOs.TagListResponse
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class TagApiService {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = false
            })
        }
    }

    private val baseUrl = "http://10.0.2.2:7070" // Замените на URL вашего бэкенда

    suspend fun create(name: String): Result<DefaultResponse> {
        return try {
            val tokenValue = TokenManagerInstance.getInstance().getAccessTokenBlocking()

            val response = client.post("$baseUrl/admin/tags") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $tokenValue")
                }
            }

            if (response.status.isSuccess()) {
                val defaultResponse = Json.decodeFromString<DefaultResponse>(response.bodyAsText())
                Result.success(defaultResponse)
            } else {
                Result.failure(Exception("HTTP Error: ${response.status}"))
            }
        } catch (e: Exception) {
            println(e.message)
            Result.failure(e)
        }
    }

    suspend fun delete(name: String): Result<DefaultResponse> {
        return try {
            val tokenValue = TokenManagerInstance.getInstance().getAccessTokenBlocking()

            val response = client.delete("$baseUrl/admin/tags") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $tokenValue")
                }
            }

            if (response.status.isSuccess()) {
                val defaultResponse = Json.decodeFromString<DefaultResponse>(response.bodyAsText())
                Result.success(defaultResponse)
            } else {
                Result.failure(Exception("HTTP Error: ${response.status}"))
            }
        } catch (e: Exception) {
            println(e.message)
            Result.failure(e)
        }
    }

    suspend fun getAll(name: String): Result<TagListResponse> {
        return try {
            val tokenValue = TokenManagerInstance.getInstance().getAccessTokenBlocking()

            val response = client.delete("$baseUrl/admin/tags") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $tokenValue")
                }
            }

            if (response.status.isSuccess()) {
                val defaultResponse = Json.decodeFromString<TagListResponse>(response.bodyAsText())
                Result.success(defaultResponse)
            } else {
                Result.failure(Exception("HTTP Error: ${response.status}"))
            }
        } catch (e: Exception) {
            println(e.message)
            Result.failure(e)
        }
    }
}