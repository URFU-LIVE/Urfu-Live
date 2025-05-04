package com.example.urfulive.ui.profile.my

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urfulive.data.api.UserApiService
import com.example.urfulive.data.manager.DtoManager
import com.example.urfulive.data.model.Post
import com.example.urfulive.data.model.User
import kotlinx.coroutines.launch
class ProfileViewModel : ViewModel() {
    private val userApiService = UserApiService()

    var user by mutableStateOf<User?>(null)
        private set

    var posts by mutableStateOf<List<Post>>(emptyList())
        private set

    init {
        fetchProfile()
    }

    private fun fetchProfile() {
        viewModelScope.launch {
            val result = userApiService.getUserProfile()
            result.onSuccess { userData ->
                val dtoManager = DtoManager()
                user = dtoManager.run { userData.toUser() }
                fetchUserPosts(userData.id)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    private fun fetchUserPosts(id: Long) {
        viewModelScope.launch {
            val result = userApiService.getUserPostsByID(id)
            result.onSuccess { postList ->
                val dtoManager = DtoManager()
                posts = postList.map { dtoManager.run { it.toPost() } }
            }.onFailure {
                it.printStackTrace()
            }
        }
    }
}
