package com.example.urfulive.ui.comments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urfulive.R
import com.example.urfulive.data.DTOs.CommentDto
import com.example.urfulive.data.api.CommentApiService
import com.example.urfulive.data.manager.DtoManager
import com.example.urfulive.data.model.Notification
import com.example.urfulive.data.model.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class CommentsViewModel(
    private val post: Post
) : ViewModel() {

    private val commentApiService = CommentApiService()

    private var _comments = MutableStateFlow<List<CommentDto>>(emptyList())
    open val comments: StateFlow<List<CommentDto>> get() = _comments

    init {
        fetchPost()
    }

    private fun fetchPost() {
        viewModelScope.launch {
            val result = commentApiService.getAll(post.id)
            result.onSuccess { commentsList ->
                _comments.emit(commentsList)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }
}