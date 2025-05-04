package com.example.urfulive.data.api

import TokenManagerInstance
import com.example.urfulive.data.DTOs.DefaultResponse
import com.example.urfulive.data.DTOs.PostCreateRequest
import com.example.urfulive.data.DTOs.PostDto
import com.example.urfulive.data.DTOs.PostListResponse
import com.example.urfulive.data.model.Post
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
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
            val tokenValue = TokenManagerInstance.getInstance().getAccessTokenBlocking()
            val requestJson = PostCreateRequest(title, text, listOf(1))

            val response = client.post("$baseUrl/posts") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $tokenValue")
                }
                contentType(ContentType.Application.Json)
                setBody(requestJson)
            }

            if (response.status.isSuccess()) {
                val defaultResponse = Json.decodeFromString<DefaultResponse>(response.bodyAsText())
                Result.success(defaultResponse)
            } else {
                Result.failure(Exception("HTTP Error: ${response.status}"))
            }
        } catch (e: Exception) {
            print(e.message)
            Result.failure(e)
        }
    }

    suspend fun getAll(): Result<List<PostDto>> {
        return try {
            val tokenValue = TokenManagerInstance.getInstance().getAccessTokenBlocking()
            val response = client.get("$baseUrl/posts") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $tokenValue")
                }
            }

            if (response.status.isSuccess()) {
                val json = Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    prettyPrint = false
                }
                println(response.bodyAsText())
                val postListResponse = json.decodeFromString<List<PostDto>>(response.bodyAsText())
                Result.success(postListResponse)
            } else {
                Result.failure(Exception("HTTP Error: ${response.status}"))
            }
        } catch (e: Exception) {
            print(e.message)
            Result.failure(e)
        }
    }

    suspend fun like(id: Long): Result<DefaultResponse> {
        return try {
            val tokenValue = TokenManagerInstance.getInstance().getAccessTokenBlocking()
            val response = client.post("$baseUrl/posts/$id/like") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $tokenValue")
                }
            }

            if (response.status.isSuccess()) {
                val defaultResponse = Json.decodeFromString<DefaultResponse>(response.bodyAsText())
                println(response.bodyAsText())
                Result.success(defaultResponse)
            } else {
                Result.failure(Exception("HTTP Error: ${response.status}"))
            }
        } catch (e: Exception) {
            print(e.message)
            Result.failure(e)
        }
    }

    suspend fun dislike(id: Long): Result<DefaultResponse> {
        return try {
            val tokenValue = TokenManagerInstance.getInstance().getAccessTokenBlocking()
            val response = client.delete("$baseUrl/posts/$id/dislike") {
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
            print(e.message)
            Result.failure(e)
        }
    }
}