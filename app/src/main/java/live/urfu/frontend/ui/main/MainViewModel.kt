package live.urfu.frontend.ui.main

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import live.urfu.frontend.data.api.PostApiService
import live.urfu.frontend.data.api.UserApiService
import live.urfu.frontend.data.manager.DtoManager
import live.urfu.frontend.data.manager.PostManagerInstance
import live.urfu.frontend.data.manager.TokenManagerInstance
import live.urfu.frontend.data.model.Post
import live.urfu.frontend.ui.search.SearchViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import live.urfu.frontend.data.api.BaseViewModel
import live.urfu.frontend.ui.interests.InterestsChangeEvent
import live.urfu.frontend.ui.interests.InterestsStateRepository

class PostViewModel : BaseViewModel() {

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

    private val _postsUiState = mutableStateOf<Map<Long, PostUiState>>(emptyMap())

    private val interestsStateRepository = InterestsStateRepository.getInstance()

    private val _bookmarkedPosts = MutableStateFlow<Set<Long>>(emptySet())
    val bookmarkedPosts: StateFlow<Set<Long>> = _bookmarkedPosts

    init {
        viewModelScope.launch {
            TokenManagerInstance.getInstance().userId.collect { userId ->
                _currentUserId.value = userId

                if (userId != null) {
                    fetchPosts()
                } else {
                    _posts.value = emptyList()
                }
            }
        }

        observeInterestsChanges()
        loadBookmarkedPostsState()
    }

    private fun fetchPosts() {
        viewModelScope.launch {
            val result = postApiService.getRecommendation(0)
            result.onSuccess { postList ->
                val posts = postList.map { DtoManager().run { it.toPost() } }
                _posts.value = posts
                initializeUiStates(posts)
                initSubscriptions(posts)
            }.onFailure {
                fetchAllPostsFallback()
            }
        }
    }

    private fun fetchAllPostsFallback() {
        viewModelScope.launch {
            val result = postApiService.getAll()
            result.onSuccess { dtoPosts ->
                val posts = dtoPosts.map { DtoManager().run { it.toPost() } }
                _posts.value = posts
                initializeUiStates(posts)
                initSubscriptions(posts)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    private fun initializeUiStates(posts: List<Post>) {
        val uiStates = posts.associate { it.id to PostUiState() }
        _postsUiState.value = uiStates
    }

    private fun initSubscriptions(posts: List<Post>) {
        val userId = _currentUserId.value?.toInt() ?: return
        val authorIds = posts.filter { it.author.followers.contains(userId) }.map { it.author.id }.toSet()
        _subscriptions.value = authorIds
    }

    fun likeAndDislike(postId: Long) {
        viewModelScope.launch {
            val currentPost = _posts.value.find { it.id == postId } ?: return@launch
            val userId = _currentUserId.value?.toInt() ?: return@launch
            val wasLiked = currentPost.likedBy.contains(userId)

            updatePostUiState(postId) { it.copy(isProcessing = true) }
            val updatedPost = optimisticallyUpdatePost(currentPost, userId, wasLiked)
            updatePostEverywhere(updatedPost)

            val result = if (wasLiked) postApiService.dislike(postId) else postApiService.like(postId)
            result.onSuccess {
                updatePostUiState(postId) { it.copy(isProcessing = false) }
            }.onFailure {
                updatePostEverywhere(currentPost)
                updatePostUiState(postId) { it.copy(isProcessing = false) }
            }
        }
    }

    fun subscribeAndUnsubscribe(post: Post) {
        viewModelScope.launch {
            val authorId = post.author.id
            val postId = post.id
            val currentUserId = _currentUserId.value?.toInt() ?: return@launch
            val isCurrentlySubscribed = post.author.followers.contains(currentUserId)

            updatePostUiState(postId) { it.copy(isSubscriptionLoading = true) }
            val updatedPosts = optimisticallyUpdateAuthorSubscription(authorId, !isCurrentlySubscribed, currentUserId)
            updateAllAuthorPostsEverywhere(updatedPosts)

            val result = if (!isCurrentlySubscribed)
                userApiService.subscribe(authorId.toLong())
            else
                userApiService.unsubscribe(authorId.toLong())

            result.onSuccess {
                refreshSubscriptionsFromPosts()
                delay(300)
            }.onFailure {
                val revertedPosts = optimisticallyUpdateAuthorSubscription(authorId, isCurrentlySubscribed, currentUserId)
                updateAllAuthorPostsEverywhere(revertedPosts)
                refreshSubscriptionsFromPosts()
            }

            updatePostUiState(postId) { it.copy(isSubscriptionLoading = false) }
        }
    }

    private fun updatePostUiState(postId: Long, update: (PostUiState) -> PostUiState) {
        val currentStates = _postsUiState.value.toMutableMap()
        currentStates[postId]?.let { currentState ->
            currentStates[postId] = update(currentState)
            _postsUiState.value = currentStates
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
        return post.copy(likes = newLikesCount, likedBy = newLikedBy)
    }

    private fun optimisticallyUpdateAuthorSubscription(authorId: String, isSubscribed: Boolean, currentUserId: Int): List<Post> {
        val currentPosts = _posts.value.toMutableList()
        val updatedPosts = mutableListOf<Post>()

        currentPosts.forEachIndexed { index, post ->
            if (post.author.id == authorId) {
                val currentFollowers = post.author.followers.toMutableList()

                if (isSubscribed && !currentFollowers.contains(currentUserId)) {
                    currentFollowers.add(currentUserId)
                } else if (!isSubscribed && currentFollowers.contains(currentUserId)) {
                    currentFollowers.remove(currentUserId)
                }

                val updatedAuthor = post.author.copy(
                    followers = currentFollowers,
                    followersCount = currentFollowers.size
                )
                val updatedPost = post.copy(author = updatedAuthor)
                currentPosts[index] = updatedPost
                updatedPosts.add(updatedPost)
            }
        }

        _posts.value = currentPosts
        return updatedPosts
    }

    private suspend fun updateAllAuthorPostsEverywhere(updatedPosts: List<Post>) {
        connectedSearchViewModels.forEach { searchViewModel ->
            updatedPosts.forEach { updatedPost ->
                searchViewModel.updatePostInSearchResults(updatedPost)
            }
        }
    }

    private fun refreshSubscriptionsFromPosts() {
        val currentUserId = _currentUserId.value?.toInt() ?: return
        val subscriptionsFromPosts = _posts.value.filter {
            it.author.followers.contains(currentUserId)
        }.map { it.author.id }.toSet()
        _subscriptions.value = subscriptionsFromPosts
    }

    private suspend fun updatePostEverywhere(updatedPost: Post) {
        updatePostInListSafe(updatedPost)
        connectedSearchViewModels.forEach { searchViewModel ->
            searchViewModel.updatePostInSearchResults(updatedPost)
        }
    }

    private suspend fun updatePostInListSafe(updatedPost: Post) {
        postsUpdateMutex.withLock {
            val currentPosts = _posts.value.toMutableList()
            val index = currentPosts.indexOfFirst { it.id == updatedPost.id }
            if (index != -1) {
                currentPosts[index] = updatedPost
                _posts.value = currentPosts
            }
        }
    }



    fun updateAuthorSubscriptionState(authorId: String, currentUserId: Int, isSubscribed: Boolean) {
        viewModelScope.launch {
            postsUpdateMutex.withLock {
                val currentPosts = _posts.value.toMutableList()
                var hasUpdates = false

                currentPosts.forEachIndexed { index, post ->
                    if (post.author.id == authorId) {
                        val currentFollowers = post.author.followers.toMutableList()

                        if (isSubscribed && !currentFollowers.contains(currentUserId)) {
                            currentFollowers.add(currentUserId)
                            hasUpdates = true
                        } else if (!isSubscribed && currentFollowers.contains(currentUserId)) {
                            currentFollowers.remove(currentUserId)
                            hasUpdates = true
                        }

                        if (hasUpdates) {
                            val updatedAuthor = post.author.copy(
                                followers = currentFollowers,
                                followersCount = currentFollowers.size
                            )
                            currentPosts[index] = post.copy(author = updatedAuthor)
                        }
                    }
                }

                if (hasUpdates) {
                    _posts.value = currentPosts

                    val authorIds = currentPosts.filter { it.author.followers.contains(currentUserId) }
                        .map { it.author.id }.toSet()
                    _subscriptions.value = authorIds

                    connectedSearchViewModels.forEach { searchViewModel ->
                        currentPosts.filter { it.author.id == authorId }.forEach { updatedPost ->
                            searchViewModel.updatePostInSearchResults(updatedPost)
                        }
                    }
                }
            }
        }
    }

    fun isPostLikedByCurrentUser(postId: Long): Boolean {
        val userId = _currentUserId.value?.toInt() ?: return false
        val post = _posts.value.find { it.id == postId } ?: return false
        return post.likedBy.contains(userId)
    }

    fun getPostLikesCount(postId: Long): Int = _posts.value.find { it.id == postId }?.likes ?: 0

    fun isPostProcessing(postId: Long): Boolean = _postsUiState.value[postId]?.isProcessing ?: false

    fun connectSearchViewModel(searchViewModel: SearchViewModel) {
        connectedSearchViewModels.add(searchViewModel)
    }

    fun disconnectSearchViewModel(searchViewModel: SearchViewModel) {
        connectedSearchViewModels.remove(searchViewModel)
    }

    suspend fun addSearchPostsIfNeeded(searchPosts: List<Post>) {
        postsUpdateMutex.withLock {
            val currentPosts = _posts.value.toMutableList()
            val currentPostIds = currentPosts.map { it.id }.toSet()
            val newPosts = searchPosts.filter { it.id !in currentPostIds }

            if (newPosts.isNotEmpty()) {
                currentPosts.addAll(newPosts)
                _posts.value = currentPosts

                val currentUiStates = _postsUiState.value.toMutableMap()
                newPosts.forEach { post ->
                    currentUiStates[post.id] = PostUiState()
                }
                _postsUiState.value = currentUiStates
            }
        }
    }

    fun reinitializeStates(posts: List<Post>) {
        initializeUiStates(posts)
        initSubscriptions(posts)
    }

    private fun observeInterestsChanges() {
        viewModelScope.launch {
            interestsStateRepository.interestsChanged.collect { event ->
                when (event) {
                    is InterestsChangeEvent.Updated -> {

                        if (event.oldInterests != event.newInterests) {
                            fetchPosts()
                        }
                    }
                }
            }
        }
    }

    fun incrementCommentsCount(postId: Long) {
        viewModelScope.launch {
            postsUpdateMutex.withLock {
                val currentPosts = _posts.value.toMutableList()
                val index = currentPosts.indexOfFirst { it.id == postId }

                if (index != -1) {
                    val currentPost = currentPosts[index]
                    val updatedPost = currentPost.copy(comments = currentPost.comments + 1)
                    currentPosts[index] = updatedPost
                    _posts.value = currentPosts

                    connectedSearchViewModels.forEach { searchViewModel ->
                        searchViewModel.updatePostInSearchResults(updatedPost)
                    }
                }
            }
        }
    }
//    Будущий функционал
//    fun decrementCommentsCount(postId: Long) {
//        viewModelScope.launch {
//            postsUpdateMutex.withLock {
//                val currentPosts = _posts.value.toMutableList()
//                val index = currentPosts.indexOfFirst { it.id == postId }
//
//                if (index != -1) {
//                    val currentPost = currentPosts[index]
//                    val updatedPost = currentPost.copy(
//                        comments = maxOf(0, currentPost.comments - 1)
//                    )
//                    currentPosts[index] = updatedPost
//                    _posts.value = currentPosts
//
//                    connectedSearchViewModels.forEach { searchViewModel ->
//                        searchViewModel.updatePostInSearchResults(updatedPost)
//                    }
//                }
//            }
//        }
//    }

    private fun loadBookmarkedPostsState() {
        viewModelScope.launch {
            val savedPostIds = PostManagerInstance.getInstance().getSavedPostBlocking()
            _bookmarkedPosts.value = savedPostIds
        }
    }

    private fun isPostBookmarked(postId: Long): Boolean {
        return _bookmarkedPosts.value.contains(postId)
    }

    fun toggleBookmark(post: Post) {
        viewModelScope.launch {
            val isCurrentlyBookmarked = isPostBookmarked(post.id)

            if (isCurrentlyBookmarked) {
                PostManagerInstance.getInstance().removePost(post)
                _bookmarkedPosts.value -= post.id
            } else {
                PostManagerInstance.getInstance().savePost(post)
                _bookmarkedPosts.value += post.id
            }
        }
    }

}

data class PostUiState(
    val isProcessing: Boolean = false,
    val isSubscriptionLoading: Boolean = false
)

data class PostColorPattern(
    val background: Color,
    val buttonColor: Color,
    val textColor: Color,
    val reactionColor: Color,
    val reactionColorFilling: Color
)

val PostColorPatterns: List<PostColorPattern> = listOf(
    PostColorPattern(
        background = Color(0xFFB2DF8A),
        buttonColor = Color(0xFFF6ECC9),
        textColor = Color.Black,
        reactionColor = Color(0xFF6E9A3C),
        reactionColorFilling = Color(0xFF4A6828),
    ),
    PostColorPattern(
        background = Color(0xFFEBE6FD),
        buttonColor = Color(0xFFBA55D3),
        textColor = Color.Black,
        reactionColor = Color(0xFF8C3F9F),
        reactionColorFilling = Color(0xFF5E2A6B),
    ),
    PostColorPattern(
        background = Color(0xFFF6ECC9),
        buttonColor = Color(0xFFEE7E56),
        textColor = Color.Black,
        reactionColor = Color(0xFFAE451F),
        reactionColorFilling = Color(0xFF702E16),
    ),
)