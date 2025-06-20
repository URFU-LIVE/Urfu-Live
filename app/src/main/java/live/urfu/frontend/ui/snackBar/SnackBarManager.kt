package live.urfu.frontend.ui.snackBar

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class SnackBarType {
    SUCCESS, ERROR, INFO
}

data class SnackBarMessage(
    val message: String,
    val type: SnackBarType = SnackBarType.SUCCESS,
    val duration: Long = 3000L,
    val id: String = "${message}_${type}_${System.currentTimeMillis()}"
)

class SnackBarManager {
    private val _queue = mutableListOf<SnackBarMessage>()
    private val _currentMessage = MutableStateFlow<SnackBarMessage?>(null)
    val currentMessage: StateFlow<SnackBarMessage?> = _currentMessage.asStateFlow()

    companion object {
        private const val MAX_QUEUE_SIZE = 5
    }

    fun showMessage(message: SnackBarMessage) {
        if (_currentMessage.value?.message == message.message &&
            _currentMessage.value?.type == message.type) {
            return
        }

        if (_queue.any { it.message == message.message && it.type == message.type }) {
            return
        }

        if (_currentMessage.value == null) {
            _currentMessage.value = message
        } else {
            if (_queue.size < MAX_QUEUE_SIZE) {
                _queue.add(message)
            }
        }
    }

    fun dismissCurrent() {
        _currentMessage.value = null
        if (_queue.isNotEmpty()) {
            val nextMessage = _queue.removeAt(0)
            _currentMessage.value = nextMessage
        }
    }
}
