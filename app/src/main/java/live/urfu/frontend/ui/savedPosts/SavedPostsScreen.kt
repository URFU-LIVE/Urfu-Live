package urfu.live.frontend.ui.saved

import AdaptiveSizes.authorAvatarSize
import SavedPostsAdaptiveSizes
import ScreenSizeInfo
import TagChip
import TagSizes
import adaptiveSafeAreaPadding
import adaptiveTextStyle
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import live.urfu.frontend.R
import urfu.live.frontend.components.BottomNavBar
import live.urfu.frontend.ui.main.PostColorPatterns
import live.urfu.frontend.data.model.Post
import live.urfu.frontend.ui.createarticle.CreateArticle
import live.urfu.frontend.ui.createarticle.CreateArticleViewModel
import live.urfu.frontend.ui.savedPosts.SavedPostsViewModel
import rememberScreenSizeInfo

@SuppressLint("ViewModelConstructorInComposable")
@Composable
@Preview
fun SavedPostsScreen(
    viewModel: SavedPostsViewModel = viewModel(),
    onProfileClick: () -> Unit = {},
    onCreateArticleClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onSavedClick: () -> Unit = {},
    onMessagesClick: () -> Unit = {},
    onPostClick: (Post) -> Unit = {},
    onSearchClick: () -> Unit = {},
    onRemoveFromSaved: (Post) -> Unit = {},
    currentScreen: String = "saved"
) {
    val screenInfo = rememberScreenSizeInfo()
    val savedPosts = viewModel.savedPosts

    var showCreateArticle by remember { mutableStateOf(false) }
    if (showCreateArticle) {
        CreateArticle(
            onClose = { showCreateArticle = false },
            onPostSuccess = {},
            onPostError = {},
            viewModel = CreateArticleViewModel()
        )
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                onProfileClick = onProfileClick,
                onCreateArticleClick = { showCreateArticle = true },
                onHomeClick = onHomeClick,
                onSavedClick = onSavedClick,
                onMessagesClick = onMessagesClick,
                currentScreen = currentScreen
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF131313))
        ) {
            // Топбар
            SavedPostsTopBar(
                screenInfo = screenInfo,
                onSearchClick = onSearchClick
            )

            // Список сохраненных постов
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = SavedPostsAdaptiveSizes.postCardPadding(screenInfo).calculateStartPadding(androidx.compose.ui.unit.LayoutDirection.Ltr),
                    end = SavedPostsAdaptiveSizes.postCardPadding(screenInfo).calculateEndPadding(androidx.compose.ui.unit.LayoutDirection.Ltr),
                    top = SavedPostsAdaptiveSizes.contentSpacing(screenInfo),
                    bottom = paddingValues.calculateBottomPadding() + SavedPostsAdaptiveSizes.contentSpacing(screenInfo)
                ),
                verticalArrangement = Arrangement.spacedBy(SavedPostsAdaptiveSizes.postSpacing(screenInfo))
            ) {
                items(savedPosts) { post ->
                    SavedPostCard(
                        post = post,
                        screenInfo = screenInfo,
                        onPostClick = { onPostClick(post) },
                        onRemoveFromSaved = { viewModel.removeFromSaved(post) }
                    )
                }

                // Если нет сохраненных постов
                if (savedPosts.isEmpty()) {
                    item {
                        EmptyStateMessage(screenInfo = screenInfo)
                    }
                }
            }
        }
    }
}

@Composable
fun SavedPostsTopBar(
    screenInfo: ScreenSizeInfo,
    onSearchClick: () -> Unit,
) {
    val safeAreaPadding = adaptiveSafeAreaPadding(screenInfo)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = safeAreaPadding.calculateTopPadding(),
                end = if (screenInfo.isCompact) 24.dp else 42.dp
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.navbarnew),
                contentDescription = "Heart Logo",
                modifier = Modifier
                    .padding(start = if (screenInfo.isCompact) 20.dp else 32.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(id = R.drawable.search),
                contentDescription = "Поиск",
                modifier = Modifier
                    .clickable { onSearchClick() }
            )
        }
    }
}

@Composable
fun SavedPostCard(
    post: Post,
    screenInfo: ScreenSizeInfo,
    onPostClick: () -> Unit,
    onRemoveFromSaved: () -> Unit,
) {
    val colorPatternIndex = post.id.rem(PostColorPatterns.size).toInt()
    val colorPattern = PostColorPatterns[colorPatternIndex]

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPostClick() },
        shape = RoundedCornerShape(31.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorPattern.background
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(SavedPostsAdaptiveSizes.postContentPadding(screenInfo))
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Заголовок поста
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = SavedPostsAdaptiveSizes.titleFontSize(screenInfo)
                    ),
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(0.85f) // Оставляем место для иконки закладки
                )

                Spacer(modifier = Modifier.height(SavedPostsAdaptiveSizes.contentSpacing(screenInfo)))

                // Автор
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AsyncImage(
                        model = post.author.avatarUrl,
                        contentDescription = "Author avatar",
                        modifier = Modifier
                            .size(authorAvatarSize(screenInfo))
                            .clip(CircleShape)
                            .border(2.dp, Color.White, CircleShape),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.ava),
                        error = painterResource(R.drawable.ava)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Автор:",
                            style = adaptiveTextStyle(MaterialTheme.typography.titleLarge, screenInfo),
                            color = Color.Black
                        )
                        Text(
                            text = post.author.username,
                            style = adaptiveTextStyle(MaterialTheme.typography.titleLarge, screenInfo),
                            color = Color.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.clickable { /* TODO onAuthorClick(post.author.id)*/ }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(SavedPostsAdaptiveSizes.contentSpacing(screenInfo)))

                // Теги
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        SavedPostsAdaptiveSizes.tagSpacing(
                            screenInfo
                        )
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    post.tags.take(2).forEach { tag ->
                        TagChip(
                            tag = tag.name,
                            color = colorPattern.buttonColor,
                            size = TagSizes.Small
                        )
                    }
                }
            }

            // Иконка закладки
            IconButton(
                onClick =  onRemoveFromSaved,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(SavedPostsAdaptiveSizes.bookmarkIconSize(screenInfo) + 8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bookmarkbottom1), // Заполненная закладка
                    contentDescription = "Remove from saved",
                    modifier = Modifier.size(SavedPostsAdaptiveSizes.bookmarkIconSize(screenInfo)),
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                        colorPattern.reactionColor
                    )
                )
            }
        }
    }
}

@Composable
fun EmptyStateMessage(screenInfo: ScreenSizeInfo) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.bookmarkbottom1),
                contentDescription = "No saved posts",
                modifier = Modifier.size(48.dp),
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.Gray)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Нет сохраненных постов",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = SavedPostsAdaptiveSizes.titleFontSize(screenInfo)
                ),
                color = Color.Gray
            )

            Text(
                text = "Сохраните интересные посты, чтобы вернуться к ним позже",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = SavedPostsAdaptiveSizes.authorFontSize(screenInfo)
                ),
                color = Color.Gray.copy(alpha = 0.7f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}
