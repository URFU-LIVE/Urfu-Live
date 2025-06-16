package live.urfu.frontend.ui.profile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import live.urfu.frontend.data.api.UserApiService
import live.urfu.frontend.data.manager.DtoManager
import live.urfu.frontend.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import live.urfu.frontend.data.api.BaseViewModel

class EditProfileViewModel : BaseViewModel() {

    private val userApiService = UserApiService()

    private var selectedAvatarUri by mutableStateOf<Uri?>(null)

    private var selectedBackgroundUri by mutableStateOf<Uri?>(null)

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    init {
        fetchUser()
    }

    fun onAvatarImageSelected(context: Context, uri: Uri) {
        selectedAvatarUri = uri
        uploadImage(context, uri, isAvatar = true)
    }

    fun onBackgroundImageSelected(context: Context, uri: Uri) {
        selectedBackgroundUri = uri
        uploadImage(context, uri, isAvatar = false)
    }

    private fun fetchUser() {
        launchApiCall(
            tag = "EditProfileViewModel",
            action = { userApiService.getUserProfile() },
            onSuccess = { userDto ->
                _user.value = DtoManager().run { userDto.toUser() }
            },
            onError = { it.printStackTrace() }
        )
    }

    private fun uploadImage(context: Context, uri: Uri, isAvatar: Boolean) {
        launchApiCall(
            tag = if (isAvatar) "UploadAvatar" else "UploadBackground",
            action = {
                val bitmap = uri.toBitmap(context) ?: return@launchApiCall Result.failure(
                    Exception(
                        "Bitmap is null"
                    )
                )
                if (isAvatar) {
                    userApiService.updateAvatar(bitmap)
                } else {
                    userApiService.updateBackground(bitmap)
                }
            },
            onSuccess = {
                // Success UI hook
            },
            onError = {
                it.printStackTrace()
            }
        )
    }

    fun updateUsername(username: String) {
        launchApiCall(
            tag = "EditProfileViewModel",
            action = { userApiService.updateUsername(username) },
            onSuccess = { /* update state or notify */ },
            onError = { it.printStackTrace() }
        )
    }

    fun updateDescription(description: String) {
        launchApiCall(
            tag = "EditProfileViewModel",
            action = { userApiService.updateDescription(description) },
            onSuccess = { /* update state or notify */ },
            onError = { it.printStackTrace() }
        )
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