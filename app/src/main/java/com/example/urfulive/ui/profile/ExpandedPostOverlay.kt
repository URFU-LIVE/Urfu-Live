package com.example.urfulive.ui.profile

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
import com.example.urfulive.R
import com.example.urfulive.data.model.Post
import com.example.urfulive.ui.main.PostColorPatterns
import TagChip
import TagSizes
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.urfulive.ui.main.PostViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

@Composable
fun ExpandedPostOverlay(
    post: Post,
    onClose: () -> Unit,
    onCommentsClick: () -> Unit = {},
    viewModel: PostViewModel,
) {
    val scrollState = rememberScrollState()
    val colorPatternIndex = post.id.rem(PostColorPatterns.size).toInt()
    val pattern = PostColorPatterns[colorPatternIndex]
    val view = androidx.compose.ui.platform.LocalView.current
    val context = view.context
    val tagScrollState = rememberScrollState()

    val posts by viewModel.posts.collectAsState()
    val currentPost = posts.find { it.id == post.id } ?: post
    val likedPosts by viewModel.likedPostIds.collectAsState()
    val isLiked = likedPosts.contains(post.id)

    val subscriptions by viewModel.subscriptions.collectAsState()
    val isSubscribed = subscriptions.contains(post.author.id)
    var isLoading by remember(post.author.id) { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val systemUiController = rememberSystemUiController()
    DisposableEffect(Unit) {
        systemUiController.setStatusBarColor(Color.Black)
        onDispose { systemUiController.setStatusBarColor(Color.Transparent) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(299f)
            .background(Color.Black)
    ) {}
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x88000000))
            .zIndex(300f)
            .statusBarsPadding()
    ) {
        // Содержимое с закругленными углами
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(52.dp))
                .background(pattern.background)
                .padding(top = 16.dp) // Дополнительный отступ для контента
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(start = 24.dp, end = 24.dp, top = 40.dp, bottom = 24.dp)
            ) {
                // Post tags
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.horizontalScroll(tagScrollState),) {
                    post.tags?.forEach { tag ->
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
                        model = post.author.avatarUrl,
                        contentDescription = "Author Icon",
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.White, CircleShape)
                            .clickable { },
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.ava),
                        error = painterResource(R.drawable.ava)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Автор:",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.Black
                        )
                        Text(
                            text = post.author.username ?: post.author.name
                            ?: "Неизвестный автор",
                            style = MaterialTheme.typography.titleLarge,
                            color = pattern.textColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = pattern.textColor,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = if (isSubscribed) "Вы подписаны" else "Подписаться",
                            modifier = Modifier
                                .clickable {
                                    coroutineScope.launch {
                                        isLoading = true
                                        viewModel.subscribeAndUnsubscribe(post)
                                        isLoading = false
                                    }
                                }
                                .background(
                                    pattern.buttonColor,
                                    shape = RoundedCornerShape(52.dp)
                                )
                                .padding(horizontal = 15.dp, vertical = 10.dp),
                            color = pattern.textColor,
                            style = MaterialTheme.typography.displaySmall
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
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clickable {
                                viewModel.likeAndDislike(post.id)
                            }
                            .size(33.dp)
                    ) {
                        if (isLiked) {
                            Image(
                                painter = painterResource(id = R.drawable.like_filling),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(pattern.reactionColorFilling),
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Image(
                            painter = painterResource(id = R.drawable.likebottom),
                            colorFilter = ColorFilter.tint(pattern.reactionColor),
                            contentDescription = "Like",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Text(
                        text = currentPost.likes.toString(),
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
