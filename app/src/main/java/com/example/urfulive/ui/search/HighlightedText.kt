package com.example.urfulive.ui.search

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@Composable
fun HighlightedText(
    text: String,
    query: String,
    highlightColor: Color = Color.White,
    normalColor: Color = Color.Gray
) {
    if (query.isBlank()) {
        Text(
            text = text,
            color = normalColor,
            style = MaterialTheme.typography.bodyMedium
        )
        return
    }

    val startIndex = text.lowercase().indexOf(query.lowercase())

    if (startIndex >= 0) {
        val endIndex = startIndex + query.length

        Row {
            // Текст до совпадения
            if (startIndex > 0) {
                Text(
                    text = text.substring(0, startIndex),
                    color = normalColor,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Выделенный текст
            Text(
                text = text.substring(startIndex, endIndex),
                color = highlightColor,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )

            // Текст после совпадения
            if (endIndex < text.length) {
                Text(
                    text = text.substring(endIndex),
                    color = normalColor,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    } else {
        // Если совпадений нет, показываем обычный текст
        Text(
            text = text,
            color = normalColor,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}