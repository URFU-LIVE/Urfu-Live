package live.urfu.frontend.ui.settings

import live.urfu.frontend.ui.NavbarCallbacks
import ScreenSizeInfo
import SettingsAdaptiveSizes.settingsItemIconSize
import live.urfu.frontend.data.manager.TokenManagerInstance
import adaptiveTextStyle
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import live.urfu.frontend.R
import live.urfu.frontend.ui.footer.BottomNavBar
import live.urfu.frontend.ui.createarticle.CreateArticle
import live.urfu.frontend.ui.createarticle.CreateArticleViewModel
import kotlinx.coroutines.launch
import rememberScreenSizeInfo

@Composable
fun SettingsScreen(
    onCloseOverlay: () -> Unit = {},
    onAccountClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {},
    onInterestsClick : () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onSavedClick: () -> Unit = {},
    onMessagesClick: () -> Unit = {},
    currentScreen: String = "profile",
    navbarCallbacks: NavbarCallbacks? = null,
    viewModel: MainSettingViewModel = viewModel(),
    onLeave: () -> Unit,
    enableAnimation: Boolean = true
) {
    val screenInfo = rememberScreenSizeInfo()
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val animatedAlpha = remember { Animatable(if (enableAnimation) 0f else 1f) }
    val animatedOffset = remember { Animatable(if (enableAnimation) screenHeight.value else 0f) }
    val scrollState = rememberScrollState()

    val userState by viewModel.user.collectAsState()
    val scope = rememberCoroutineScope()

    var showCreateArticle by remember { mutableStateOf(false) }



    if (showCreateArticle) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(300f)
        ) {
            CreateArticle(
                onClose = { showCreateArticle = false },
                onPostSuccess = {},
                onPostError = {},
                viewModel = CreateArticleViewModel()
            )
        }
    }

    BackHandler { onCloseOverlay() }

    if (enableAnimation) {
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
                    .fillMaxWidth()
                    .padding(top = 23.dp, bottom = 15.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.chevron_left),
                        contentDescription = "Arrow",
                        modifier = Modifier
                            .clickable { onCloseOverlay() }
                            .padding(start = 15.dp)
                    )

                    Text(
                        text = "Настройки",
                        color = Color.White,
                        style = adaptiveTextStyle(
                            MaterialTheme.typography.headlineLarge,
                            screenInfo
                        ),
                        modifier = Modifier.padding(start = if (screenInfo.isCompact) 8.dp else 10.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth() .weight(1f)
                    .verticalScroll(rememberScrollState()).padding(bottom = 110.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (userState != null) {
                    AsyncImage(
                        model = userState!!.avatarUrl,
                        contentDescription = "Author Icon",
                        modifier = Modifier
                            .size(SettingsAdaptiveSizes.avatarSize(screenInfo))
                            .clip(CircleShape)
                            .border(2.dp, Color.White, CircleShape)
                            .clickable { },
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.ava),
                        error = painterResource(R.drawable.ava)
                    )

                    Text(
                        text = userState!!.username,
                        style = adaptiveTextStyle(
                            MaterialTheme.typography.labelMedium.copy(
                                color = Color.White,
                            ),
                            screenInfo
                        ),
                        modifier = Modifier.padding(top = if (screenInfo.isCompact) 6.dp else 8.dp)
                    )
                } else {
                    // Заглушка при загрузке
                    Image(
                        painter = painterResource(id = R.drawable.ava),
                        contentDescription = "Placeholder avatar",
                        modifier = Modifier
                            .size(SettingsAdaptiveSizes.avatarSize(screenInfo))
                            .clip(CircleShape)
                            .border(2.dp, Color.Gray, CircleShape)
                    )

                    Text(
                        text = "Загрузка...",
                        style = adaptiveTextStyle(
                            MaterialTheme.typography.labelMedium.copy(color = Color.Gray),
                            screenInfo
                        ),
                        modifier = Modifier.padding(top = if (screenInfo.isCompact) 6.dp else 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                SettingsItem(
                    title = "Аккаунт",
                    onClick = onAccountClick,
                    image = R.drawable.profilenew,
                    screenInfo = screenInfo
                )

                SettingsItem(
                    title = "Уведомления",
                    onClick = onNotificationsClick,
                    image = R.drawable.bell,
                    screenInfo = screenInfo
                )

                SettingsItem(
                    title = "Приватность",
                    onClick = onPrivacyClick,
                    image = R.drawable.eye,
                    screenInfo = screenInfo
                )

                SettingsItem(
                    title = "Изменить интересы",
                    onClick = onInterestsClick,
                    image = R.drawable.edit,
                    screenInfo = screenInfo
                )


                SettingsItem(
                    title = "Выйти из аккаунта",
                    onClick = {
                        scope.launch {
                            onLeave()
                            TokenManagerInstance.getInstance().clearTokens()
                        }
                    },
                    image = R.drawable.x,
                    screenInfo = screenInfo
                )
            }
        }

        BottomNavBar(
            onProfileClick = onCloseOverlay,
            onCreateArticleClick = { showCreateArticle = true },
            onHomeClick = navbarCallbacks?.onHomeClick ?: onHomeClick,
            onSavedClick = onSavedClick,
            onMessagesClick = onMessagesClick,
            currentScreen = currentScreen,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun SettingsItem(
    title: String,
    onClick: () -> Unit = {},
    image: Int,
    textColor: Color = Color.White,
    screenInfo: ScreenSizeInfo
) {
    val internalPadding = SettingsAdaptiveSizes.itemInternalPadding(screenInfo)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 5.dp)
            .background(Color(0xFF292929), shape = RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(internalPadding),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(27.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = image),
                contentDescription = "Icon",
                modifier = Modifier.size(settingsItemIconSize(screenInfo)),
            )

            Text(
                text = title,
                style = adaptiveTextStyle(
                    MaterialTheme.typography.titleMedium.copy(
                        fontSize = if (screenInfo.isCompact) 20.sp else 22.sp,
                        lineHeight = if (screenInfo.isCompact) 20.sp else 22.sp
                    ),
                    screenInfo
                ),
                color = textColor,
            )
        }
    }
}