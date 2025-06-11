// app/src/main/java/com/example/urfulive/ui/components/SimpleTagInput.kt
package com.example.urfulive.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.urfulive.data.model.Tag
import com.example.urfulive.ui.components.viewmodel.TagInputViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTagInput(
    selectedTags: List<Tag>,
    onTagsChanged: (List<Tag>) -> Unit,
    modifier: Modifier = Modifier,
    maxTags: Int = 10,
    placeholder: String = "Добавить тег...",
    isEnabled: Boolean = true,
    viewModel: TagInputViewModel = hiltViewModel()
) {
    var inputValue by remember { mutableStateOf("") }
    var showSuggestions by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Получаем предложения для автокомплита
    val suggestions = remember(inputValue, selectedTags, uiState.allTags) {
        viewModel.getTagSuggestions(inputValue, selectedTags)
    }

    // Обработка ошибок
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            // Здесь можно показать Snackbar или Toast
            // Пока просто очищаем ошибку
            viewModel.clearError()
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Выбранные теги
        AnimatedVisibility(
            visible = selectedTags.isNotEmpty(),
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(selectedTags) { tag ->
                    TagChip(
                        tag = tag,
                        onRemove = {
                            onTagsChanged(selectedTags.filter { it.id != tag.id })
                        },
                        isEnabled = isEnabled
                    )
                }
            }
        }

        // Поле ввода с кнопкой обновления
        Box {
            OutlinedTextField(
                value = inputValue,
                onValueChange = { newValue ->
                    inputValue = newValue
                    showSuggestions = newValue.length >= 2 && suggestions.isNotEmpty()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        showSuggestions = focusState.isFocused &&
                                inputValue.length >= 2 &&
                                suggestions.isNotEmpty()
                    },
                placeholder = { Text(placeholder) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (inputValue.isNotBlank() && selectedTags.size < maxTags) {
                            // Создаем новый тег, если его нет среди существующих
                            val existingTag = uiState.allTags.find {
                                it.name.equals(inputValue.trim(), ignoreCase = true)
                            }

                            if (existingTag != null) {
                                // Добавляем существующий тег
                                onTagsChanged(selectedTags + existingTag)
                            } else {
                                // Создаем новый тег (можно добавить валидацию)
                                val newTag = Tag(
                                    id = System.currentTimeMillis(), // Временный ID
                                    name = inputValue.trim()
                                )
                                onTagsChanged(selectedTags + newTag)
                            }

                            inputValue = ""
                            showSuggestions = false
                        }
                        focusManager.clearFocus()
                    }
                ),
                trailingIcon = {
                    Row {
                        // Индикатор загрузки
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            // Кнопка обновления
                            IconButton(
                                onClick = { viewModel.refreshTags() }
                            ) {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = "Обновить теги"
                                )
                            }
                        }
                    }
                },
                enabled = isEnabled && selectedTags.size < maxTags,
                supportingText = {
                    when {
                        selectedTags.size >= maxTags -> {
                            Text(
                                text = "Достигнуто максимальное количество тегов ($maxTags)",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        uiState.error != null -> {
                            Text(
                                text = uiState.error!!,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        else -> {
                            Text("${selectedTags.size}/$maxTags тегов")
                        }
                    }
                }
            )
        }

        // Выпадающий список предложений
        AnimatedVisibility(
            visible = showSuggestions && suggestions.isNotEmpty(),
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 200.dp)
                ) {
                    items(suggestions) { suggestion ->
                        SuggestionItem(
                            tag = suggestion,
                            searchQuery = inputValue,
                            onClick = {
                                if (selectedTags.size < maxTags) {
                                    onTagsChanged(selectedTags + suggestion)
                                    inputValue = ""
                                    showSuggestions = false
                                    focusManager.clearFocus()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TagChip(
    tag: Tag,
    onRemove: () -> Unit,
    isEnabled: Boolean = true
) {
    Surface(
        modifier = Modifier.clip(RoundedCornerShape(16.dp)),
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = tag.name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )

            if (isEnabled) {
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(16.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Удалить тег",
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SuggestionItem(
    tag: Tag,
    searchQuery: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = buildHighlightedText(tag.name, searchQuery),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Строит текст с выделением совпадающей части
 * В реальном приложении можно использовать AnnotatedString для подсветки
 */
private fun buildHighlightedText(fullText: String, query: String): String {
    // Простая реализация без подсветки
    // Можно улучшить используя AnnotatedString и SpanStyle
    return fullText
}