package com.example.urfulive.ui.main

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import com.example.urfulive.data.api.PostApiService
import com.example.urfulive.data.api.UserApiService
import com.example.urfulive.data.manager.DtoManager
import com.example.urfulive.data.manager.PostManagerInstance
import com.example.urfulive.data.model.Post
import com.example.urfulive.ui.search.SearchViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class PostColorPattern(
    val background: Color,
    val buttonColor: Color,
    val textColor: Color,
    val reactionColor: Color,
    val reactionColorFilling: Color
)

private val postColorPattern = listOf(
    PostColorPattern(
        background = Color(0xFFB2DF8A),
        buttonColor = Color(0xFFF6ECC9),
        textColor = Color.Black,
        reactionColor = (Color(0xFF6E9A3C)),
        reactionColorFilling = (Color(0xFF4A6828)),
    ),
    PostColorPattern(
        background = Color(0xFFEBE6FD),
        buttonColor = Color(0xFFBA55D3),
        textColor = Color.Black,
        reactionColor = (Color(0xFF8C3F9F)),
        reactionColorFilling = (Color(0xFF5E2A6B)),
    ),
    PostColorPattern(
        background = Color(0xFFF6ECC9),
        buttonColor = Color(0xFFEE7E56),
        textColor = Color.Black,
        reactionColor = (Color(0xFFAE451F)),
        reactionColorFilling = (Color(0xFF702E16)),
    ),
)

val PostColorPatterns: List<PostColorPattern> get() = postColorPattern

data class PostUiState(
    val isProcessing: Boolean = false,
    val isSubscriptionLoading: Boolean = false
)

class PostViewModel : ViewModel() {
    private val postApiService = PostApiService()
    private val userApiService = UserApiService()

    private val postsUpdateMutex = Mutex()
    private val connectedSearchViewModels = mutableSetOf<SearchViewModel>()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> get() = _posts

    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> get() = _currentUserId

    private val _subscriptions = MutableStateFlow<Set<String>>(emptySet())
    val subscriptions: StateFlow<Set<String>> = _subscriptions

    // 🎮 UI СОСТОЯНИЯ (только для процессинга)
    private val _postsUiState = mutableStateOf<Map<Long, PostUiState>>(emptyMap())
    val postsUiState: Map<Long, PostUiState> by _postsUiState

    init {
        viewModelScope.launch {
            TokenManagerInstance.getInstance().userId.collect { userId ->
                Log.d("PostViewModel", "🔄 User ID changed: $userId")
                _currentUserId.value = userId

                if (userId != null) {
                    fetchPosts()
                } else {
                    _posts.value = emptyList()
                }
            }
        }
    }

    private fun fetchPosts() {
        viewModelScope.launch {
            val result = postApiService.getRecommendation(0)
            result.onSuccess { postList ->
                val dtoManager = DtoManager()
                val posts = postList.map { dtoManager.run { it.toPost() } }
                _posts.value = posts
                initializeUiStates(posts)
                initSubscriptions(posts)
            }.onFailure {
                viewModelScope.launch {
                    val newResult = postApiService.getAll()
                    newResult.onSuccess { dtoPosts ->
                        val dtoManager = DtoManager()
                        val posts = dtoPosts.map { dtoManager.run { it.toPost() } }
                        _posts.value = posts
                        initializeUiStates(posts)
                        initSubscriptions(posts)
                    }.onFailure {
                        it.printStackTrace()
                    }
                }
            }
        }
    }

    private fun initializeUiStates(posts: List<Post>) {
        val uiStates = posts.associate { post ->
            post.id to PostUiState(
                isProcessing = false,
                isSubscriptionLoading = false
            )
        }
        _postsUiState.value = uiStates
    }

    private fun initSubscriptions(posts: List<Post>) {
        val userId = _currentUserId.value?.toInt() ?: return
        val authorIds = posts
            .filter { post -> post.author.followers.contains(userId) }
            .map { it.author.id }
            .toSet()
        _subscriptions.value = authorIds
    }

    fun isPostLikedByCurrentUser(postId: Long): Boolean {
        val userId = _currentUserId.value?.toInt() ?: return false
        val post = _posts.value.find { it.id == postId } ?: return false
        return post.likedBy.contains(userId)
    }

    fun getPostLikesCount(postId: Long): Int {
        val post = _posts.value.find { it.id == postId } ?: return 0
        return post.likes
    }

    fun isPostProcessing(postId: Long): Boolean {
        return _postsUiState.value[postId]?.isProcessing ?: false
    }

    fun likeAndDislike(postId: Long) {
        viewModelScope.launch {
            val currentPost = _posts.value.find { it.id == postId }
            if (currentPost == null) {
                return@launch
            }
            val currentUiState = _postsUiState.value[postId]
            if (currentUiState == null) {
                return@launch
            }

            if (currentUiState.isProcessing) {
                Log.w("PostViewModel", "⚠️ Post $postId is already processing - BLOCKING")
                return@launch
            }

            val userId = _currentUserId.value?.toInt()
            if (userId == null) {
                return@launch
            }

            val isCurrentlyLiked = currentPost.likedBy.contains(userId)

            updatePostUiState(postId) { it.copy(isProcessing = true) }

            val updatedPost = optimisticallyUpdatePost(currentPost, userId, isCurrentlyLiked)

            try {
                updatePostEverywhere(updatedPost)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                val result = if (isCurrentlyLiked) {
                    postApiService.dislike(postId)
                } else {
                    postApiService.like(postId)
                }

                result.onSuccess { response ->
                    Log.d("PostViewModel", "✅ API SUCCESS for post $postId")
                    updatePostUiState(postId) { it.copy(isProcessing = false) }

                    val finalPost = _posts.value.find { it.id == postId }
                    Log.d("PostViewModel", "📊 FINAL STATE:")
                    Log.d("PostViewModel", "   Final likes: ${finalPost?.likes}")
                    Log.d("PostViewModel", "   Final likedBy: ${finalPost?.likedBy}")

                }.onFailure { error ->
                    updatePostEverywhere(currentPost)
                    updatePostUiState(postId) { it.copy(isProcessing = false) }
                    error.printStackTrace()
                }
            } catch (e: Exception) {
                updatePostEverywhere(currentPost)
                updatePostUiState(postId) { it.copy(isProcessing = false) }
                e.printStackTrace()
            }
        }
    }

    private fun optimisticallyUpdatePost(post: Post, userId: Int, wasLiked: Boolean): Post {
        val newLikedBy = post.likedBy.toMutableList()
        val newLikesCount = if (wasLiked) {
            newLikedBy.remove(userId)
            maxOf(0, post.likes - 1)
        } else {
            newLikedBy.add(userId)
            post.likes + 1
        }

        return post.copy(
            likes = newLikesCount,
            likedBy = newLikedBy
        )
    }

    private fun updatePostInList(updatedPost: Post) {
        val currentPosts = _posts.value.toMutableList()
        val index = currentPosts.indexOfFirst { it.id == updatedPost.id }
        if (index != -1) {
            currentPosts[index] = updatedPost
            _posts.value = currentPosts
        }
    }

    private fun updatePostUiState(postId: Long, update: (PostUiState) -> PostUiState) {
        val currentStates = _postsUiState.value.toMutableMap()
        currentStates[postId]?.let { currentState ->
            currentStates[postId] = update(currentState)
            _postsUiState.value = currentStates
        }
    }

    fun subscribeAndUnsubscribe(post: Post) {
        viewModelScope.launch {
            val authorId = post.author.id
            val currentUiState = _postsUiState.value[post.id] ?: return@launch

            if (currentUiState.isSubscriptionLoading) {
                return@launch
            }

            updatePostUiState(post.id) {
                it.copy(isSubscriptionLoading = true)
            }

            try {
                val isSubscribed = _subscriptions.value.contains(authorId)
                val result = if (!isSubscribed) {
                    userApiService.subscribe(authorId.toLong())
                } else {
                    userApiService.unsubscribe(authorId.toLong())
                }

                result.onSuccess {
                    updateSubscriptionStateLocally(authorId)
                    kotlinx.coroutines.delay(300)
                }.onFailure {
                    it.printStackTrace()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                updatePostUiState(post.id) {
                    it.copy(isSubscriptionLoading = false)
                }
            }
        }
    }

    private fun updateSubscriptionStateLocally(authorId: String) {
        val currentSubscriptions = _subscriptions.value.toMutableSet()
        val isCurrentlySubscribed = currentSubscriptions.contains(authorId)

        if (isCurrentlySubscribed) {
            currentSubscriptions.remove(authorId)
        } else {
            currentSubscriptions.add(authorId)
        }
        _subscriptions.value = currentSubscriptions
    }

    fun reinitializeStates(posts: List<Post>) {
        initializeUiStates(posts)
        initSubscriptions(posts)
    }

    fun isUserSubscribe(post: Post): Boolean {
        return _subscriptions.value.contains(post.author.id)
    }

    fun isSubscriptionLoading(postId: Long): Boolean {
        return _postsUiState.value[postId]?.isSubscriptionLoading ?: false
    }

    fun connectSearchViewModel(searchViewModel: SearchViewModel) {
        connectedSearchViewModels.add(searchViewModel)
        Log.d("PostViewModel", "🔗 Connected SearchViewModel: ${searchViewModel.hashCode()}")
    }

    fun disconnectSearchViewModel(searchViewModel: SearchViewModel) {
        connectedSearchViewModels.remove(searchViewModel)
        Log.d("PostViewModel", "🔌 Disconnected SearchViewModel: ${searchViewModel.hashCode()}")
    }

    suspend fun addSearchPostsIfNeeded(searchPosts: List<Post>) {
        postsUpdateMutex.withLock {
            val currentPosts = _posts.value.toMutableList()
            val currentPostIds = currentPosts.map { it.id }.toSet()

            // Добавляем только те посты, которых еще нет в основном списке
            val newPosts = searchPosts.filter { it.id !in currentPostIds }

            if (newPosts.isNotEmpty()) {
                currentPosts.addAll(newPosts)
                _posts.value = currentPosts

                // Инициализируем UI состояния для новых постов
                val currentUiStates = _postsUiState.value.toMutableMap()
                newPosts.forEach { post ->
                    currentUiStates[post.id] = PostUiState(
                        isProcessing = false,
                        isSubscriptionLoading = false
                    )
                }
                _postsUiState.value = currentUiStates
            }
        }
    }

    private suspend fun updatePostEverywhere(updatedPost: Post) {
        try {
            Log.d("PostViewModel", "📝 Updating in main list...")
            updatePostInListSafe(updatedPost)

            connectedSearchViewModels.forEach { searchViewModel ->
                try {
                    Log.d("PostViewModel", "   Syncing with SearchVM: ${searchViewModel.hashCode()}")
                    searchViewModel.updatePostInSearchResults(updatedPost)
                    Log.d("PostViewModel", "   ✅ Sync successful")
                } catch (e: Exception) {
                    Log.e("PostViewModel", "   ❌ Sync failed: ${e.message}")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    private suspend fun updatePostInListSafe(updatedPost: Post) {
        postsUpdateMutex.withLock {
            val currentPosts = _posts.value.toMutableList()
            val index = currentPosts.indexOfFirst { it.id == updatedPost.id }

            if (index != -1) {
                currentPosts[index] = updatedPost
                _posts.value = currentPosts
                Log.d("PostViewModel", "✅ Updated post ${updatedPost.id} at index $index")
                Log.d("PostViewModel", "📊 New state - Likes: ${updatedPost.likes}, LikedBy: ${updatedPost.likedBy}")
            } else {
                Log.e("PostViewModel", "❌ Post ${updatedPost.id} NOT FOUND for update")
                Log.d("PostViewModel", "📊 Available post IDs: ${currentPosts.map { it.id }}")
            }
        }
    }

    fun refreshUserAuth() {
        viewModelScope.launch {
            Log.d("PostViewModel", "🔄 Manual auth refresh called")
        }
    }

    fun savePost(post: Post) {
        viewModelScope.launch {
            PostManagerInstance.getInstance().savePost(post)
        }
    }
}