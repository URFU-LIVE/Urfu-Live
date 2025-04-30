package com.example.urfulive.data.auth

import android.content.Context
import android.content.SharedPreferences
import com.example.urfulive.data.model.UserRole
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Класс для управления сессией пользователя и правами доступа
 */
class UserSession(context: Context) {

    private val preferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    // Информация о текущей сессии
    var userId: String? = null
        private set

    var username: String? = null
        private set

    var email: String? = null
        private set

    var token: String? = null
        private set

    var userRole: UserRole = UserRole.USER
        private set

    init {
        // Загрузка данных при инициализации
        loadSession()
    }

    private fun loadSession() {
        userId = preferences.getString(KEY_USER_ID, null)
        username = preferences.getString(KEY_USERNAME, null)
        email = preferences.getString(KEY_EMAIL, null)
        token = preferences.getString(KEY_TOKEN, null)

        val roleStr = preferences.getString(KEY_ROLE, null)
        userRole = if (roleStr != null) {
            try {
                UserRole.valueOf(roleStr)
            } catch (e: IllegalArgumentException) {
                UserRole.USER
            }
        } else {
            UserRole.USER
        }
    }

    fun saveSession(userId: String, username: String, email: String, token: String, role: UserRole) {
        this.userId = userId
        this.username = username
        this.email = email
        this.token = token
        this.userRole = role

        preferences.edit().apply {
            putString(KEY_USER_ID, userId)
            putString(KEY_USERNAME, username)
            putString(KEY_EMAIL, email)
            putString(KEY_TOKEN, token)
            putString(KEY_ROLE, role.name)
            apply()
        }
    }

    fun clearSession() {
        userId = null
        username = null
        email = null
        token = null
        userRole = UserRole.USER

        preferences.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean {
        return token != null
    }

    fun hasAdminRights(): Boolean {
        return userRole == UserRole.ADMIN
    }

    fun hasModeratorRights(): Boolean {
        return userRole == UserRole.WRITER || userRole == UserRole.ADMIN
    }

    companion object {
        private const val PREF_NAME = "user_session"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
        private const val KEY_TOKEN = "token"
        private const val KEY_ROLE = "role"
    }
}