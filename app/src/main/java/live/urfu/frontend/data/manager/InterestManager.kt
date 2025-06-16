package live.urfu.frontend.data.manager

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import live.urfu.frontend.data.model.Interest

private val Context.interestDataStore: DataStore<Preferences> by preferencesDataStore(name = "interest_preferences")

object InterestManagerInstance {
    @SuppressLint("StaticFieldLeak")
    private var instance: InterestManager? = null

    fun initialize(context: Context) {
        if (instance == null) {
            instance = InterestManager(context.applicationContext)
        }
    }

    fun getInstance(): InterestManager {
        return instance ?: throw IllegalStateException("InterestManager not initialized")
    }
}

class InterestManager(context: Context) {
    private val dataStore = context.interestDataStore

    companion object {
        private val INTERESTS_KEY = stringSetPreferencesKey("selected_interests")
    }

    // Сохраняем выбранные интересы
    suspend fun saveSelectedInterests(interests: Set<Interest>) {
        dataStore.edit { prefs ->
            prefs[INTERESTS_KEY] = interests.map { it.nameEn }.toSet()
        }
    }

    // Получаем интересы (блокирующая версия)
    suspend fun getSelectedInterestsBlocking(): Set<String> {
        return dataStore.data.first()[INTERESTS_KEY] ?: emptySet()
    }

    // Поток с интересами (для использования с Compose/Flow)
    val selectedInterests: Flow<Set<String>> = dataStore.data
        .map { prefs -> prefs[INTERESTS_KEY] ?: emptySet() }

    // Очистка интересов
    suspend fun clearSelectedInterests() {
        dataStore.edit { prefs ->
            prefs.remove(INTERESTS_KEY)
        }
    }
}