package live.urfu.frontend.ui.snackBar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import live.urfu.frontend.R

@Composable
fun TopSnackBarWithDismiss(
    message: String,
    visible: Boolean,
    onDismiss: () -> Unit,
    autoHideDuration: Long = 3000L,
    backgroundColor: Color = Color(0xFF4CAF50),
) {
    var effectKey by remember { mutableLongStateOf(0L) }

    LaunchedEffect(visible) {
        if (visible) {
            effectKey = System.currentTimeMillis()
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { fullHeight -> -fullHeight - 150 },
            animationSpec = spring(
                dampingRatio = 0.75f, // Легкий отскок для жизненности
                stiffness = 300f // Немного медленнее для плавности
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = 500,
                easing = FastOutSlowInEasing
            )
        ),
        exit = slideOutVertically(
            targetOffsetY = { fullHeight -> -fullHeight - 100 },
            animationSpec = spring(
                dampingRatio = 1f, // Без отскока при выходе
                stiffness = 500f // Быстрее исчезает
            )
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = 200
            )
        )
    ) {
        Column {
            Spacer(modifier = Modifier.statusBarsPadding())

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 11.dp)
                    .zIndex(1000f),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .widthIn(max = 400.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(backgroundColor)
                        .padding(horizontal = 25.dp, vertical = 30.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = message,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(0.9f)
                        )

                        Spacer(modifier = Modifier.weight(0.05f))

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.x),
                                contentDescription = "Закрыть",
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }
            }
        }

        LaunchedEffect(effectKey) {
            if (visible && autoHideDuration > 0) {
                delay(autoHideDuration)
                onDismiss()
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose {}
    }
}
