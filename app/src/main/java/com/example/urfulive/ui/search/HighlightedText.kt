package com.example.urfulive.ui.search

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

@Composable
fun HighlightedText(
    text: String,
    query: String,
    highlightColor: Color,
    normalColor: Color,
    modifier: Modifier = Modifier
) {
    if (query.isBlank()) {
        Text(
            text = text,
            color = normalColor,
            modifier = modifier,
            fontSize = 14.sp,
            lineHeight = 16.sp,
        )
        return
    }

    val annotatedString = buildAnnotatedString {
        val lowerCaseText = text.lowercase()
        val lowerCaseQuery = query.lowercase()
        var lastIndex = 0
        var startIndex = lowerCaseText.indexOf(lowerCaseQuery, lastIndex)

        while (startIndex != -1) {
            // Добавляем текст до найденного совпадения
            if (startIndex > lastIndex) {
                withStyle(style = SpanStyle(color = normalColor)) {
                    append(text.substring(lastIndex, startIndex))
                }
            }

            // Добавляем найденное совпадение с подсветкой
            withStyle(
                style = SpanStyle(
                    color = highlightColor,
                    fontWeight = FontWeight.Medium
                )
            ) {
                append(text.substring(startIndex, startIndex + query.length))
            }

            lastIndex = startIndex + query.length
            startIndex = lowerCaseText.indexOf(lowerCaseQuery, lastIndex)
        }

        // Добавляем оставшуюся часть текста
        if (lastIndex < text.length) {
            withStyle(style = SpanStyle(color = normalColor)) {
                append(text.substring(lastIndex))
            }
        }
    }

    Text(
        text = annotatedString,
        modifier = modifier,
        fontSize = 14.sp
    )
}