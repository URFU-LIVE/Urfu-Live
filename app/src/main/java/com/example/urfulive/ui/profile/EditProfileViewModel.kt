package com.example.urfulive.ui.profile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urfulive.data.api.UserApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditProfileViewModel: ViewModel() {

     val userApiService: UserApiService = UserApiService()

    var selectedAvatarUri by mutableStateOf<Uri?>(null)
        private set

    var selectedBackgroundUri by mutableStateOf<Uri?>(null)
        private set

    fun onAvatarImageSelected(context: Context, uri: Uri) {
        selectedAvatarUri = uri
        uploadAvatar(context, uri)
    }

    fun onBackgroundImageSelected(context: Context, uri: Uri) {
        selectedBackgroundUri = uri
        uploadBackground(context, uri)
    }

    private fun uploadAvatar(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val bitmap = withContext(Dispatchers.IO) {
                    uri.toBitmap(context)
                }
                bitmap?.let {
                    val result = userApiService.updateAvatar(it)
                    result.onSuccess {
                        // Обработка успешной загрузки
                    }.onFailure {
                        // Обработка ошибки
                    }
                }
            } catch (e: Exception) {
                // Ошибки игнорируем
            }
        }
    }

    private fun uploadBackground(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val bitmap = withContext(Dispatchers.IO) {
                    uri.toBitmap(context)
                }
                bitmap?.let {
                    val result = userApiService.updateBackground(it)
                    result.onSuccess {
                        // Обработка успешной загрузки
                    }.onFailure {
                        // Обработка ошибки
                    }
                }
            } catch (e: Exception) {
                // Ошибки игнорируем
            }
        }
    }

    fun updateUsername(username: String) {
        viewModelScope.launch {
            val result = userApiService.updateUsername(username)
            result.onSuccess {

            }

            result.onFailure {
                it.printStackTrace()
            }
        }
    }

    fun updateDescription(description: String) {
        viewModelScope.launch {
            val result = userApiService.updateDescription(description)
            result.onSuccess {

            }

            result.onFailure {
                it.printStackTrace()
            }
        }
    }

    private suspend fun Uri.toBitmap(context: Context): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(this@toBitmap)?.use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}