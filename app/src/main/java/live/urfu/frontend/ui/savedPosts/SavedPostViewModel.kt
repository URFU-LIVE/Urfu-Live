package live.urfu.frontend.ui.savedPosts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import live.urfu.frontend.data.api.PostApiService
import live.urfu.frontend.data.manager.DtoManager
import live.urfu.frontend.data.manager.PostManagerInstance
import live.urfu.frontend.data.model.Post
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import live.urfu.frontend.data.api.BaseViewModel

class SavedPostsViewModel : BaseViewModel() {

    private val postApiService = PostApiService()
    private val dtoManager = DtoManager()

    private var _savedPosts by mutableStateOf<List<Post>>(emptyList())
    val savedPosts: List<Post> get() = _savedPosts

    init {
        loadSavedPosts()
    }

    private fun loadSavedPosts() {
        launchApiCall(
            tag = "SavedPostsViewModel",
            action = {
                val postIds = PostManagerInstance.getInstance().getSavedPostBlocking()

                val results = postIds.map { id ->
                    viewModelScope.async {
                        postApiService.get(id)
                    }
                }.awaitAll()

                val validPosts = results.mapNotNull { result ->
                    result.getOrNull()
                }

                Result.success(validPosts)
            },
            onSuccess = { postDtos ->
                _savedPosts = postDtos.map { dtoManager.run { it.toPost() } }
            },
            onError = { it.printStackTrace() }
        )
    }

    fun removeFromSaved(post: Post) {
        viewModelScope.launch {
            PostManagerInstance.getInstance().removePost(post)
            _savedPosts = _savedPosts.filter { it.id != post.id }
        }
    }

    fun refreshSavedPosts() {
        loadSavedPosts()
    }

    fun isPostSaved(postId: Long): Boolean {
        return _savedPosts.any { it.id == postId }
    }
}
