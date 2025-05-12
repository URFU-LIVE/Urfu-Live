package com.example.urfulive.ui.createarticle

import FakeCreateArticleViewModel
import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.urfulive.R
import com.example.urfulive.data.DTOs.DefaultResponse
import com.example.urfulive.ui.theme.UrfuLiveTheme
import kotlinx.coroutines.launch

@Composable
fun CreateArticle(
    onClose: () -> Unit,
    viewModel: CreateArticleViewModel,
    onPostSuccess: (DefaultResponse) -> Unit,
    onPostError: (Exception) -> Unit,
    animationsEnabled: Boolean = true
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    var isClosing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val animatedAlpha = remember { Animatable(if (animationsEnabled) 0f else 1f) }
    val animatedOffset = remember { Animatable(if (animationsEnabled) screenHeight.value else 0f) }

    val postCallBack = remember {
        object : CreateArticleViewModel.PostCallBack {
            override fun onSuccess(user: DefaultResponse) {
                onPostSuccess(user)
                onClose()
            }

            override fun onError(error: Exception) {
                onPostError(error)
            }
        }
    }

    fun handleClose() {
        if (!isClosing) {
            isClosing = true
            if (animationsEnabled) {
                scope.launch {
                    launch {
                        animatedAlpha.animateTo(0f, tween(300, easing = FastOutSlowInEasing))
                    }
                    launch {
                        animatedOffset.animateTo(screenHeight.value, tween(300, easing = FastOutSlowInEasing))
                    }
                    onClose()
                }
            } else {
                onClose()
            }
        }
    }

    if (animationsEnabled) {
        LaunchedEffect(Unit) {
            launch {
                animatedAlpha.animateTo(1f, tween(300, easing = FastOutSlowInEasing))
            }
            launch {
                animatedOffset.animateTo(0f, tween(300, easing = FastOutSlowInEasing))
            }
        }
    }

    BackHandler(enabled = !isClosing) {
        handleClose()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(100f)
            .then(
                if (animationsEnabled) Modifier.graphicsLayer {
                    alpha = animatedAlpha.value
                    translationY = animatedOffset.value
                } else Modifier
            )
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
                    placeholder = { Text("Введите заголовок...", color = grayText) },
                    modifier = Modifier.fillMaxWidth(),
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
                            Text("Напишите что-нибудь...", style = MaterialTheme.typography.bodyLarge, color = grayText)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(419.dp)
                            .border(
                                width = 1.dp,
                                color = Color(red = 131, green = 131, blue = 131),
                            )
                            .background(Color(0xFF131313), shape = RoundedCornerShape(8.dp)),
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
                        textStyle = TextStyle(color = Color.White)
                    )
                }

                TextField(
                    value = tagsText,
                    onValueChange = { tagsText = it },
                    placeholder = { Text("Теги(через запятую)", color = grayText) },
                    modifier = Modifier.fillMaxWidth(),
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
                            viewModel.onPublishClick(titleText, contentText, tagsText, postCallBack)
                        },
                        modifier = Modifier.padding(WindowInsets.navigationBars.asPaddingValues()),
                        shape = RoundedCornerShape(42.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(red = 238, green = 126, blue = 86),
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Опубликовать", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(name = "Small screen (360x640)", device = "spec:width=360dp,height=640dp", backgroundColor = 10, showSystemUi = true)
@Composable
fun CreateArticlePreviewSmall() {
    UrfuLiveTheme {
        CreateArticle(
            onClose = {},
            onPostSuccess = {},
            onPostError = {},
            viewModel = FakeCreateArticleViewModel(),
            animationsEnabled = false
        )
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(name = "Default screen", showBackground = true, showSystemUi = true, backgroundColor = 10)
@Composable
fun CreateArticlePreviewDefault() {
    UrfuLiveTheme {
        CreateArticle(
            onClose = {},
            onPostSuccess = {},
            onPostError = {},
            viewModel = FakeCreateArticleViewModel(),
            animationsEnabled = false
        )
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(name = "Large screen (500x1000)", device = "spec:width=500dp,height=1000dp", showSystemUi = true)
@Composable
fun CreateArticlePreviewLarge() {
    UrfuLiveTheme {
        CreateArticle(
            onClose = {},
            onPostSuccess = {},
            onPostError = {},
            viewModel = FakeCreateArticleViewModel(),
            animationsEnabled = false
        )
    }
}
