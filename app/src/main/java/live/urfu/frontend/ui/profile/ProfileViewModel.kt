package live.urfu.frontend.ui.profile

import TokenManagerInstance
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import live.urfu.frontend.data.api.UserApiService
import live.urfu.frontend.data.manager.DtoManager
import live.urfu.frontend.data.model.Post
import live.urfu.frontend.data.model.User
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val userApiService = UserApiService()

    var user by mutableStateOf<User?>(null)
        private set

    var posts by mutableStateOf<List<Post>>(emptyList())
        private set

    var currentUserId by mutableStateOf<Int?>(null)
        private set

    init {
        fetchCurrentUserId()
    }

    private fun fetchCurrentUserId() {
        viewModelScope.launch {
            val userId = TokenManagerInstance.getInstance().getUserIdBlocking()
            if (userId != null) {
                currentUserId = userId.toInt();
            } else {
                // todo Сделать какую-нибудь заглушку
            }
        }
    }

    fun fetchProfile() {
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
                posts = emptyList();
            }
        }
    }

    fun fetchUserProfileById(userId: Long) {
        viewModelScope.launch {
            val result = userApiService.getUserProfileByID(userId)
            result.onSuccess { userData ->
                val dtoManager = DtoManager()
                user = dtoManager.run { userData.toUser() }
                fetchUserPosts(userData.id)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    fun clearData() {
        user = null
        posts = emptyList()
    }
}
