package com.example.urfulive.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urfulive.data.api.PostApiService
import com.example.urfulive.data.api.TagApiService
import com.example.urfulive.data.manager.DtoManager
import com.example.urfulive.data.model.Post
import com.example.urfulive.data.model.Tag
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SearchViewModel : ViewModel() {

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

    // Список популярных тегов для подсказок
    private val mockTags = listOf(
        "Учеба", "Программирование", "Android", "Kotlin", "React", "JavaScript",
        "Веб-разработка", "Mobile", "UI/UX", "Дизайн", "Backend", "Frontend",
        "Искусственный интеллект", "Machine Learning", "Data Science", "DevOps",
        "Стартапы", "Бизнес", "Карьера", "Образование", "Наука", "Исследования",
        "Новости", "События", "Мероприятия", "Конференции", "Вебинары",
        "Спорт", "Здоровье", "Путешествия", "Фотография", "Музыка", "Кино",
        "Юмор", "Внеучебная деятельность", "Стажировка", "Знакомства", "Работа", "Волонтерство"
    )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query

        // Показываем подсказки только если есть текст
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
            delay(300) // Debounce

            // Фильтруем теги по введенному запросу
            val filtered = mockTags.filter { tag ->
                tag.lowercase().contains(query.lowercase())
            }.take(5)

            _tagSuggestions.value = filtered
        }
    }

    fun searchByTag(tag: String) {
        if (tag.isBlank()) return

        // Добавляем в недавние поиски
        addToRecentSearches(tag)

        // Обновляем запрос и скрываем подсказки
        _searchQuery.value = tag
        _showSuggestions.value = false
        _hasSearched.value = true

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _isLoading.value = true

            try {
                val result = postApiService.searchByTag(tag)
                result.onSuccess { postDtos ->
                    _searchResults.value = postDtos.map { dtoManager.run { it.toPost() } }
                }.onFailure {
                    _searchResults.value = emptyList()
                }
            } catch (e: Exception) {
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectSuggestion(tag: String) {
        searchByTag(tag)
    }

    private fun addToRecentSearches(search: String) {
        val current = _recentSearches.value.toMutableList()
        current.remove(search) // Удаляем если уже есть
        current.add(0, search) // Добавляем в начало
        _recentSearches.value = current.take(5) // Ограничиваем до 5 элементов
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
        // Пробрасываем состояния
        val searchQuery = searchViewModel.searchQuery
        val tagSuggestions = searchViewModel.tagSuggestions
        val isLoading = searchViewModel.isLoading
        val showSuggestions = searchViewModel.showSuggestions

        // Пробрасываем методы
        fun updateSearchQuery(query: String) {
            searchViewModel.updateSearchQuery(query)
        }

        fun hideSuggestions() {
            searchViewModel.hideSuggestions()
        }

        fun selectSuggestion(suggestion: String) {
            searchViewModel.selectSuggestion(suggestion)
        }
    }

    suspend fun updatePostInSearchResults(updatedPost: Post) {
        searchResultsMutex.withLock {
            val currentResults = _searchResults.value.toMutableList()
            val index = currentResults.indexOfFirst { it.id == updatedPost.id }
            if (index != -1) {
                currentResults[index] = updatedPost
                _searchResults.value = currentResults
                Log.d("SearchViewModel", "🔄 Updated post ${updatedPost.id} in search results")
                Log.d("SearchViewModel", "   New likes: ${updatedPost.likes}, likedBy: ${updatedPost.likedBy}")
            } else {
                Log.w("SearchViewModel", "⚠️ Post ${updatedPost.id} not found in search results for update")
            }
        }
    }
}