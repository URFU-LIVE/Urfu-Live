package com.example.urfulive.data.api

import TokenManager
import com.example.urfulive.data.DTOs.AuthResponse
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

    @OptIn(InternalAPI::class)
    suspend fun login(username: String, password: String): Result<AuthResponse> {
        println("Запущен логин")
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
                tokenManager.saveTokens(authResponse.accessToken, authResponse.refreshToken)
                getUserProfile()
                Result.success(authResponse)
            } else {
                println(response.status)
                println("Ошибка")
                Result.failure(Exception("HTTP Error: ${response.status}"))
            }
        } catch (e: Exception) {
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
                val authResponse = Json.decodeFromString<AuthResponse>(response.bodyAsText())
                val tokenManager = TokenManagerInstance.getInstance()
                tokenManager.saveTokens(authResponse.accessToken, authResponse.refreshToken)
                Result.success(authResponse)

            } else {
                println(response.status)
                Result.failure(Exception("HTTP Error: ${response.status}"))
            }
        } catch (e: Exception) {
            println(e.message)
            println(e.stackTrace)
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(): Result<User> {
        return try {
            val tokenManager = TokenManagerInstance.getInstance()
            val response = client.get("$baseUrl/auth/me") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $tokenManager.accessToken")
                }
            }

            if (response.status.isSuccess()) {
                println(response.bodyAsText())
                val user = Json.decodeFromString<User>(response.bodyAsText())
                Result.success(user)
            } else {
                Result.failure(Exception("HTTP Error: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserRole(userId: String, newRole: String, adminToken: String): Result<User> {
        return try {
            val response = client.put("$baseUrl/users/$userId/role") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $adminToken")
                }
                contentType(ContentType.Application.Json)
                setBody(mapOf("role" to newRole))
            }

            if (response.status.isSuccess()) {
                val user = Json.decodeFromString<User>(response.bodyAsText())
                Result.success(user)
            } else {
                Result.failure(Exception("HTTP Error: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}