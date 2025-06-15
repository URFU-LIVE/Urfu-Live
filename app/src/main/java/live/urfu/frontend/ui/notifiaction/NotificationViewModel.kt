package live.urfu.frontend.ui.notifiaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import live.urfu.frontend.data.api.NotificationApiService
import live.urfu.frontend.data.manager.DtoManager
import live.urfu.frontend.data.model.Notification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class NotificationViewModel : ViewModel() {

    private val notificationApiService = NotificationApiService()

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    open val notifications: StateFlow<List<Notification>> get() = _notifications

    init {
        viewModelScope.launch {
            fetchNotification()
        }
    }

    private fun fetchNotification() {
        viewModelScope.launch {
            val result = notificationApiService.getAll()
            result.onSuccess { notificationList ->
                val dtoManager = DtoManager()
                _notifications.value = notificationList.map { dtoManager.run { it.toNotification() } }
            }.onFailure {
                it.printStackTrace()
            }
        }
    }
}