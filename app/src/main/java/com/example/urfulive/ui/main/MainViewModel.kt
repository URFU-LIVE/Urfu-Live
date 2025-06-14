package com.example.urfulive.ui.main

import androidx.lifecycle.ViewModel
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import com.example.urfulive.data.api.PostApiService
import com.example.urfulive.data.api.UserApiService
import com.example.urfulive.data.manager.DtoManager
import com.example.urfulive.data.model.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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

class PostViewModel : ViewModel() {
    private val postApiService = PostApiService()
    private val userApiService = UserApiService()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> get() = _posts

    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> get() = _currentUserId


    private val _likedPostIds = MutableStateFlow<Set<Long>>(emptySet())
    val likedPostIds: StateFlow<Set<Long>> = _likedPostIds

    private val _postLikes = MutableStateFlow<Map<Long, Int>>(emptyMap())
    val postLikes: StateFlow<Map<Long, Int>> = _postLikes

    private val _subscriptions = MutableStateFlow<Set<String>>(emptySet())
    val subscriptions: StateFlow<Set<String>> = _subscriptions

    private val _isSubscriptionLoading = MutableStateFlow<Set<String>>(emptySet())
    val isSubscriptionLoading: StateFlow<Set<String>> = _isSubscriptionLoading
    private val _likesLoading = MutableStateFlow<Set<Long>>(emptySet())
    val likesLoading: StateFlow<Set<Long>> = _likesLoading
    private val _processingLikeRequests = MutableStateFlow<Set<Long>>(emptySet())

    init {
        viewModelScope.launch {
            _currentUserId.value = TokenManagerInstance.getInstance().getUserIdBlocking()
            fetchPosts()
        }
    }

    private fun initPostLikes(posts: List<Post>) {
        val likesMap = posts.associate { it.id to it.likes }
        _postLikes.value = likesMap
    }

    private fun fetchPosts() {
        viewModelScope.launch {
            val result = postApiService.getRecommendation(0)
            result.onSuccess { postList ->
                val dtoManager = DtoManager()
                val posts = postList.map { dtoManager.run { it.toPost() } }
                _posts.value = posts

                initLikedPosts(posts)
                initSubscriptions(posts)
                initPostLikes(posts)
            }.onFailure {
                viewModelScope.launch {
                    val newResult = postApiService.getAll()

                    newResult.onSuccess { dtoPosts ->
                        val dtoManager = DtoManager()
                        val posts = dtoPosts.map { dtoManager.run { it.toPost() } }
                        _posts.value = posts;

                        initLikedPosts(posts)
                        initSubscriptions(posts)
                        initPostLikes(posts)
                    }.onFailure {
                        it.printStackTrace()
                    }
                }
            }
        }
    }

    private fun initLikedPosts(posts: List<Post>) {
        val userId = _currentUserId.value?.toInt() ?: return
        val likedIds = posts
            .filter { post -> post.likedBy.contains(userId) }
            .map { it.id }
            .toSet()
        _likedPostIds.value = likedIds
    }

    private fun initSubscriptions(posts: List<Post>) {
        val userId = _currentUserId.value?.toInt() ?: return
        val authorIds = posts
            .filter { post -> post.author.followers.contains(userId) }
            .map { it.author.id }
            .toSet()
        _subscriptions.value = authorIds
    }

    private fun updateLikeStateLocally(id: Long) {
        val userId = _currentUserId.value?.toInt() ?: return

        // Обновляем состояние в _likedPostIds
        val currentLikedIds = _likedPostIds.value.toMutableSet()
        val isCurrentlyLiked = currentLikedIds.contains(id)

        if (isCurrentlyLiked) {
            currentLikedIds.remove(id)
        } else {
            currentLikedIds.add(id)
        }
        _likedPostIds.value = currentLikedIds

        // Обновляем счетчик лайков в списке постов
        val currentPosts = _posts.value.toMutableList()
        val postIndex = currentPosts.indexOfFirst { it.id == id }

        if (postIndex != -1) {
            val post = currentPosts[postIndex]
            val likedBy = post.likedBy.toMutableList()
            val newLikesCount = if (isCurrentlyLiked) {
                likedBy.remove(userId)
                post.likes - 1
            } else {
                likedBy.add(userId)
                post.likes + 1
            }

            currentPosts[postIndex] = post.copy(
                likes = newLikesCount,
                likedBy = likedBy
            )
            _posts.value = currentPosts
        }

        // Обновляем отдельный Map для счетчиков лайков
        val currentLikesMap = _postLikes.value.toMutableMap()
        val currentCount = currentLikesMap[id] ?: 0
        val newCount = if (isCurrentlyLiked) currentCount - 1 else currentCount + 1
        currentLikesMap[id] = maxOf(0, newCount) // Не даем счетчику стать отрицательным
        _postLikes.value = currentLikesMap
    }

    fun isPostLikedByCurrentUser(post: Post): Boolean {
        return _likedPostIds.value.contains(post.id)
    }

    fun isUserSubscribe(post: Post): Boolean {
        return _subscriptions.value.contains(post.author.id)
    }


    fun likeAndDislike(id: Long) {
        viewModelScope.launch {
            try {
                // Проверяем, не обрабатывается ли уже запрос для этого поста
                if (_processingLikeRequests.value.contains(id)) {
                    return@launch
                }

                // Добавляем пост в множество обрабатываемых запросов
                _processingLikeRequests.value = _processingLikeRequests.value + id
                _likesLoading.value = _likesLoading.value + id

                // Определяем текущее состояние лайка
                val isCurrentlyLiked = _likedPostIds.value.contains(id)

                // Немедленно обновляем UI (оптимистичное обновление)
                updateLikeStateLocally(id)

                // Отправляем запрос на сервер
                val result = if (isCurrentlyLiked) {
                    postApiService.dislike(id)
                } else {
                    postApiService.like(id)
                }

                result.onSuccess {
                    // Сервер подтвердил изменение - состояние уже обновлено локально
                    kotlinx.coroutines.delay(100) // Небольшая задержка для плавности анимации
                }.onFailure { error ->
                    //  Откатываем изменения при ошибке
                    updateLikeStateLocally(id) // Возвращаем предыдущее состояние
                    error.printStackTrace()
                }
            } catch (e: Exception) {
                // Откатываем изменения при исключении
                updateLikeStateLocally(id)
                e.printStackTrace()
            } finally {
                _likesLoading.value = _likesLoading.value - id
                _processingLikeRequests.value = _processingLikeRequests.value - id
            }
        }
    }


    fun subscribeAndUnsubscribe(post: Post) {
        viewModelScope.launch {
            // Сохраняем ID автора
            val authorId = post.author.id

            try {
                // Устанавливаем состояние загрузки
                _isSubscriptionLoading.value = _isSubscriptionLoading.value + authorId

                // Определяем текущее состояние подписки
                val isSub = _subscriptions.value.contains(authorId)

                // Выполняем запрос
                val result = if (!isSub)
                    userApiService.subscribe(authorId.toLong())
                else
                    userApiService.unsubscribe(authorId.toLong())

                result.onSuccess {
                    updateSubscriptionStateLocally(authorId)

                    kotlinx.coroutines.delay(300)
                }.onFailure {
                    it.printStackTrace()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                // В любом случае снимаем состояние загрузки
                _isSubscriptionLoading.value = _isSubscriptionLoading.value - authorId
            }
        }
    }

    // Обновление локального состояния подписки
    private fun updateSubscriptionStateLocally(authorId: String) {
        val currentSubscriptions = _subscriptions.value.toMutableSet()
        val isCurrentlySubscribed = currentSubscriptions.contains(authorId)

        if (isCurrentlySubscribed) {
            currentSubscriptions.remove(authorId)
        } else {
            currentSubscriptions.add(authorId)
        }
        _subscriptions.value = currentSubscriptions

        // Обновляем список подписчиков в авторе
        val currentPosts = _posts.value.toMutableList()
        val userId = _currentUserId.value?.toInt() ?: return

        for (i in currentPosts.indices) {
            val post = currentPosts[i]
            if (post.author.id == authorId) {
                val followers = post.author.followers.toMutableList()

                if (isCurrentlySubscribed) {
                    followers.remove(userId)
                } else {
                    followers.add(userId)
                }

                // Создаем копию автора с обновленным списком подписчиков
                val updatedAuthor = post.author.copy(followers = followers)
                // Создаем копию поста с обновленным автором
                currentPosts[i] = post.copy(author = updatedAuthor)
            }
        }
        _posts.value = currentPosts
    }

    fun reinitializeStates(posts: List<Post>) {
        initLikedPosts(posts)
        initSubscriptions(posts)
        initPostLikes(posts)
    }

    fun getActualLikesCount(postId: Long): Int {
        return _postLikes.value[postId] ?: _posts.value.find { it.id == postId }?.likes ?: 0
    }

    // Функция для проверки состояния лайка поста
    fun isPostLikedByCurrentUser(postId: Long): Boolean {
        return _likedPostIds.value.contains(postId)
    }

    // Функция для проверки, загружается ли лайк
    fun isLikeLoading(postId: Long): Boolean {
        return _likesLoading.value.contains(postId)
    }
}