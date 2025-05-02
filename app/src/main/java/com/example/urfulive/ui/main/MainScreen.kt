import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.urfulive.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Text
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.urfulive.ui.theme.UrfuLiveTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.lerp
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.zIndex
import com.example.urfulive.components.BottomNavBar
import com.example.urfulive.data.DTOs.DefaultResponse
import com.example.urfulive.ui.createarticle.CreateArticle
import com.example.urfulive.ui.createarticle.CreateArticleViewModel
import com.example.urfulive.ui.notifiaction.FullScreenNotifications
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay

@Preview(showBackground = true)
@Composable
fun ArticlesScreenPreview() {
    UrfuLiveTheme {
        CarouselScreen(
            onProfileClick = {},
        )
    }
}

@Composable
fun TagChip(tag: String, color: Color) {
    Box(
        modifier = Modifier
            .graphicsLayer {
                this.alpha = 1f
            }
            .background(
                color = color,
                shape = RoundedCornerShape(52.dp)
            )
            .padding(horizontal = 15.dp, vertical = 10.dp)
    ) {
        Text(
            text = tag,
            color = Color.Black,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
fun ArticleCard(
    article: Article,
    onClick: () -> Unit,
    expansionProgress: Float = .0f,
) {
    val pattern = ArticleColorPatterns[article.colorPatternIndex]
    val fadeOutSpeed = 1.0f
    val fadeOutEasing = FastOutSlowInEasing
    remember(expansionProgress) {
        (1f - (fadeOutEasing.transform(expansionProgress) * fadeOutSpeed)).coerceIn(0f, 1f)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(580.dp)
            .clickable { onClick() }
            .background(pattern.background, shape = RoundedCornerShape(52.dp))
            .padding(start = 25.dp, top = 40.dp, end = 25.dp, bottom = 37.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    article.tags.take(2).forEach { tag ->
                        TagChip(tag = tag, color = pattern.buttonColor)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = article.title,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(20.dp))
                Column {
                    Text(
                        text = "Опубликовано: ${article.date}",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.profile),
                                contentDescription = "Author Icon",
                                modifier = Modifier.size(50.dp),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(modifier = Modifier.width(8.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Автор:",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.Black
                                )
                                Text(
                                    text = article.author,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.Black,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Text(
                                text = "Подписаться",
                                modifier = Modifier
                                    .clickable { }
                                    .background(
                                        pattern.buttonColor,
                                        shape = RoundedCornerShape(52.dp)
                                    )
                                    .padding(
                                        horizontal = 15.dp,
                                        vertical = 10.dp
                                    ),
                                color = Color.Black,
                                style = MaterialTheme.typography.displaySmall,
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = article.content,
                    style = MaterialTheme.typography.displayMedium,
                    color = Color.Black,
                    maxLines = 10,
                    overflow = TextOverflow.Ellipsis
                )
            }

            val animatedAlpha = (1f - (expansionProgress * fadeOutSpeed)).coerceIn(0f, 1f)

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .graphicsLayer {
                        alpha = animatedAlpha
                    }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.likebottom),
                    contentDescription = "Like Logo",
                    colorFilter = ColorFilter.tint(pattern.reactionColor),
                    modifier = Modifier
                        .clickable { /* TODO Поставить лайк*/ }
                        .size(33.dp),
                )

                Text(
                    text = article.likes.toString(),
                    color = Color.Black,
                    style = MaterialTheme.typography.displayLarge,
                )
                Image(
                    painter = painterResource(id = R.drawable.commentbottom),
                    colorFilter = ColorFilter.tint(pattern.reactionColor),
                    contentDescription = "Comment Logo",
                    modifier = Modifier
                        .clickable { /* TODO Оставить комментарий*/ }
                        .size(35.dp),
                )
                Text(
                    text = article.comments.toString(),
                    color = Color.Black,
                    style = MaterialTheme.typography.displayLarge,
                )
                Image(
                    painter = painterResource(id = R.drawable.bookmarkbottom1),
                    contentDescription = "Bookmark Logo",
                    colorFilter = ColorFilter.tint(pattern.reactionColor),
                    modifier = Modifier
                        .clickable { /* TODO Сохранить себе*/ }
                        .size(30.dp),
                )

                Text(
                    text = article.sakladka.toString(),
                    color = Color.Black,
                    style = MaterialTheme.typography.displayLarge,
                )
            }
        }
    }
}

@Composable
fun TopBar(
    onNotificationsClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 70.dp, end = 42.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.navbarnew),
                contentDescription = "Heart Logo",
                modifier = Modifier
                    .clickable { /* TODO Переход куда-нибудь*/ }
                    .padding(start = 32.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(id = R.drawable.bell),
                contentDescription = "Bell Logo",
                modifier = Modifier
                    .clickable { onNotificationsClick() }
                    .size(40.dp)
            )
        }
    }
}



@Composable
fun HorizontalTagRow(tags: List<String>, color: Color, expandProgress: Float = 1f) {
    val initialVisibleTags = 2
    val additionalTags = tags.size - initialVisibleTags
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        tags.take(initialVisibleTags).forEach { tag ->
            TagChip(tag = tag, color = color)
        }

        if (additionalTags > 0) {
            tags.drop(initialVisibleTags).forEachIndexed { index, tag ->
                val tagProgress = (expandProgress - 0.3f - (index * 0.1f)).coerceIn(0f, 1f)

                AnimatedVisibility(
                    visible = tagProgress > 0,
                    enter = fadeIn() + expandHorizontally(),
                    exit = fadeOut() + shrinkHorizontally()
                ) {
                    TagChip(
                        tag = tag,
                        color = color
                    )
                }
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun CarouselScreen(
    viewModel: ArticlesViewModel = viewModel(),
    onProfileClick: () -> Unit,
) {
    val articles = viewModel.articles
    val pagerState = rememberPagerState(pageCount = { articles.size })
    var expandedIndex by remember { mutableStateOf(-1) }

    val scope = rememberCoroutineScope()
    var selectedCardCenter by remember { mutableStateOf(Offset.Zero) }
    var selectedCardSize by remember { mutableStateOf(IntSize(0, 0)) }
    var isAnimationInProgress by remember { mutableStateOf(false) }
    var lastActionTime by remember { mutableStateOf(0L) }
    val minActionInterval = 0L
    var lastCloseTime by remember { mutableStateOf(0L) }

    val density = LocalDensity.current
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp + 100.dp

    val initialCardWidth = 350.dp
    val initialCardHeight = 580.dp
    val partialExpandHeight = screenHeight * 0.82f
    val fullExpandHeight = screenHeight * 1.5f
    var isFullyExpanded by remember { mutableStateOf(false) }
    var shouldHideBottomNav by remember { mutableStateOf(false) }
    var isClosing by remember { mutableStateOf(false) }
    var previousCardCenter by remember { mutableStateOf(Offset.Zero) }
    var dragOffset by remember { mutableStateOf(0f) }

    val expanded = expandedIndex != -1
    val transition = updateTransition(targetState = expanded, label = "expandTransition")
    val fullExpansionTransition =
        updateTransition(targetState = isFullyExpanded, label = "fullExpansionTransition")
    val closeAnimator = remember { Animatable(0f) }
    val SmoothEasing = CubicBezierEasing(0.05f, 0.0f, 0.15f, 1.0f)

    LaunchedEffect(key1 = isAnimationInProgress) {
        if (isAnimationInProgress) {
            delay(1500)
            isAnimationInProgress = false
        }
    }

    LaunchedEffect(isClosing) {
        if (isClosing) {
            isAnimationInProgress = true
            val fixedCloseDuration = 750

            closeAnimator.snapTo(0f)
            try {
                closeAnimator.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = fixedCloseDuration,
                        easing = SmoothEasing
                    )
                )

                expandedIndex = -1
                lastCloseTime = System.currentTimeMillis()
                lastActionTime = System.currentTimeMillis()
                isFullyExpanded = false
                shouldHideBottomNav = false
                dragOffset = 0f
                isClosing = false
                closeAnimator.snapTo(0f)
            } catch (e: Exception) {
                expandedIndex = -1
                isClosing = false
                isFullyExpanded = false
                shouldHideBottomNav = false
            } finally {
                delay(100)
                isAnimationInProgress = false
            }
        }
    }

    val fixedOpenDuration = 600
    val fixedSwipeDuration = 300

    val animSpec = tween<Float>(
        durationMillis = fixedOpenDuration,
        easing = SmoothEasing,
        delayMillis = 30
    )

    val swipeAnimSpec = tween<Float>(
        durationMillis = fixedSwipeDuration,
        easing = SmoothEasing
    )

    val expansionProgress by transition.animateFloat(
        transitionSpec = { animSpec },
        label = "expansionProgress"
    ) { isExpanded ->
        if (isExpanded) 1f else 0f
    }

    val fullExpansionProgress by fullExpansionTransition.animateFloat(
        transitionSpec = { swipeAnimSpec },
        label = "fullExpansionProgress"
    ) { isFullyExpanded ->
        if (isFullyExpanded) 1f else 0f
    }

    val closingProgressTransformed = if (isClosing) {
        SmoothEasing.transform(closeAnimator.value)
    } else {
        0f
    }

    val currentWidth = with(density) {
        val initialWidth = initialCardWidth.toPx()
        val targetWidth = screenWidth.toPx()

        if (isClosing) {
            lerp(targetWidth, initialWidth, closingProgressTransformed)
        } else {
            lerp(
                initialWidth,
                targetWidth,
                FastOutSlowInEasing.transform(expansionProgress)
            )
        }
    }

    val currentHeight = with(density) {
        when {
            isClosing -> {
                val startHeight = if (isFullyExpanded) {
                    fullExpandHeight.toPx()
                } else {
                    partialExpandHeight.toPx()
                }
                lerp(startHeight, initialCardHeight.toPx(), closingProgressTransformed)
            }
            isFullyExpanded -> fullExpandHeight.toPx()
            expanded -> lerp(
                initialCardHeight.toPx(),
                partialExpandHeight.toPx() + (fullExpandHeight.toPx() - partialExpandHeight.toPx()) * fullExpansionProgress,
                expansionProgress
            )
            else -> initialCardHeight.toPx()
        }
    }

    val cardCenterX = selectedCardCenter.x
    val initialLeftX = cardCenterX - with(density) { initialCardWidth.toPx() / 2 }
    val finalLeftX = with(density) { (screenWidth / 2 - screenWidth / 2).toPx() }

    val currentLeftX = if (isClosing) {
        lerp(finalLeftX, initialLeftX, closingProgressTransformed)
    } else {
        with(density) { (screenWidth / 2).toPx() } - (currentWidth / 2)
    }

    val initialTopY = selectedCardCenter.y - with(density) { initialCardHeight.toPx() / 2 }
    val partialExpandTopY = with(density) { (screenHeight - partialExpandHeight).toPx() }
    val fullExpandTopY = -1f

    val targetTopY = if (expanded || isClosing) {
        if (isClosing) {
            partialExpandTopY
        } else {
            lerp(partialExpandTopY, fullExpandTopY, fullExpansionProgress)
        }
    } else {
        initialTopY
    }

    val currentTopY = if (isClosing) {
        lerp(targetTopY, initialTopY, closingProgressTransformed)
    } else if (isFullyExpanded && dragOffset > 0) {
        lerp(initialTopY, targetTopY, expansionProgress)
    } else {
        lerp(initialTopY, targetTopY, expansionProgress) + dragOffset
    }

    val closeExpandedArticle = {
        val currentTime = System.currentTimeMillis()
        if (!isClosing && !isAnimationInProgress && (currentTime - lastActionTime > minActionInterval)) {
            isClosing = true
            isAnimationInProgress = true
            lastActionTime = currentTime
            shouldHideBottomNav = false
            previousCardCenter = selectedCardCenter
        }
    }

    val toggleFullExpansion = {
        val currentTime = System.currentTimeMillis()
        if (!isClosing && !isAnimationInProgress && (currentTime - lastActionTime > minActionInterval)) {
            isFullyExpanded = !isFullyExpanded
            lastActionTime = currentTime
            shouldHideBottomNav = isFullyExpanded
            dragOffset = 0f
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D0D))
    ) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = (screenWidth - initialCardWidth) / 2),
            pageSpacing = 0.dp,
            modifier = Modifier
                .fillMaxSize()
                .height(580.dp),
            userScrollEnabled = expandedIndex == -1 && !isClosing
        ) { page ->
            Box(
                modifier = Modifier
                    .width(initialCardWidth)
                    .onGloballyPositioned { coordinates ->
                        if ((page == pagerState.currentPage && expandedIndex == -1) ||
                            (expandedIndex == page)
                        ) {
                            val position = coordinates.positionInRoot()
                            val size = coordinates.size
                            selectedCardCenter = Offset(
                                position.x + size.width / 2f,
                                position.y + size.height / 2f
                            )
                            selectedCardSize = size
                        }
                    }
                    .graphicsLayer {
                        val pageOffset = ((pagerState.currentPage - page) +
                                pagerState.currentPageOffsetFraction).absoluteValue
                        scaleX = lerp(0.85f, 1f, 1f - pageOffset.coerceIn(0f, 1f))
                        scaleY = lerp(0.85f, 1f, 1f - pageOffset.coerceIn(0f, 1f))

                        if (expandedIndex != -1) {
                            alpha =
                                if (expandedIndex == page) 1f else 1f
                        } else if (isClosing && expandedIndex == page) {
                            alpha = closeAnimator.value
                        } else {
                            alpha = 1f
                        }
                    }
            ) {
                ArticleCard(
                    article = articles[page],
                    onClick = {
                        val currentTime = System.currentTimeMillis()
                        if (expandedIndex == -1 && !isClosing && !isAnimationInProgress &&
                            (currentTime - lastActionTime > minActionInterval)
                        ) {
                            dragOffset = 0f
                            lastActionTime = currentTime

                            scope.launch {
                                isAnimationInProgress = true
                                delay(16)
                                expandedIndex = page
                                isFullyExpanded = false

                                try {
                                    delay(fixedOpenDuration.toLong())
                                } finally {
                                    isAnimationInProgress = false
                                }
                            }
                        }
                    },
                    expansionProgress = if (isClosing) {
                        1f - closeAnimator.value
                    } else {
                        expansionProgress
                    },
                )
            }
        }
        var showCreateArticle by remember { mutableStateOf(false) }
        AnimatedVisibility(
            visible = !shouldHideBottomNav,
            enter = fadeIn(
                animationSpec = tween(durationMillis = 300, easing = SmoothEasing)
            ),
            exit = fadeOut(
                animationSpec = tween(durationMillis = 300, easing = SmoothEasing)
            ),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                BottomNavBar(
                    onProfileClick = onProfileClick,
                    onCreateArticleClick = { showCreateArticle = true },
                    onHomeClick = { /* TODO: handle home click */ },
                    onSavedClick = { /* TODO: handle saved click */ },
                    onMessagesClick = { /* TODO: handle messages click */ },
                    currentScreen = "home"
                )
            }
        }
        if (showCreateArticle) {
            CreateArticle(
                onClose = { showCreateArticle = false },
                onPostSuccess = {},
                onPostError = {},
                viewModel = CreateArticleViewModel()
            )
        }
        var showNotificationsOverlay by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .zIndex(if (showNotificationsOverlay) 0f else 1f)
            ) {
                TopBar(
                    onNotificationsClick = { showNotificationsOverlay = true }
                )
            }

            if (showNotificationsOverlay) {
                FullScreenNotifications(onClose = { showNotificationsOverlay = false })
            }
        }

        if (expandedIndex != -1 || isClosing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = expandedIndex != -1 && !isClosing && !isAnimationInProgress
                    ) {
                        if (!isClosing && !isAnimationInProgress) {
                            closeExpandedArticle()
                        }
                    }
            )

            Box(
                modifier = Modifier
                    .graphicsLayer {
                        this.translationX = currentLeftX
                        this.translationY = currentTopY
                        this.clip = !isFullyExpanded
                    }
                    .width(with(density) { currentWidth.toDp() })
                    .height(with(density) { currentHeight.toDp() })
                    .background(
                        color = if (expandedIndex >= 0 && expandedIndex < articles.size)
                            ArticleColorPatterns[articles[expandedIndex].colorPatternIndex].background
                        else
                            Color.Transparent,
                        shape = RoundedCornerShape(
                            topStart = 52.dp,
                            topEnd = 52.dp,
                            bottomStart = 52.dp,
                            bottomEnd = 52.dp
                        )
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { }
                    .zIndex(10f)
            ) {
                if ((expandedIndex >= 0 && expandedIndex < articles.size) || isClosing) {
                    val articleIndex =
                        if (expandedIndex >= 0 && expandedIndex < articles.size) expandedIndex else 0

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        ArticleContent(
                            article = articles[articleIndex],
                            expandProgress = if (isClosing) {
                                1f - closeAnimator.value
                            } else {
                                expansionProgress
                            },
                            onHeaderSwipe = { toggleFullExpansion() }
                        )
                    }

                    AnimatedVisibility(
                        visible = (if (isClosing) 1f - closeAnimator.value else expansionProgress) > 0.7f,
                        enter = fadeIn(
                            animationSpec = tween(durationMillis = 200, easing = SmoothEasing)
                        ),
                        exit = fadeOut(
                            animationSpec = tween(durationMillis = 200, easing = SmoothEasing)
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp)
                                .size(32.dp)
                                .background(Color.Gray.copy(alpha = 0.5f), CircleShape)
                                .clickable {
                                    if (!isClosing && !isAnimationInProgress) {
                                        closeExpandedArticle()
                                    }
                                }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(16.dp),
                                tint = Color.White
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = (if (isClosing) 1f - closeAnimator.value else expansionProgress) > 0.9f &&
                                (if (isClosing) 0f else fullExpansionProgress) < 0.1f,
                        enter = fadeIn(
                            animationSpec = tween(durationMillis = 200, easing = SmoothEasing)
                        ),
                        exit = fadeOut(
                            animationSpec = tween(durationMillis = 200, easing = SmoothEasing)
                        ),
                        modifier = Modifier.align(Alignment.TopCenter)
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .width(40.dp)
                                .height(4.dp)
                                .background(Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(2.dp)))
                    }
                }
            }
        }
    }
}

@Composable
fun ArticleContent(
    article: Article,
    expandProgress: Float,
    onHeaderSwipe: () -> Unit,
) {
    val titleSizeAndHeight = lerp(24.sp, 26.sp, expandProgress)
    val articleHeight = lerp(17.6.sp, 19.2.sp, expandProgress)
    val paddingAfterDate = lerp(6.dp, 15.dp, expandProgress)

    val scrollState = rememberScrollState()
    val pattern = ArticleColorPatterns[article.colorPatternIndex]

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .then(
                if (expandProgress > 0.9f) {
                    Modifier.verticalScroll(scrollState)
                } else {
                    Modifier
                }
            )
    ) {
        Column(
            modifier = Modifier
                .padding(start = 25.dp, end = 25.dp, top = 40.dp)
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragStart = { },
                        onDragEnd = { },
                        onDragCancel = { },
                        onVerticalDrag = { change, dragAmount ->
                            change.consume()
                            if (dragAmount < 0) {
                                onHeaderSwipe()
                            } else if (dragAmount > 0) {
                                onHeaderSwipe()
                            }
                        }
                    )
                }
        ) {
            HorizontalTagRow(
                tags = article.tags,
                color = ArticleColorPatterns[article.colorPatternIndex].buttonColor,
                expandProgress = expandProgress
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = article.title,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = titleSizeAndHeight,
                    lineHeight = titleSizeAndHeight
                ),
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Опубликовано: ${article.date}",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(paddingAfterDate))

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = "Author Icon",
                        modifier = Modifier.size(50.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Автор:",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.Black
                        )
                        Text(
                            text = article.author,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Text(
                        text = "Подписаться",
                        modifier = Modifier
                            .clickable { /*TODO: подписка*/ }
                            .background(pattern.buttonColor, shape = RoundedCornerShape(52.dp))
                            .padding(
                                horizontal = 15.dp,
                                vertical = 10.dp
                            ),
                        color = Color.Black,
                        style = MaterialTheme.typography.displaySmall,
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 25.dp)
                .padding(top = 20.dp)
        ) {
            Text(
                text = article.content,
                style = MaterialTheme.typography.displayMedium.copy(lineHeight = articleHeight),
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(20.dp))

            ReactionPanelBottomContent(
                article = article,
                pattern = pattern,
                expandProgress = expandProgress
            )

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun ReactionPanelBottomContent(
    article: Article,
    pattern: ArticleColorPattern,
    expandProgress: Float
) {
    val reactionPanelOpacity = if (expandProgress < 0.3f) {
        0f
    } else {
        ((expandProgress - 0.3f) * (1f / 0.7f)).coerceIn(0f, 1f)
    }

    val reactionPanelOffset = animateDpAsState(
        targetValue = if (expandProgress > 0.5f) 0.dp else 20.dp,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "reactionPanelOffset"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                alpha = reactionPanelOpacity
                translationY = reactionPanelOffset.value.toPx()
            }
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.likebottom),
                contentDescription = "Like Logo",
                colorFilter = ColorFilter.tint(pattern.reactionColor),
                modifier = Modifier
                    .clickable { /* TODO Поставить лайк*/ }
                    .size(33.dp),
            )

            Text(
                text = article.likes.toString(),
                color = Color.Black,
                style = MaterialTheme.typography.displayLarge,
            )
            Image(
                painter = painterResource(id = R.drawable.commentbottom),
                colorFilter = ColorFilter.tint(pattern.reactionColor),
                contentDescription = "Comment Logo",
                modifier = Modifier
                    .clickable { /* TODO Оставить комментарий*/ }
                    .size(35.dp),
            )
            Text(
                text = article.comments.toString(),
                color = Color.Black,
                style = MaterialTheme.typography.displayLarge,
            )
            Image(
                painter = painterResource(id = R.drawable.bookmarkbottom1),
                contentDescription = "Bookmark Logo",
                colorFilter = ColorFilter.tint(pattern.reactionColor),
                modifier = Modifier
                    .clickable { /* TODO Сохранить себе*/ }
                    .size(30.dp),
            )

            Text(
                text = article.sakladka.toString(),
                color = Color.Black,
                style = MaterialTheme.typography.displayLarge,
            )

            Spacer(modifier = Modifier.width(140.dp))

            Image(
                painter = painterResource(id = R.drawable.flag),
                contentDescription = "Bookmark Logo",
                colorFilter = ColorFilter.tint(pattern.reactionColor),
                modifier = Modifier
                    .clickable { /* TODO Сохранить себе*/ }
                    .size(27.dp),
            )
        }
    }
}