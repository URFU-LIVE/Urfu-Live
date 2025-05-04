package com.example.urfulive.ui.createarticle

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.urfulive.R
import com.example.urfulive.data.DTOs.DefaultResponse
import kotlinx.coroutines.launch

@Composable
fun CreateArticle(
    onClose: () -> Unit,
    viewModel: CreateArticleViewModel,
    onPostSuccess: (DefaultResponse) -> Unit,
    onPostError: (Exception) -> Unit,
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    var isClosing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val postCallBack = remember {
        object : CreateArticleViewModel.PostCallBack {
            override fun onSuccess(user: DefaultResponse) {
                onPostSuccess(user)
            }

            override fun onError(error: Exception) {
                onPostError(error)
            }
        }
    }

    val animatedAlpha = remember { Animatable(0f) }
    val animatedOffset = remember { Animatable(screenHeight.value) }

    fun handleClose() {
        if (!isClosing) {
            isClosing = true
            scope.launch {
                // Запускаем обе анимации параллельно
                launch {
                    animatedAlpha.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                    )
                }
                launch {
                    animatedOffset.animateTo(
                        targetValue = screenHeight.value,
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                    )
                }
                // Вызываем колбэк после завершения анимации
                onClose()
            }
        }
    }

    // Анимация появления при открытии
    LaunchedEffect(Unit) {
        launch {
            animatedAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            )
        }
        launch {
            animatedOffset.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            )
        }
    }

    // Обработка кнопки "Назад"
    BackHandler(enabled = !isClosing) {
        handleClose()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(100f)
            .graphicsLayer {
                alpha = animatedAlpha.value
                translationY = animatedOffset.value
            }
            .background(Color(0xFF131313))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 23.dp, bottom = 15.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.chevron_left),
                    contentDescription = "Arrow",
                    modifier = Modifier
                        .clickable { handleClose() }
                        .padding(start = 15.dp)
                )

                Text(
                    text = "Создать пост",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(start = 67.dp)
                )
            }

            val darkBackground = Color(0xFF131313)
            val darkSurface = Color(0xFF131313)
            val grayText = Color(red = 125, green = 125, blue = 125)
            val lightGrayText = Color(0xFFBBBBBB)

            var titleText by remember { mutableStateOf("") }
            var contentText by remember { mutableStateOf("") }
            var tagsText by remember { mutableStateOf("") }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(darkBackground)
                    .padding(top = 0.dp, start = 16.dp, end = 16.dp)
            ) {
                TextField(
                    value = titleText,
                    onValueChange = { titleText = it },
                    placeholder = {
                        Text(
                            text = "Введите заголовок...",
                            color = grayText
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 0.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = darkSurface,
                        unfocusedContainerColor = darkSurface,
                        disabledContainerColor = darkSurface,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = lightGrayText,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = TextStyle(color = Color.White),
                    singleLine = true
                )

                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 14.dp)
                        .offset(y = (-8).dp),
                    thickness = 1.dp,
                    color = Color(red = 131, green = 131, blue = 131)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 22.dp)
                ) {
                    TextField(
                        value = contentText,
                        onValueChange = { contentText = it },
                        placeholder = {
                            Text(
                                text = "Напишите что-нибудь...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = grayText
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(419.dp)
                            .border(
                                width = 1.dp,
                                color = Color(red = 131, green = 131, blue = 131),
                            )
                            .background(
                                color = Color(0xFF131313),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = lightGrayText,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        textStyle = TextStyle(color = Color.White),
                    )
                }

                TextField(
                    value = tagsText,
                    onValueChange = { tagsText = it },
                    placeholder = {
                        Text(
                            text = "Теги(через запятую)",
                            color = grayText
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = darkSurface,
                        unfocusedContainerColor = darkSurface,
                        disabledContainerColor = darkSurface,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = lightGrayText,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = TextStyle(color = Color.White),
                    singleLine = true
                )
                Spacer(Modifier.weight(1f))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 14.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            viewModel.onPublishClick(
                                titleText,
                                contentText,
                                tagsText,
                                postCallBack
                            )
                        },
                        modifier = Modifier
                            .padding(WindowInsets.navigationBars.asPaddingValues()),
                        shape = RoundedCornerShape(42.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(red = 238, green = 126, blue = 86),
                            contentColor = Color.Black
                        )
                    ) {
                        Text(
                            text = "Опубликовать",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}