package com.example.urfulive.ui.comments

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.urfulive.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun Comments(
    viewModel: CommentsViewModel = viewModel(),
//    postId: String,
    onClose: () -> Unit = {},
) {
    val comments by viewModel.comments.collectAsState()
//    LaunchedEffect(postId) { /*TODO*/
//        viewModel.loadComments(postId)
//    }
    var replyText by remember { mutableStateOf("") }
    var replyingTo by remember { mutableStateOf<Comment?>(null) }
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
                        text = "Комментарии",
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
                    .padding(top = 11.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyColumn {
                    items(comments) { comment ->
                        CommentsItem(
                            comment = comment,
                            onReplyClick = { /* обработка ответа */ },
                            onLikeClick = { /* обработка лайка */ },
                            onProfileClick = { /* переход на профиль */ }
                        )
                        comment.replies.forEach { reply ->
                            CommentReplyItem(
                                comment = reply,
                                onReplyClick = { replyingTo = it },
                                onLikeClick = { /* обработка лайка */ },
                                onProfileClick = { /* переход на профиль */ }
                            )
                        }
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter)
                .systemBarsPadding(),
        ) {
            CommentInputField(
                value = replyText,
                onValueChange = { replyText = it },
                onSend = {
                    if (replyText.isNotEmpty()) {
                        if (replyingTo != null) {
                            // viewModel.addReply(replyingTo!!, replyText)
                            replyingTo = null
                        } else {
                            // viewModel.addComment(postId, replyText)
                        }
                        replyText = ""
                    }
                }
            )
        }
    }
}


@Composable
fun CommentsItem(
    comment: Comment,
    onReplyClick: (Comment) -> Unit,
    onLikeClick: (Comment) -> Unit,
    onProfileClick: (String) -> Unit, // authorId
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 29.dp)
            .padding(vertical = 5.dp)
            .background(Color(0xFF292929), shape = RoundedCornerShape(20.dp))
            .padding(vertical = 16.dp, horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Image(
                    painter = painterResource(id = comment.authorProfileImage),
                    contentDescription = "Элемент внутри",
                    modifier = Modifier
                        .size(50.dp),
                )
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = comment.authorName,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                    )
                    Text(
                        text = comment.text,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = Color.White,
                    )
                }
                Box(
                    modifier = Modifier
                        .size(18.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.flag),
                        contentDescription = "Пожаловаться",
                        colorFilter = ColorFilter.tint(Color.White),
                        modifier = Modifier
                            .clickable { /*TODO отправить жалобу*/ },
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Ответить",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 12.sp,
                        lineHeight = 15.6.sp
                    ),
                    modifier = Modifier
                        .clickable { onReplyClick(comment) }
                        .padding(vertical = 4.dp)
                )
                Spacer(Modifier.width(12.dp))
                // Форматированное время
                Text(
                    text = comment.date,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 12.sp,
                        lineHeight = 15.6.sp
                    ),
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
    // Стилизация ответа - с отступом и визуальным индикатором
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 45.dp, end = 29.dp, top = 5.dp, bottom = 5.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 25.dp) // Дополнительный отступ справа
                .background(Color(0xFF292929), shape = RoundedCornerShape(20.dp))
                .padding(vertical = 16.dp, horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Image(
                        painter = painterResource(id = comment.authorProfileImage),
                        contentDescription = "Аватар пользователя",
                        modifier = Modifier
                            .size(50.dp)
                            .clickable { onProfileClick(comment.authorId) },
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = comment.authorName,
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 13.sp, lineHeight = 16.9.sp),
                            color = Color.White,
                        )
                        Text(
                            text = comment.text,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                lineHeight = 18.2.sp
                            ),
                            color = Color.White,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.flag),
                            contentDescription = "Пожаловаться",
                            colorFilter = ColorFilter.tint(Color.White),
                            modifier = Modifier
                                .clickable { /*TODO отправить жалобу*/ },
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "Ответить",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 11.sp,
                            lineHeight = 14.3.sp
                        ),
                        modifier = Modifier
                            .clickable { onReplyClick(comment) }
                            .padding(vertical = 4.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = comment.date,
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 11.sp,
                            lineHeight = 14.3.sp
                        ),
                    )
                }

            }
        }
    }
}

@Composable
fun CommentInputField(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF292929), RoundedCornerShape(52.dp))
            .padding(horizontal = 21.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.material3.TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("Оставить комментарий...", color = Color.Gray) },
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
                .clickable(enabled = value.isNotEmpty()) { onSend() },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.chevron_left),
                contentDescription = "Отправить",
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier.size(20.dp).graphicsLayer(scaleX = -1f)
            )
        }
    }
}

data class Comment(
    val id: String,
    val authorId: String,
    val authorName: String,
    val authorProfileImage: Int,
    val text: String,
    val date: String,
    val postId: String,
    val parentId: String? = null, // ID родительского комментария (null для корневых комментариев)
    val replies: List<Comment> = emptyList() // Вложенные ответы
)