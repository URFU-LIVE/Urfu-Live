package live.urfu.frontend.ui.interests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import live.urfu.frontend.data.manager.InterestManagerInstance
import live.urfu.frontend.data.model.Interest
import live.urfu.frontend.data.repository.InterestsRepository

class InterestsViewModel : ViewModel() {

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

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val interestManager = InterestManagerInstance.getInstance()

                interestManager.migrateOldData()

                val savedInterestNames = interestManager.getSelectedInterestsBlocking()
                val savedInterests = InterestsRepository.fromNameEnSet(savedInterestNames)
                _selectedInterests.value = savedInterests

                isInitialized = true

            } catch (e: Exception) {
                _selectedInterests.value = emptySet()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onToggleInterest(interest: Interest) {
        val current = _selectedInterests.value
        val newSelection = if (current.contains(interest)) current - interest else current + interest

        _selectedInterests.value = newSelection
    }

    fun saveInterests() {
        viewModelScope.launch {
            try {
                InterestManagerInstance.getInstance().saveSelectedInterests(_selectedInterests.value)
            } catch (e: Exception) {
                println("❌ live.urfu.frontend.ui.interests.InterestsViewModel: Ошибка сохранения интересов: ${e.message}")
            }
        }
    }
}
