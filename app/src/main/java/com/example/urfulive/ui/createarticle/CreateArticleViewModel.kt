package com.example.urfulive.ui.createarticle

import androidx.lifecycle.ViewModel
import com.example.urfulive.data.DTOs.DefaultResponse
import com.example.urfulive.data.api.PostApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateArticleViewModel : ViewModel() {

    private val postApiService = PostApiService()

    interface PostCallBack {
        fun onSuccess(user: DefaultResponse)
        fun onError(error: Exception)
    }

    fun onPublishClick(titleText: String, contentText: String, tagsText: String, callback: PostCallBack) {
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