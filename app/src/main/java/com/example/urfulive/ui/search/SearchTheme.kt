// app/src/main/java/com/example/urfulive/ui/search/SearchTheme.kt
package com.example.urfulive.ui.search

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object SearchTheme {
    // Цвета
    object Colors {
        val SearchBarBackground = Color(0xFF131313)
        val TextFieldBackground = Color(0xFF292929)
        val SuggestionsBackground = Color(0xFF232323)
        val AccentColor = Color(0xFFEE7E56)
        val TextPrimary = Color.White
        val TextSecondary = Color.Gray
        val Transparent = Color.Transparent
    }

    // Размеры
    object Dimensions {
        val SearchBarPadding = 16.dp
        val SearchBarPaddingLarge = 24.dp
        val SearchBarVerticalPadding = 12.dp
        val IconSize = 24.dp
        val SmallIconSize = 20.dp
        val SpacerWidth = 12.dp
        val BorderRadius = 52.dp
        val SuggestionRadius = 12.dp
        val SuggestionMaxHeight = 200.dp
        val SuggestionItemPadding = 16.dp
        val SuggestionItemVerticalPadding = 12.dp
        val SuggestionsTopPadding = 8.dp
        val LoadingStrokeWidth = 2.dp
        val SearchBarZIndex = 200f
        val SearchBarAlpha = 0.95f
    }

    // Анимации
    object Animation {
        const val DURATION_FAST = 200
        const val DEBOUNCE_DELAY = 300L
        const val INITIAL_ALPHA = 0f
        const val FINAL_ALPHA = 1f
        const val INITIAL_SCALE = 0.9f
        const val FINAL_SCALE = 1f
    }

    // Настройки
    object Config {
        const val MAX_SUGGESTIONS = 5
    }
}

// Данные для тегов
object TagsData {
    val popularTags = listOf(
        "Учеба", "Программирование", "Android", "Kotlin", "React", "JavaScript",
        "Веб-разработка", "Mobile", "UI/UX", "Дизайн", "Backend", "Frontend",
        "Искусственный интеллект", "Machine Learning", "Data Science", "DevOps",
        "Стартапы", "Бизнес", "Карьера", "Образование", "Наука", "Исследования",
        "Новости", "События", "Мероприятия", "Конференции", "Вебинары",
        "Спорт", "Здоровье", "Путешествия", "Фотография", "Музыка", "Кино",
        "Юмор", "Внеучебная деятельность", "Стажировка", "Знакомства", "Работа", "Волонтерство"
    )
}