import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// Create extension property for dataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_preferences")

object TokenManagerInstance {
    @SuppressLint("StaticFieldLeak")
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
    private val dataStore = context.dataStore

    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val ACCESS_TOKEN_TIMESTAMP_KEY = stringPreferencesKey("access_token_timestamp")
        private val REFRESH_TOKEN_TIMESTAMP_KEY = stringPreferencesKey("refresh_token_timestamp")

        // Время жизни токенов в миллисекундах
        private const val ACCESS_TOKEN_TTL = 24 * 60 * 60 * 1000L // 1 день
        private const val REFRESH_TOKEN_TTL = 7 * 24 * 60 * 60 * 1000L // 7 дней
    }

    // Сохранение токенов и ID пользователя
    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        val currentTime = System.currentTimeMillis()
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
            preferences[REFRESH_TOKEN_KEY] = refreshToken
            preferences[ACCESS_TOKEN_TIMESTAMP_KEY] = currentTime.toString()
            preferences[REFRESH_TOKEN_TIMESTAMP_KEY] = currentTime.toString()
        }
    }

    // Сохранение только access token
    suspend fun saveAccessToken(accessToken: String) {
        val currentTime = System.currentTimeMillis()
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
            preferences[ACCESS_TOKEN_TIMESTAMP_KEY] = currentTime.toString()
        }
    }

    // Сохранение только ID пользователя
    suspend fun saveID(id: String) {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = id
        }
    }

    // Получение access токена с проверкой срока действия
    suspend fun getAccessTokenBlocking(): String? {
        val preferences = dataStore.data.first()
        val savedAt = preferences[ACCESS_TOKEN_TIMESTAMP_KEY]?.toLongOrNull()
        val currentTime = System.currentTimeMillis()

        return if (savedAt != null && currentTime - savedAt < ACCESS_TOKEN_TTL) {
            preferences[ACCESS_TOKEN_KEY]
        } else {
            null // Токен истёк
        }
    }

    // Получение refresh токена с проверкой срока действия
    suspend fun getRefreshTokenBlocking(): String? {
        val preferences = dataStore.data.first()
        val savedAt = preferences[REFRESH_TOKEN_TIMESTAMP_KEY]?.toLongOrNull()
        val currentTime = System.currentTimeMillis()

        return if (savedAt != null && currentTime - savedAt < REFRESH_TOKEN_TTL) {
            preferences[REFRESH_TOKEN_KEY]
        } else {
            null // Рефреш токен истёк
        }
    }

    // Получение ID пользователя
    suspend fun getUserIdBlocking(): String? {
        val preferences = dataStore.data.first()
        return preferences[USER_ID_KEY]
    }

    // Очистка всех токенов и ID пользователя
    suspend fun clearTokens() {
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
            preferences.remove(USER_ID_KEY)
            preferences.remove(ACCESS_TOKEN_TIMESTAMP_KEY)
            preferences.remove(REFRESH_TOKEN_TIMESTAMP_KEY)
        }
    }

    // Очистка только токенов
    suspend fun clearAuthTokens() {
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
            preferences.remove(ACCESS_TOKEN_TIMESTAMP_KEY)
            preferences.remove(REFRESH_TOKEN_TIMESTAMP_KEY)
        }
    }

    // Получение Flow для доступа к токенам и ID
    val accessToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN_KEY]
    }

    val refreshToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[REFRESH_TOKEN_KEY]
    }

    val userId: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_ID_KEY]
    }
}
