package live.urfu.frontend.ui.savedPosts

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import live.urfu.frontend.data.manager.PostManagerInstance
import live.urfu.frontend.data.model.Post
import kotlinx.coroutines.launch
import live.urfu.frontend.data.api.BaseViewModel
import live.urfu.frontend.ui.main.PostViewModel

class SavedPostsViewModel(
    private val sharedPostViewModel: PostViewModel
) : BaseViewModel() {
    private var _savedPosts by mutableStateOf<List<Post>>(emptyList())
    val savedPosts: List<Post> get() = _savedPosts

    init {
        loadSavedPosts()
        observePostsChanges()
    }

    private fun observePostsChanges() {
        viewModelScope.launch {
            sharedPostViewModel.posts.collect { allPosts ->
                updateSavedPostsList(allPosts)
            }
        }
    }

    private fun loadSavedPosts() {
        viewModelScope.launch {
            try {
                val savedPostIds = PostManagerInstance.getInstance().getSavedPostBlocking()

                val allPosts = sharedPostViewModel.posts.value

                updateSavedPostsList(allPosts, savedPostIds)

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error loading saved posts", e)
            }
        }
    }


    private fun updateSavedPostsList(
        allPosts: List<Post>,
        savedPostIds: Set<Long>? = null
    ) {
        viewModelScope.launch {
            val idsToUse = savedPostIds ?: PostManagerInstance.getInstance().getSavedPostBlocking()

            val savedPostsList = allPosts.filter { post ->
                post.id in idsToUse
            }

            _savedPosts = savedPostsList
        }
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

}

class SavedPostsViewModelFactory(
    private val sharedPostViewModel: PostViewModel
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SavedPostsViewModel::class.java)) {
            return SavedPostsViewModel(sharedPostViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}