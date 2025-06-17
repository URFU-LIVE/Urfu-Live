package live.urfu.frontend.ui.snackBar

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class SnackBarType {
    SUCCESS, ERROR, INFO
}

data class SnackBarMessage(
    val message: String,
    val type: SnackBarType = SnackBarType.SUCCESS,
    val duration: Long = 3000L
)

class SnackBarManager {
    private val _queue = mutableListOf<SnackBarMessage>()
    private val _currentMessage = MutableStateFlow<SnackBarMessage?>(null)
    val currentMessage: StateFlow<SnackBarMessage?> = _currentMessage.asStateFlow()

    fun showMessage(message: SnackBarMessage) {
        if (_currentMessage.value == null) {
            _currentMessage.value = message
        } else {
            _queue.add(message)
        }
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun dismissCurrent() {
        _currentMessage.value = null
        if (_queue.isNotEmpty()) {
            _currentMessage.value = _queue.removeFirst()
        }
    }
}
