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

class EditProfileViewModel(
    private val userApiService: UserApiService = UserApiService()
) : ViewModel() {

    var selectedImageUri by mutableStateOf<Uri?>(null)
        private set

    fun onImageSelected(context: Context, uri: Uri?) {
        selectedImageUri = uri
        uri?.let { uploadImage(context, it) }
    }

    private fun uploadImage(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val bitmap = withContext(Dispatchers.IO) {
                    uri.toBitmap(context)
                }
                bitmap?.let { userApiService.updatePhoto(it) }
            } catch (e: Exception) {
                // Ошибки игнорируем
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