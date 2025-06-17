import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import live.urfu.frontend.data.DTOs.AuthResponse
import live.urfu.frontend.data.api.BaseViewModel
import live.urfu.frontend.data.api.UserApiService

class LoginViewModel : BaseViewModel() {

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

    fun onLoginClick(login: String, password: String, callback: LoginCallback) {
        launchApiCall(
            tag = "LoginViewModel",
            action = { userApiService.login(login, password) },
            onSuccess = { callback.onSuccess(it) },
            onError = { callback.onError(it as Exception) }
        )
    }

    fun onRestorePasswordClick() {
        // TODO: Реализовать переход на восстановление пароля - Сделать заглушка пока
    }

}