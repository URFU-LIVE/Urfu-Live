package live.urfu.frontend.ui.comments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import live.urfu.frontend.data.api.CommentApiService
import live.urfu.frontend.data.manager.DtoManager
import live.urfu.frontend.data.model.Comment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import live.urfu.frontend.data.api.BaseViewModel

class CommentsViewModel(private val postId: Long,  private val onCommentAdded: (() -> Unit)? = null) : BaseViewModel() {

    private val commentApiService = CommentApiService()
    private val dtoManager = DtoManager()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadComments()
    }

    private fun loadComments() {
        launchApiCall(
            tag = "CommentsViewModel",
            action = { commentApiService.getAll(postId) },
            onSuccess = { commentsDto ->
                _comments.value = commentsDto.map { dtoManager.run { it.toComment() } }
            },
            onError = { error ->
                _error.value = error.message ?: "Ошибка загрузки комментариев"
            }
        )
    }

    fun sendComment(text: String) {
        launchApiCall(
            tag = "CommentsViewModel",
            action = { commentApiService.create(postId, text) },
            onSuccess = {
                onCommentAdded?.invoke()
                loadComments()
            },
            onError = { error ->
                _error.value = error.message ?: "Ошибка отправки комментария"
            }
        )
    }
}

class CommentsViewModelFactory(
    private val postId: Long,
    private val onCommentAdded: (() -> Unit)? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CommentsViewModel::class.java)) {
            return CommentsViewModel(postId, onCommentAdded) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}