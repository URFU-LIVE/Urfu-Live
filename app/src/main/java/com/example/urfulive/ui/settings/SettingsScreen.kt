package com.example.urfulive.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.urfulive.R
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(onCloseOverlay: () -> Unit = {},
                   onAccountClick: () -> Unit = {},
                   onNotificationsClick: () -> Unit = {},
                   onPrivacyClick: () -> Unit = {}) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    val animatedAlpha = remember { Animatable(0f) }
    val animatedOffset = remember { Animatable(screenHeight.value) }

    BackHandler() {
        onCloseOverlay()
    }

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(100f)
            .graphicsLayer {
                alpha = animatedAlpha.value
                translationY = animatedOffset.value
            }
            .background(Color(0xFF0D0D0D)))
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 23.dp, bottom = 15.dp)
            )
            {
                Image(
                    painter = painterResource(id = R.drawable.chevron_left),
                    contentDescription = "Arrow",
                    modifier = Modifier
                        .clickable { onCloseOverlay() }
                        .padding(start = 15.dp))

                Text(
                    text = "Настройки",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(start = 67.dp)
                )
            }


            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally // Center only items in this column
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ava),
                    contentDescription = "Аватар пользователя",
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .size(84.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape),
                    contentScale = ContentScale.Crop
                )

                // Add more centered elements below the avatar here
                // For example:
                Text(
                    text = "username",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Color.White
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                SettingsItem(
                    title = "Аккаунт",
                    onClick = onAccountClick
                )

                SettingsItem(
                    title = "Уведомления",
                    onClick = onNotificationsClick
                )

                SettingsItem(
                    title = "Приватность",
                    onClick = onPrivacyClick
                )
            }
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color(0xFF292929), shape = RoundedCornerShape(15.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 20.dp, horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )
    }
}