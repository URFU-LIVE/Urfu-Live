package com.example.urfulive.data.api

import TokenManagerInstance
import android.graphics.Bitmap
import com.example.urfulive.data.DTOs.AuthResponse
import com.example.urfulive.data.DTOs.DefaultResponse
import com.example.urfulive.data.DTOs.PostDto
import com.example.urfulive.data.DTOs.RefreshResponse
import com.example.urfulive.data.DTOs.UserDto
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File

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

    // todo надо вынести в одну переменную
    private val baseUrl = "http://45.144.53.244:7070"

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
                // Todo ХУЙНЯ ИСПРАВТЬ!!!!!
                println(response.bodyAsText())
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
                tokenManager.clearTokens()
                tokenManager.saveTokens(authResponse.accessToken, authResponse.refreshToken)
                println(response.bodyAsText())
                Result.success(authResponse)
            } else {
                println(response.bodyAsText())
                Result.failure(Exception("HTTP Error: ${response.status}"))
            }
        } catch (e: Exception) {
            println(e.message)
            Result.failure(e)
        }
    }

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
                println(response.bodyAsText())
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
            val response = client.post("$baseUrl/auth/refresh") {
                headers {
                    append("X-Refresh-token", "$tokenValue")
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

    suspend fun getUserProfileByID(id: Long): Result<UserDto> {
        return try {
            val tokenManager = TokenManagerInstance.getInstance()
            val tokenValue = tokenManager.getAccessTokenBlocking()
            val response = client.get("$baseUrl/users/$id") {
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


    suspend fun getUserPostsByID(id: Long): Result<List<PostDto>> {
        return try {
            val tokenManager = TokenManagerInstance.getInstance()
            val tokenValue = tokenManager.getAccessTokenBlocking()
            val response = client.get("$baseUrl/users/$id/posts") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $tokenValue")
                }
            }

            if (response.status.isSuccess()) {
                // todo Хардкод надо сделать нормальную иницилазацию
                val refreshResponse = Json {ignoreUnknownKeys = true }.decodeFromString<List<PostDto>>(response.bodyAsText());
                Result.success(refreshResponse);
            } else {
                Result.failure(Exception("HTTP Error: ${response.status}"))

            }
        } catch (e: Exception) {
            println(e.message)
            Result.failure(e)
        }
    }

    suspend fun subscribe(id: Long): Result<DefaultResponse> {
        return try {
            val tokenValue = TokenManagerInstance.getInstance().getAccessTokenBlocking()
            val response = client.post("$baseUrl/users/$id/subscribe") {
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
    suspend fun unsubscribe(id: Long): Result<DefaultResponse> {
        return try {
            val tokenValue = TokenManagerInstance.getInstance().getAccessTokenBlocking()
            val response = client.delete("$baseUrl/users/$id/subscribe") {
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

    suspend fun updateAvatar(image: Bitmap): Result<DefaultResponse> {
        return try {
            val tokenValue = TokenManagerInstance.getInstance().getAccessTokenBlocking()
            val file = withContext(Dispatchers.IO) {
                File.createTempFile("avatar", ".jpg")
            }
            file.outputStream().use {
                image.compress(Bitmap.CompressFormat.JPEG, 90, it)
            }

            val response = client.post("$baseUrl/users/me/avatar") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $tokenValue")
                }
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append(
                                "file",
                                file.readBytes(),
                                Headers.build {
                                    append(HttpHeaders.ContentType, "image/jpeg")
                                    append(HttpHeaders.ContentDisposition, "filename=${file.name}")
                                }
                            )
                        }
                    )
                )
            }

            file.delete()

            if (response.status.isSuccess()) {
                val defaultResponse = Json.decodeFromString<DefaultResponse>(response.bodyAsText())
                println(defaultResponse)
                Result.success(defaultResponse)
            } else {
                Result.failure(Exception("HTTP Error: ${response.status}"))
            }
        } catch (e: Exception) {
            println(e.message)
            Result.failure(e)
        }
    }

    suspend fun updateBackground(image: Bitmap): Result<DefaultResponse> {
        return try {
            val tokenValue = TokenManagerInstance.getInstance().getAccessTokenBlocking()
            val file = withContext(Dispatchers.IO) {
                File.createTempFile("avatar", ".jpg")
            }
            file.outputStream().use {
                image.compress(Bitmap.CompressFormat.JPEG, 90, it)
            }

            val response = client.post("$baseUrl/users/me/background") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $tokenValue")
                }
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append(
                                "file",
                                file.readBytes(),
                                Headers.build {
                                    append(HttpHeaders.ContentType, "image/jpeg")
                                    append(HttpHeaders.ContentDisposition, "filename=${file.name}")
                                }
                            )
                        }
                    )
                )
            }

            file.delete()

            if (response.status.isSuccess()) {
                val defaultResponse = Json.decodeFromString<DefaultResponse>(response.bodyAsText())
                println(defaultResponse)
                Result.success(defaultResponse)
            } else {
                println(response.status)
                Result.failure(Exception("HTTP Error: ${response.status}"))
            }
        } catch (e: Exception) {
            println(e.message)
            Result.failure(e)
        }
    }

    suspend fun updateUsername(username: String): Result<DefaultResponse> {
        return try {
            val tokenValue = TokenManagerInstance.getInstance().getAccessTokenBlocking()
            val response = client.patch("$baseUrl/users/me") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $tokenValue")
                }
                contentType(ContentType.Application.Json)
                setBody(mapOf(
                    "username" to username,
                ))
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

    suspend fun updateDescription(description: String): Result<DefaultResponse> {
        return try {
            val tokenValue = TokenManagerInstance.getInstance().getAccessTokenBlocking()
            val response = client.patch("$baseUrl/users/me") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $tokenValue")
                }
                contentType(ContentType.Application.Json)
                setBody(mapOf(
                    "description" to description,
                ))
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

    suspend fun logout(): Result<DefaultResponse> {
        return try {
            val tokenManager = TokenManagerInstance.getInstance()
            val tokenValue = tokenManager.getAccessTokenBlocking()

            // Делаем запрос на сервер для инвалидации токена
            val response = client.post("$baseUrl/auth/logout") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $tokenValue")
                }
            }

            // Очищаем локальные токены
            tokenManager.clearTokens()

            if (response.status.isSuccess()) {
                val defaultResponse = Json.decodeFromString<DefaultResponse>(response.bodyAsText())
                Result.success(defaultResponse)
            } else {
                // Даже при ошибке считаем выход успешным, так как токены очищены
                Result.success(DefaultResponse("Logged out successfully"))
            }
        } catch (e: Exception) {
            // При ошибке соединения тоже очищаем токены
            TokenManagerInstance.getInstance().clearTokens()
            println("Logout error: ${e.message}")
            Result.success(DefaultResponse("Logged out successfully"))
        }
    }
}
