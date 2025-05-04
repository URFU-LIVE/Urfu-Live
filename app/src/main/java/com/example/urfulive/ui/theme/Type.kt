package com.example.urfulive.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import com.example.urfulive.R

val Montserrat = FontFamily(
    Font(R.font.montserrat_regular, FontWeight.Normal),
    Font(R.font.montserrat_semibold, FontWeight.SemiBold),
    Font(R.font.montserrat_semibolditalic, FontWeight.SemiBold),
    Font(R.font.montserrat_bold, FontWeight.Bold),
    Font(R.font.montserrat_light, FontWeight.Light)
)

// Set of Material typography styles to start with
val Typography = Typography(
    titleMedium = TextStyle( // Для заголовков
        fontFamily = Montserrat,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp
    ),
    bodySmall = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle( // Для кнопок
        fontFamily = Montserrat,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),
    titleSmall = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.SemiBold,
        fontStyle = FontStyle.Italic,
        fontSize = 16.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp
    ),
    labelMedium = TextStyle( // Теги
        fontFamily = Montserrat,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp
    ),
    labelLarge = TextStyle( // Заголовки статей
        fontFamily = Montserrat,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle( // Имя автора
        fontFamily = Montserrat,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 18.2.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle( // Подписаться, текст уведомления
        fontFamily = Montserrat,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 12.sp,
        letterSpacing = 0.sp
    ),
    displayMedium = TextStyle( // Текст статьи
        fontFamily = Montserrat,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 17.6.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle( // Темы
        fontFamily = Montserrat,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        lineHeight = 15.sp,
        letterSpacing = 0.sp
    ),
    displayLarge = TextStyle( // Рядом с лайком
        fontFamily = Montserrat,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 19.8.sp,
        letterSpacing = 0.sp
    ),

    headlineLarge = TextStyle( // Уведомления
        fontFamily = Montserrat,
        fontWeight = FontWeight.SemiBold,
        fontSize = 26.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp
    ),

    headlineMedium = TextStyle( // Заголовок уведомления
        fontFamily = Montserrat,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp
    ),

    headlineSmall = TextStyle( // Подписчики
        fontFamily = Montserrat,
        fontWeight = FontWeight.Light,
        fontSize = 16.sp,
        lineHeight = 20.8.sp,
        letterSpacing = 0.sp
    ),
)