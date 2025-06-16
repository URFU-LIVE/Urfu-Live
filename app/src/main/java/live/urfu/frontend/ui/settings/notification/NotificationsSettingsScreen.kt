package live.urfu.frontend.ui.settings.notification

import NavbarCallbacks
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import live.urfu.frontend.R
import live.urfu.frontend.ui.footer.BottomNavBar
import live.urfu.frontend.ui.createarticle.CreateArticle
import live.urfu.frontend.ui.createarticle.CreateArticleViewModel
import live.urfu.frontend.ui.settings.ToggleSettingsItem

@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun NotificationsSettings(
    onClose: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onSavedClick: () -> Unit = {},
    onMessagesClick: () -> Unit = {},
    currentScreen: String = "profile",
    navbarCallbacks: NavbarCallbacks? = null,
) {
    var systemNotificationsEnabled by remember { mutableStateOf(false) }
    var newMessagesEnabled by remember { mutableStateOf(true) }
    var postInteractionsEnabled by remember { mutableStateOf(true) }
    var subscriptionsEnabled by remember { mutableStateOf(true) }
    var commentRepliesEnabled by remember { mutableStateOf(true) }
    var newCommentsEnabled by remember { mutableStateOf(true) }
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val isSmallScreen = screenWidth < 400
    val spaceAfterHeader = when {
        isSmallScreen -> 0.dp
        else -> 20.dp
    }

    var showCreateArticle by remember { mutableStateOf(false) }
    if (showCreateArticle) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(300f)
        ) {  // Используем очень высокий zIndex
            CreateArticle(
                onClose = { showCreateArticle = false },
                onPostSuccess = {},
                onPostError = {},
                viewModel = CreateArticleViewModel()
            )
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(100f)
            .background(Color(0xFF131313))
    )
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),

            ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 23.dp, bottom = 15.dp),
            )
            {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.chevron_left),
                        contentDescription = "Arrow",
                        modifier = Modifier
                            .clickable { onClose() }
                            .padding(start = 15.dp)
                    )


                    Text(
                        text = "Уведодмления",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier
                            .padding(start = 10.dp)
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = spaceAfterHeader),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Центрированные элементы настроек с ограниченной шириной
                ToggleSettingsItem(
                    title = "Системные уведомления",
                    isEnabled = systemNotificationsEnabled,
                    onToggleChanged = { systemNotificationsEnabled = it }
                )

                ToggleSettingsItem(
                    title = "Новые сообщения",
                    isEnabled = newMessagesEnabled,
                    onToggleChanged = { newMessagesEnabled = it }
                )

                ToggleSettingsItem(
                    title = "Взаимодействия с постами",
                    isEnabled = postInteractionsEnabled,
                    onToggleChanged = { postInteractionsEnabled = it }
                )

                ToggleSettingsItem(
                    title = "Подписки",
                    isEnabled = subscriptionsEnabled,
                    onToggleChanged = { subscriptionsEnabled = it }
                )

                ToggleSettingsItem(
                    title = "Ответы на комментарии",
                    isEnabled = commentRepliesEnabled,
                    onToggleChanged = { commentRepliesEnabled = it }
                )

                ToggleSettingsItem(
                    title = "Новые комментарии",
                    isEnabled = newCommentsEnabled,
                    onToggleChanged = { newCommentsEnabled = it }
                )
            }
        }
        BottomNavBar(
            onProfileClick = onClose,
            onCreateArticleClick = { showCreateArticle = true },
            onHomeClick = navbarCallbacks?.onHomeClick ?: onHomeClick,
            onSavedClick = onSavedClick,
            onMessagesClick = onMessagesClick,
            currentScreen = currentScreen,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}