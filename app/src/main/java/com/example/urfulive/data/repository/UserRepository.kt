package com.example.urfulive.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.urfulive.data.api.UserApiService
import com.example.urfulive.data.auth.UserSession
import com.example.urfulive.data.model.User
import com.example.urfulive.data.model.UserRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class UserRepository(
    private val context: Context,
    private val apiService: UserApiService = UserApiService(),
    private val userSession: UserSession = UserSession(context)
) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: Flow<User?> = _currentUser.asStateFlow()

    init {
        // При инициализации загружаем пользователя из SharedPreferences, если он там есть
        if (userSession.isLoggedIn()) {
            _currentUser.value = getUserFromSession()
        }
    }

    private fun loadUserFromPrefs() {
        val userJson = sharedPreferences.getString(USER_KEY, null)
        if (userJson != null) {
            try {
                val user = Json.decodeFromString<User>(userJson)
                _currentUser.value = user
            } catch (e: Exception) {
                // Ошибка при десериализации, удаляем некорректные данные
                sharedPreferences.edit().remove(USER_KEY).apply()
            }
        }
    }

    private fun saveUserToPrefs(user: User) {
        val userJson = Json.encodeToString(user)
        sharedPreferences.edit().putString(USER_KEY, userJson).apply()
        _currentUser.value = user
    }

    private fun clearUserFromPrefs() {
        sharedPreferences.edit().remove(USER_KEY).apply()
        _currentUser.value = null
    }

    suspend fun login(username: String, password: String): Result<User> {
        return withContext(Dispatchers.IO) {
            val result = apiService.login(username, password)
            if (result.isSuccess) {
                val user = result.getOrThrow()
                userSession.saveSession(
                    userId = user.id,
                    username = user.username,
                    email = user.email,
                    token = user.token ?: "",
                    role = user.role
                )
                _currentUser.value = user
            }
            result
        }
    }

    suspend fun register(username: String, email: String, password: String): Result<User> {
        return withContext(Dispatchers.IO) {
            val result = apiService.register(username, email, password)
            if (result.isSuccess) {
                val user = result.getOrThrow()
                userSession.saveSession(
                    userId = user.id,
                    username = user.username,
                    email = user.email,
                    token = user.token ?: "",
                    role = user.role
                )
                _currentUser.value = user
            }
            result
        }
    }

    suspend fun getUserProfile(): Result<User> {
        return withContext(Dispatchers.IO) {
            if (!userSession.isLoggedIn()) {
                return@withContext Result.failure(IllegalStateException("User not logged in"))
            }

            val token = userSession.token ?: return@withContext Result.failure(
                IllegalStateException("No authentication token available")
            )

            val result = apiService.getUserProfile(token)
            if (result.isSuccess) {
                val updatedUser = result.getOrThrow()
                userSession.saveSession(
                    userId = updatedUser.id,
                    username = updatedUser.username,
                    email = updatedUser.email,
                    token = updatedUser.token ?: token,
                    role = updatedUser.role
                )
                _currentUser.value = updatedUser
            }
            result
        }
    }

    suspend fun updateUserRole(userId: String, newRole: UserRole): Result<User> {
        return withContext(Dispatchers.IO) {
            if (!userSession.hasAdminRights()) {
                return@withContext Result.failure(
                    IllegalStateException("Only admin can update user roles")
                )
            }

            val token = userSession.token ?: return@withContext Result.failure(
                IllegalStateException("No authentication token available")
            )

            apiService.updateUserRole(userId, newRole.name, token)
        }
    }

    fun logout() {
        userSession.clearSession()
        _currentUser.value = null
    }

    fun hasAdminRights(): Boolean {
        return userSession.hasAdminRights()
    }

    fun hasModeratorRights(): Boolean {
        return userSession.hasModeratorRights()
    }

    /**
     * Получает данные пользователя из сессии
     */
    fun getUserFromSession(): User {
        return User(
            id = userSession.userId ?: "",
            username = userSession.username ?: "",
            email = userSession.email ?: "",
            role = userSession.userRole,
            token = userSession.token
        )
    }

    companion object {
        private const val USER_KEY = "current_user"
    }
}