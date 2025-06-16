package live.urfu.frontend.ui.settings

import live.urfu.frontend.data.api.UserApiService
import live.urfu.frontend.data.manager.DtoManager
import live.urfu.frontend.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import live.urfu.frontend.data.api.BaseViewModel

class MainSettingViewModel : BaseViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val userApiService = UserApiService()

    init {
        fetchUser()
    }

    private fun fetchUser() {
        launchApiCall(
            tag = "MainSettingViewModel",
            action = { userApiService.getUserProfile() },
            onSuccess = { userDto ->
                _user.value = DtoManager().run { userDto.toUser() }
            },
            onError = {
                // Обработка ошибок, если необходимо
            }
        )
    }
}
