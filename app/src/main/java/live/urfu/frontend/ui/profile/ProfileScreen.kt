package live.urfu.frontend.ui.profile

import NavbarCallbacks
import TagChip
import TagSizes
import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import live.urfu.frontend.R
import live.urfu.frontend.ui.footer.BottomNavBar
import live.urfu.frontend.ui.createarticle.CreateArticle
import live.urfu.frontend.ui.createarticle.CreateArticleViewModel
import live.urfu.frontend.ui.main.PostColorPatterns
import coil.compose.AsyncImage
import live.urfu.frontend.ui.main.PostViewModel
import live.urfu.frontend.ui.notifiaction.NotificationsScreen


@SuppressLint("ViewModelConstructorInComposable")
@Composable
@Preview
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    isOwnProfile: Boolean = true,
    onProfileClick: () -> Unit = {},
    onCreateArticleClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onSavedClick: () -> Unit = {},
    onMessagesClick: () -> Unit = {},
    onReportClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    currentScreen: String = "profile",
    navbarCallbacks: NavbarCallbacks? = null,
    onCloseOverlay: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onSubscribeClick: () -> Unit = {},
    onCommentsClick: (Long) -> Unit = {},
    sharedPostViewModel: PostViewModel,
    onNotificationsClick: () -> Unit = {}
) {
    val user = viewModel.user
    val posts = viewModel.posts

    val backgroundColor = Color(0xFF131313)
    val accentColor = Color(0xFFF6ECC9)
    val cornerRadius = 31.dp

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val isSmallScreen = screenWidth < 400
    val editProfileFontSize = when {
        isSmallScreen -> 14.sp
        else -> 16.sp
    }

    val userId = viewModel.currentUserId

    var expandedPostIndex by remember { mutableStateOf<Int?>(null) }
    var showCreateArticle by remember { mutableStateOf(false) }
    var showNotificationsOverlay by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }

    expandedPostIndex?.let { index ->
        if (index < posts.size) {
            ExpandedPostOverlay(
                post = posts[index],
                onClose = { expandedPostIndex = null },
                onCommentsClick = { postId ->
                    onCommentsClick(postId)
                },
                viewModel = sharedPostViewModel
            )
        }
    }

    // Handle back press when post is expanded
    BackHandler(enabled = expandedPostIndex != null) {
        expandedPostIndex = null
    }

    if (showCreateArticle) {
        CreateArticle(
            onClose = { showCreateArticle = false },
            onPostSuccess = {},
            onPostError = {},
            viewModel = CreateArticleViewModel()
        )
    }

    // Используем Scaffold для правильного размещения контента и навигационной панели
    Scaffold(
        bottomBar = {
            // Добавляем нижнюю панель навигации только для своего профиля
            if (isOwnProfile) {
                BottomNavBar(
                    onProfileClick = navbarCallbacks?.onProfileClick ?: onProfileClick,
                    onCreateArticleClick = { showCreateArticle = true },
                    onHomeClick = navbarCallbacks?.onHomeClick ?: onHomeClick,
                    onSavedClick = onSavedClick,
                    onMessagesClick = onMessagesClick,
                    currentScreen = currentScreen
                )
            }
        }
    ) { paddingValues ->
        // Основное содержимое с учетом отступов для навигационной панели
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Верхняя часть профиля - не изменяется
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.5f),
                    contentAlignment = Alignment.Center
                ) {
                    // Ensure AsyncImage is first (background)
                    AsyncImage(
                        model = user?.backgroundUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize(),
                        contentScale = ContentScale.None
                    )
                    if (isOwnProfile) {
                        Image(
                            painter = painterResource(id = R.drawable.settings),
                            contentDescription = "Настройки пользователя",
                            modifier = Modifier
                                .systemBarsPadding()
                                .align(Alignment.TopStart)
                                .padding(top = 16.dp, start = 16.dp)
                                .size(35.dp)
                                .clickable { onSettingsClick() }
                        )
                        Image(
                            painter = painterResource(id = R.drawable.bell),
                            contentDescription = "Уведомления",
                            modifier = Modifier
                                .systemBarsPadding()
                                .align(Alignment.TopEnd)
                                .padding(top = 16.dp, end = 16.dp)
                                .size(35.dp)
                                .clickable { showNotificationsOverlay = true }
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.flag),
                            contentDescription = "Пожаловаться на пользователя",
                            modifier = Modifier
                                .navigationBarsPadding()
                                .align(Alignment.TopEnd)
                                .padding(top = 31.dp, end = 16.dp)
                                .size(35.dp)
                                .clickable { showReportDialog = true },
                            colorFilter = ColorFilter.tint(Color.White)
                        )
                        BackHandler {
                            onCloseOverlay()
                        }
                        Image(
                            painter = painterResource(id = R.drawable.chevron_left),
                            contentDescription = "Назад",
                            modifier = Modifier
                                .navigationBarsPadding()
                                .align(Alignment.TopStart)
                                .padding(top = 31.dp, end = 16.dp)
                                .size(35.dp)
                                .clickable { onCloseOverlay() },
                            colorFilter = ColorFilter.tint(Color.White)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Блок аватарки
                        AsyncImage(
                            model = user?.avatarUrl,
                            contentDescription = "Аватар пользователя",
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color.White, CircleShape),
                            contentScale = ContentScale.Fit,
                            placeholder = painterResource(R.drawable.ava),
                            error = painterResource(R.drawable.ava)
                        )

                        if (user != null) {
                            Text(
                                text = user.username,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 26.sp
                                ),
                                modifier = Modifier.padding(top = 8.dp)
                            )

                            Text(
                                text = "${user.followersCount} подписчиков",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.DarkGray,
                            )

                            Button(
                                onClick = { if (isOwnProfile) onEditProfileClick() else onSubscribeClick() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isOwnProfile) Color(0xFF191818) else Color(0xFF3D7BF4),
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.padding(top = 6.dp, start = 60.dp, end = 60.dp)
                            ) {
                                Text(
                                    text = when {
                                        isOwnProfile -> "Редактировать профиль"
                                        userId == null -> "Загрузка"
                                        else -> {
                                            if (user.followers.contains(userId.toInt())) "Подписан" else "Подписаться"
                                        }
                                    },
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = editProfileFontSize,
                                        lineHeight = editProfileFontSize * 1.3
                                    )
                                )
                            }

                            Text(
                                text = user.description ?: "Описание отсутствует",
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = Color.Gray,
                                modifier = Modifier
                                    .padding(top = 6.dp, start = 58.dp, end = 58.dp)
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center,
                            )
                        } else {
                            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.6f)
                        .background(backgroundColor)
                        .padding(
                            top = 7.dp,
                            start = 16.dp,
                            end = 16.dp
                        )
                ) {
                    Text(
                        text = "Посты",
                        style = MaterialTheme.typography.bodyLarge,
                        color = accentColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 5.dp),
                        textAlign = TextAlign.Center
                    )

                    HorizontalDivider(thickness = 2.dp, color = accentColor)

                    Spacer(modifier = if (isOwnProfile) Modifier.height(16.dp) else Modifier.height(9.dp))

                    // Убираем дополнительный Box и лишние отступы
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        // Правильно настраиваем contentPadding для нижней панели
                        contentPadding = PaddingValues(
                            bottom = paddingValues.calculateBottomPadding()
                        ),
                    ) {
                        if (isOwnProfile) {
                            item {
                                Button(
                                    onClick = { showCreateArticle = true },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF292929),
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(46.dp)
                                        .clip(RoundedCornerShape(cornerRadius))
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.plus_circle),
                                            contentDescription = "plus",
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                        Text(
                                            text = "Добавить пост",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }
                            }
                        }

                        items(posts.size) { index ->
                            val post = posts[index]
                            val colorPatternIndex = post.id.rem(PostColorPatterns.size).toInt()

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .clip(RoundedCornerShape(cornerRadius))
                                    .background(color = PostColorPatterns[colorPatternIndex].background)
                                    .clickable { expandedPostIndex = index },
                            ) {
                                Text(
                                    text = post.title,
                                    modifier = Modifier.padding(top = 16.dp, start = 25.dp, end = 25.dp),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.Black
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.padding(top = 58.dp, start = 25.dp, end = 25.dp),
                                ) {
                                    post.tags.take(2).forEach { tag ->
                                        TagChip(
                                            tag = tag.name,
                                            color = PostColorPatterns[colorPatternIndex].buttonColor,
                                            size = TagSizes.Small
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (showNotificationsOverlay) {
                NotificationsScreen(
                    onClose = { showNotificationsOverlay = false },
                )
            }

            // Модальное окно жалобы
            if (showReportDialog) {
                ReportDialog(
                    onDismiss = { showReportDialog = false },
                    onSubmit = { reason ->
                        // Здесь вызываем callback для отправки жалобы
                        onReportClick()
                        showReportDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun ReportDialog(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var reportReason by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
                .clickable(enabled = false) { }, // Предотвращаем закрытие при клике на карту
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2A2A2A)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Опишите причину\nжалобы ниже",
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 22.sp, lineHeight = 22.sp, color = Color.White),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                OutlinedTextField(
                    value = reportReason,
                    onValueChange = { reportReason = it },
                    placeholder = {
                        Text(
                            text = "Причина жалобы",
                            color = Color.Gray,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 14.sp,)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.Gray,
                        unfocusedBorderColor = Color.Gray,
                        cursorColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 4
                )

                Button(
                    onClick = { onSubmit(reportReason) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF404040),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = reportReason.isNotBlank()
                ) {
                    Text(
                        text = "Отправить",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}