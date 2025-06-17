import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import live.urfu.frontend.data.DTOs.AuthResponse
import live.urfu.frontend.data.api.UserApiService
import live.urfu.frontend.data.manager.TokenManagerInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import live.urfu.frontend.data.api.BaseViewModel
import live.urfu.frontend.data.api.CheckerApiService
import java.time.LocalDate

class RegistrationViewModel : BaseViewModel() {

    interface RegisterCallback {
        fun onSuccess(user: AuthResponse)
        fun onError(error: Exception)
    }

    private val userApiService = UserApiService()
    private val checkerApiService = CheckerApiService()

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

    @RequiresApi(Build.VERSION_CODES.O)
    fun onRegisterClick(
        username: String,
        email: String,
        password: String,
        fio: String,
        birthDate: String,
        callback: RegisterCallback
    ) {
        val fioSplit = fio.split(" ")
        val formattedBirthDate = formatBirthDateForApi(birthDate)

        if (formattedBirthDate == null || fioSplit.size < 2) {
            callback.onError(IllegalArgumentException("Неверный формат данных"))
            return
        }

        launchApiCall(
            tag = "RegistrationViewModel",
            action = {
                userApiService.register(
                    username,
                    email,
                    password,
                    fioSplit[0],
                    fioSplit[1],
                    formattedBirthDate
                )
            },
            onSuccess = { authResponse ->
                val userId = JwtParser.extractUserIdFromToken(authResponse.accessToken)
                if (userId != null ){
                    viewModelScope.launch {
                        TokenManagerInstance.getInstance().saveID(userId)
                    }
                }
                callback.onSuccess(authResponse)
            }
,
            onError = { error ->
                callback.onError(error as Exception)
            }
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatBirthDateForApi(raw: String): String? {
        return try {
            if (raw.length != 8) return null

            val day = raw.substring(0, 2)
            val month = raw.substring(2, 4)
            val year = raw.substring(4, 8)

            val localDate = LocalDate.of(year.toInt(), month.toInt(), day.toInt())
            localDate.atStartOfDay().toString()
        } catch (e: Exception) {
            null
        }
    }

    fun checkUsername(username: String, callback: (Boolean) -> Unit) {
        launchApiCall(
            tag = "RegistrationViewModel",
            action = {
                checkerApiService.checkUsername(username)
            },
            onSuccess = { callback(it.available) },
            onError = { println(it.message) }
        )
    }

    fun checkEmail(email: String, callback: (Boolean) -> Unit) {
        launchApiCall(
            tag = "RegistrationViewModel",
            action = {
                checkerApiService.checkEmail(email)
            },
            onSuccess = { callback(it.available) },
            onError = { println(it.message) }
        )
    }
}
