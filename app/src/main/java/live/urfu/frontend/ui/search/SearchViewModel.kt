package live.urfu.frontend.ui.search

import android.util.Log
import androidx.lifecycle.viewModelScope
import live.urfu.frontend.data.api.PostApiService
import live.urfu.frontend.data.api.TagApiService
import live.urfu.frontend.data.manager.DtoManager
import live.urfu.frontend.data.model.Post
import live.urfu.frontend.data.model.Tag
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import live.urfu.frontend.data.api.BaseViewModel

class SearchViewModel : BaseViewModel() {

    private val postApiService = PostApiService()
    private val tagApiService = TagApiService()
    private val dtoManager = DtoManager()

    private val searchResultsMutex = Mutex()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Post>>(emptyList())
    val searchResults: StateFlow<List<Post>> = _searchResults.asStateFlow()

    private val _tagSuggestions = MutableStateFlow<List<String>>(emptyList())
    val tagSuggestions: StateFlow<List<String>> = _tagSuggestions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _showSuggestions = MutableStateFlow(false)
    val showSuggestions: StateFlow<Boolean> = _showSuggestions.asStateFlow()

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

    private val _hasSearched = MutableStateFlow(false)
    val hasSearched: StateFlow<Boolean> = _hasSearched.asStateFlow()

    private var searchJob: Job? = null
    private var suggestionJob: Job? = null

    private val _tags = MutableStateFlow<List<Tag?>>(emptyList())
    val tags: StateFlow<List<Tag?>> get() = _tags

    init {
        fetchTags()
    }

    private fun fetchTags() {
        launchApiCall(
            tag = "SearchViewModel",
            action = { tagApiService.getAll() },
            onSuccess = { _tags.value = it },
            onError = { it.printStackTrace() }
        )
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isNotBlank()) {
            loadTagSuggestions(query)
            _showSuggestions.value = true
        } else {
            _showSuggestions.value = false
            _tagSuggestions.value = emptyList()
        }
    }

    private fun loadTagSuggestions(query: String) {
        suggestionJob?.cancel()
        suggestionJob = viewModelScope.launch {
            delay(300) // debounce

            val trimmedQuery = query.lowercase().trim()
            val filtered = tags.value
                .asSequence()
                .filterNotNull()
                .filter { tag -> tag.name.lowercase().contains(trimmedQuery) }
                .sortedBy { tag ->
                    val name = tag.name.lowercase()
                    when {
                        name.startsWith(trimmedQuery) -> 0
                        name.contains(trimmedQuery) -> 1
                        else -> 2
                    }
                }
                .map { it.name }
                .take(5)
                .toList()

            _tagSuggestions.value = filtered
        }
    }

    fun searchByTag(tag: String) {
        if (tag.isBlank()) return

        addToRecentSearches(tag)
        _searchQuery.value = tag
        _showSuggestions.value = false
        _hasSearched.value = true

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _isLoading.value = true

            launchApiCall(
                tag = "SearchViewModel",
                action = { postApiService.searchByTag(tag) },
                onSuccess = { dtoList ->
                    _searchResults.value = dtoList.map { dtoManager.run { it.toPost() } }
                },
                onError = {
                    _searchResults.value = emptyList()
                }
            )

            _isLoading.value = false
        }
    }

    fun selectSuggestion(tag: String) {
        searchByTag(tag)
    }

    private fun addToRecentSearches(search: String) {
        val current = _recentSearches.value.toMutableList()
        current.remove(search)
        current.add(0, search)
        _recentSearches.value = current.take(5)
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _searchResults.value = emptyList()
        _showSuggestions.value = false
        _hasSearched.value = false
    }

    fun hideSuggestions() {
        _showSuggestions.value = false
    }

    class SearchBarAdapter(
        private val searchViewModel: SearchViewModel
    ) {
        val searchQuery = searchViewModel.searchQuery
        val tagSuggestions = searchViewModel.tagSuggestions
        val isLoading = searchViewModel.isLoading
        val showSuggestions = searchViewModel.showSuggestions

        fun updateSearchQuery(query: String) = searchViewModel.updateSearchQuery(query)
        fun hideSuggestions() = searchViewModel.hideSuggestions()
        fun selectSuggestion(suggestion: String) = searchViewModel.selectSuggestion(suggestion)
    }

    suspend fun updatePostInSearchResults(updatedPost: Post) {
        searchResultsMutex.withLock {
            val currentResults = _searchResults.value.toMutableList()
            val index = currentResults.indexOfFirst { it.id == updatedPost.id }
            if (index != -1) {
                currentResults[index] = updatedPost
                _searchResults.value = currentResults
                Log.d("SearchViewModel", "🔄 Updated post ${updatedPost.id} in search results")
            } else {
                Log.w("SearchViewModel", "⚠️ Post ${updatedPost.id} not found in search results")
            }
        }
    }
}
