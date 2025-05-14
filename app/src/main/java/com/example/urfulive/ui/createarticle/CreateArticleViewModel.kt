package com.example.urfulive.ui.createarticle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urfulive.data.DTOs.DefaultResponse
import com.example.urfulive.data.api.PostApiService
import com.example.urfulive.data.api.UserApiService
import com.example.urfulive.data.manager.DtoManager
import com.example.urfulive.data.model.Notification
import com.example.urfulive.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class CreateArticleViewModel : ViewModel() {

    private val postApiService = PostApiService()
    private val userApiService = UserApiService()

    private val _user = MutableStateFlow<User?>(null)
    open val user: StateFlow<User?> get() = _user

    init {
       fetchUser()
    }

    fun fetchUser() {
        viewModelScope.launch {
            userApiService.getUserProfile().onSuccess { userDto ->
                val dtoManager = DtoManager()
                _user.value = dtoManager.run { userDto.toUser() }
            }
        }
    }

    interface PostCallBack {
        fun onSuccess(user: DefaultResponse)
        fun onError(error: Exception)
    }

    open fun onPublishClick(titleText: String, contentText: String, tagsText: String, callback: PostCallBack) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = postApiService.create(
                    titleText,
                    contentText,
                )

                withContext(Dispatchers.Main) {
                    if (result.isSuccess) {
                        callback.onSuccess(result.getOrThrow())
                    } else {
                        callback.onError(Exception("Неизвестная ошибка"))
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onError(e)
                }
            }
        }
    }
}