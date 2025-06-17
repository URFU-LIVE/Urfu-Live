package live.urfu.frontend.ui.interests

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import live.urfu.frontend.data.api.BaseViewModel
import live.urfu.frontend.data.manager.InterestManagerInstance
import live.urfu.frontend.data.model.Interest

class InterestsViewModel : BaseViewModel() {

    val allInterests: List<Interest> get() = InterestsRepository.ALL_INTERESTS

    private val _selectedInterests = MutableStateFlow(setOf<Interest>())
    val selectedInterests = _selectedInterests.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var isInitialized = false

    init {
        loadSavedInterests()
    }

    private fun loadSavedInterests() {
        if (isInitialized) return

        _isLoading.value = true

        launchApiCall(
            tag = "InterestsViewModel",
            action = {
                val interestManager = InterestManagerInstance.getInstance()
                interestManager.migrateOldData()
                runCatching { interestManager.getSelectedInterestsBlocking() }
            },
            onSuccess = { savedInterestNames ->
                val savedInterests = InterestsRepository.fromNameEnSet(savedInterestNames)
                _selectedInterests.value = savedInterests
                isInitialized = true
                _isLoading.value = false
            },
            onError = {
                _selectedInterests.value = emptySet()
                _isLoading.value = false
            }
        )
    }


    fun onToggleInterest(interest: Interest) {
        val current = _selectedInterests.value
        val newSelection = if (current.contains(interest)) current - interest else current + interest
        _selectedInterests.value = newSelection
    }

    fun saveInterests() {
        launchApiCall(
            tag = "InterestsViewModel",
            action = {
                runCatching {
                    InterestManagerInstance.getInstance().saveSelectedInterests(_selectedInterests.value)
                }
            },
            onSuccess = {
                // optionally handle success
            },
            onError = { e ->
                println("❌ live.urfu.frontend.ui.interests.InterestsViewModel: Ошибка сохранения интересов: ${e.message}")
            }
        )
    }
}
