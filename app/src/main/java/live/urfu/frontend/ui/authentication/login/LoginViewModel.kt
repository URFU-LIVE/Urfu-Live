import android.util.Log
import androidx.lifecycle.ViewModel
import live.urfu.frontend.data.DTOs.AuthResponse
import live.urfu.frontend.data.api.UserApiService
import live.urfu.frontend.ui.main.PostViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel : ViewModel() {
    interface LoginCallback {
        fun onSuccess(user: AuthResponse)
        fun onError(error: Exception)
    }
    private val userApiService = UserApiService()

    private val _login = MutableStateFlow("")
    val login = _login.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    // Обработка изменения поля «Логин»
    fun onLoginChange(newValue: String) {
        _login.value = newValue
    }

    // Обработка изменения поля «Пароль»
    fun onPasswordChange(newValue: String) {
        _password.value = newValue
    }

    // Нажатие на кнопку «Войти»
    fun onLoginClick(login: String, password: String, callback: LoginCallback, postViewModel: PostViewModel? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("LoginViewModel", "🔑 LOGIN ATTEMPT for user: $login")
                val result = userApiService.login(login, password)

                withContext(Dispatchers.Main) {
                    if (result.isSuccess) {
                        callback.onSuccess(result.getOrThrow())
                    } else {
                        callback.onError(Exception("Неизвестная ошибка"))
                    }
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "💥 LOGIN EXCEPTION: ${e.message}")
                withContext(Dispatchers.Main) {
                    callback.onError(e)
                }
            }
        }
    }

    // Нажатие на «Восстановить пароль»
    fun onRestorePasswordClick() {
        // Логика восстановления пароля (переход на экран восстановления, запрос к серверу и т.д.)
    }

    // Нажатие на «Зарегистрируйтесь»
    fun onRegisterClick() {
        // Переход на экран регистрации, если требуется
    }
}
