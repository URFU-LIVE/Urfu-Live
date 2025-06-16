import android.util.Log
import androidx.lifecycle.ViewModel
import live.urfu.frontend.data.DTOs.AuthResponse
import live.urfu.frontend.data.api.UserApiService
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

    fun onLoginChange(newValue: String) {
        _login.value = newValue
    }

    fun onPasswordChange(newValue: String) {
        _password.value = newValue
    }

    // todo Что-то сделать с логированием
    fun onLoginClick(login: String, password: String, callback: LoginCallback) {
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

    // todo Убрать уже проход
    // Нажатие на «Восстановить пароль»
    fun onRestorePasswordClick() {
        // Логика восстановления пароля (переход на экран восстановления, запрос к серверу и т.д.)
    }

    // Нажатие на «Зарегистрируйтесь»
    fun onRegisterClick() {
        // Переход на экран регистрации, если требуется
    }
}
