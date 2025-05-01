import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_preferences")
object TokenManagerInstance {
    private var instance: TokenManager? = null

    fun initialize(context: Context) {
        if (instance == null) {
            instance = TokenManager(context.applicationContext)
        }
    }

    fun getInstance(): TokenManager {
        return instance ?: throw IllegalStateException("TokenManager not initialized")
    }
}

class TokenManager(private val context: Context) {
    // Используем dataStore из extension-свойства
    private val dataStore = context.dataStore
    // Определяем ключи для DataStore
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
    }

    // Функция для сохранения токенов
    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
            preferences[REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    // Функция для получения access token в виде Flow
    val accessToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN_KEY]
    }

    // Функция для получения refresh token в виде Flow
    val refreshToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[REFRESH_TOKEN_KEY]
    }

    // Синхронные функции для получения токенов (при необходимости)
    fun getAccessTokenBlocking(): String? = runBlocking {
        var result: String? = null
        dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN_KEY]
        }.collect { token ->
            result = token
        }
        result
    }

    fun getRefreshTokenBlocking(): String? = runBlocking {
        var result: String? = null
        dataStore.data.map { preferences ->
            preferences[REFRESH_TOKEN_KEY]
        }.collect { token ->
            result = token
        }
        result
    }

    // Функция для очистки токенов (при выходе из аккаунта)
    suspend fun clearTokens() {
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
        }
    }
}