package live.urfu.frontend.ui.savedPosts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import live.urfu.frontend.data.DTOs.PostDto
import live.urfu.frontend.data.api.PostApiService
import live.urfu.frontend.data.manager.DtoManager
import live.urfu.frontend.data.manager.PostManagerInstance
import live.urfu.frontend.data.model.Post
import live.urfu.frontend.data.model.Tag
import live.urfu.frontend.data.model.User
import live.urfu.frontend.data.model.UserRole
import kotlinx.coroutines.launch


// ViewModel для сохраненных постов
class SavedPostsViewModel : ViewModel() {

    val postApiService = PostApiService()
    private val _savedPosts = mutableStateOf<List<Post>>(emptyList())
    val savedPosts: List<Post> by _savedPosts

    init {
        loadSavedPosts()
    }

    private fun loadSavedPosts() {
        viewModelScope.launch {
            val postsId = PostManagerInstance.getInstance().getSavedPostBlocking()
            val postsDto = mutableSetOf<PostDto>()

            for (postId in postsId) {
                val result = postApiService.get(postId) // Result<PostDto>

                result.onSuccess { post ->
                    postsDto.add(post)
                }.onFailure { error ->
                    error.printStackTrace()
                }
            }

            val dtoManager = DtoManager()
            _savedPosts.value = postsDto.map { dtoManager.run { it.toPost() } }
        }
    }


    fun removeFromSaved(post: Post) {
        viewModelScope.launch {
            PostManagerInstance.getInstance().removePost(post)
        }
        _savedPosts.value = _savedPosts.value.filter { it.id != post.id }
    }
}