package live.urfu.frontend.ui.notifiaction

import live.urfu.frontend.data.api.NotificationApiService
import live.urfu.frontend.data.manager.DtoManager
import live.urfu.frontend.data.model.Notification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import live.urfu.frontend.data.api.BaseViewModel

open class NotificationViewModel : BaseViewModel() {

    private val notificationApiService = NotificationApiService()

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    open val notifications: StateFlow<List<Notification>> get() = _notifications

    init {
        fetchNotification()
    }

    private fun fetchNotification() {
        launchApiCall(
            tag = "NotificationViewModel",
            action = { notificationApiService.getAll() },
            onSuccess = { notificationList ->
                val dtoManager = DtoManager()
                _notifications.value = notificationList.map { dtoManager.run { it.toNotification() } }
            },
            onError = {
                it.printStackTrace()
            }
        )
    }
}
