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
import live.urfu.frontend.ui.main.PostViewModel

class ProfileViewModel : BaseViewModel() {

    private val userApiService = UserApiService()
    private val dtoManager = DtoManager()

    var user by mutableStateOf<User?>(null)
        private set

    var posts by mutableStateOf<List<Post>>(emptyList())
        private set

    var currentUserId by mutableStateOf<Int?>(null)
        private set

    private var isSubscriptionLoading by mutableStateOf(false)
    private var subscriptionError by mutableStateOf<String?>(null)

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

    fun toggleSubscription(targetUserId: Long) {
        val currentUser = user ?: return
        val currentUserIdInt = currentUserId ?: return

        if (currentUserIdInt.toLong() == targetUserId) {
            subscriptionError = "Нельзя подписаться на самого себя"
            return
        }

        if (isSubscriptionLoading) {
            return
        }

        subscriptionError = null
        isSubscriptionLoading = true

        val isCurrentlySubscribed = currentUser.followers.contains(currentUserIdInt)

        val updatedFollowers = if (isCurrentlySubscribed) {
            currentUser.followers.filter { it != currentUserIdInt }
        } else {
            currentUser.followers + currentUserIdInt
        }

        val updatedUser = currentUser.copy(
            followers = updatedFollowers,
            followersCount = updatedFollowers.size
        )
        user = updatedUser

        viewModelScope.launch {
            try {
                val result = if (isCurrentlySubscribed) {
                    userApiService.unsubscribe(targetUserId)
                } else {
                    userApiService.subscribe(targetUserId)
                }

                result.onSuccess {
                    refreshUserData(targetUserId)
                }.onFailure { error ->
                    user = currentUser
                    android.util.Log.e("ProfileViewModel", "Subscription failed", error)
                }
            } catch (e: Exception) {
                user = currentUser
                subscriptionError = "Произошла неожиданная ошибка"
                android.util.Log.e("ProfileViewModel", "Unexpected error in toggleSubscription", e)
            } finally {
                isSubscriptionLoading = false
            }
        }
    }

    private fun refreshUserData(userId: Long) {
        launchApiCall(
            tag = "ProfileViewModel_Refresh",
            action = { userApiService.getUserProfileByID(userId) },
            onSuccess = { userDto ->
                user = dtoManager.run { userDto.toUser() }
            },
            onError = {
                it.printStackTrace()
            }
        )
    }

    fun clearData() {
        user = null
        posts = emptyList()
    }
}
