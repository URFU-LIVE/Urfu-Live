package live.urfu.frontend.data.manager

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import live.urfu.frontend.data.model.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private val Context.postsDataStore: DataStore<Preferences> by preferencesDataStore(name = "posts_preferences")

object PostManagerInstance{
    @SuppressLint("StaticFieldLeak")
    private var instance: PostManager? = null

    fun initialize(context: Context) {
        if (instance == null) {
            instance = PostManager(context.applicationContext)
        }
    }

    fun getInstance(): PostManager {
        return instance ?: throw IllegalStateException("Posts not initialized")
    }
}

class PostManager(context: Context) {
    private val dataStore = context.postsDataStore
    private val mutex = Mutex()

    companion object {
        private val POSTS_KEY = stringSetPreferencesKey("selected_posts")
    }

    suspend fun savePost(post: Post) {
        mutex.withLock {
            try {
                dataStore.edit { prefs ->
                    val currentPosts = prefs[POSTS_KEY] ?: emptySet()
                    val updatedPosts = currentPosts + post.id.toString()
                    prefs[POSTS_KEY] = updatedPosts
                }

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error saving post ${post.id}", e)
                throw e
            }
        }
    }

    suspend fun removePost(post: Post) {
        mutex.withLock {
            try {
                dataStore.edit { prefs ->
                    val currentPosts = prefs[POSTS_KEY] ?: emptySet()
                    val updatedPosts = currentPosts - post.id.toString()
                    prefs[POSTS_KEY] = updatedPosts
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error removing post ${post.id}", e)
                throw e
            }
        }
    }

    suspend fun getSavedPostBlocking(): Set<Long> {
        return try {
            val result = dataStore.data.first()[POSTS_KEY]?.map { it.toLong() }?.toSet() ?: emptySet()
            result
        } catch (e: Exception) {
            emptySet()
        }
    }

    val savedPosts: Flow<Set<Long>> = dataStore.data
        .map { prefs -> prefs[POSTS_KEY]?.map { it.toLong() }?.toSet() ?: emptySet() }

    suspend fun clearSavedPosts() {
        dataStore.edit { prefs ->
            prefs.remove(POSTS_KEY)
        }
    }
}




