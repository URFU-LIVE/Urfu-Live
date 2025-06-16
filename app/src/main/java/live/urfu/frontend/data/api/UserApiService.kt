package live.urfu.frontend.data.api

import android.graphics.Bitmap
import live.urfu.frontend.data.DTOs.AuthResponse
import live.urfu.frontend.data.DTOs.DefaultResponse
import live.urfu.frontend.data.DTOs.PostDto
import live.urfu.frontend.data.DTOs.RefreshResponse
import live.urfu.frontend.data.DTOs.UserDto
import io.ktor.client.request.delete
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import live.urfu.frontend.data.manager.TokenManagerInstance
import java.io.File

class UserApiService: BaseApiService() {

    suspend fun login(username: String, password: String): Result<AuthResponse> {
        return authorizedRequest<AuthResponse> {
            client.post("$baseUrl/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("username" to username, "password" to password))
            }
        }.onSuccess { authResponse ->
            // 1. Сначала сохраняем токены
            saveTokens(authResponse.accessToken, authResponse.refreshToken)

            // 2. Теперь можем получить профиль пользователя (токены уже есть)
            try {
                val userProfile = getUserProfile().getOrNull()
                if (userProfile != null) {
                    // Используем live.urfu.frontend.data.manager.TokenManagerInstance напрямую
                    TokenManagerInstance.getInstance().saveID(userProfile.id.toString())
                    android.util.Log.d("UserApiService", "✅ Saved User ID: ${userProfile.id}")
                } else {
                    android.util.Log.e("UserApiService", "❌ Failed to get user profile")
                }
            } catch (e: Exception) {
                android.util.Log.e("UserApiService", "❌ Exception getting user profile: ${e.message}")
                e.printStackTrace()
            }
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
        }.onSuccess { authResponse ->
            // Аналогично для регистрации
            saveTokens(authResponse.accessToken, authResponse.refreshToken)

            try {
                val userProfile = getUserProfile().getOrNull()
                if (userProfile != null) {
                    TokenManagerInstance.getInstance().saveID(userProfile.id.toString())
                    android.util.Log.d("UserApiService", "✅ Registration: Saved User ID: ${userProfile.id}")
                }
            } catch (e: Exception) {
                android.util.Log.e("UserApiService", "❌ Registration: Failed to get user profile: ${e.message}")
            }
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
