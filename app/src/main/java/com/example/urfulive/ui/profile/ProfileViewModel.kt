package com.example.urfulive.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urfulive.data.DTOs.UserDto
import com.example.urfulive.data.api.PostApiService
import com.example.urfulive.data.api.UserApiService
import com.example.urfulive.data.manager.DtoManager
import com.example.urfulive.data.model.Post
import com.example.urfulive.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel : ViewModel() {

    private val postApiService = PostApiService()
    private val userApiService = UserApiService()

    public lateinit var user: User;
    public lateinit var _posts: List<Post>;
    public lateinit var posts: StateFlow<List<Post>>;


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
            val result = userApiService.getUserPosts(id)
            result.onSuccess { postList ->
                val dtoManager = DtoManager()
                _posts = postList.map { dtoManager.run { it.toPost() } }
            }.onFailure {
                it.printStackTrace()
            }
        }
    }
}