package com.example.urfulive.ui.search

import ScreenSizeInfo
import adaptiveSafeAreaPadding
import adaptiveTextStyle
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.urfulive.R
import kotlinx.coroutines.launch

@Composable
fun SearchBar(
    onClose: () -> Unit,
    onTagSelected: (String) -> Unit,
    screenInfo: ScreenSizeInfo,
    adapter: SearchViewModel.SearchBarAdapter,
    enableAnimations: Boolean = true,
    showBackButton: Boolean = false
) {
    val searchQuery by adapter.searchQuery.collectAsState()
    val tagSuggestions by adapter.tagSuggestions.collectAsState()
    val isLoading by adapter.isLoading.collectAsState()
    val showSuggestions by adapter.showSuggestions.collectAsState()
    val hasSuggestions = tagSuggestions.isNotEmpty()

    // Анимации
    val animatedAlpha = remember {
        Animatable(if (enableAnimations) SearchTheme.Animation.INITIAL_ALPHA else SearchTheme.Animation.FINAL_ALPHA)
    }
    val animatedScale = remember {
        Animatable(if (enableAnimations) SearchTheme.Animation.INITIAL_SCALE else SearchTheme.Animation.FINAL_SCALE)
    }

    if (enableAnimations) {
        LaunchedEffect(Unit) {
            launch {
                animatedAlpha.animateTo(
                    targetValue = SearchTheme.Animation.FINAL_ALPHA,
                    animationSpec = tween(
                        durationMillis = SearchTheme.Animation.DURATION_FAST,
                        easing = FastOutSlowInEasing
                    )
                )
            }
            launch {
                animatedScale.animateTo(
                    targetValue = SearchTheme.Animation.FINAL_SCALE,
                    animationSpec = tween(
                        durationMillis = SearchTheme.Animation.DURATION_FAST,
                        easing = FastOutSlowInEasing
                    )
                )
            }
        }
    }

    BackHandler { onClose() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(SearchTheme.Dimensions.SearchBarZIndex)
            .then(
                if (enableAnimations) Modifier.graphicsLayer {
                    alpha = animatedAlpha.value
                    scaleX = animatedScale.value
                    scaleY = animatedScale.value
                } else Modifier
            )
            .padding(
                horizontal = if (screenInfo.isCompact)
                    SearchTheme.Dimensions.SearchBarPadding
                else
                    SearchTheme.Dimensions.SearchBarPaddingLarge
            )
            .padding(vertical = SearchTheme.Dimensions.SearchBarVerticalPadding)
    ) {
        Column {
            SearchInputRow(
                searchQuery = searchQuery,
                isLoading = isLoading,
                onQueryChange = adapter::updateSearchQuery,
                onSearchClick = {
                    if (searchQuery.isNotBlank()) {
                        onTagSelected(searchQuery.trim())
                    }
                },
                onBackClick = onClose,
                screenInfo = screenInfo,
                showSuggestions = showSuggestions,
                hasSuggestions = hasSuggestions,
                showBackButton = showBackButton
            )
            AnimatedVisibility(
                visible = showSuggestions && tagSuggestions.isNotEmpty(),
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = SearchTheme.Animation.SUGGESTIONS_DURATION,
                        easing = FastOutSlowInEasing
                    )
                ),
                exit = fadeOut(
                    animationSpec = tween(
                        durationMillis = SearchTheme.Animation.SUGGESTIONS_DURATION,
                        easing = FastOutSlowInEasing
                    )
                )
            ) {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color(0xFF838383),
                    modifier = Modifier.fillMaxWidth().padding(start = if (showBackButton) SearchTheme.Dimensions.BackButtonOffset else 0.dp)
                )
            }

            SearchSuggestions(
                visible = showSuggestions && tagSuggestions.isNotEmpty(),
                suggestions = tagSuggestions,
                searchQuery = searchQuery,
                onSuggestionClick = onTagSelected,
                showBackButton = showBackButton
            )
        }
    }
}

@Composable
private fun SearchInputRow(
    searchQuery: String,
    isLoading: Boolean,
    onQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onBackClick: () -> Unit,
    screenInfo: ScreenSizeInfo,
    showSuggestions: Boolean,
    hasSuggestions: Boolean,
    showBackButton: Boolean = false
) {
    val safeAreaPadding = adaptiveSafeAreaPadding(screenInfo)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = safeAreaPadding.calculateTopPadding() - 15.dp,
            )
    ) {
        if (showBackButton) {
            Image(
                painter = painterResource(id = R.drawable.chevron_left),
                contentDescription = "Назад",
                modifier = Modifier
                    .clickable { onBackClick() }
                    .size(24.dp),
                colorFilter = ColorFilter.tint(Color.White)
            )

            Spacer(modifier = Modifier.width(12.dp))
        }

        SearchTextField(
            value = searchQuery,
            onValueChange = onQueryChange,
            isLoading = isLoading,
            onSearchClick = onSearchClick,
            screenInfo = screenInfo,
            showSuggestions = showSuggestions,
            hasSuggestions = hasSuggestions,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    isLoading: Boolean,
    onSearchClick: () -> Unit,
    screenInfo: ScreenSizeInfo,
    showSuggestions: Boolean,
    hasSuggestions: Boolean,
    modifier: Modifier = Modifier,
) {
    val bottomRadius by animateDpAsState(
        targetValue = if (showSuggestions && hasSuggestions) {
            0.dp
        } else {
            SearchTheme.Dimensions.SuggestionRadius
        },
        animationSpec = tween(
            durationMillis = SearchTheme.Animation.SUGGESTIONS_DURATION,
            easing = FastOutSlowInEasing
        ),
        label = "bottomRadius"
    )
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                "Поиск по тегам...",
                color = SearchTheme.Colors.TextSecondary,
                style = adaptiveTextStyle(
                    MaterialTheme.typography.titleSmall.copy(
                        fontSize = 18.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Medium
                    ), screenInfo
                )
            )
        },
        modifier = modifier,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = SearchTheme.Colors.TextPrimary,
            unfocusedTextColor = SearchTheme.Colors.TextPrimary,
            focusedContainerColor = SearchTheme.Colors.TextFieldBackground,
            unfocusedContainerColor = SearchTheme.Colors.TextFieldBackground,
            focusedBorderColor = SearchTheme.Colors.Transparent,
            unfocusedBorderColor = SearchTheme.Colors.Transparent,
            cursorColor = SearchTheme.Colors.TextPrimary
        ),
        shape = RoundedCornerShape(
            topStart = SearchTheme.Dimensions.SuggestionRadius,
            topEnd = SearchTheme.Dimensions.SuggestionRadius,
            bottomEnd = bottomRadius,
            bottomStart = bottomRadius
        ),
        singleLine = true,
        trailingIcon = {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(SearchTheme.Dimensions.SmallIconSize),
                        color = SearchTheme.Colors.AccentColor,
                        strokeWidth = SearchTheme.Dimensions.LoadingStrokeWidth
                    )
                }

                value.isNotEmpty() -> {
                    Image(
                        painter = painterResource(id = R.drawable.search),
                        contentDescription = "Поиск",
                        modifier = Modifier
                            .clickable { onSearchClick() }
                            .size(SearchTheme.Dimensions.SmallIconSize),
                        colorFilter = ColorFilter.tint(SearchTheme.Colors.TextPrimary)
                    )
                }
            }
        }
    )
}

@Composable
private fun SearchSuggestions(
    visible: Boolean,
    suggestions: List<String>,
    searchQuery: String,
    onSuggestionClick: (String) -> Unit,
    showBackButton: Boolean = false
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = SearchTheme.Animation.SUGGESTIONS_DURATION,
                easing = FastOutSlowInEasing
            )
        ) + expandVertically(
            animationSpec = tween(
                durationMillis = SearchTheme.Animation.SUGGESTIONS_DURATION,
                easing = FastOutSlowInEasing
            ),
            expandFrom = Alignment.Top
        ),
        exit = fadeOut(
            animationSpec = tween(
                durationMillis = SearchTheme.Animation.SUGGESTIONS_DURATION,
                easing = FastOutSlowInEasing
            )
        ) + shrinkVertically(
            animationSpec = tween(
                durationMillis = SearchTheme.Animation.SUGGESTIONS_DURATION,
                easing = FastOutSlowInEasing
            ),
            shrinkTowards = Alignment.Top
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = if (showBackButton) SearchTheme.Dimensions.BackButtonOffset else 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        SearchTheme.Colors.SuggestionsBackground,
                        RoundedCornerShape(
                            topStart = 0.dp,
                            topEnd = 0.dp,
                            bottomEnd = SearchTheme.Dimensions.SuggestionRadius,
                            bottomStart = SearchTheme.Dimensions.SuggestionRadius
                        )
                    )
                    .heightIn(max = SearchTheme.Dimensions.SuggestionMaxHeight)
            ) {
                suggestions.forEach { suggestion ->
                    SuggestionItem(
                        text = suggestion,
                        query = searchQuery,
                        onClick = { onSuggestionClick(suggestion) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SuggestionItem(
    text: String,
    query: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(
                horizontal = SearchTheme.Dimensions.SuggestionItemPadding,
                vertical = SearchTheme.Dimensions.SuggestionItemVerticalPadding
            )
    ) {
        HighlightedText(
            text = text,
            query = query.trim(),
            highlightColor = SearchTheme.Colors.TextPrimary,
            normalColor = SearchTheme.Colors.TextSecondary
        )
    }
}