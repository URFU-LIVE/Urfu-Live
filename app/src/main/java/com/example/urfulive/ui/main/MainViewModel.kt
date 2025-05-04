package com.example.urfulive.ui.main

import androidx.lifecycle.ViewModel
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.urfulive.data.api.PostApiService
import com.example.urfulive.data.manager.DtoManager
import com.example.urfulive.data.model.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

val dtoManager = DtoManager()

data class PostColorPattern(
    val background: Color,
    val buttonColor: Color,
    val textColor: Color,
    val reactionColor: Color,
)

private val postColorPattern = listOf(
    PostColorPattern(
        background = Color(0xFFB2DF8A),
        buttonColor = Color(0xFFF6ECC9),
        textColor = Color.Black,
        reactionColor = (Color(0xFF6E9A3C)),
    ),
    PostColorPattern(
        background = Color(0xFFEBE6FD),
        buttonColor = Color(0xFFBA55D3),
        textColor = Color.Black,
        reactionColor = (Color(0xFF8C3F9F)),
    ),
    PostColorPattern(
        background = Color(0xFFF6ECC9),
        buttonColor = Color(0xFFEE7E56),
        textColor = Color.Black,
        reactionColor = (Color(0xFFAE451F)),
    ),
)

val PostColorPatterns: List<PostColorPattern> get() = postColorPattern

class PostViewModel : ViewModel() {
    private val postApiService = PostApiService()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> get() = _posts

    var userId: String? = null

    init {
        viewModelScope.launch {
            userId = TokenManagerInstance.getInstance().getUserIdBlocking()
            fetchPosts()
        }
    }
    private fun fetchPosts() {
        viewModelScope.launch {
            val result = postApiService.getAll()
            result.onSuccess { postList ->
                val dtoManager = DtoManager()
                _posts.value = postList.map { dtoManager.run { it.toPost() } }

            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    fun likeAndDislike(id: Long) {
        viewModelScope.launch {
            val currentPosts = _posts.value.toMutableList()
            val index = currentPosts.indexOfFirst { it.id == id }

            if (index != -1 && userId != null) {
                val post = currentPosts[index]
                val likedBy = post.likedBy.toMutableList()

                val isLiked = likedBy.contains(userId)

                // Вызываем нужный API в зависимости от текущего состояния
                val result = if (isLiked) {
                    postApiService.dislike(id)
                } else {
                    postApiService.like(id)
                }

                result.onSuccess {
                    if (isLiked) {
                        likedBy.remove(userId)
                    } else {
                        likedBy.add(userId!!)
                    }

                    currentPosts[index] = post.copy(
                        likes = if (isLiked) post.likes - 1 else post.likes + 1,
                        likedBy = likedBy
                    )
                    _posts.value = currentPosts
                }.onFailure {
                    it.printStackTrace()
                }
            }
        }
    }
}
