package com.example.urfulive.ui.settings.account

import NavbarCallbacks
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.urfulive.R
import com.example.urfulive.components.BottomNavBar
import com.example.urfulive.ui.createarticle.CreateArticle
import com.example.urfulive.ui.createarticle.CreateArticleViewModel
import com.example.urfulive.ui.settings.ArrowSettingsItem

@Composable
fun AccountSettings(
    onClose: () -> Unit = {},
    onUsernameChangeClick: () -> Unit = {},
    onDateChangeClick: () -> Unit = {},
    onMailChangeClick: () -> Unit = {},
    onPasswordChangeClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onSavedClick: () -> Unit = {},
    onMessagesClick: () -> Unit = {},
    currentScreen: String = "profile",
    navbarCallbacks: NavbarCallbacks? = null,
    viewModel: AccountViewModel = viewModel()
) {

    val userState by viewModel.user.collectAsState()

    var showCreateArticle by remember { mutableStateOf(false) }
    if (showCreateArticle) {
        Box(modifier = Modifier
            .fillMaxSize()
            .zIndex(300f)) {  // Используем очень высокий zIndex
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
                        text = "Аккаунт",
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
                    .padding(top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (userState != null) {
                    // Центрированные элементы настроек с ограниченной шириной
                    ArrowSettingsItem(
                        title = "Имя пользователя",
                        currentValue = "${"@"}" + userState!!.username,
                        onClick = onUsernameChangeClick,
                    )

                    ArrowSettingsItem(
                        title = "Дата рождения",
                        currentValue = userState!!.birthDate,
                        onClick = onDateChangeClick,
                    )

                    ArrowSettingsItem(
                        title = "E-mail",
                        currentValue = userState!!.email,
                        onClick = onMailChangeClick,
                    )

                } else {
                    ArrowSettingsItem(
                        title = "Имя пользователя",
                        currentValue = "${"@"}username",
                        onClick = onUsernameChangeClick,
                    )

                    ArrowSettingsItem(
                        title = "Дата рождения",
                        currentValue = "01.01.2001",
                        onClick = onDateChangeClick,
                    )

                    ArrowSettingsItem(
                        title = "E-mail",
                        currentValue = "your_mail@gmail.com",
                        onClick = onMailChangeClick,
                    )
                }
                ArrowSettingsItem(
                    title = "Сменить пароль",
                    currentValue = "********",
                    onClick = onPasswordChangeClick,
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

