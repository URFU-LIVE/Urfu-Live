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

    private suspend fun getInterestsKey(): Preferences.Key<Set<String>> {
        val userId = try {
            TokenManagerInstance.getInstance().getUserIdBlocking()
        } catch (e: Exception) {
            null
        }

        return if (userId != null) {
            stringSetPreferencesKey("interests_$userId")
        } else {
            stringSetPreferencesKey("interests_anonymous")
        }
    }

    suspend fun saveSelectedInterests(interests: Set<Interest>) {
        val key = getInterestsKey()
        dataStore.edit { prefs ->
            prefs[key] = interests.map { it.nameEn }.toSet()
        }
    }

    suspend fun getSelectedInterestsBlocking(): Set<String> {
        val key = getInterestsKey()
        return dataStore.data.first()[key] ?: emptySet()
    }

    val selectedInterests: Flow<Set<String>> = dataStore.data
        .map { prefs ->
            val key = try {
                getInterestsKey()
            } catch (e: Exception) {
                stringSetPreferencesKey("interests_anonymous")
            }
            prefs[key] ?: emptySet()
        }

    suspend fun clearSelectedInterests() {
        val key = getInterestsKey()
        dataStore.edit { prefs ->
            prefs.remove(key)
        }
    }

    suspend fun migrateOldData() {
        val oldKey = stringSetPreferencesKey("selected_interests")
        val userId = try {
            TokenManagerInstance.getInstance().getUserIdBlocking()
        } catch (e: Exception) {
            return
        }

        if (userId == null) return

        val prefs = dataStore.data.first()
        val oldInterests = prefs[oldKey]

        if (!oldInterests.isNullOrEmpty()) {
            val newKey = stringSetPreferencesKey("interests_$userId")

            dataStore.edit { editPrefs ->
                editPrefs[newKey] = oldInterests
                editPrefs.remove(oldKey)
            }
        }
    }
}