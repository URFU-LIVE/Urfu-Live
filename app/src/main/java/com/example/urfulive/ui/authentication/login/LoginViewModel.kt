import androidx.lifecycle.ViewModel
import com.example.urfulive.data.DTOs.AuthResponse
import com.example.urfulive.data.api.UserApiService
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
    fun onLoginClick(login: String, password: String, callback: LoginCallback) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = userApiService.login(
                    login,
                    password,
                )

                // Переключаемся на главный поток для обратного вызова
                withContext(Dispatchers.Main) {
                    if (result.isSuccess) {
                        TokenManagerInstance.getInstance().saveID(
                            userApiService.getUserProfile().getOrNull()?.id.toString()
                        )
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

    // Нажатие на «Восстановить пароль»
    fun onRestorePasswordClick() {
        // Логика восстановления пароля (переход на экран восстановления, запрос к серверу и т.д.)
    }

    // Нажатие на «Зарегистрируйтесь»
    fun onRegisterClick() {
        // Переход на экран регистрации, если требуется
    }
}
