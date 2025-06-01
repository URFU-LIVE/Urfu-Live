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

    init {
        loadComments()
    }

    private fun loadComments() {
        viewModelScope.launch {
            commentApiService.getAll(postId).onSuccess { commentsDto ->
                _comments.value = commentsDto.map { dtoManager.run { it.toComment() } }
            }.onFailure {
                // Handle error
            }
        }
    }

    fun sendComment(text: String) {
        viewModelScope.launch {
            commentApiService.create(postId, text).onSuccess {
                loadComments()
            }
        }
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