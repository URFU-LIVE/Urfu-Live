import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RegistrationViewModel : ViewModel() {
    private val _login = MutableStateFlow("")
    val login = _login.asStateFlow()

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _birthDate = MutableStateFlow("")
    val birthDate = _birthDate.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    fun onLoginChange(newValue: String) {
        _login.value = newValue
    }

    fun onNameChange(newValue: String) {
        _name.value = newValue
    }

    fun onBirthDateChange(newValue: String) {
        _birthDate.value = newValue
    }

    fun onPasswordChange(newValue: String) {
        _password.value = newValue
    }

    fun onRegisterClick() {
        // Логика регистрации (валидация, запрос к серверу и т.д.)
    }

    fun onLogoClick(newValue: String) {

    }

}
