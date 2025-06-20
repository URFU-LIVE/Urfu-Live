package live.urfu.frontend.ui.createarticle

import live.urfu.frontend.data.DTOs.DefaultResponse
import live.urfu.frontend.data.api.PostApiService
import live.urfu.frontend.data.api.TagApiService
import live.urfu.frontend.data.api.UserApiService
import live.urfu.frontend.data.manager.DtoManager
import live.urfu.frontend.data.model.Tag
import live.urfu.frontend.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import live.urfu.frontend.data.DTOs.PostDto
import live.urfu.frontend.data.api.BaseViewModel

open class CreateArticleViewModel : BaseViewModel() {

    private val postApiService = PostApiService()
    private val userApiService = UserApiService()
    private val tagApiService = TagApiService()
    private val dtoManager = DtoManager()

    private val _user = MutableStateFlow<User?>(null)
    open val user: StateFlow<User?> = _user.asStateFlow()

    private val _tags = MutableStateFlow<List<Tag>>(emptyList())
    open val tags: StateFlow<List<Tag>> = _tags.asStateFlow()

    init {
        fetchUser()
        fetchTags()
    }

    private fun fetchUser() {
        launchApiCall(
            tag = "CreateArticleViewModel",
            action = { userApiService.getUserProfile() },
            onSuccess = { userDto -> _user.value = dtoManager.run { userDto.toUser() } },
            onError = { /* можно добавить StateFlow ошибки */ }
        )
    }

    private fun fetchTags() {
        launchApiCall(
            tag = "CreateArticleViewModel",
            action = { tagApiService.getAll() },
            onSuccess = { _tags.value = it },
            onError = { /* можно логировать или показывать ошибку */ }
        )
    }

    interface PostCallBack {
        fun onSuccess(user: PostDto)
        fun onError(error: Exception)
    }

    open fun onPublishClick(
        titleText: String,
        contentText: String,
        tagsText: String,
        callback: PostCallBack
    ) {
        val tags = tagsText.split(",").map { it.trim() }

        launchApiCall(
            tag = "CreateArticleViewModel",
            action = { postApiService.create(titleText, contentText, tags) },
            onSuccess = { callback.onSuccess(it) },
            onError = { callback.onError(it as Exception) }
        )
    }
}
