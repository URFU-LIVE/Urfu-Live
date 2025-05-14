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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.urfulive.R
import com.example.urfulive.data.DTOs.DefaultResponse
import com.example.urfulive.data.model.UserRole
import com.example.urfulive.ui.theme.UrfuLiveTheme
import kotlinx.coroutines.launch

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateArticle(
    onClose: () -> Unit,
    viewModel: CreateArticleViewModel,
    onPostSuccess: (DefaultResponse) -> Unit,
    onPostError: (Exception) -> Unit,
    animationsEnabled: Boolean = true
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    // Адаптивные отступы в зависимости от размера экрана
    val horizontalPadding = screenWidth.times(0.04f).coerceAtLeast(16.dp)

    // Высота текстового поля: от 35% до 45% высоты экрана в зависимости от размера экрана
    val contentFieldHeight = remember(screenHeight) {
        val percentHeight = when {
            screenHeight < 600.dp -> 0.35f
            screenHeight < 800.dp -> 0.40f
            else -> 0.45f
        }
        screenHeight.times(percentHeight)
    }

    var isClosing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Анимация
    val animatedAlpha = remember { Animatable(if (animationsEnabled) 0f else 1f) }
    val animatedOffset = remember { Animatable(if (animationsEnabled) screenHeight.value else 0f) }

    val userState by viewModel.user.collectAsState()

    if (userState != null && userState!!.role == UserRole.USER) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.95f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .background(Color.DarkGray, shape = RoundedCornerShape(12.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Недостаточно прав",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Вы не можете создавать статьи. Подайте заявку на получение прав модератора.",
                    color = Color.LightGray,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { onClose() }) {
                    Text("Ок")
                }
            }
        }
        return
    }

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

    val darkBackground = Color(0xFF131313)
    val darkSurface = Color(0xFF131313)
    val grayText = Color(red = 125, green = 125, blue = 125)
    val lightGrayText = Color(0xFFBBBBBB)

    // Используем Scaffold для лучшей организации макета и правильных отступов
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(100f)
            .then(
                if (animationsEnabled) Modifier.graphicsLayer {
                    alpha = animatedAlpha.value
                    translationY = animatedOffset.value
                } else Modifier
            ),
        containerColor = darkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Создать пост",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { handleClose() }) {
                        Image(
                            painter = painterResource(id = R.drawable.chevron_left),
                            contentDescription = "Назад"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF131313)
                )
            )
        }
    ) { innerPadding ->

        var titleText by remember { mutableStateOf("") }
        var contentText by remember { mutableStateOf("") }
        var tagsText by remember { mutableStateOf("") }

        // Основной контент с возможностью прокрутки
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = horizontalPadding)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Заголовок
            OutlinedTextField(
                value = titleText,
                onValueChange = { titleText = it },
                placeholder = { Text("Введите заголовок...", color = grayText) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
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
                thickness = 1.dp,
                color = Color(red = 131, green = 131, blue = 131)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Содержание статьи - адаптивная высота
            OutlinedTextField(
                value = contentText,
                onValueChange = { contentText = it },
                placeholder = {
                    Text(
                        "Напишите что-нибудь...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = grayText
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 150.dp, max = contentFieldHeight)
                    .border(
                        width = 1.dp,
                        color = Color(red = 131, green = 131, blue = 131),
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
                textStyle = TextStyle(color = Color.White)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Теги
            OutlinedTextField(
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

            // Эластичный разделитель для разных размеров экрана
            Spacer(modifier = Modifier.weight(1f))

            // Кнопка публикации - адаптивное расположение
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        viewModel.onPublishClick(titleText, contentText, tagsText, postCallBack)
                    },
                    shape = RoundedCornerShape(42.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(red = 238, green = 126, blue = 86),
                        contentColor = Color.Black
                    ),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        "Опубликовать",
                        style = MaterialTheme.typography.bodyLarge
                    )
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
