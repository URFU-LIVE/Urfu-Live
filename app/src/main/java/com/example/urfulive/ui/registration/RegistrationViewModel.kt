import androidx.lifecycle.ViewModel
import com.example.urfulive.data.DTOs.AuthResponse
import com.example.urfulive.data.api.UserApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegistrationViewModel : ViewModel() {
    interface RegisterCallback {
        fun onSuccess(user: AuthResponse)
        fun onError(error: Exception)
    }
    private val userApiService = UserApiService()


    private val _login = MutableStateFlow("")
    val login = _login.asStateFlow()

    private val _mail = MutableStateFlow("")
    val mail = _mail.asStateFlow()

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

    fun onMailChange(newValue: String) {
        _mail.value = newValue
    }

    fun onBirthDateChange(newValue: String) {
        _birthDate.value = newValue
    }

    fun onPasswordChange(newValue: String) {
        _password.value = newValue
    }

    fun onRegisterClick(username: String, email: String, password: String, fio: String, birthDate: String, callback: RegisterCallback) {
        var fioSplit = fio.split(" ")
        println("Нажата кнопка")
        //userApiService.register(username,email,password,fioSplit[0],fioSplit[1],birthDate)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = userApiService.register(
                    username,
                    email,
                    password,
                    fioSplit[0],
                    fioSplit[1],
                    birthDate
                )

                // Переключаемся на главный поток для обратного вызова
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

    fun onLogoClick(newValue: String) {

    }


}