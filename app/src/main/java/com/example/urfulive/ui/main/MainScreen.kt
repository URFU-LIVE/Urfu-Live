import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.urfulive.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Text
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.example.urfulive.ui.theme.UrfuLiveTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.Icon
//import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.lerp
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.zIndex
import com.example.urfulive.data.DTOs.AuthResponse
import com.example.urfulive.data.DTOs.DefaultResponse
import com.example.urfulive.data.api.UserApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ArticlesScreenPreview() {
    UrfuLiveTheme {
        CarouselScreen(
            onProfileClick = {},
            //createArticle = {}
        )
    }
}

enum class SheetState { COLLAPSED, PARTIAL, FULL }

var userApiService = UserApiService()

@Composable
fun TagChip(tag: String, color: Color, alpha: Float = 1f) {
    Box(
        modifier = Modifier
            .graphicsLayer {
                this.alpha = 1f
            }
            .background(
                color = color,
                shape = RoundedCornerShape(52.dp)
            )
            .padding(horizontal = 15.dp, vertical = 10.dp)
    ) {
        Text(
            text = tag,
            color = Color.Black,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
fun ArticleCard(
    article: Article,
    onClick: () -> Unit,
    // Параметр для управления анимацией
    expansionProgress: Float = .0f,

    ) {
    val pattern = ArticleColorPatterns[article.colorPatternIndex]

    // СТАБИЛИЗАЦИЯ: Коэффициент скорости исчезновения панели реакций
    // Используем фиксированное значение для обеспечения стабильности анимации
    val fadeOutSpeed = 1.0f

    // СТАБИЛИЗАЦИЯ: Функция плавности для исчезновения
    // SmoothEasing обеспечивает более плавную анимацию, устойчивую к спаму
    val fadeOutEasing = FastOutSlowInEasing

    // СТАБИЛИЗАЦИЯ: Вычисляем сглаженную прозрачность
    val fadeOutAlpha = remember(expansionProgress) {
        // Применяем сглаживание к значению expansionProgress перед расчетом alpha
        // Это делает анимацию более устойчивой к быстрым переключениям
        (1f - (fadeOutEasing.transform(expansionProgress) * fadeOutSpeed)).coerceIn(0f, 1f)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(580.dp)
            .clickable { onClick() }
            .background(pattern.background, shape = RoundedCornerShape(52.dp))
            .padding(start = 25.dp, top = 40.dp, end = 25.dp, bottom = 37.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween // Верхняя часть и нижняя строка разделены равномерно
        ) {
            Column {
                // Теги (пример)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    article.tags.take(2).forEach { tag ->
                        TagChip(tag = tag, color = pattern.buttonColor)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Заголовок
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(20.dp))
                Column {
                    Text(
                        text = "Опубликовано: ${article.date}",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Автор и кнопка подписки с фиксированными размерами
                    Column {
                        // Автор
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Иконка автора (заглушка)
                            Image(
                                painter = painterResource(id = R.drawable.profile),
                                contentDescription = "Author Icon",
                                modifier = Modifier.size(50.dp), // Уменьшаем размер аватара
                                contentScale = ContentScale.Fit
                            )
                            Spacer(modifier = Modifier.width(8.dp)) // Уменьшаем отступ

                            // Имя автора с ограничением ширины
                            Column(
                                modifier = Modifier.weight(1f) // Занимает доступное пространство, но не больше
                            ) {
                                Text(
                                    text = "Автор:",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.Black
                                )
                                Text(
                                    text = article.author,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.Black,
                                    maxLines = 1, // Ограничиваем количество строк
                                    overflow = TextOverflow.Ellipsis // Добавляем многоточие при переполнении
                                )
                            }

                            // Кнопка подписки с уменьшенными отступами
                            Text(
                                text = "Подписаться",
                                modifier = Modifier
                                    .clickable { }
                                    .background(
                                        pattern.buttonColor,
                                        shape = RoundedCornerShape(52.dp)
                                    )
                                    .padding(
                                        horizontal = 15.dp,
                                        vertical = 10.dp
                                    ), // Уменьшаем отступы кнопки
                                color = Color.Black,
                                style = MaterialTheme.typography.displaySmall,
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp)) // Уменьшаем отступ

                // Текст статьи
                Text(
                    text = article.content,
                    style = MaterialTheme.typography.displayMedium,
                    color = Color.Black,
                    maxLines = 10, // Ограничиваем количество строк для превью
                    overflow = TextOverflow.Ellipsis // Добавляем многоточие при переполнении
                )
            }

            // СТАБИЛИЗАЦИЯ: Анимируем панель реакций с использованием animateFloatAsState
            // вместо прямого изменения alpha через graphicsLayer
            // Это обеспечивает более плавную анимацию при быстрых переключениях
            val animatedAlpha = (1f - (expansionProgress * fadeOutSpeed)).coerceIn(0f, 1f)

            // Панель реакций с улучшенной анимацией прозрачности
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .graphicsLayer {
                        // Используем анимированное значение alpha
                        alpha = animatedAlpha
                    }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.likebottom),
                    contentDescription = "Like Logo",
                    colorFilter = ColorFilter.tint(pattern.reactionColor),
                    modifier = Modifier
                        .clickable { /* TODO Поставить лайк*/ }
                        .size(33.dp),
                )

                // Иконка лайка и количество лайков
                Text(
                    text = article.likes.toString(),
                    color = Color.Black,
                    style = MaterialTheme.typography.displayLarge,
                )
                Image(
                    painter = painterResource(id = R.drawable.commentbottom),
                    colorFilter = ColorFilter.tint(pattern.reactionColor),
                    contentDescription = "Comment Logo",
                    modifier = Modifier
                        .clickable { /* TODO Оставить комментарий*/ }
                        .size(35.dp),
                )
                // Иконка комментариев и количество
                Text(
                    text = article.comments.toString(),
                    color = Color.Black,
                    style = MaterialTheme.typography.displayLarge,
                )
                Image(
                    painter = painterResource(id = R.drawable.bookmarkbottom1),
                    contentDescription = "Bookmark Logo",
                    colorFilter = ColorFilter.tint(pattern.reactionColor),
                    modifier = Modifier
                        .clickable { /* TODO Сохранить себе*/ }
                        .size(30.dp),
                )

                Text(
                    text = article.sakladka.toString(),
                    color = Color.Black,
                    style = MaterialTheme.typography.displayLarge,
                )
            }
        }
    }
}

@Composable
fun TopBar(
    onNotificationsClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 70.dp, end = 42.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.navbarnew),
                contentDescription = "Heart Logo",
                modifier = Modifier
                    .clickable { /* TODO Переход куда-нибудь*/ }
                    .padding(start = 32.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(id = R.drawable.bell),
                contentDescription = "Bell Logo",
                modifier = Modifier
                    .clickable { onNotificationsClick() }
                    .size(40.dp)
            )
        }
    }
}

@Composable
fun FullScreenNotifications(onClose: () -> Unit) {
    val density = LocalDensity.current
    val screenWidth = with(density) { LocalConfiguration.current.screenWidthDp.dp }
    val screenHeight = with(density) { LocalConfiguration.current.screenHeightDp.dp }

    // Анимация появления
    val animatedAlpha = remember { Animatable(0f) }
    val animatedOffset = remember { Animatable(screenHeight.value) }

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

    // Обработка нажатия кнопки "Назад"
    BackHandler {
        onClose()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(100f) // Гарантируем, что будут поверх всего
            .graphicsLayer {
                alpha = animatedAlpha.value
                translationY = animatedOffset.value
            }
            .background(Color(0xFF0D0D0D))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding() // Учитываем системные панели (статус-бар и т.д.)
        ) {
            // Верхняя панель с заголовком и кнопкой закрытия
            Box(
                modifier = Modifier
                    .padding(top = 23.dp, bottom = 15.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.chevron_left),
                    contentDescription = "Arrow",
                    modifier = Modifier
                        .clickable { onClose() }
                        .padding(start = 15.dp)
                )

                Text(
                    text = "Уведомления",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(start = 67.dp)
                )
            }

            // Список уведомлений
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                val notifications = Notifications

                if (notifications.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .padding(bottom = 16.dp),
                                    tint = Color.Gray
                                )
                                Text(
                                    text = "У вас нет новых уведомлений",
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                } else {
                    items(notifications) { notification ->
                        NotificationItemEnhanced(notification) {
                            // Действие при нажатии на уведомление
                            // Например, отметить как прочитанное или перейти к связанному контенту
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItemEnhanced(
    notification: NotificationData,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 0.dp, vertical = 4.dp)
            .background(Color(0xFF292929), shape = RoundedCornerShape(15.dp))
            .padding(vertical = 20.dp, horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Аватар или иконка уведомления
        Box(
            modifier = Modifier
                .size(52.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                when {
                    notification.title.contains("принята") -> painterResource(id = R.drawable.check_circle)
                    notification.title.contains("подарок") -> painterResource(id = R.drawable.gift)
                    notification.title.contains("Напоминание") -> painterResource(id = R.drawable.clock)
                    else -> painterResource(id = R.drawable.chevron_left)
                },
                contentDescription = null,
                modifier = Modifier.size(52.dp)
            )
        }

        Spacer(modifier = Modifier.width(21.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = notification.title,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = notification.message,
                style = MaterialTheme.typography.displaySmall,
                color = Color.LightGray
            )

            //Spacer(modifier = Modifier.height(4.dp))

//            Text(
//                text = notification.time,
//
//                color = Color.Gray
//            )
        }

        // Индикатор непрочитанного уведомления
        if (!notification.isRead) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .align(Alignment.Top)
                    .background(color = Color(0xFFFB6C39), shape = CircleShape)
            )
        }
    }
}

@Composable
fun BottomNavStub(
    onProfileClick: () -> Unit,
    onCreateArticleClick: () -> Unit,
    //createArticle: () -> Unit,
    containerWidth: Dp = 400.dp,  // Фиксированная ширина контейнера
    containerHeight: Dp = 110.dp, // Фиксированная высота контейнера
    horizontalPadding: Dp = 21.dp, // Отступы внутри контейнера по горизонтали
    verticalPadding: Dp = 20.dp    // Отступы внутри контейнера по вертикали
) {
    // Первый Box занимает весь экран (или родительский контейнер)
    Box(
        modifier = Modifier
            .padding(start = 20.dp, top = 0.dp, end = 20.dp)
            .padding(WindowInsets.navigationBars.asPaddingValues())// Можно заменить на другой размер, если нужно
    ) {
        // Второй Box — сама «фигура», которую мы хотим отцентрировать
        Box(
            modifier = Modifier
                .align(Alignment.Center)              // Центрируем по горизонтали и вертикали
                .width(containerWidth)
                .height(containerHeight)
                .background(Color(0xFF292929), shape = RoundedCornerShape(52.dp))
                .padding(horizontal = horizontalPadding, vertical = verticalPadding)
        ) {
            var showNotificationsOverlay by remember { mutableStateOf(false) }
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.home),
                    contentDescription = "Home Logo",
                    modifier = Modifier
                        //.size(45.dp)
                        .clickable { /* TODO Переход домой */ }
                )
                Image(
                    painter = painterResource(id = R.drawable.savenew),
                    contentDescription = "Save Logo",
                    modifier = Modifier
                        //.size(45.dp)
                        .clickable { /* TODO Сохраненные */ }
                )
                Image(
                    painter = painterResource(id = R.drawable.resource_new),
                    contentDescription = "Add Logo",
                    modifier = Modifier
                        //.size(45.dp)
                        .clickable { onCreateArticleClick() }
                )
                Image(
                    painter = painterResource(id = R.drawable.messagenew),
                    contentDescription = "Message Logo",
                    modifier = Modifier
                        //.size(45.dp)
                        .clickable { /* TODO Сообщения */ }
                )
                Image(
                    painter = painterResource(id = R.drawable.profilenew),
                    contentDescription = "Profile Logo",
                    modifier = Modifier
                        .size(35.dp)
                        .clickable { onProfileClick() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateArticle(
    onClose: () -> Unit, viewModel: ArticlesViewModel,
    onPostSuccess: (DefaultResponse) -> Unit,
    onPostError: (Exception) -> Unit,
) {
    val density = LocalDensity.current
    val screenWidth = with(density) { LocalConfiguration.current.screenWidthDp.dp }
    val screenHeight = with(density) { LocalConfiguration.current.screenHeightDp.dp }

    val postCallBack = remember {
        object : ArticlesViewModel.PostCallBack {
            override fun onSuccess(user: DefaultResponse) {
                onPostSuccess(user)
//                onPostClick() // Навигация после успешной регистрации
            }

            override fun onError(error: Exception) {
                onPostError(error)
            }
        }
    }


    // Анимация появления
    val animatedAlpha = remember { Animatable(0f) }
    val animatedOffset = remember { Animatable(screenHeight.value) }

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

    // Обработка нажатия кнопки "Назад"
    BackHandler {
        onClose()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(100f) // Гарантируем, что будут поверх всего
            .graphicsLayer {
                alpha = animatedAlpha.value
                translationY = animatedOffset.value
            }
            .background(Color(0xFF131313))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding() // Учитываем системные панели (статус-бар и т.д.)
        ) {
            // Верхняя панель с заголовком и кнопкой закрытия
            Box(
                modifier = Modifier
                    .padding(top = 23.dp, bottom = 15.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.chevron_left),
                    contentDescription = "Arrow",
                    modifier = Modifier
                        .clickable { onClose() }
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

            // Список уведомлений
            val darkBackground = Color(0xFF131313)
            val darkSurface = Color(0xFF131313)
            val grayText = Color(0xFF9E9E9E)
            val lightGrayText = Color(0xFFBBBBBB)

            // Состояния для текстовых полей
            var titleText by remember { mutableStateOf("") }
            var contentText by remember { mutableStateOf("") }
            var tagsText by remember { mutableStateOf("") }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(darkBackground)
                    .padding(16.dp)
            ) {
                // Поле для заголовка
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
                        .padding(bottom = 16.dp),
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
                    modifier = Modifier.padding(horizontal = 1.dp),
                    thickness = 1.dp,
                    color = Color.White.copy(alpha = 0.3f)
                )
                // Поле для содержимого (многострочное)
                TextField(
                    value = contentText,
                    onValueChange = { contentText = it },
                    placeholder = {
                        Text(
                            text = "Напишите что-нибудь...",
                            color = grayText
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(bottom = 16.dp),
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
                    maxLines = 10
                )

                // Поле для тегов
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
            }
            Button(
                onClick = { viewModel.onPublishClick(titleText, contentText, tagsText, postCallBack) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(WindowInsets.navigationBars.asPaddingValues()),
                shape = RoundedCornerShape(15.dp),
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


@Composable
fun HorizontalTagRow(tags: List<String>, color: Color, expandProgress: Float = 1f) {
    // Определяем, сколько тегов показывать изначально
    val initialVisibleTags = 2
    val additionalTags = tags.size - initialVisibleTags
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Отображаем первые два тега всегда
        tags.take(initialVisibleTags).forEach { tag ->
            TagChip(tag = tag, color = color)
        }

        // Отображаем дополнительные теги с анимацией
        if (additionalTags > 0) {
            tags.drop(initialVisibleTags).forEachIndexed { index, tag ->
                // Вычисляем индивидуальный прогресс для каждого дополнительного тега
                // Это создает эффект постепенного появления тегов один за другим
                val tagProgress = (expandProgress - 0.3f - (index * 0.1f)).coerceIn(0f, 1f)

                AnimatedVisibility(
                    visible = tagProgress > 0,
                    enter = fadeIn() + expandHorizontally(),
                    exit = fadeOut() + shrinkHorizontally()
                ) {
                    TagChip(
                        tag = tag,
                        color = color,
                        alpha = tagProgress
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable


fun CarouselScreen(
    viewModel: ArticlesViewModel = viewModel(),
    onProfileClick: () -> Unit,
) { //createArticle: () -> Unit
    val articles = viewModel.articles
    val pagerState = rememberPagerState(pageCount = { articles.size })
    var expandedIndex by remember { mutableStateOf(-1) }

    // Создаем scope для корректной работы с корутинами
    val scope = rememberCoroutineScope()

    // Сохраняем центр выбранной карточки
    var selectedCardCenter by remember { mutableStateOf(Offset.Zero) }
    var selectedCardSize by remember { mutableStateOf(IntSize(0, 0)) }

    // ЗАЩИТА ОТ СПАМА АНИМАЦИЙ: добавляем блокировку на период анимации
    // Это предотвратит начало новой анимации, пока предыдущая не завершена
    var isAnimationInProgress by remember { mutableStateOf(false) }

    // ЗАЩИТА ОТ СПАМА: добавляем таймер последнего действия
    var lastActionTime by remember { mutableStateOf(0L) }

    // ЗАЩИТА ОТ СПАМА: минимальное время между действиями (в миллисекундах)
    val minActionInterval = 0L // Уменьшили до 400мс для лучшей отзывчивости

    // Отслеживаем время закрытия для контроля скорости анимации
    var lastCloseTime by remember { mutableStateOf(0L) }

    val density = LocalDensity.current
    val screenWidth = with(density) { LocalConfiguration.current.screenWidthDp.dp }
    val screenHeight = with(density) { LocalConfiguration.current.screenHeightDp.dp + 100.dp }

    val initialCardWidth = 350.dp
    val initialCardHeight = 580.dp
    val targetCardWidth = screenWidth

    // Добавляем два состояния высоты для частичного и полного расширения
    val partialExpandHeight = screenHeight * 0.82f // 82% экрана снизу
    val fullExpandHeight = screenHeight * 1.5f

    // Состояние для отслеживания полного разворачивания
    var isFullyExpanded by remember { mutableStateOf(false) }

    // Добавляем состояние для скрытия BottomNavStub
    var shouldHideBottomNav by remember { mutableStateOf(false) }

    // Обновляем состояние скрытия BottomNav в зависимости от полного развертывания
    LaunchedEffect(isFullyExpanded) {
        shouldHideBottomNav = isFullyExpanded
    }

    // Добавляем состояние анимации для плавного закрытия
    var isClosing by remember { mutableStateOf(false) }

    // Отслеживаем текущий прогресс закрытия для ручного управления анимацией
    var closeAnimationProgress by remember { mutableStateOf(0f) }

    // Сохраняем предыдущее состояние для анимации к нему
    var previousCardCenter by remember { mutableStateOf(Offset.Zero) }

    // Для отслеживания жеста свайпа вниз и его силы
    var dragOffset by remember { mutableStateOf(0f) }
    val dragThresholdToClose = 150f // Увеличенный порог для закрытия статьи свайпом вниз

    // Создаем переход состояния для плавной анимации
    val expanded = expandedIndex != -1
    val transition = updateTransition(targetState = expanded, label = "expandTransition")

    // Отдельный переход для полного разворачивания
    val fullExpansionTransition =
        updateTransition(targetState = isFullyExpanded, label = "fullExpansionTransition")

    // Анимация закрытия (обратная анимация)
    val closeAnimator = remember { Animatable(0f) }

    // СТАБИЛИЗАЦИЯ АНИМАЦИИ: используем более плавную кривую для анимации
    val SmoothEasing = CubicBezierEasing(0.05f, 0.0f, 0.15f, 1.0f)

    // ИСПРАВЛЕНИЕ: Добавляем LaunchedEffect для сброса блокировки после определенного времени
    // Это страховка на случай, если что-то пойдет не так и блокировка не будет снята
    LaunchedEffect(key1 = isAnimationInProgress) {
        if (isAnimationInProgress) {
            // Установим таймер безопасности, который гарантирует сброс блокировки
            delay(1500) // Максимальное время блокировки - 1.5 секунды
            isAnimationInProgress = false
        }
    }

    // Запускаем анимацию закрытия при необходимости
    LaunchedEffect(isClosing) {
        if (isClosing) {
            // Устанавливаем флаг, что анимация в процессе
            isAnimationInProgress = true

            // Фиксированная длительность анимации закрытия для стабильности
            val fixedCloseDuration = 750

            closeAnimator.snapTo(0f)
            try {
                closeAnimator.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = fixedCloseDuration,
                        easing = SmoothEasing
                    )
                )

                // После завершения анимации закрытия
                expandedIndex = -1
                lastCloseTime = System.currentTimeMillis()
                lastActionTime = System.currentTimeMillis() // Обновляем время последнего действия
                isFullyExpanded = false
                shouldHideBottomNav = false
                dragOffset = 0f
                isClosing = false
                closeAnimator.snapTo(0f)
            } catch (e: Exception) {
                // Если анимация была прервана, все равно сбрасываем состояния
                expandedIndex = -1
                isClosing = false
                isFullyExpanded = false
                shouldHideBottomNav = false
            } finally {
                // ИСПРАВЛЕНИЕ: Всегда сбрасываем флаг анимации в блоке finally
                // Это гарантирует, что блокировка будет снята в любом случае
                delay(100) // Небольшая задержка для предотвращения двойных кликов
                isAnimationInProgress = false
            }
        }
    }

    // СТАБИЛИЗАЦИЯ: фиксированные длительности анимации для стабильности
    val fixedOpenDuration = 600 // Уменьшили с 700 до 600 для более быстрого отклика
    val fixedSwipeDuration = 300 // Фиксированная длительность для свайпа

    // СТАБИЛИЗАЦИЯ: Спецификации анимации с фиксированными значениями
    val animSpec = tween<Float>(
        durationMillis = fixedOpenDuration,
        easing = SmoothEasing,
        delayMillis = 30 // Уменьшили с 50 до 30 для более быстрого отклика
    )

    val swipeAnimSpec = tween<Float>(
        durationMillis = fixedSwipeDuration,
        easing = SmoothEasing
    )

    // Анимируем единый прогресс расширения от 0 до 1
    val expansionProgress by transition.animateFloat(
        transitionSpec = { animSpec },
        label = "expansionProgress"
    ) { isExpanded ->
        if (isExpanded) 1f else 0f
    }

    // Анимация для перехода между частичным и полным расширением
    val fullExpansionProgress by fullExpansionTransition.animateFloat(
        transitionSpec = { swipeAnimSpec },
        label = "fullExpansionProgress"
    ) { isFullyExpanded ->
        if (isFullyExpanded) 1f else 0f
    }

    // ИСПРАВЛЕНИЕ: Используем единый метод трансформации для обеих анимаций закрытия
    val closingProgressTransformed = if (isClosing) {
        SmoothEasing.transform(closeAnimator.value)
    } else {
        0f
    }

    // Вычисляем текущую ширину на основе прогресса с единым методом трансформации
    val currentWidth = with(density) {
        val initialWidth = initialCardWidth.toPx()
        val targetWidth = targetCardWidth.toPx()

        if (isClosing) {
            lerp(targetWidth, initialWidth, closingProgressTransformed)
        } else {
            lerp(
                initialWidth,
                targetWidth,
                FastOutSlowInEasing.transform(expansionProgress)
            )
        }
    }

    // ИСПРАВЛЕНИЕ: Упрощаем расчет высоты при закрытии, напрямую интерполируя между
    // полной высотой и начальной без промежуточных состояний
    val currentHeight = with(density) {
        when {
            isClosing -> {
                // Определяем начальную высоту для анимации закрытия
                val startHeight = if (isFullyExpanded) {
                    fullExpandHeight.toPx()
                } else {
                    partialExpandHeight.toPx()
                }
                // Используем тот же трансформированный прогресс, что и для ширины
                lerp(startHeight, initialCardHeight.toPx(), closingProgressTransformed)
            }

            isFullyExpanded -> fullExpandHeight.toPx()
            expanded -> lerp(
                initialCardHeight.toPx(),
                partialExpandHeight.toPx() + (fullExpandHeight.toPx() - partialExpandHeight.toPx()) * fullExpansionProgress,
                expansionProgress
            )

            else -> initialCardHeight.toPx()
        }
    }

    // Вычисляем текущее положение карточки
    val cardCenterX = selectedCardCenter.x
    val screenCenterX = with(density) { (screenWidth / 2).toPx() }

    // Рассчитываем начальную и конечную левые границы
    val initialLeftX = cardCenterX - with(density) { initialCardWidth.toPx() / 2 }
    val targetLeftX = with(density) { (screenWidth / 2 - targetCardWidth / 2).toPx() }
    val finalWidth = with(density) { targetCardWidth.toPx() }
    val finalLeftX = with(density) { (screenWidth / 2 - targetCardWidth / 2).toPx() }

    // Используем тот же прогресс для всех анимаций
    val currentLeftX = if (isClosing) {
        // При закрытии возвращаемся к исходной позиции с единым методом трансформации
        lerp(finalLeftX, initialLeftX, closingProgressTransformed)
    } else {
        // При открытии сразу устанавливаем позицию относительно центра экрана
        // Ширина меняется, но всегда остается центрированной
        with(density) { (screenWidth / 2).toPx() } - (currentWidth / 2)
    }

    // ИСПРАВЛЕНИЕ: Более тщательно рассчитываем позиции верхней границы
    val initialTopY = selectedCardCenter.y - with(density) { initialCardHeight.toPx() / 2 }
    val partialExpandTopY = with(density) { (screenHeight - partialExpandHeight).toPx() }

    // ИСПРАВЛЕНИЕ: При полном развертывании карточка должна начинаться точно с верха экрана
    val fullExpandTopY = -1f // Начинаем с верха экрана с небольшим отрицательным смещением

    // Интерполируем между частичным и полным расширением
    val targetTopY = if (expanded || isClosing) {
        if (isClosing) {
            partialExpandTopY
        } else {
            lerp(partialExpandTopY, fullExpandTopY, fullExpansionProgress)
        }
    } else {
        initialTopY
    }

    // Вычисляем текущую позицию для анимации с единым методом трансформации
    val currentTopY = if (isClosing) {
        lerp(targetTopY, initialTopY, closingProgressTransformed)
    } else if (isFullyExpanded && dragOffset > 0) {
        // В случае свайпа вниз из полного экрана - плавная анимация без смещения
        lerp(initialTopY, targetTopY, expansionProgress)
    } else {
        // В остальных случаях можно добавлять dragOffset
        lerp(initialTopY, targetTopY, expansionProgress) + dragOffset
    }

    // ЗАЩИТА ОТ СПАМА: обработка закрытия статьи с проверкой временного интервала
    val closeExpandedArticle = {
        val currentTime = System.currentTimeMillis()
        // Проверяем, не выполняется ли уже анимация и прошло ли достаточно времени с последнего действия
        if (!isClosing && !isAnimationInProgress && (currentTime - lastActionTime > minActionInterval)) {
            isClosing = true
            isAnimationInProgress = true // Устанавливаем флаг, что анимация в процессе
            lastActionTime = currentTime // Обновляем время последнего действия
            shouldHideBottomNav = false // Показываем BottomNav при закрытии
            previousCardCenter = selectedCardCenter
        }
    }

    // ЗАЩИТА ОТ СПАМА: функция для переключения между частичным и полным расширением
    val toggleFullExpansion = {
        val currentTime = System.currentTimeMillis()
        // Проверяем, не выполняется ли уже анимация и прошло ли достаточно времени с последнего действия
        if (!isClosing && !isAnimationInProgress && (currentTime - lastActionTime > minActionInterval)) {
            isFullyExpanded = !isFullyExpanded
            lastActionTime = currentTime // Обновляем время последнего действия
            shouldHideBottomNav = isFullyExpanded
            dragOffset = 0f
        }
    }

    // Создаем основной контейнер
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D0D))
    ) {
        // 1. Карусель с карточками (нижний слой) - всегда видима
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = (screenWidth - initialCardWidth) / 2),
            pageSpacing = 0.dp,
            modifier = Modifier
                .fillMaxSize()
                .height(580.dp),
            userScrollEnabled = expandedIndex == -1 && !isClosing // Запрещаем прокрутку при открытой статье
        ) { page ->
            Box(
                modifier = Modifier
                    .width(initialCardWidth)
                    .onGloballyPositioned { coordinates ->
                        if ((page == pagerState.currentPage && expandedIndex == -1) ||
                            (expandedIndex == page)
                        ) {
                            val position = coordinates.positionInRoot()
                            val size = coordinates.size
                            selectedCardCenter = Offset(
                                position.x + size.width / 2f,
                                position.y + size.height / 2f
                            )
                            selectedCardSize = size
                        }
                    }
                    .graphicsLayer {
                        val pageOffset = ((pagerState.currentPage - page) +
                                pagerState.currentPageOffsetFraction).absoluteValue
                        scaleX = lerp(0.85f, 1f, 1f - pageOffset.coerceIn(0f, 1f))
                        scaleY = lerp(0.85f, 1f, 1f - pageOffset.coerceIn(0f, 1f))

                        if (expandedIndex != -1) {
                            // Все карточки видимы, но с пониженной прозрачностью
                            alpha =
                                if (expandedIndex == page) 1f else 1f
                        } else if (isClosing && expandedIndex == page) {
                            // Анимация появления карточки при закрытии
                            alpha = closeAnimator.value
                        } else {
                            alpha = 1f
                        }
                    }
            ) {
                ArticleCard(
                    article = articles[page],
                    onClick = {
                        val currentTime = System.currentTimeMillis()
                        // ЗАЩИТА ОТ СПАМА: проверяем интервал между действиями и блокировку анимации
                        if (expandedIndex == -1 && !isClosing && !isAnimationInProgress &&
                            (currentTime - lastActionTime > minActionInterval)
                        ) {
                            // Prepare animation values before setting expandedIndex
                            dragOffset = 0f
                            lastActionTime = currentTime // Обновляем время последнего действия

                            // ИСПРАВЛЕНИЕ: Устанавливаем isAnimationInProgress внутри launch,
                            // чтобы обеспечить правильную последовательность
                            // Use coroutine to add a tiny delay before starting expansion
                            scope.launch {
                                isAnimationInProgress = true // Блокируем анимацию
                                delay(16) // Wait for one frame
                                expandedIndex = page
                                isFullyExpanded = false

                                // ИСПРАВЛЕНИЕ: Используем try/finally для гарантированного сброса флага
                                try {
                                    // Ждем завершения анимации открытия
                                    delay(fixedOpenDuration.toLong())
                                } finally {
                                    // Гарантированно сбрасываем флаг блокировки
                                    isAnimationInProgress = false
                                }
                            }
                        }
                    },
                    // СТАБИЛИЗАЦИЯ: используем более плавное значение прогресса
                    expansionProgress = if (isClosing) {
                        // Применяем сглаживание к анимации закрытия
                        1f - closeAnimator.value
                    } else {
                        // Применяем сглаживание к анимации открытия
                        expansionProgress
                    },
                )
            }
        }
        var showCreateArticle by remember { mutableStateOf(false) }
        // 2. BottomNavStub внизу экрана (средний слой)
        AnimatedVisibility(
            visible = !shouldHideBottomNav,
            enter = fadeIn(
                // СТАБИЛИЗАЦИЯ: фиксированная длительность анимации появления
                animationSpec = tween(durationMillis = 300, easing = SmoothEasing)
            ),
            exit = fadeOut(
                // СТАБИЛИЗАЦИЯ: фиксированная длительность анимации исчезновения
                animationSpec = tween(durationMillis = 300, easing = SmoothEasing)
            ),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                BottomNavStub(
                    onProfileClick,
                    onCreateArticleClick = { showCreateArticle = true }) //createArticle
            }
        }
        if (showCreateArticle) {
            CreateArticle(onClose = { showCreateArticle = false }, viewModel, onPostError ={}, onPostSuccess = {})
        }
        var showNotificationsOverlay by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {

            var showNotificationsOverlay by remember { mutableStateOf(false) }

            // Верхняя панель
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .zIndex(if (showNotificationsOverlay) 0f else 1f) // Если уведомления открыты, панель под ними
            ) {
                TopBar(
                    onNotificationsClick = { showNotificationsOverlay = true }
                )
            }

            // Полноэкранное окно - размещаем НА УРОВНЕ КОРНЕВОГО BOX
            if (showNotificationsOverlay) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(10f) // Обеспечиваем, что ПОВЕРХ всего
                ) {
                    FullScreenNotifications(onClose = { showNotificationsOverlay = false })
                }
            }
        }


        // 3. Невидимая область для обработки касаний вне статьи (верхний слой)
        if (expandedIndex != -1 || isClosing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = expandedIndex != -1 && !isClosing && !isAnimationInProgress
                    ) {
                        // Важно: при клике на фон закрываем статью только если не выполняется анимация
                        if (!isClosing && !isAnimationInProgress) {
                            closeExpandedArticle()
                        }
                    }
            )

            // 4. Отображаем расширенную статью (самый верхний слой)
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        this.translationX = currentLeftX
                        this.translationY = currentTopY
                        this.clip = !isFullyExpanded
                    }
                    .width(with(density) { currentWidth.toDp() })
                    .height(with(density) { currentHeight.toDp() })
                    .background(
                        color = if (expandedIndex >= 0 && expandedIndex < articles.size)
                            ArticleColorPatterns[articles[expandedIndex].colorPatternIndex].background
                        else
                            Color.Transparent,
                        shape = RoundedCornerShape(
                            topStart = 52.dp,
                            topEnd = 52.dp,
                            bottomStart = 52.dp,
                            bottomEnd = 52.dp
                        )
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { /* Клик на статью ничего не делает */ }
                    .zIndex(10f)

            ) {
                if ((expandedIndex >= 0 && expandedIndex < articles.size) || isClosing) {
                    val articleIndex =
                        if (expandedIndex >= 0 && expandedIndex < articles.size) expandedIndex else 0

                    // Весь контент статьи в одном Box
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        // СТАБИЛИЗАЦИЯ: передаем более сглаженные значения прогресса
                        ArticleContent(
                            article = articles[articleIndex],
                            expandProgress = if (isClosing) {
                                1f - closeAnimator.value
                            } else {
                                expansionProgress
                            },
                            fullExpandProgress = if (isClosing) 0f else fullExpansionProgress,
                            onHeaderSwipe = { toggleFullExpansion() }
                        )
                    }

                    // Кнопка закрытия - показываем при любом расширении
                    AnimatedVisibility(
                        visible = (if (isClosing) 1f - closeAnimator.value else expansionProgress) > 0.7f,
                        enter = fadeIn(
                            // СТАБИЛИЗАЦИЯ: фиксированная длительность анимации
                            animationSpec = tween(durationMillis = 200, easing = SmoothEasing)
                        ),
                        exit = fadeOut(
                            // СТАБИЛИЗАЦИЯ: фиксированная длительность анимации
                            animationSpec = tween(durationMillis = 200, easing = SmoothEasing)
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp)
                                .size(32.dp)
                                .background(Color.Gray.copy(alpha = 0.5f), CircleShape)
                                .clickable {
                                    // ЗАЩИТА ОТ СПАМА: проверяем, не выполняется ли уже анимация
                                    if (!isClosing && !isAnimationInProgress) {
                                        closeExpandedArticle()
                                    }
                                }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(16.dp),
                                tint = Color.White
                            )
                        }
                    }

                    // Индикатор свайпа - показываем только в частично развернутом режиме
                    AnimatedVisibility(
                        visible = (if (isClosing) 1f - closeAnimator.value else expansionProgress) > 0.9f &&
                                (if (isClosing) 0f else fullExpansionProgress) < 0.1f,
                        enter = fadeIn(
                            // СТАБИЛИЗАЦИЯ: фиксированная длительность анимации
                            animationSpec = tween(durationMillis = 200, easing = SmoothEasing)
                        ),
                        exit = fadeOut(
                            // СТАБИЛИЗАЦИЯ: фиксированная длительность анимации
                            animationSpec = tween(durationMillis = 200, easing = SmoothEasing)
                        ),
                        modifier = Modifier.align(Alignment.TopCenter)
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .width(40.dp)
                                .height(4.dp)
                                .background(Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                        )
                    }
                }
            }
        }
    }

    // ИСПРАВЛЕНИЕ: Дополнительная проверка на сброс состояния анимации
    // Это защита "последнего шанса" на случай, если блокировка не была снята
    DisposableEffect(Unit) {
        onDispose {
            // Сбрасываем все блокировки при уничтожении компонента
            isAnimationInProgress = false
        }
    }
}

// Обновленный компонент для контента статьи, который учитывает прогресс раскрытия


@Composable
fun ArticleContent(
    article: Article,
    expandProgress: Float,
    fullExpandProgress: Float,
    onHeaderSwipe: () -> Unit,

    ) {
    // Используем те же шрифты и содержимое, что и на карточке,
    // с анимацией увеличения при раскрытии
    val titleSizeAndHeight = lerp(24.sp, 26.sp, expandProgress)
    val subscribeSize = lerp(12.sp, 14.sp, expandProgress)
    val articleHeight = lerp(17.6.sp, 19.2.sp, expandProgress)
    val paddingAfterDate = lerp(6.dp, 15.dp, expandProgress)

    val subscribeButtonPadding = lerp(30.dp, 86.dp, expandProgress)
    val subscribeButtonHorizontalPadding = lerp(15.dp, 25.dp, expandProgress)
    val subscribeButtonHeight = lerp(36.dp, 38.dp, expandProgress)

    // Состояние прокрутки для всего контента
    val scrollState = rememberScrollState()

    // Получаем цветовую схему для карточки
    val pattern = ArticleColorPatterns[article.colorPatternIndex]

    // Весь контент помещаем в скролл
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .then(
                if (expandProgress > 0.9f) {
                    Modifier.verticalScroll(scrollState)
                } else {
                    Modifier
                }
            )
    ) {
        // Верхняя часть статьи (заголовок, автор, теги) - теперь включена в скролл
        Column(
            modifier = Modifier
                .padding(start = 25.dp, end = 25.dp, top = 40.dp)
                // Обработка свайпа для верхней части
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragStart = { },
                        onDragEnd = { },
                        onDragCancel = { },
                        onVerticalDrag = { change, dragAmount ->
                            change.consume()
                            if (dragAmount < 0) {
                                // Свайп вверх - расширяем
                                onHeaderSwipe()
                            } else if (dragAmount > 0) {
                                // Свайп вниз - сжимаем
                                onHeaderSwipe()
                            }
                        }
                    )
                }
        ) {
            // Теги с анимированным появлением
            HorizontalTagRow(
                tags = article.tags,
                color = ArticleColorPatterns[article.colorPatternIndex].buttonColor,
                expandProgress = expandProgress
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Заголовок
            Text(
                text = article.title,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = titleSizeAndHeight,
                    lineHeight = titleSizeAndHeight
                ),
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Опубликовано: ${article.date}",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(paddingAfterDate))

            // Автор и кнопка подписки
            Column {
                // Автор
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Иконка автора (заглушка)
                    Image(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = "Author Icon",
                        modifier = Modifier.size(50.dp), // Уменьшаем размер аватара
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(8.dp)) // Уменьшаем отступ

                    // Имя автора с ограничением ширины
                    Column(
                        modifier = Modifier.weight(1f) // Занимает доступное пространство, но не больше
                    ) {
                        Text(
                            text = "Автор:",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.Black
                        )
                        Text(
                            text = article.author,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.Black,
                            maxLines = 1, // Ограничиваем количество строк
                            overflow = TextOverflow.Ellipsis // Добавляем многоточие при переполнении
                        )
                    }

                    // Кнопка подписки с уменьшенными отступами
                    Text(
                        text = "Подписаться",
                        modifier = Modifier
                            .clickable { /*TODO: подписка*/ }
                            .background(pattern.buttonColor, shape = RoundedCornerShape(52.dp))
                            .padding(
                                horizontal = 15.dp,
                                vertical = 10.dp
                            ), // Уменьшаем отступы кнопки
                        color = Color.Black,
                        style = MaterialTheme.typography.displaySmall,
                    )
                }
            }
        }

        // Контент статьи
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 25.dp)
                .padding(top = 20.dp)
        ) {
            // Текст статьи
            Text(
                text = article.content,
                style = MaterialTheme.typography.displayMedium.copy(lineHeight = articleHeight),
                color = Color.Black
            )

            // Добавляем панель реакций, которая плавно появляется в конце статьи
            Spacer(modifier = Modifier.height(20.dp))

            // Панель реакций с анимацией появления
            // Она должна появляться примерно в тот момент, когда исчезает из свернутой карточки
            ReactionPanelBottomContent(
                article = article,
                pattern = pattern,
                expandProgress = expandProgress
            )

            // Добавляем отступ внизу для удобства прокрутки
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun ReactionPanelBottomContent(
    article: Article,
    pattern: ArticleColorPattern,
    expandProgress: Float
) {
    // Анимируем появление панели реакций
    // При открытии статьи ее opacity меняется от 0 до 1
    // Начинаем показывать, когда expandProgress > 0.3 (примерно когда панель в карточке начинает исчезать)
    val reactionPanelOpacity = if (expandProgress < 0.3f) {
        0f
    } else {
        ((expandProgress - 0.3f) * (1f / 0.7f)).coerceIn(0f, 1f)
    }

    // Создаем анимацию для вертикального смещения
    // Панель "выезжает" снизу (начинается ниже видимой области и поднимается вверх)
    val reactionPanelOffset = animateDpAsState(
        targetValue = if (expandProgress > 0.5f) 0.dp else 20.dp,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "reactionPanelOffset"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                // Применяем анимацию прозрачности
                alpha = reactionPanelOpacity
                // Применяем анимацию смещения
                translationY = reactionPanelOffset.value.toPx()
            }
    ) {
        // Панель реакций (лайки, комментарии, закладки)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.likebottom),
                contentDescription = "Like Logo",
                colorFilter = ColorFilter.tint(pattern.reactionColor),
                modifier = Modifier
                    .clickable { /* TODO Поставить лайк*/ }
                    .size(33.dp),
            )

            // Иконка лайка и количество лайков
            Text(
                text = article.likes.toString(),
                color = Color.Black,
                style = MaterialTheme.typography.displayLarge,
            )
            Image(
                painter = painterResource(id = R.drawable.commentbottom),
                colorFilter = ColorFilter.tint(pattern.reactionColor),
                contentDescription = "Comment Logo",
                modifier = Modifier
                    .clickable { /* TODO Оставить комментарий*/ }
                    .size(35.dp),
            )
            // Иконка комментариев и количество
            Text(
                text = article.comments.toString(),
                color = Color.Black,
                style = MaterialTheme.typography.displayLarge,
            )
            Image(
                painter = painterResource(id = R.drawable.bookmarkbottom1),
                contentDescription = "Bookmark Logo",
                colorFilter = ColorFilter.tint(pattern.reactionColor),
                modifier = Modifier
                    .clickable { /* TODO Сохранить себе*/ }
                    .size(30.dp),
            )

            Text(
                text = article.sakladka.toString(),
                color = Color.Black,
                style = MaterialTheme.typography.displayLarge,
            )

            Spacer(modifier = Modifier.width(140.dp))

            Image(
                painter = painterResource(id = R.drawable.flag),
                contentDescription = "Bookmark Logo",
                colorFilter = ColorFilter.tint(pattern.reactionColor),
                modifier = Modifier
                    .clickable { /* TODO Сохранить себе*/ }
                    .size(27.dp),
            )
        }
    }
}
// ПоДписаться ...