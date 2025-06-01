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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File

class UserApiService: BaseApiService() {

    suspend fun login(username: String, password: String): Result<AuthResponse> {
        return authorizedRequest<AuthResponse> {
            client.post("$baseUrl/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("username" to username, "password" to password))
            }
        }.onSuccess {
            clearTokens()
            saveTokens(it.accessToken, it.refreshToken)
        }
    }

    suspend fun register(username: String, email: String, password: String, name: String, surname: String, birthDate: String): Result<AuthResponse> {
        return authorizedRequest<AuthResponse> {
            client.post("$baseUrl/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(
                    mapOf(
                        "username" to username,
                        "email" to email,
                        "password" to password,
                        "name" to name,
                        "surname" to surname,
                        "birthDate" to birthDate
                    )
                )
            }
        }.onSuccess {
            clearTokens()
            saveTokens(it.accessToken, it.refreshToken)
        }
    }

    suspend fun getUserProfile(): Result<UserDto> {
        val token = getToken()
        return authorizedRequest {
            client.get("$baseUrl/auth/me") {
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }
        }
    }

    suspend fun refreshToken(): Result<RefreshResponse> {
        val refresh = getRefreshToken()
        return authorizedRequest<RefreshResponse> {
            client.post("$baseUrl/auth/refresh") {
                headers { append("X-Refresh-token", refresh.toString()) }
            }
        }.onSuccess {
            saveAccess(it.accessToken)
        }
    }

    suspend fun getUserProfileByID(id: Long): Result<UserDto> {
        val token = getToken()
        return authorizedRequest {
            client.get("$baseUrl/users/$id") {
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }
        }
    }

    suspend fun getUserPostsByID(id: Long): Result<List<PostDto>> {
        val token = getToken()
        return authorizedRequest {
            client.get("$baseUrl/users/$id/posts") {
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }
        }
    }

    suspend fun subscribe(id: Long): Result<DefaultResponse> {
        val token = getToken()
        return authorizedRequest {
            client.post("$baseUrl/users/$id/subscribe") {
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }
        }
    }

    suspend fun unsubscribe(id: Long): Result<DefaultResponse> {
        val token = getToken()
        return authorizedRequest {
            client.delete("$baseUrl/users/$id/subscribe") {
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }
        }
    }

    suspend fun updateAvatar(image: Bitmap): Result<DefaultResponse> {
        val token = getToken()

        val file = withContext(Dispatchers.IO) {
            File.createTempFile("avatar", ".jpg")
        }
        file.outputStream().use {
            image.compress(Bitmap.CompressFormat.JPEG, 90, it)
        }

        return authorizedRequest<DefaultResponse> {
            client.post("$baseUrl/users/me/avatar") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
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
        }.also { file.delete() }
    }

    suspend fun updateBackground(image: Bitmap): Result<DefaultResponse> {
        val token = getToken()

        val file = withContext(Dispatchers.IO) {
            File.createTempFile("avatar", ".jpg")
        }
        file.outputStream().use {
            image.compress(Bitmap.CompressFormat.JPEG, 90, it)
        }
        return authorizedRequest<DefaultResponse> {
            client.post("$baseUrl/users/me/background") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
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
        }.also { file.delete() }
    }

    suspend fun updateUsername(username: String): Result<DefaultResponse> {
        val token = getToken()
        return authorizedRequest {
            client.patch("$baseUrl/users/me") {
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
                contentType(ContentType.Application.Json)
                setBody(mapOf("username" to username))
            }
        }
    }

    suspend fun updateDescription(description: String): Result<DefaultResponse> {
        val token = getToken()
        return authorizedRequest {
            client.patch("$baseUrl/users/me") {
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
                contentType(ContentType.Application.Json)
                setBody(mapOf("description" to description))
            }
        }
    }
}
