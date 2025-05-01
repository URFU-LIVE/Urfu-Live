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
    // Access dataStore using extension property
    private val dataStore = context.dataStore

    // Keys for the DataStore preferences
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
    }

    // Save tokens asynchronously
    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
            preferences[REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    // Get access token as Flow
    val accessToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN_KEY]
    }

    // Get refresh token as Flow
    val refreshToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[REFRESH_TOKEN_KEY]
    }

    // Get access token synchronously (use only in contexts that can suspend or launch a coroutine)
    suspend fun getAccessTokenBlocking(): String? {
        val preferences = dataStore.data.first() // `first()` gets the value without needing `runBlocking`
        return preferences[ACCESS_TOKEN_KEY]
    }

    // Get refresh token synchronously
    suspend fun getRefreshTokenBlocking(): String? {
        val preferences = dataStore.data.first() // `first()` gets the value without needing `runBlocking`
        return preferences[REFRESH_TOKEN_KEY]
    }

    // Clear tokens from DataStore
    suspend fun clearTokens() {
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
        }
    }
}