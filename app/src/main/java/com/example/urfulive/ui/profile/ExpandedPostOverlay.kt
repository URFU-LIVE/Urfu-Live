package com.example.urfulive.ui.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.urfulive.R
import com.example.urfulive.data.model.Post
import com.example.urfulive.ui.main.PostColorPatterns
import TagChip
import TagSizes
import androidx.compose.foundation.border
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

@Composable
fun ExpandedPostOverlay(
    post: Post,
    onClose: () -> Unit,
    onCommentsClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val colorPatternIndex = post.id.rem(PostColorPatterns.size).toInt()
    val pattern = PostColorPatterns[colorPatternIndex]
    val view = androidx.compose.ui.platform.LocalView.current
    val context = view.context

    DisposableEffect(Unit) {
        val window = (context as? android.app.Activity)?.window
        window?.statusBarColor = android.graphics.Color.BLACK

        onDispose {
            // Cleanup when composable is removed from the composition
            window?.statusBarColor = android.graphics.Color.TRANSPARENT // or BLACK depending on composable
        }
    }
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(tween(300, easing = FastOutSlowInEasing)) +
                    slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    ),
            exit = fadeOut(tween(300)) +
                    slideOutVertically(
                        targetOffsetY = { it / 2 },
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x88000000))
                    .systemBarsPadding()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .align(Alignment.BottomCenter)
                        .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                        .background(pattern.background)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(start = 24.dp, end = 24.dp, top = 56.dp, bottom = 24.dp)
                    ) {
                        // Post tags
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            post.tags?.take(3)?.forEach { tag ->
                                TagChip(
                                    tag = tag.name,
                                    color = pattern.buttonColor,
                                    size = TagSizes.Standard
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Post title
                        Text(
                            text = post.title ?: "",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontSize = 26.sp,
                                lineHeight = 26.sp
                            ),
                            color = pattern.textColor
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Publication date
                        Text(
                            text = "Опубликовано: ${post.time?.substring(0, 10) ?: ""}",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Author info
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AsyncImage(
                                model = post.author.avatarUrl, // URL из объекта пользователя

                                contentDescription = "Author Icon",
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, Color.White, CircleShape)
                                    .clickable { },
                                contentScale = ContentScale.Crop,
                                placeholder = painterResource(R.drawable.ava), // Плейсхолдер если загрузка или нет URL
                                error = painterResource(R.drawable.ava)       // Плейсхолдер если ошибка загрузки
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = "Автор:",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.Black
                                )
                                Text(
                                    text = post.author.username ?: post.author.name ?: "Неизвестный автор",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = pattern.textColor,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Post content
                        Text(
                            text = post.text ?: "",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 18.sp,
                                lineHeight = 28.sp
                            ),
                            color = pattern.textColor
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Reactions
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Like
                            Image(
                                painter = painterResource(id = R.drawable.likebottom),
                                colorFilter = ColorFilter.tint(pattern.reactionColor),
                                contentDescription = "Like",
                                modifier = Modifier.size(33.dp)
                            )

                            Text(
                                text = post.likes.toString(),
                                style = MaterialTheme.typography.displayLarge,
                                color = pattern.textColor
                            )


                            // Comment
                            Image(
                                painter = painterResource(id = R.drawable.commentbottom),
                                colorFilter = ColorFilter.tint(pattern.reactionColor),
                                contentDescription = "Comment",
                                modifier = Modifier
                                    .clickable { onCommentsClick() }
                                    .size(35.dp)
                            )

                            Text(
                                text = post.comments.toString(),
                                style = MaterialTheme.typography.displayLarge,
                                color = pattern.textColor
                            )

                            // Bookmark
                            Image(
                                painter = painterResource(id = R.drawable.bookmarkbottom1),
                                colorFilter = ColorFilter.tint(pattern.reactionColor),
                                contentDescription = "Bookmark",
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }

                    // Close button
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .size(36.dp)
                            .background(Color.Gray.copy(alpha = 0.7f), CircleShape)
                            .clickable { onClose() }
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}