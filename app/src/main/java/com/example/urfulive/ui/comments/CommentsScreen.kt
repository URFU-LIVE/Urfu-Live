package com.example.urfulive.ui.comments

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.urfulive.R
import com.example.urfulive.data.model.Comment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CommentsScreen(
    postId: Long,
    onClose: () -> Unit = {},
    viewModel: CommentsViewModel = viewModel(factory = CommentsViewModelFactory(postId))
) {
    val comments by viewModel.comments.collectAsState()
    val commentText = remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val previousSize = remember { mutableStateOf(0) }

    // Скролл вниз при добавлении нового комментария
    LaunchedEffect(comments.size) {
        if (comments.size > previousSize.value) {
            coroutineScope.launch {
                listState.animateScrollToItem(comments.size - 1)
            }
            previousSize.value = comments.size
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(100f)
            .background(Color(0xFF131313))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Header
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
                            .clickable { onClose() }
                            .padding(start = 15.dp)
                    )

                    Text(
                        text = "Комментарии",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }
            }

            // Comments List
            LazyColumn(
                modifier = Modifier.weight(1f),
                state = listState
            ) {
                items(comments) { comment ->
                    CommentsItem(
                        comment = comment,
                        onReplyClick = { /* TODO */ },
                        onLikeClick = { /* TODO */ },
                        onProfileClick = { /* TODO */ }
                    )
                }
            }

            // Input Field
            CommentInputField(
                text = commentText.value,
                onTextChange = { commentText.value = it },
                onSend = {
                    val trimmed = commentText.value.trim()
                    if (trimmed.isNotEmpty()) {
                        viewModel.sendComment(trimmed)
                        commentText.value = ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}



@Composable
fun CommentsItem(
    comment: Comment,
    onReplyClick: (Comment) -> Unit,
    onLikeClick: (Comment) -> Unit,
    onProfileClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(Color(0xFF292929), shape = RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AsyncImage(
                    model = comment.author.avatarUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.FillBounds,
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = comment.author.username,
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = comment.text,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Image(
                    painter = painterResource(id = R.drawable.flag),
                    contentDescription = "Report",
                    colorFilter = ColorFilter.tint(Color.White),
                    modifier = Modifier
                        .size(18.dp)
                        .clickable { /* Report comment */ }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ответить",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.clickable { onReplyClick(comment) }
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = comment.createdAt,
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
fun CommentReplyItem(
    comment: Comment,
    onReplyClick: (Comment) -> Unit,
    onLikeClick: (Comment) -> Unit,
    onProfileClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 32.dp, end = 16.dp, top = 4.dp, bottom = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF292929), shape = RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            // Similar to CommentsItem but with smaller font sizes
            Column {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.ava),
                        contentDescription = "Автор",
                        modifier = Modifier
                            .size(40.dp)
                            .clickable { onProfileClick(comment.author.id) }
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = comment.author.username,
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = comment.text,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Image(
                        painter = painterResource(id = R.drawable.flag),
                        contentDescription = "Report",
                        colorFilter = ColorFilter.tint(Color.White),
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { /* Report comment */ }
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ответить",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.clickable { onReplyClick(comment) }
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = comment.createdAt.toString(),
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
fun CommentInputField(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF292929), RoundedCornerShape(52.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.material3.TextField(
            value = text,
            onValueChange = onTextChange,
            placeholder = { Text("Написать комментарий...", color = Color.Gray) },
            modifier = Modifier.weight(1f),
            colors = androidx.compose.material3.TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Color(0xFF5B9DFC), CircleShape)
                .clickable(enabled = text.isNotEmpty()) { onSend() },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.arrow),
                contentDescription = "Отправить",
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier
                    .size(20.dp)
                    .graphicsLayer {
                        rotationZ = 180f
                    }
            )
        }
    }
}