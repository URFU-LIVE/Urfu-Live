package com.example.urfulive.ui.comments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urfulive.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CommentsViewModel : ViewModel() {
    private val _comments = MutableStateFlow<List<Comment>>(
        listOf(
            Comment(
                id = "1", authorId = "1",
                authorName = "1",
                authorProfileImage = R.drawable.profile,
                text = "Тестер",
                date = "5123123123123",
                postId = "1",
            )
        )
    )
    val comments = _comments.asStateFlow()

    fun loadComments(postId: String) {
        viewModelScope.launch {
            // Загрузка комментариев с бэкенда
            // _comments.value = результат
        }
    }

    fun addComment(postId: String, text: String) {
        // Отправка комментария на бэкенд
    }
}