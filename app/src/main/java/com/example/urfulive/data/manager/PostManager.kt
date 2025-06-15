package com.example.urfulive.data.manager

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.urfulive.data.model.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

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

    companion object {
        private val POSTS_KEY = stringSetPreferencesKey("selected_posts")
    }

    suspend fun savePost(post: Post) {
        dataStore.edit { prefs ->
            val currentPosts = prefs[POSTS_KEY] ?: emptySet()
            val updatedPosts = currentPosts + post.id.toString()
            prefs[POSTS_KEY] = updatedPosts
        }
    }

    suspend fun removePost(post: Post) {
        dataStore.edit { prefs ->
            val currentPosts = prefs[POSTS_KEY] ?: emptySet()
            val updatedPosts = currentPosts - post.id.toString()
            prefs[POSTS_KEY] = updatedPosts
        }
    }

    suspend fun getSavedPostBlocking(): Set<Long> {
        return dataStore.data.first()[POSTS_KEY]?.map { it.toLong() }?.toSet() ?: emptySet()
    }

    val savedPosts: Flow<Set<Long>> = dataStore.data
        .map { prefs -> prefs[POSTS_KEY]?.map { it.toLong() }?.toSet() ?: emptySet() }

    suspend fun clearSavedPosts() {
        dataStore.edit { prefs ->
            prefs.remove(POSTS_KEY)
        }
    }
}




