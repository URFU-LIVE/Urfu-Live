package live.urfu.frontend.ui.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import live.urfu.frontend.data.api.BaseViewModel
import live.urfu.frontend.data.api.UserApiService
import live.urfu.frontend.data.manager.DtoManager
import live.urfu.frontend.data.manager.TokenManagerInstance
import live.urfu.frontend.data.model.Post
import live.urfu.frontend.data.model.User

class ProfileViewModel : BaseViewModel() {

    private val userApiService = UserApiService()
    private val dtoManager = DtoManager()

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
            currentUserId = userId?.toInt()
        }
    }

    fun fetchProfile() {
        launchApiCall(
            tag = "ProfileViewModel",
            action = { userApiService.getUserProfile() },
            onSuccess = { userDto ->
                user = dtoManager.run { userDto.toUser() }
                fetchUserPosts(userDto.id)
            },
            onError = { it.printStackTrace() }
        )
    }

    fun fetchUserProfileById(userId: Long) {
        launchApiCall(
            tag = "ProfileViewModel",
            action = { userApiService.getUserProfileByID(userId) },
            onSuccess = { userDto ->
                user = dtoManager.run { userDto.toUser() }
                fetchUserPosts(userDto.id)
            },
            onError = { it.printStackTrace() }
        )
    }

    private fun fetchUserPosts(id: Long) {
        launchApiCall(
            tag = "ProfileViewModel",
            action = { userApiService.getUserPostsByID(id) },
            onSuccess = { postsDto ->
                posts = postsDto.map { dtoManager.run { it.toPost() } }
            },
            onError = {
                posts = emptyList()
            }
        )
    }

    fun clearData() {
        user = null
        posts = emptyList()
    }
}
