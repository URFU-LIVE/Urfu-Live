package live.urfu.frontend.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchBarViewModel : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _tagSuggestions = MutableStateFlow<List<String>>(emptyList())
    val tagSuggestions: StateFlow<List<String>> = _tagSuggestions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _showSuggestions = MutableStateFlow(false)
    val showSuggestions: StateFlow<Boolean> = _showSuggestions.asStateFlow()

    private var searchJob: Job? = null

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query

        if (query.isNotBlank()) {
            loadTagSuggestions(query)
            _showSuggestions.value = true
        } else {
            viewModelScope.launch {
                delay(150) // Даем время анимации начаться
                _showSuggestions.value = false
            }
        }
    }

    fun hideSuggestions() {
        _showSuggestions.value = false
        _tagSuggestions.value = emptyList()
    }

    private fun loadTagSuggestions(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            try {
                delay(SearchTheme.Animation.DEBOUNCE_DELAY)

                _isLoading.value = true

                // Имитируем сетевой запрос
                delay(100)

                val suggestions = filterTags(query)

                _tagSuggestions.value = suggestions
                _isLoading.value = false

            } catch (e: Exception) {
                _isLoading.value = false
                _tagSuggestions.value = emptyList()

                e.printStackTrace()
            }
        }
    }

    private fun filterTags(query: String): List<String> {
        if (query.isBlank()) return emptyList()

        return TagsData.popularTags
            .filter { tag ->
                tag.lowercase().contains(query.lowercase().trim())
            }
            .take(SearchTheme.Config.MAX_SUGGESTIONS)
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }
}