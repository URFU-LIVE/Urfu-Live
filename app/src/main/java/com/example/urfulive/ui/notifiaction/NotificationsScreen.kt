// NotificationsScreen.kt
package com.example.urfulive.ui.notifiaction

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.urfulive.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.urfulive.data.model.Notification
import com.example.urfulive.ui.main.PostViewModel
import kotlinx.coroutines.launch

@Composable
fun FullScreenNotifications(
    onClose: () -> Unit,
    viewModel: NotificationViewModel = viewModel(),
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

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
                        .systemBarsPadding()
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 23.dp, bottom = 15.dp))
                    {
                        Image(
                            painter = painterResource(id = R.drawable.chevron_left),
                            contentDescription = "Arrow",
                            modifier = Modifier
                                .clickable { onClose() }
                                .padding(start = 15.dp))

                        Text(
                            text = "Уведомления",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineLarge,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(start = 67.dp))
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp))
                    {
                        val notifications = viewModel.notifications

                        if (notifications.value.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 32.dp),
                                    contentAlignment = Alignment.Center)
                                {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Default.Notifications,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(64.dp)
                                                .padding(bottom = 16.dp),
                                            tint = Color.Gray)
                                        Text(
                                            text = "У вас нет новых уведомлений",
                                            color = Color.Gray)
                                    }
                                }
                            }
                        } else {
                            println(notifications)
                            items(notifications.value) { notification ->
                                NotificationItemEnhanced(notification) {

                                }
                            }
                        }
                    }
                }
            }
}

@Composable
fun NotificationItemEnhanced(
    notification: Notification,
    onClick: () -> Unit = {},

) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 0.dp, vertical = 4.dp)
            .background(Color(0xFF292929), shape = RoundedCornerShape(15.dp))
            .padding(vertical = 20.dp, horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically)
    {
        // Аватар или иконка уведомления
        Box(
            modifier = Modifier
                .size(52.dp),
            contentAlignment = Alignment.Center)
        {
            Image(
                when {
                    notification.title.contains("принята") -> painterResource(id = R.drawable.check_circle)
                    notification.title.contains("подарок") -> painterResource(id = R.drawable.gift)
                    notification.title.contains("Напоминание") -> painterResource(id = R.drawable.clock)
                    else -> painterResource(id = R.drawable.chevron_left)
                },
                contentDescription = null,
                modifier = Modifier.size(52.dp))
        }

        Spacer(modifier = Modifier.width(21.dp))

        Column(
            modifier = Modifier.weight(1f))
        {
            Text(
                text = notification.title,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = notification.message,
                style = MaterialTheme.typography.displaySmall,
                color = Color.LightGray)
        }

        if (!notification.isRead) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .align(Alignment.Top)
                    .background(color = Color(0xFFFB6C39), shape = CircleShape))
        }
    }
}