package live.urfu.frontend.data.api

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseViewModel : ViewModel() {
    fun <T> launchApiCall(
        tag: String = "ApiRequest",
        action: suspend () -> Result<T>,
        onSuccess: (T) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            apiRequest(tag, action, onSuccess, onError)
        }
    }

    companion object {
        suspend fun <T> apiRequest(
            tag: String = "ApiRequest",
            action: suspend () -> Result<T>,
            onSuccess: (T) -> Unit,
            onError: (Throwable) -> Unit
        ) {
            withContext(Dispatchers.IO) {
                try {
                    Log.d(tag, "🚀 Calling API")
                    val result = action()

                    withContext(Dispatchers.Main) {
                        result.onSuccess {
                            Log.d(tag, "✅ Success")
                            onSuccess(it)
                        }.onFailure {
                            Log.e(tag, "❌ Failure: ${it.message}")
                            onError(it)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(tag, "💥 Exception: ${e.message}", e)
                    withContext(Dispatchers.Main) {
                        onError(e)
                    }
                }
            }
        }
    }
}


