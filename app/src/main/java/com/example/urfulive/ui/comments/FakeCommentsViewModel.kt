package com.example.urfulive.ui.comments

import com.example.urfulive.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeCommentsViewModel : CommentsViewModel() {
    private val _comments = MutableStateFlow<List<Comment>>(sampleComments())
    override val comments: StateFlow<List<Comment>> = _comments
}

fun sampleComments(): List<Comment> {
    val reply = Comment(
        id = "2",
        authorId = "user2",
        authorName = "Иван",
        authorProfileImage = R.drawable.profile,
        text = "Это ответ",
        date = "10 мин назад",
        postId = "1",
        parentId = "1"
    )

    val comment = Comment(
        id = "1",
        authorId = "user1",
        authorName = "Мария",
        authorProfileImage = R.drawable.profile,
        text = "Это комментарий",
        date = "15 мин назад",
        postId = "1",
        replies = listOf(reply)
    )

    return listOf(comment)
}
