// app/src/main/java/com/example/urfulive/ui/components/viewmodel/TagInputViewModel.kt
package com.example.urfulive.ui.components.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urfulive.data.model.Tag
import com.example.urfulive.data.service.SimpleTagService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TagInputUiState(
    val allTags: List<Tag> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class TagInputViewModel @Inject constructor(
    private val tagService: SimpleTagService
) : ViewModel() {

    private val _uiState = MutableStateFlow(TagInputUiState())
    val uiState: StateFlow<TagInputUiState> = _uiState.asStateFlow()

    init {
        loadAllTags()
    }

    /**
     * Загрузка всех тегов с сервера
     */
    fun loadAllTags() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            tagService.getAllTags()
                .onSuccess { tags ->
                    _uiState.update {
                        it.copy(
                            allTags = tags,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Ошибка загрузки тегов: ${exception.message}"
                        )
                    }
                }
        }
    }

    /**
     * Получение предложений тегов для автокомплита
     */
    fun getTagSuggestions(
        query: String,
        selectedTags: List<Tag>,
        limit: Int = 8
    ): List<Tag> {
        if (query.length < 2) return emptyList()

        val normalizedQuery = query.trim().lowercase()
        val currentTags = _uiState.value.allTags

        return currentTags
            .filter { tag ->
                // Исключаем уже выбранные теги
                !selectedTags.any { it.id == tag.id } &&
                        // Ищем совпадения в названии
                        tag.name.lowercase().contains(normalizedQuery)
            }
            .sortedWith(compareBy<Tag> {
                // Сначала теги, которые начинаются с запроса
                !it.name.lowercase().startsWith(normalizedQuery)
            }.thenBy { it.name })
            .take(limit)
    }

    /**
     * Очистка ошибки
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Принудительное обновление кэша
     */
    fun refreshTags() {
        tagService.clearCache()
        loadAllTags()
    }
}