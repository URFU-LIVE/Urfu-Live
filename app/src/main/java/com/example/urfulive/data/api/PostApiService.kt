package com.example.urfulive.data.api

import TokenManagerInstance
import com.example.urfulive.data.DTOs.DefaultResponse
import com.example.urfulive.data.DTOs.PostCreateRequest
import com.example.urfulive.data.DTOs.PostDto
import com.example.urfulive.data.manager.InterestManager
import com.example.urfulive.data.manager.InterestManagerInstance
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
import io.ktor.http.HttpStatusCode
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

    // todo надо вынести в одну переменную
    private val baseUrl = "http://45.144.53.244:7070"

    suspend fun create(title: String, text: String, tags: List<String>): Result<DefaultResponse> {
        return try {
            val tokenValue = TokenManagerInstance.getInstance().getAccessTokenBlocking()
            val requestJson = PostCreateRequest(title, text, tags)

            val response = client.post("$baseUrl/posts") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $tokenValue")
                }
                contentType(ContentType.Application.Json)
                setBody(requestJson)
            }

            if (response.status.isSuccess()) {
                println(response.bodyAsText())
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

    suspend fun getAll(): Result<List<PostDto>> {
        return try {
            val tokenValue = TokenManagerInstance.getInstance().getAccessTokenBlocking()
            val response = client.get("$baseUrl/posts") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $tokenValue")
                }
            }

            if (response.status.isSuccess()) {
                // todo Хардкод надо сделать нормальную иницилазацию
                val json = Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    prettyPrint = false
                }
                val postListResponse = json.decodeFromString<List<PostDto>>(response.bodyAsText())
                println(response.bodyAsText())
                Result.success(postListResponse)
            } else {
                Result.failure(Exception("HTTP Error: ${response.status}"))
            }
        } catch (e: Exception) {
            println(e.message)
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
                Result.success(defaultResponse)
            } else {
                Result.failure(Exception("HTTP Error: ${response.status}"))
            }
        } catch (e: Exception) {
            println(e.message)
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
            println(e.message)
            Result.failure(e)
        }
    }

    suspend fun getRecommendation(page: Int): Result<List<PostDto>> {
        return try {
            val tokenValue = TokenManagerInstance.getInstance().getAccessTokenBlocking()
            val interests = InterestManagerInstance.getInstance().getSelectedInterestsBlocking()
            val interestsParam = interests.joinToString(",")
            println(interestsParam)

            val response = client.get("$baseUrl/posts/recommended") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $tokenValue")
                }

                url {
                    parameters.append("categories", interestsParam)
                    parameters.append("size", "10")
                    parameters.append("page", page.toString())
                }
            }

            if (response.status.isSuccess()) {
                val json = Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    prettyPrint = false
                }
                val posts = json.decodeFromString<List<PostDto>>(response.bodyAsText())
                Result.success(posts)
            } else {
                println(response.bodyAsText())
                Result.failure(Exception("HTTP Error: ${response.status}"))
            }
        } catch (e: Exception) {
            println(e.message)
            Result.failure(e)
        }
    }
}