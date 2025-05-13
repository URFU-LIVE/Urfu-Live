package com.example.urfulive.ui.comments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.urfulive.data.DTOs.CommentDto
import com.example.urfulive.data.api.CommentApiService
import com.example.urfulive.data.manager.DtoManager
import com.example.urfulive.data.model.Comment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CommentsViewModel(
    private val postId: Long
) : ViewModel() {

    private val commentApiService = CommentApiService()
    private val dtoManager = DtoManager()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    private val _replyingTo = MutableStateFlow<Comment?>(null)
    val replyingTo: StateFlow<Comment?> = _replyingTo.asStateFlow()

    init {
        loadComments()
    }

    fun loadComments() {
        viewModelScope.launch {
            commentApiService.getAll(postId).onSuccess { commentsDto ->
                _comments.value = commentsDto.map { dtoManager.run { it.toComment() } }
            }.onFailure {
                // Handle error
            }
        }
    }

//    fun addComment(text: String) {
//        viewModelScope.launch {
//            val commentDto = CommentDto(
//                text = text,
//                postId = postId,
//                parentCommentId = _replyingTo.value?.id
//            )
//
//            commentApiService.createComment(commentDto).onSuccess { newCommentDto ->
//                _comments.update { currentComments ->
//                    currentComments + dtoManager.run { newCommentDto.toComment() }
//                }
//                _replyingTo.value = null
//            }.onFailure {
//                // Handle error
//            }
//        }
//    }
//
//    fun toggleLike(comment: Comment) {
//        viewModelScope.launch {
//            commentApiService.toggleLike(comment.id).onSuccess {
//                loadComments() // Refresh comments after like
//            }.onFailure {
//                // Handle error
//            }
//        }
//    }

    fun setReplyingTo(comment: Comment?) {
        _replyingTo.value = comment
    }
}

class CommentsViewModelFactory(private val postId: Long) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CommentsViewModel::class.java)) {
            return CommentsViewModel(postId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}