import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.BeyondBoundsLayout
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.urfulive.R
import com.example.urfulive.components.BottomNavBar
import com.example.urfulive.data.model.Post
import com.example.urfulive.data.model.Tag
import com.example.urfulive.data.model.User
import com.example.urfulive.data.model.UserRole
import com.example.urfulive.ui.createarticle.CreateArticle
import com.example.urfulive.ui.createarticle.CreateArticleViewModel
import com.example.urfulive.ui.main.PostColorPattern
import com.example.urfulive.ui.main.PostColorPatterns
import com.example.urfulive.ui.main.PostViewModel
import com.example.urfulive.ui.notifiaction.NotificationsScreen
import com.example.urfulive.ui.search.SearchScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

enum class TagSizes {
    Standard, Small
}

@Preview(showBackground = true)
@Composable
fun ArticlesScreenPreview() {
    val previewNavController = rememberNavController()
    PostCard(
        post = Post(
            id = 1,
            title = "Title",
            text = "Text",
            author = User(
                id = "1",
                username = "Test user",
                name = null,
                surname = null,
                birthDate = null,
                role = UserRole.WRITER,
                email = "testmail@gmail.com",
                followersCount = 1,
                followingCount = 1,
                followers = listOf(1),
                avatarUrl = null,
                backgroundUrl = null
            ),
            likedBy = listOf(1),
            tags = listOf(Tag(id=1, name="Тест")),
            time = "Точно прямо сейчас",
            comments = 1,
            likes = 1,
        ),
        onClick = {},
        onAuthorClick = {},
        onCommentsClick = {},
    )
    CarouselScreen(
        onProfileClick = {},
        onCommentsClick = {}
    )
}

@Composable
fun TagChip(
    tag: String,
    color: Color,
    size: TagSizes = TagSizes.Standard
) {
    val screenInfo = rememberScreenSizeInfo()
    val padding = AdaptiveSizes.tagPadding(screenInfo, size)

    Box(
        modifier = Modifier
            .graphicsLayer { this.alpha = 1f }
            .background(
                color = color,
                shape = RoundedCornerShape(52.dp)
            )
            .padding(padding)
    ) {
        val textStyle = if (size == TagSizes.Standard) {
            adaptiveTextStyle(MaterialTheme.typography.labelMedium, screenInfo)
        } else {
            adaptiveTextStyle(
                MaterialTheme.typography.labelMedium.copy(
                    fontSize = 12.sp,
                    lineHeight = 12.sp
                ),
                screenInfo
            )
        }

        Text(
            text = tag,
            color = Color.Black,
            style = textStyle,
        )
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun PostCard(
    post: Post,
    onClick: () -> Unit,
    expansionProgress: Float = .0f,
    onAuthorClick: (String) -> Unit,
    viewModel: PostViewModel = viewModel(),
    onCommentsClick: (Long) -> Unit
) {
    val screenInfo = rememberScreenSizeInfo()
    val colorPatternIndex = remember(post.id) { post.id.rem(PostColorPatterns.size) }
    val pattern = remember(colorPatternIndex) { PostColorPatterns[colorPatternIndex.toInt()] }
    val rememberedPost = remember(post) { post }

    val likedPosts by viewModel.likedPostIds.collectAsState()
    val isLiked = likedPosts.contains(rememberedPost.id)

    val likeScale by remember { mutableStateOf(1f) }
    val animatedLikeScale by animateFloatAsState(
        targetValue = likeScale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    val subscriptions by viewModel.subscriptions.collectAsState()
    val isSubscribed = subscriptions.contains(rememberedPost.author.id)
    var isLoading by remember(post.author.id) { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val cardPadding = AdaptiveSizes.cardPadding(screenInfo)
    val avatarSize = AdaptiveSizes.authorAvatarSize(screenInfo)
    val buttonPadding = AdaptiveSizes.buttonPadding(screenInfo)

    val userId = viewModel.currentUserId;

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(
                min = if (screenInfo.isCompact) 250.dp else 300.dp,
                max = AdaptiveSizes.cardHeight(screenInfo)
            )
            .clickable { onClick() }
            .background(pattern.background, shape = RoundedCornerShape(52.dp))
            .padding(cardPadding)
    ) {
        val spacing = maxHeight * 0.025f

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(horizontalArrangement = Arrangement.spacedBy(if (screenInfo.isCompact) 6.dp else 10.dp)) {
                    rememberedPost.tags.take(2).forEach { tag ->
                        TagChip(tag = tag.name, color = pattern.buttonColor)
                    }
                }

                Spacer(modifier = Modifier.height(AdaptiveSizes.spacerHeight(screenInfo, SpacerType.Large)))

                Text(
                    text = rememberedPost.title,
                    style = adaptiveTextStyle(
                        MaterialTheme.typography.labelLarge.copy(
                            color = pattern.textColor,
                            fontWeight = FontWeight.Bold
                        ),
                        screenInfo
                    )
                )

                Spacer(modifier = Modifier.height(AdaptiveSizes.spacerHeight(screenInfo, SpacerType.Medium)))

                Column {
                    Text(
                        text = "Опубликовано: ${rememberedPost.time.substring(0, 10)}",
                        style = adaptiveTextStyle(MaterialTheme.typography.titleLarge, screenInfo),
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(AdaptiveSizes.spacerHeight(screenInfo, SpacerType.Small)))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AsyncImage(
                            model = post.author.avatarUrl,
                            contentDescription = "Author Icon",
                            modifier = Modifier
                                .size(avatarSize)
                                .clip(CircleShape)
                                .border(2.dp, Color.White, CircleShape)
                                .clickable { onAuthorClick(rememberedPost.author.id) },
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(R.drawable.ava),
                            error = painterResource(R.drawable.ava)
                        )

                        Spacer(modifier = Modifier.width(if (screenInfo.isCompact) 6.dp else 10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Автор:",
                                style = adaptiveTextStyle(MaterialTheme.typography.titleLarge, screenInfo),
                                color = Color.Black
                            )
                            Text(
                                text = rememberedPost.author.username,
                                style = adaptiveTextStyle(MaterialTheme.typography.titleLarge, screenInfo),
                                color = pattern.textColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.clickable {
                                    onAuthorClick(rememberedPost.author.id)
                                }
                            )
                        }

                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(if (screenInfo.isCompact) 16.dp else 20.dp),
                                color = pattern.textColor,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = when {
                                    post.author.id.equals(userId) -> "Это вы!"
                                    isSubscribed -> "Вы подписаны"
                                    else -> "Подписаться"
                                },

                                modifier = Modifier
                                    .clickable {
                                        coroutineScope.launch {
                                            isLoading = true
                                            viewModel.subscribeAndUnsubscribe(rememberedPost)
                                            isLoading = false
                                        }
                                    }
                                    .background(
                                        pattern.buttonColor,
                                        shape = RoundedCornerShape(52.dp)
                                    )
                                    .padding(buttonPadding),
                                color = pattern.textColor,
                                style = adaptiveTextStyle(MaterialTheme.typography.displaySmall, screenInfo)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(AdaptiveSizes.spacerHeight(screenInfo, SpacerType.Large)))

                Text(
                    text = rememberedPost.text,
                    style = adaptiveTextStyle(MaterialTheme.typography.displayMedium, screenInfo),
                    color = pattern.textColor,
                    maxLines = if (screenInfo.isCompact) 2 else 10,
                    overflow = TextOverflow.Ellipsis
                )
            }

            val animatedAlpha = (1f - (expansionProgress * 1f)).coerceIn(0f, 1f)

            // Адаптивная панель реакций
            Row(
                horizontalArrangement = Arrangement.spacedBy(if (screenInfo.isCompact) 4.dp else 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.graphicsLayer { alpha = animatedAlpha }
            ) {
                val iconSize = if (screenInfo.isCompact) 28.dp else 33.dp
                val likeFillingSize = if (screenInfo.isCompact) 24.dp else 28.dp

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clickable {
                            viewModel.likeAndDislike(rememberedPost.id)
                        }
                        .size(iconSize)
                        .scale(animatedLikeScale)
                ) {
                    if (isLiked) {
                        Image(
                            painter = painterResource(id = R.drawable.like_filling),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(pattern.reactionColorFilling),
                            modifier = Modifier.size(likeFillingSize)
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
                    text = rememberedPost.likes.toString(),
                    color = pattern.textColor,
                    style = adaptiveTextStyle(MaterialTheme.typography.displayLarge, screenInfo),
                )

                Image(
                    painter = painterResource(id = R.drawable.commentbottom),
                    colorFilter = ColorFilter.tint(pattern.reactionColor),
                    contentDescription = "Comment",
                    modifier = Modifier
                        .clickable { onCommentsClick(post.id) }
                        .size(if (screenInfo.isCompact) 30.dp else 35.dp),
                )

                Text(
                    text = rememberedPost.comments.toString(),
                    color = pattern.textColor,
                    style = adaptiveTextStyle(MaterialTheme.typography.displayLarge, screenInfo),
                )

                Image(
                    painter = painterResource(id = R.drawable.bookmarkbottom1),
                    contentDescription = "Bookmark",
                    colorFilter = ColorFilter.tint(pattern.reactionColor),
                    modifier = Modifier
                        .clickable { /* TODO: Handle bookmark */ }
                        .size(if (screenInfo.isCompact) 25.dp else 30.dp),
                )
            }
        }
    }
}

@Composable
fun TopBar(onNotificationsClick: () -> Unit,
           onSearchClick: () -> Unit) {
    val screenInfo = rememberScreenSizeInfo()
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
fun HorizontalTagRow(tags: List<String>, color: Color, expandProgress: Float = 1f, screenInfo: ScreenSizeInfo) {
    val initialVisibleTags = 2
    val additionalTags = tags.size - initialVisibleTags
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(if (screenInfo.isCompact) 6.dp else 10.dp)
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
    viewModel: PostViewModel = viewModel(),
    onProfileClick: () -> Unit,
    onAuthorClick: (String) -> Unit = {},
    showNavBar: Boolean = true,
    onCommentsClick: (Long) -> Unit,
    onSavedPostsClick: () -> Unit = {}
) {
    val screenInfo = rememberScreenSizeInfo()
    val postsState by viewModel.posts.collectAsState()
    val pagerState = rememberPagerState(pageCount = { postsState.size })
    var expandedIndex by remember { mutableStateOf(-1) }

    val cardTopPositions = remember { mutableStateOf(mutableMapOf<Int, Float>()) }
    val cardHeights = remember { mutableStateOf(mutableMapOf<Int, Float>()) }

    val scope = rememberCoroutineScope()
    var selectedCardCenter by remember { mutableStateOf(Offset.Zero) }
    var selectedCardSize by remember { mutableStateOf(IntSize(0, 0)) }
    var isAnimationInProgress by remember { mutableStateOf(false) }
    var lastActionTime by remember { mutableStateOf(0L) }
    val minActionInterval = 300L
    var lastCloseTime by remember { mutableStateOf(0L) }

    val density = LocalDensity.current
    val screenWidth = screenInfo.screenWidthDp
    val screenHeight = screenInfo.screenHeightDp + WindowInsets.systemBars
        .only(WindowInsetsSides.Top)
        .asPaddingValues()
        .calculateTopPadding()

    // Адаптивные размеры карточек
    val initialCardWidth = AdaptiveSizes.cardWidth(screenInfo)
    val initialCardHeight = AdaptiveSizes.cardHeight(screenInfo)

    var isFullyExpanded by remember { mutableStateOf(false) }
    var shouldHideBottomNav by remember { mutableStateOf(false) }
    var isClosing by remember { mutableStateOf(false) }
    var previousCardCenter by remember { mutableStateOf(Offset.Zero) }
    var dragOffset by remember { mutableStateOf(0f) }

    val expanded = expandedIndex != -1
    val transition = updateTransition(targetState = expanded, label = "expandTransition")
    val fullExpansionTransition = updateTransition(targetState = isFullyExpanded, label = "fullExpansionTransition")
    val closeAnimator = remember { Animatable(0f) }
    val SmoothEasing = CubicBezierEasing(0.05f, 0.0f, 0.15f, 1.0f)

    // Адаптивные длительности анимаций
    val fixedOpenDuration = if (screenInfo.isCompact) 600 else 750
    val fixedSwipeDuration = if (screenInfo.isCompact) 250 else 300
    val fullExpandDuration = if (screenInfo.isCompact) 350 else 400

    var showSearchOverlay by remember { mutableStateOf(false) }


    LaunchedEffect(postsState) {
        if (postsState.isNotEmpty()) {
            viewModel.reinitializeStates(postsState)
        }
    }

    LaunchedEffect(isAnimationInProgress) {
        if (isAnimationInProgress) {
            delay(1500)
            isAnimationInProgress = false
        }
    }

    LaunchedEffect(isClosing) {
        if (isClosing) {
            isAnimationInProgress = true
            val fixedCloseDuration = if (screenInfo.isCompact) 600 else 750

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

    val animSpec = tween<Float>(
        durationMillis = fixedOpenDuration,
        easing = SmoothEasing,
        delayMillis = 30
    )

    val expansionProgress by transition.animateFloat(
        transitionSpec = { animSpec },
        label = "expansionProgress"
    ) { isExpanded ->
        if (isExpanded) 1f else 0f
    }

    val fullExpansionProgress by fullExpansionTransition.animateFloat(
        transitionSpec = {
            if (targetState) {
                tween(durationMillis = fullExpandDuration, easing = FastOutSlowInEasing)
            } else {
                tween(durationMillis = fullExpandDuration, easing = FastOutSlowInEasing)
            }
        },
        label = "fullExpansionProgress"
    ) { state ->
        if (state) 1f else 0f
    }

    val closingProgressTransformed = if (isClosing) {
        SmoothEasing.transform(closeAnimator.value)
    } else {
        0f
    }

    // Адаптивные расчеты размеров с анимацией
    val (currentWidth, currentHeight) = calculateAdaptiveExpandSizes(
        screenInfo = screenInfo,
        initialCardSize = selectedCardSize,
        expansionProgress = if (isClosing) 1f - closingProgressTransformed else expansionProgress,
        fullExpansionProgress = fullExpansionProgress
    )

    val cardCenterX = selectedCardCenter.x
    val initialLeftX = cardCenterX - with(density) { initialCardWidth.toPx() / 2 }
    val currentLeftX = with(density) { (screenWidth / 2).toPx() } - (with(density) { currentWidth.toPx() } / 2)

    val statusBarHeight = WindowInsets.systemBars
        .only(WindowInsetsSides.Top)
        .asPaddingValues()
        .calculateTopPadding()
        .value * density.density

    val fullExpandTopY = statusBarHeight + if (screenInfo.isCompact) 10f else 0f

    val currentTopY = when {
        isClosing -> cardTopPositions.value[expandedIndex] ?: 0f
        expandedIndex != -1 -> {
            val startPos = cardTopPositions.value[expandedIndex] ?: 0f
            if (isFullyExpanded || fullExpansionProgress > 0f) {
                androidx.compose.ui.util.lerp(startPos, fullExpandTopY, fullExpansionProgress) + dragOffset
            } else {
                startPos + dragOffset
            }
        }
        else -> 0f
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

            scope.launch {
                isAnimationInProgress = true
                delay(fullExpandDuration.toLong() + 50)
                isAnimationInProgress = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D0D))
    ) {
        // Горизонтальный пейджер с адаптивными отступами
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = (screenWidth - initialCardWidth) / 2),
            pageSpacing = 0.dp,
            modifier = Modifier
                .fillMaxSize()
                .height(initialCardHeight),
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
                            val updatedPosMap = cardTopPositions.value.toMutableMap()
                            updatedPosMap[page] = position.y
                            cardTopPositions.value = updatedPosMap

                            val cardHeight = coordinates.size.height.toFloat()
                            val updatedHeightMap = cardHeights.value.toMutableMap()
                            updatedHeightMap[page] = cardHeight
                            cardHeights.value = updatedHeightMap

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

                        // Адаптивное масштабирование
                        val minScale = if (screenInfo.isCompact) 0.8f else 0.85f
                        scaleX = lerp(minScale, 1f, 1f - pageOffset.coerceIn(0f, 1f))
                        scaleY = lerp(minScale, 1f, 1f - pageOffset.coerceIn(0f, 1f))

                        alpha = if (expandedIndex != -1) {
                            if (expandedIndex == page) 1f else 1f
                        } else if (isClosing && expandedIndex == page) {
                            closeAnimator.value
                        } else {
                            1f
                        }
                    }
            ) {
                if (page < postsState.size) {
                    PostCard(
                        post = postsState[page],
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
                        onAuthorClick = { onAuthorClick(postsState[page].author.id) },
                        viewModel = viewModel,
                        onCommentsClick = onCommentsClick
                    )
                }
            }
        }

        // Навигационная панель
        var showCreateArticle by remember { mutableStateOf(false) }
        AnimatedVisibility(
            visible = !shouldHideBottomNav && showNavBar,
            enter = fadeIn(animationSpec = tween(durationMillis = 300, easing = SmoothEasing)),
            exit = fadeOut(animationSpec = tween(durationMillis = 300, easing = SmoothEasing)),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                BottomNavBar(
                    onProfileClick = onProfileClick,
                    onCreateArticleClick = { showCreateArticle = true },
                    onHomeClick = { /* TODO */ },
                    onSavedClick = {  onSavedPostsClick()  },
                    onMessagesClick = { /* TODO */ },
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

        // Верхняя панель
        var showNotificationsOverlay by remember { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .zIndex(if (showNotificationsOverlay) 0f else 1f)
            ) {
                TopBar(onNotificationsClick = { showNotificationsOverlay = true },
                    onSearchClick = { showSearchOverlay = true })
            }

            if (showNotificationsOverlay) {
                NotificationsScreen(onClose = { showNotificationsOverlay = false })
            }

            if (showSearchOverlay) {
                SearchScreen(
                    onClose = { showSearchOverlay = false },
                    onPostClick = { post ->
                        // Можно добавить навигацию к посту или открыть его
                        showSearchOverlay = false
                        // onPostClick(post) если нужно
                    },
                    onAuthorClick = { authorId ->
                        showSearchOverlay = false
                        onAuthorClick(authorId)
                    }
                )
            }
        }

        // Развернутая карточка с адаптивными размерами
        if (expandedIndex != -1 || isClosing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
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
                    .width(currentWidth)
                    .height(currentHeight)
                    .background(
                        color = if (expandedIndex >= 0 && expandedIndex < postsState.size) {
                            val index =
                                postsState[expandedIndex].id.rem(PostColorPatterns.size).toInt()
                            PostColorPatterns[index].background
                        } else {
                            Color.Transparent
                        },
                        shape = RoundedCornerShape(52.dp)
                    )
                    .clickable(
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                        indication = null
                    ) { }
                    .zIndex(10f)
            ) {
                if (expandedIndex >= 0 && expandedIndex < postsState.size) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        ExpandedPostContent(
                            post = postsState[expandedIndex],
                            expandProgress = if (isClosing) 1f - closeAnimator.value else expansionProgress,
                            onHeaderSwipe = { toggleFullExpansion() },
                            onAuthorClick = onAuthorClick,
                            viewModel = viewModel,
                            onCommentsClick = onCommentsClick
                        )
                    }

                    // Адаптивные кнопки управления
                    AnimatedVisibility(
                        visible = (if (isClosing) 1f - closeAnimator.value else expansionProgress) > 0.7f,
                        enter = fadeIn(animationSpec = tween(durationMillis = 200, easing = SmoothEasing)),
                        exit = fadeOut(animationSpec = tween(durationMillis = 200, easing = SmoothEasing))
                    ) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(if (screenInfo.isCompact) 12.dp else 16.dp)
                                .size(if (screenInfo.isCompact) 28.dp else 32.dp)
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
                                    .size(if (screenInfo.isCompact) 14.dp else 16.dp),
                                tint = Color.White
                            )
                        }
                    }

                    // Индикатор свайпа
                    AnimatedVisibility(
                        visible = (if (isClosing) 1f - closeAnimator.value else expansionProgress) > 0.9f &&
                                (if (isClosing) 0f else fullExpansionProgress) < 0.1f,
                        enter = fadeIn(animationSpec = tween(durationMillis = 200, easing = SmoothEasing)),
                        exit = fadeOut(animationSpec = tween(durationMillis = 200, easing = SmoothEasing)),
                        modifier = Modifier.align(Alignment.TopCenter)
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = if (screenInfo.isCompact) 6.dp else 8.dp)
                                .width(if (screenInfo.isCompact) 32.dp else 40.dp)
                                .height(if (screenInfo.isCompact) 3.dp else 4.dp)
                                .background(Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExpandedPostContent(
    post: Post,
    expandProgress: Float,
    onHeaderSwipe: () -> Unit,
    onAuthorClick: (String) -> Unit = {},
    viewModel: PostViewModel = viewModel(),
    onCommentsClick: (Long) -> Unit
) {
    val screenInfo = rememberScreenSizeInfo()
    val scrollState = rememberScrollState()
    val colorPatternIndex = post.id.rem(PostColorPatterns.size).toInt()
    val pattern = PostColorPatterns[colorPatternIndex]

    val subscriptions by viewModel.subscriptions.collectAsState()
    val isSubscribed = subscriptions.contains(post.author.id)
    var isLoading by remember(post.author.id) { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val likedPosts by viewModel.likedPostIds.collectAsState()
    val isLiked = likedPosts.contains(post.id)

    var isHeaderVisible by remember { mutableStateOf(true) }
    LaunchedEffect(scrollState.value) {
        isHeaderVisible = scrollState.value < 100
    }

    // Используем те же отступы, что и в PostCard для консистентности
    val cardPadding = AdaptiveSizes.cardPadding(screenInfo)
    val avatarSize = AdaptiveSizes.authorAvatarSize(screenInfo)
    val buttonPadding = AdaptiveSizes.buttonPadding(screenInfo)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(52.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Используем точно такой же верхний отступ, как в PostCard
            Spacer(modifier = Modifier.height(cardPadding.calculateTopPadding()))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        detectVerticalDragGestures { change, dragAmount ->
                            if (isHeaderVisible && scrollState.value == 0) {
                                change.consume()
                                onHeaderSwipe()
                            }
                        }
                    }
            ) {
                Column(
                    modifier = Modifier
                        .padding(
                            start = cardPadding.calculateLeftPadding(LayoutDirection.Ltr),
                            end = cardPadding.calculateRightPadding(LayoutDirection.Ltr)
                        )
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
                    val tagNames = post.tags.map { it.name }

                    HorizontalTagRow(
                        tags = tagNames,
                        color = pattern.buttonColor,
                        expandProgress = expandProgress,
                        screenInfo = screenInfo
                    )

                    Spacer(modifier = Modifier.height(AdaptiveSizes.spacerHeight(screenInfo, SpacerType.Large)))

                    Text(
                        text = post.title,
                        style = adaptiveTextStyle(
                            MaterialTheme.typography.labelLarge.copy(
                                color = pattern.textColor,
                                fontWeight = FontWeight.Bold
                            ),
                            screenInfo
                        )
                    )

                    Spacer(modifier = Modifier.height(AdaptiveSizes.spacerHeight(screenInfo, SpacerType.Medium)))

                    Text(
                        text = "Опубликовано: ${post.time.substring(0, 10)}",
                        style = adaptiveTextStyle(MaterialTheme.typography.titleLarge, screenInfo),
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(AdaptiveSizes.spacerHeight(screenInfo, SpacerType.Small)))

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AsyncImage(
                                model = post.author.avatarUrl,
                                contentDescription = "Author Icon",
                                modifier = Modifier
                                    .size(avatarSize)
                                    .clip(CircleShape)
                                    .border(2.dp, Color.White, CircleShape)
                                    .clickable { onAuthorClick(post.author.id) },
                                contentScale = ContentScale.Crop,
                                placeholder = painterResource(R.drawable.ava),
                                error = painterResource(R.drawable.ava)
                            )

                            Spacer(modifier = Modifier.width(if (screenInfo.isCompact) 6.dp else 10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Автор:",
                                    style = adaptiveTextStyle(MaterialTheme.typography.titleLarge, screenInfo),
                                    color = Color.Black
                                )
                                Text(
                                    text = post.author.username,
                                    style = adaptiveTextStyle(MaterialTheme.typography.titleLarge, screenInfo),
                                    color = pattern.textColor,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.clickable { onAuthorClick(post.author.id) }
                                )
                            }

                            val userId = viewModel.currentUserId

                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(if (screenInfo.isCompact) 16.dp else 20.dp),
                                    color = pattern.textColor,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = when {
                                        post.author.id.equals(userId) -> "Это вы!"
                                        isSubscribed -> "Вы подписаны"
                                        else -> "Подписаться"
                                    },
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
                                        .padding(buttonPadding),
                                    color = pattern.textColor,
                                    style = adaptiveTextStyle(MaterialTheme.typography.displaySmall, screenInfo)
                                )
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = cardPadding.calculateLeftPadding(LayoutDirection.Ltr),
                        end = cardPadding.calculateRightPadding(LayoutDirection.Ltr),
                        bottom = cardPadding.calculateBottomPadding()
                    )
            ) {
                Spacer(modifier = Modifier.height(AdaptiveSizes.spacerHeight(screenInfo, SpacerType.Large)))

                Text(
                    text = post.text,
                    style = adaptiveTextStyle(MaterialTheme.typography.displayMedium, screenInfo),
                    color = pattern.textColor
                )

                Spacer(modifier = Modifier.height(AdaptiveSizes.spacerHeight(screenInfo, SpacerType.Large)))

                AdaptiveReactionPanel(
                    post = post,
                    pattern = pattern,
                    expandProgress = expandProgress,
                    viewModel = viewModel,
                    onCommentsClick = onCommentsClick,
                    screenInfo = screenInfo
                )

                // Адаптивный нижний отступ
                Spacer(modifier = Modifier.height(if (screenInfo.isCompact) 100.dp else 150.dp))
            }
        }
    }
}

@Composable
fun AdaptiveReactionPanel(
    post: Post,
    pattern: PostColorPattern,
    expandProgress: Float,
    viewModel: PostViewModel,
    onCommentsClick: (Long) -> Unit,
    screenInfo: ScreenSizeInfo
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

    val likedPosts by viewModel.likedPostIds.collectAsState()
    val isLiked = likedPosts.contains(post.id)

    // Адаптивные размеры иконок
    val iconSizes = when {
        screenInfo.isCompact -> Triple(28.dp, 30.dp, 25.dp) // like, comment, bookmark
        screenInfo.isMedium -> Triple(30.dp, 33.dp, 28.dp)
        else -> Triple(33.dp, 35.dp, 30.dp)
    }

    val flagSize = if (screenInfo.isCompact) 24.dp else 27.dp
    val likeFillingSize = if (screenInfo.isCompact) 24.dp else 28.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                alpha = reactionPanelOpacity
                translationY = reactionPanelOffset.value.toPx()
            }
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(if (screenInfo.isCompact) 6.dp else 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = if (screenInfo.isCompact) 6.dp else 8.dp)
        ) {
            // Like button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clickable { viewModel.likeAndDislike(post.id) }
                    .size(iconSizes.first)
            ) {
                if (isLiked) {
                    Image(
                        painter = painterResource(id = R.drawable.like_filling),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(pattern.reactionColorFilling),
                        modifier = Modifier.size(likeFillingSize)
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
                text = post.likes.toString(),
                color = Color.Black,
                style = adaptiveTextStyle(MaterialTheme.typography.displayLarge, screenInfo),
            )

            // Comment button
            Image(
                painter = painterResource(id = R.drawable.commentbottom),
                colorFilter = ColorFilter.tint(pattern.reactionColor),
                contentDescription = "Comment",
                modifier = Modifier
                    .clickable { onCommentsClick(post.id) }
                    .size(iconSizes.second),
            )

            Text(
                text = post.comments.toString(),
                color = Color.Black,
                style = adaptiveTextStyle(MaterialTheme.typography.displayLarge, screenInfo),
            )

            // Bookmark button
            Image(
                painter = painterResource(id = R.drawable.bookmarkbottom1),
                contentDescription = "Bookmark",
                colorFilter = ColorFilter.tint(pattern.reactionColor),
                modifier = Modifier
                    .clickable { /* TODO */ }
                    .size(iconSizes.third),
            )

            Text(
                text = "0",
                color = Color.Black,
                style = adaptiveTextStyle(MaterialTheme.typography.displayLarge, screenInfo),
            )

            Spacer(modifier = Modifier.weight(1f))

            // Flag button
            Image(
                painter = painterResource(id = R.drawable.flag),
                contentDescription = "Report",
                colorFilter = ColorFilter.tint(pattern.reactionColor),
                modifier = Modifier
                    .clickable { /* TODO */ }
                    .size(flagSize),
            )
        }
    }
}