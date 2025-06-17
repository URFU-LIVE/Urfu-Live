package live.urfu.frontend.ui.search

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import live.urfu.frontend.ui.theme.Montserrat

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
            fontFamily = Montserrat,
            fontSize = 18.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        return
    }

    val annotatedString = buildAnnotatedString {
        val lowerCaseText = text.lowercase()
        val lowerCaseQuery = query.lowercase()
        var lastIndex = 0
        var startIndex = lowerCaseText.indexOf(lowerCaseQuery, lastIndex)

        while (startIndex != -1) {
            if (startIndex > lastIndex) {
                withStyle(style = SpanStyle(color = normalColor)) {
                    append(text.substring(lastIndex, startIndex))
                }
            }

            withStyle(
                style = SpanStyle(
                    color = highlightColor,
                )
            ) {
                append(text.substring(startIndex, startIndex + query.length))
            }

            lastIndex = startIndex + query.length
            startIndex = lowerCaseText.indexOf(lowerCaseQuery, lastIndex)
        }

        if (lastIndex < text.length) {
            withStyle(style = SpanStyle(color = normalColor)) {
                append(text.substring(lastIndex))
            }
        }
    }

    Text(
        text = annotatedString,
        modifier = modifier,
        fontFamily = Montserrat,
        fontSize = 18.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.SemiBold
    )
}