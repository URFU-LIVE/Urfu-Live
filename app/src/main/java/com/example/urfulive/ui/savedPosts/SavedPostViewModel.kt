package com.example.urfulive.ui.savedPosts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urfulive.data.DTOs.PostDto
import com.example.urfulive.data.api.PostApiService
import com.example.urfulive.data.manager.DtoManager
import com.example.urfulive.data.manager.PostManagerInstance
import com.example.urfulive.data.model.Post
import com.example.urfulive.data.model.Tag
import com.example.urfulive.data.model.User
import com.example.urfulive.data.model.UserRole
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
        println("ААААА УДАЛЕНИЯ ПОСТА!!!!")
        viewModelScope.launch {
            PostManagerInstance.getInstance().removePost(post)
        }
        _savedPosts.value = _savedPosts.value.filter { it.id != post.id }
    }

    private fun getSampleSavedPosts(): List<Post> {
        val sampleUser = User(
            id = "1",
            username = "Peemkay_42",
            avatarUrl = "",
            backgroundUrl = "",
            description = "",
            followersCount = 42,
            followers = emptyList(),
            email = "asd",
            role = UserRole.WRITER
        )

        val sampleTags = listOf(
            Tag(1, "Учеба"),
            Tag(2, "42Братухи")
        )

        return listOf(
            Post(
                id = 1,
                title = "«42, Братухи»: что означает популярная фраза из «ТикТока»",
                text = "",
                author = sampleUser,
                tags = sampleTags,
                time = "",
                likes = 0,
                comments = 0
            ),
            Post(
                id = 2,
                title = "«42, Братухи»: что означает популярная фраза из «ТикТока»",
                text = "",
                author = sampleUser,
                tags = sampleTags,
                time = "",
                likes = 0,
                comments = 0
            ),
            Post(
                id = 3,
                title = "«42, Братухи»: что означает популярная фраза из «ТикТока»",
                text = "",
                author = sampleUser,
                tags = sampleTags,
                time = "",
                likes = 0,
                comments = 0
            ),
        )
    }
}