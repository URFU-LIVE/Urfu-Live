import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel : ViewModel() {

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
    fun onLoginClick() {
        // Здесь логика авторизации: валидация полей, запрос к серверу и т.д.
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
