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

// 🎯 НОВАЯ СТРУКТУРА: Только UI состояния для обработки
data class PostUiState(
    val isProcessing: Boolean = false,
    val isSubscriptionLoading: Boolean = false
)

class PostViewModel : ViewModel() {
    private val postApiService = PostApiService()
    private val userApiService = UserApiService()

    private val postsUpdateMutex = Mutex()
    private val connectedSearchViewModels = mutableSetOf<SearchViewModel>()

    // 📊 ОСНОВНЫЕ ДАННЫЕ
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
                    Log.d("PostViewModel", "✅ User authorized, User ID: $userId")
                    // 🎯 Загружаем посты только когда пользователь авторизован
                    fetchPosts()
                } else {
                    Log.d("PostViewModel", "❌ User not authorized")
                    // Очищаем посты при разлогине
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

    // 🎯 КЛЮЧЕВЫЕ МЕТОДЫ: Используют РЕАЛЬНЫЕ данные поста

    /**
     * Проверяет лайк на основе РЕАЛЬНЫХ данных поста
     */
    fun isPostLikedByCurrentUser(postId: Long): Boolean {
        val userId = _currentUserId.value?.toInt() ?: return false
        val post = _posts.value.find { it.id == postId } ?: return false
        return post.likedBy.contains(userId)
    }

    /**
     * Получает актуальное количество лайков из РЕАЛЬНОГО поста
     */
    fun getPostLikesCount(postId: Long): Int {
        val post = _posts.value.find { it.id == postId } ?: return 0
        return post.likes
    }

    /**
     * Проверяет состояние обработки
     */
    fun isPostProcessing(postId: Long): Boolean {
        return _postsUiState.value[postId]?.isProcessing ?: false
    }

    /**
     * Основной метод лайков с правильной логикой
     */
    fun likeAndDislike(postId: Long) {
        Log.d("PostViewModel", "🎯 START likeAndDislike for post $postId")

        viewModelScope.launch {
            // === ПРОВЕРКА 1: Существование поста ===
            val currentPost = _posts.value.find { it.id == postId }
            if (currentPost == null) {
                Log.e("PostViewModel", "❌ CRITICAL ERROR: Post $postId NOT FOUND in main list")
                Log.d("PostViewModel", "📊 Available posts: ${_posts.value.map { it.id }}")
                return@launch
            }
            Log.d("PostViewModel", "✅ Post found: ${currentPost.title}")

            // === ПРОВЕРКА 2: UI состояние ===
            val currentUiState = _postsUiState.value[postId]
            if (currentUiState == null) {
                Log.e("PostViewModel", "❌ CRITICAL ERROR: UI State for post $postId NOT FOUND")
                Log.d("PostViewModel", "📊 Available UI states: ${_postsUiState.value.keys}")
                return@launch
            }
            Log.d("PostViewModel", "✅ UI State found: $currentUiState")

            // === ПРОВЕРКА 3: Обработка ===
            if (currentUiState.isProcessing) {
                Log.w("PostViewModel", "⚠️ Post $postId is already processing - BLOCKING")
                return@launch
            }

            // === ПРОВЕРКА 4: Пользователь ===
            val userId = _currentUserId.value?.toInt()
            if (userId == null) {
                Log.e("PostViewModel", "❌ CRITICAL ERROR: User ID is NULL")
                Log.d("PostViewModel", "📊 Current user value: ${_currentUserId.value}")
                return@launch
            }
            Log.d("PostViewModel", "✅ User ID: $userId")

            // === ПРОВЕРКА 5: Текущее состояние лайка ===
            val isCurrentlyLiked = currentPost.likedBy.contains(userId)
            Log.d("PostViewModel", "📊 BEFORE LIKE STATE:")
            Log.d("PostViewModel", "   Currently liked: $isCurrentlyLiked")
            Log.d("PostViewModel", "   Likes count: ${currentPost.likes}")
            Log.d("PostViewModel", "   LikedBy list: ${currentPost.likedBy}")

            // === НАЧАЛО ОБРАБОТКИ ===
            Log.d("PostViewModel", "🔄 Setting processing state...")
            updatePostUiState(postId) { it.copy(isProcessing = true) }

            // === ОПТИМИСТИЧНОЕ ОБНОВЛЕНИЕ ===
            Log.d("PostViewModel", "⚡ Applying optimistic update...")
            val updatedPost = optimisticallyUpdatePost(currentPost, userId, isCurrentlyLiked)

            Log.d("PostViewModel", "📊 AFTER OPTIMISTIC UPDATE:")
            Log.d("PostViewModel", "   New likes count: ${updatedPost.likes}")
            Log.d("PostViewModel", "   New likedBy list: ${updatedPost.likedBy}")

            // 🎯 КРИТИЧЕСКИЙ МОМЕНТ: Вызываем правильный метод синхронизации
            try {
                Log.d("PostViewModel", "🌍 Calling updatePostEverywhere...")
                updatePostEverywhere(updatedPost)
                Log.d("PostViewModel", "✅ updatePostEverywhere completed")
            } catch (e: Exception) {
                Log.e("PostViewModel", "💥 updatePostEverywhere FAILED: ${e.message}")
                e.printStackTrace()
            }

            // === API ВЫЗОВ ===
            try {
                Log.d("PostViewModel", "🌐 Making API call...")
                val result = if (isCurrentlyLiked) {
                    Log.d("PostViewModel", "🔥 SENDING DISLIKE for post $postId")
                    postApiService.dislike(postId)
                } else {
                    Log.d("PostViewModel", "❤️ SENDING LIKE for post $postId")
                    postApiService.like(postId)
                }

                result.onSuccess { response ->
                    Log.d("PostViewModel", "✅ API SUCCESS for post $postId")
                    updatePostUiState(postId) { it.copy(isProcessing = false) }

                    // Проверяем финальное состояние
                    val finalPost = _posts.value.find { it.id == postId }
                    Log.d("PostViewModel", "📊 FINAL STATE:")
                    Log.d("PostViewModel", "   Final likes: ${finalPost?.likes}")
                    Log.d("PostViewModel", "   Final likedBy: ${finalPost?.likedBy}")

                }.onFailure { error ->
                    Log.e("PostViewModel", "❌ API FAILED for post $postId: ${error.message}")
                    // Откат с правильной синхронизацией
                    updatePostEverywhere(currentPost)
                    updatePostUiState(postId) { it.copy(isProcessing = false) }
                    error.printStackTrace()
                }
            } catch (e: Exception) {
                Log.e("PostViewModel", "💥 API EXCEPTION for post $postId: ${e.message}")
                updatePostEverywhere(currentPost)
                updatePostUiState(postId) { it.copy(isProcessing = false) }
                e.printStackTrace()
            }
        }
    }

    /**
     * Оптимистично обновляет пост
     */
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

    /**
     * Обновляет пост в списке
     */
    private fun updatePostInList(updatedPost: Post) {
        val currentPosts = _posts.value.toMutableList()
        val index = currentPosts.indexOfFirst { it.id == updatedPost.id }
        if (index != -1) {
            currentPosts[index] = updatedPost
            _posts.value = currentPosts
        }
    }

    /**
     * Обновляет UI состояние поста
     */
    private fun updatePostUiState(postId: Long, update: (PostUiState) -> PostUiState) {
        val currentStates = _postsUiState.value.toMutableMap()
        currentStates[postId]?.let { currentState ->
            currentStates[postId] = update(currentState)
            _postsUiState.value = currentStates
        }
    }

    // 🔄 ПОДПИСКИ
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

    // 🔄 ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ
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

    /**
     * Подключает SearchViewModel для синхронизации
     */
    fun connectSearchViewModel(searchViewModel: SearchViewModel) {
        connectedSearchViewModels.add(searchViewModel)
        Log.d("PostViewModel", "🔗 Connected SearchViewModel: ${searchViewModel.hashCode()}")
    }

    /**
     * Отключает SearchViewModel от синхронизации
     */
    fun disconnectSearchViewModel(searchViewModel: SearchViewModel) {
        connectedSearchViewModels.remove(searchViewModel)
        Log.d("PostViewModel", "🔌 Disconnected SearchViewModel: ${searchViewModel.hashCode()}")
    }

    /**
     * 🔄 Добавляет посты из поиска в основной список для синхронизации лайков
     */
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

                Log.d("PostViewModel", "✅ Added ${newPosts.size} search posts to main list")
                Log.d("PostViewModel", "📊 Total posts now: ${_posts.value.size}")
            }
        }
    }

    /**
     * 🔄 Обновляет пост во всех местах и уведомляет подключенные SearchViewModel
     */
    private suspend fun updatePostEverywhere(updatedPost: Post) {
        Log.d("PostViewModel", "🔄 updatePostEverywhere START for post ${updatedPost.id}")

        try {
            // Обновляем в основном списке
            Log.d("PostViewModel", "📝 Updating in main list...")
            updatePostInListSafe(updatedPost)

            // Синхронизируем с SearchViewModel
            Log.d("PostViewModel", "🔗 Syncing with ${connectedSearchViewModels.size} SearchViewModels...")
            connectedSearchViewModels.forEach { searchViewModel ->
                try {
                    Log.d("PostViewModel", "   Syncing with SearchVM: ${searchViewModel.hashCode()}")
                    searchViewModel.updatePostInSearchResults(updatedPost)
                    Log.d("PostViewModel", "   ✅ Sync successful")
                } catch (e: Exception) {
                    Log.e("PostViewModel", "   ❌ Sync failed: ${e.message}")
                }
            }

            Log.d("PostViewModel", "✅ updatePostEverywhere COMPLETED")
        } catch (e: Exception) {
            Log.e("PostViewModel", "💥 updatePostEverywhere EXCEPTION: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    // 🎯 КЛЮЧЕВЫЕ МЕТОДЫ: Используют РЕАЛЬНЫЕ данные поста


    /**
     * Thread-safe обновление поста в списке
     */
    private suspend fun updatePostInListSafe(updatedPost: Post) {
        Log.d("PostViewModel", "🔒 updatePostInListSafe START for post ${updatedPost.id}")

        postsUpdateMutex.withLock {
            val currentPosts = _posts.value.toMutableList()
            val index = currentPosts.indexOfFirst { it.id == updatedPost.id }

            Log.d("PostViewModel", "🔍 Looking for post ${updatedPost.id} in list of ${currentPosts.size} posts")
            Log.d("PostViewModel", "🔍 Found at index: $index")

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

    /**
     * 📊 Метод для отладки состояния
     */
    fun debugState() {
        Log.d("PostViewModel", "=== DEBUG STATE ===")
        Log.d("PostViewModel", "ViewModel instance: ${this.hashCode()}")
        Log.d("PostViewModel", "Posts count: ${_posts.value.size}")
        Log.d("PostViewModel", "Current user ID: ${_currentUserId.value}")
        Log.d("PostViewModel", "Connected SearchViewModels: ${connectedSearchViewModels.size}")
        _posts.value.forEach { post ->
            Log.d("PostViewModel", "Post ${post.id}: likes=${post.likes}, likedBy=${post.likedBy}")
        }
        Log.d("PostViewModel", "UI States: ${_postsUiState.value.keys}")
        Log.d("PostViewModel", "==================")
    }

    fun refreshUserAuth() {
        viewModelScope.launch {
            Log.d("PostViewModel", "🔄 Manual auth refresh called")
        }
    }
}