package live.urfu.frontend.ui.settings.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import live.urfu.frontend.data.api.UserApiService
import live.urfu.frontend.data.manager.DtoManager
import live.urfu.frontend.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AccountViewModel : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val userApiService = UserApiService()

    init {
        fetchUser()
    }

    private fun fetchUser() {
        viewModelScope.launch {
            val result = userApiService.getUserProfile()
            result.onSuccess { userDto ->
                val dtoManager = DtoManager()
                _user.value = dtoManager.run { userDto.toUser() }
            }
            result.onFailure {
            }
        }
    }
}