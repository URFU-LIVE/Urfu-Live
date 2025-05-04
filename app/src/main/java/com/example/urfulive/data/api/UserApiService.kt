package com.example.urfulive.data.api

import TokenManager
import com.example.urfulive.data.DTOs.AuthResponse
import com.example.urfulive.data.DTOs.PostDto
import com.example.urfulive.data.DTOs.RefreshResponse
import com.example.urfulive.data.DTOs.UserDto
import com.example.urfulive.data.model.User
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.InternalAPI
import kotlinx.serialization.json.Json

class UserApiService {
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

    suspend fun login(username: String, password: String): Result<AuthResponse> {
        return try {
            val response = client.post("$baseUrl/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(mapOf(
                    "username" to username,
                    "password" to password
                ))
            }

            if (response.status.isSuccess()) {
                val authResponse = Json.decodeFromString<AuthResponse>(response.bodyAsText())
                val tokenManager = TokenManagerInstance.getInstance()
                tokenManager.clearTokens()
                tokenManager.saveTokens(authResponse.accessToken, authResponse.refreshToken)
                println(authResponse)
                Result.success(authResponse)
            } else {
                Result.failure(Exception("HTTP Error: ${response.status}"))
            }
        } catch (e: Exception) {
            println(e.message)
            Result.failure(e)
        }
    }

    suspend fun register(username: String, email: String, password: String, name: String, surname: String, birthDate: String): Result<AuthResponse> {
        return try {
            val response = client.post("$baseUrl/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(mapOf(
                    "username" to username,
                    "email" to email,
                    "name" to name,
                    "surname" to surname,
                    "password" to password,
                    "birthDate" to birthDate,
                ))
            }

            if (response.status.isSuccess()) {
                println(response.bodyAsText())
                val authResponse = Json.decodeFromString<AuthResponse>(response.bodyAsText())
                val tokenManager = TokenManagerInstance.getInstance()
                tokenManager.clearTokens()
                tokenManager.saveTokens(authResponse.accessToken, authResponse.refreshToken)
                Result.success(authResponse)
            } else {
                Result.failure(Exception("HTTP Error: ${response.status}"))
            }
        } catch (e: Exception) {
            println(e.message)
            Result.failure(e)
        }
    }

    // todo Данный метод уже полностью рабочий.
    suspend fun getUserProfile(): Result<UserDto> {
        return try {
            val tokenManager = TokenManagerInstance.getInstance()
            val tokenValue = tokenManager.getAccessTokenBlocking()
            val response = client.get("$baseUrl/auth/me") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $tokenValue")
                }
            }

            if (response.status.isSuccess()) {
                val user = Json.decodeFromString<UserDto>(response.bodyAsText())
                Result.success(user)
            } else {
                Result.failure(Exception("HTTP Error: ${response.status}"))
            }
        } catch (e: Exception) {
            println(e.message)
            Result.failure(e)
        }
    }

    suspend fun refreshToken(): Result<RefreshResponse> {
        return try {
            val tokenManager = TokenManagerInstance.getInstance()
            val tokenValue = tokenManager.getRefreshTokenBlocking()
            val response = client.get("$baseUrl/auth/me") {
                headers {
                    append("X-Resfresh-token", "$tokenValue")
                }
            }

            if (response.status.isSuccess()) {
                val refreshResponse = Json.decodeFromString<RefreshResponse>(response.bodyAsText());
                Result.success(refreshResponse);
            } else {
                Result.failure(Exception("HTTP Error: ${response.status}"))
            }
        } catch (e: Exception) {
            println(e.message)
            Result.failure(e)
        }
    }

    suspend fun getUserPosts(id: Long): Result<List<PostDto>> {
        return try {
            val tokenManager = TokenManagerInstance.getInstance()
            val tokenValue = tokenManager.getAccessTokenBlocking()
            val response = client.get("$baseUrl/user/$id/posts") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $tokenValue")
                }
            }

            if (response.status.isSuccess()) {
                val refreshResponse = Json.decodeFromString<List<PostDto>>(response.bodyAsText());
                Result.success(refreshResponse);
            } else {
                Result.failure(Exception("HTTP Error: ${response.status}"))
            }
        } catch (e: Exception) {
            println(e.message)
            Result.failure(e)
        }
    }
}
