package live.urfu.frontend.ui.notifiaction

import live.urfu.frontend.data.model.Notification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeNotificationViewModel : NotificationViewModel() {

    private val _notifications = MutableStateFlow(listOf(
        Notification(
            id = 1,
            title = "Добро пожаловать!",
            message = "Спасибо за регистрацию в UrfuLive.",
            time = "10:30",
            isRead = false
        ),
        Notification(
            id = 2,
            title = "Новое обновление",
            message = "Мы добавили новые функции! Ознакомьтесь прямо сейчас.",
            time = "12:45",
            isRead = true
        ),
        Notification(
            id = 3,
            title = "Событие сегодня",
            message = "Не пропустите лекцию по Android-разработке в 17:00.",
            time = "15:10",
            isRead = false
        )
    ))

    override val notifications: StateFlow<List<Notification>> get() = _notifications
}

