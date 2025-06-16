import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import live.urfu.frontend.data.DTOs.AuthResponse
import live.urfu.frontend.data.api.UserApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import live.urfu.frontend.data.manager.TokenManagerInstance
import java.time.LocalDate

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

    @RequiresApi(Build.VERSION_CODES.O)
    fun onRegisterClick(username: String, email: String, password: String, fio: String, birthDate: String, callback: RegisterCallback) {
        val fioSplit = fio.split(" ")
        val formattedBirthDate = formatBirthDateForApi(birthDate)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = userApiService.register(
                    username,
                    email,
                    password,
                    fioSplit[0],
                    fioSplit[1],
                    formattedBirthDate.toString()
                )

                withContext(Dispatchers.Main) {
                    if (result.isSuccess) {
                        val authResponse = result.getOrThrow()
                        val userId = JwtParser.extractUserIdFromToken(authResponse.accessToken)


                        if (userId != null) {
                            TokenManagerInstance.getInstance().saveID(userId)
                            Log.d("LoginViewModel", "✅ Saved User ID from JWT: $userId")
                        } else {
                            Log.e("LoginViewModel", "❌ Failed to extract User ID from JWT")
                        }
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatBirthDateForApi(raw: String): String? {
        return try {
            if (raw.length != 8) return null // Некорректная длина

            val day = raw.substring(0, 2)
            val month = raw.substring(2, 4)
            val year = raw.substring(4, 8)

            val localDate = LocalDate.of(year.toInt(), month.toInt(), day.toInt())
            localDate.atStartOfDay().toString() // Вернет в формате "2000-01-01T00:00:00"
        } catch (e: Exception) {
            null
        }
    }
}