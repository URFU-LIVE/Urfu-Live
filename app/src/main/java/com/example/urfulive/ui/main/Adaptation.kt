import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp

// 1. Создайте класс для управления размерами экрана
data class ScreenSizeInfo(
    val screenWidthDp: Dp,
    val screenHeightDp: Dp,
    val density: Density,
    val isCompact: Boolean,
    val isMedium: Boolean,
    val isExpanded: Boolean
)

@Composable
fun rememberScreenSizeInfo(): ScreenSizeInfo {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    return remember(configuration) {
        val widthDp = configuration.screenWidthDp.dp
        val heightDp = configuration.screenHeightDp.dp

        ScreenSizeInfo(
            screenWidthDp = widthDp,
            screenHeightDp = heightDp,
            density = density,
            isCompact = widthDp < 360.dp || heightDp < 650.dp,
            isMedium = widthDp in 360.dp..600.dp && heightDp in 650.dp..840.dp,
            isExpanded = widthDp > 600.dp || heightDp > 840.dp
        )
    }
}

// 2. Создайте адаптивные размеры
object AdaptiveSizes {
    @Composable
    fun cardWidth(screenInfo: ScreenSizeInfo): Dp = when {
        screenInfo.isCompact -> screenInfo.screenWidthDp * 0.85f
        screenInfo.isMedium -> screenInfo.screenWidthDp * 0.9f
        else -> screenInfo.screenWidthDp * 0.9f
    }.coerceAtMost(400.dp)

    @Composable
    fun cardHeight(screenInfo: ScreenSizeInfo): Dp = when {
        screenInfo.isCompact -> screenInfo.screenHeightDp * 0.5f
        screenInfo.isMedium -> screenInfo.screenHeightDp * 0.6f
        else -> screenInfo.screenHeightDp * 0.65f
    }.coerceIn(300.dp, 600.dp)

    @Composable
    fun tagPadding(screenInfo: ScreenSizeInfo, size: TagSizes): PaddingValues = when {
        screenInfo.isCompact -> PaddingValues(
            horizontal = if (size == TagSizes.Standard) 10.dp else 8.dp,
            vertical = if (size == TagSizes.Standard) 8.dp else 5.dp
        )
        else -> PaddingValues(
            horizontal = if (size == TagSizes.Standard) 15.dp else 13.dp,
            vertical = if (size == TagSizes.Standard) 13.dp else 7.dp
        )
    }

    @Composable
    fun authorAvatarSize(screenInfo: ScreenSizeInfo): Dp = when {
        screenInfo.isCompact -> 35.dp
        screenInfo.isMedium -> 45.dp
        else -> 50.dp
    }

    @Composable
    fun buttonPadding(screenInfo: ScreenSizeInfo): PaddingValues = when {
        screenInfo.isCompact -> PaddingValues(horizontal = 8.dp, vertical = 5.dp)
        screenInfo.isMedium -> PaddingValues(horizontal = 12.dp, vertical = 8.dp)
        else -> PaddingValues(horizontal = 15.dp, vertical = 10.dp)
    }

    @Composable
    fun fontSize(screenInfo: ScreenSizeInfo, baseSize: TextUnit): TextUnit = when {
        screenInfo.isCompact -> baseSize * 0.85f
        screenInfo.isMedium -> baseSize * 0.95f
        else -> baseSize
    }

    @Composable
    fun cardPadding(screenInfo: ScreenSizeInfo): PaddingValues = when {
        screenInfo.isCompact -> PaddingValues(
            start = 16.dp, top = 24.dp, end = 16.dp, bottom = 20.dp
        )
        screenInfo.isMedium -> PaddingValues(
            start = 20.dp, top = 32.dp, end = 20.dp, bottom = 28.dp
        )
        else -> PaddingValues(
            start = 25.dp, top = 40.dp, end = 25.dp, bottom = 37.dp
        )
    }

    @Composable
    fun spacerHeight(screenInfo: ScreenSizeInfo, type: SpacerType): Dp = when (type) {
        SpacerType.Small -> when {
            screenInfo.isCompact -> 4.dp
            screenInfo.isMedium -> 6.dp
            else -> 6.dp
        }
        SpacerType.Medium -> when {
            screenInfo.isCompact -> 8.dp
            screenInfo.isMedium -> 15.dp
            else -> 20.dp
        }
        SpacerType.Large -> when {
            screenInfo.isCompact -> 15.dp
            screenInfo.isMedium -> 20.dp
            else -> 20.dp
        }
    }
}

enum class SpacerType {
    Small, Medium, Large
}

// 3. Создайте адаптивные стили текста
@Composable
fun adaptiveTextStyle(
    baseStyle: TextStyle,
    screenInfo: ScreenSizeInfo
): TextStyle = baseStyle.copy(
    fontSize = when {
        screenInfo.isCompact -> baseStyle.fontSize * 0.85f
        screenInfo.isMedium -> baseStyle.fontSize * 0.95f
        else -> baseStyle.fontSize
    },
    lineHeight = when {
        screenInfo.isCompact -> baseStyle.lineHeight * 0.85f
        screenInfo.isMedium -> baseStyle.lineHeight * 0.95f
        else -> baseStyle.lineHeight
    }
)

// 4. Создайте адаптивный модификатор для анимаций
@Composable
fun Modifier.adaptiveAnimatedSize(
    screenInfo: ScreenSizeInfo,
    baseWidth: Dp,
    baseHeight: Dp,
    animationProgress: Float = 0f
): Modifier {
    val targetWidth = when {
        screenInfo.isCompact -> baseWidth * 0.9f
        else -> baseWidth
    }

    val targetHeight = when {
        screenInfo.isCompact -> baseHeight * 0.9f
        else -> baseHeight
    }

    val animatedWidth by animateDpAsState(
        targetValue = lerp(targetWidth * 0.9f, targetWidth, animationProgress),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    val animatedHeight by animateDpAsState(
        targetValue = lerp(targetHeight * 0.9f, targetHeight, animationProgress),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    return this
        .width(animatedWidth)
        .height(animatedHeight)
}

// 5. Адаптивные расчеты для анимаций развертывания
@Composable
fun calculateAdaptiveExpandSizes(
    screenInfo: ScreenSizeInfo,
    initialCardSize: IntSize,
    expansionProgress: Float,
    fullExpansionProgress: Float
): Pair<Dp, Dp> {
    val density = screenInfo.density

    // Базовые размеры с учетом типа экрана
    val partialExpandMultiplier = when {
        screenInfo.isCompact -> 1f
        screenInfo.isMedium -> 1f
        else -> 1f
    }

    val fullExpandMultiplier = 5f

    with(density) {
        val initialWidth = initialCardSize.width.toDp()
        val initialHeight = initialCardSize.height.toDp()

        val targetWidth = screenInfo.screenWidthDp
        val partialHeight = screenInfo.screenHeightDp * partialExpandMultiplier
        val fullHeight = screenInfo.screenHeightDp * fullExpandMultiplier

        val currentWidth = lerp(initialWidth, targetWidth, expansionProgress)
        val currentHeight = lerp(
            initialHeight,
            lerp(partialHeight, fullHeight, fullExpansionProgress),
            expansionProgress
        )

        return Pair(currentWidth, currentHeight)
    }
}

// 6. Вспомогательная функция для безопасных отступов
@Composable
fun adaptiveSafeAreaPadding(screenInfo: ScreenSizeInfo): PaddingValues {
    val systemBars = WindowInsets.systemBars.asPaddingValues()
    val topPadding = systemBars.calculateTopPadding()

    return PaddingValues(
        top = topPadding + when {
            screenInfo.isCompact -> 32.dp
            screenInfo.isMedium -> 40.dp
            else -> 45.dp
        },
        bottom = systemBars.calculateBottomPadding()
    )
}

// Адаптивные размеры для экрана настроек
object SettingsAdaptiveSizes {
    @Composable
    fun avatarSize(screenInfo: ScreenSizeInfo): Dp = when {
        screenInfo.isCompact -> 70.dp
        screenInfo.isMedium -> 84.dp
        else -> 96.dp
    }

    @Composable
    fun itemInternalPadding(screenInfo: ScreenSizeInfo): PaddingValues = when {
        screenInfo.isCompact -> PaddingValues(
            vertical = 10.dp,
            horizontal = 16.dp
        )
        screenInfo.isMedium -> PaddingValues(
            vertical = 14.dp,
            horizontal = 20.dp
        )
        else -> PaddingValues(
            vertical = 18.dp,
            horizontal = 24.dp
        )
    }
}

// Адаптивные стили текста для настроек
@Composable
fun adaptiveSettingsTextStyle(
    baseStyle: TextStyle,
    screenInfo: ScreenSizeInfo,
    scaleFactor: Float = 1f
): TextStyle = baseStyle.copy(
    fontSize = when {
        screenInfo.isCompact -> baseStyle.fontSize * 0.85f * scaleFactor
        screenInfo.isMedium -> baseStyle.fontSize * 0.95f * scaleFactor
        else -> baseStyle.fontSize * scaleFactor
    },
    lineHeight = when {
        screenInfo.isCompact -> baseStyle.lineHeight * 0.85f * scaleFactor
        screenInfo.isMedium -> baseStyle.lineHeight * 0.95f * scaleFactor
        else -> baseStyle.lineHeight * scaleFactor
    }
)

object SavedPostsAdaptiveSizes {
    @Composable
    fun searchIconSize(screenInfo: ScreenSizeInfo): Dp = when {
        screenInfo.isCompact -> 24.dp
        screenInfo.isMedium -> 28.dp
        else -> 32.dp
    }

    @Composable
    fun postCardPadding(screenInfo: ScreenSizeInfo): PaddingValues = when {
        screenInfo.isCompact -> PaddingValues(
            horizontal = 16.dp,
            vertical = 8.dp
        )
        screenInfo.isMedium -> PaddingValues(
            horizontal = 20.dp,
            vertical = 12.dp
        )
        else -> PaddingValues(
            horizontal = 24.dp,
            vertical = 16.dp
        )
    }

    @Composable
    fun postContentPadding(screenInfo: ScreenSizeInfo): PaddingValues = when {
        screenInfo.isCompact -> PaddingValues(vertical = 16.dp, horizontal = 20.dp)
        else -> PaddingValues(vertical = 20.dp, horizontal = 25.dp)
    }

    @Composable
    fun bookmarkIconSize(screenInfo: ScreenSizeInfo): Dp = when {
        screenInfo.isCompact -> 20.dp
        screenInfo.isMedium -> 24.dp
        else -> 28.dp
    }

    @Composable
    fun titleFontSize(screenInfo: ScreenSizeInfo): TextUnit = when {
        screenInfo.isCompact -> 14.sp
        screenInfo.isMedium -> 16.sp
        else -> 18.sp
    }

    @Composable
    fun authorFontSize(screenInfo: ScreenSizeInfo): TextUnit = when {
        screenInfo.isCompact -> 12.sp
        screenInfo.isMedium -> 13.sp
        else -> 14.sp
    }

    @Composable
    fun tagSpacing(screenInfo: ScreenSizeInfo): Dp = when {
        screenInfo.isCompact -> 8.dp
        else -> 10.dp
    }

    @Composable
    fun postSpacing(screenInfo: ScreenSizeInfo): Dp = when {
        screenInfo.isCompact -> 8.dp
        else -> 10.dp
    }

    @Composable
    fun contentSpacing(screenInfo: ScreenSizeInfo): Dp = when {
        screenInfo.isCompact -> 8.dp
        else -> 10.dp
    }
}

object SearchAdaptiveSizes {
    @Composable
    fun reactionIconSize(screenInfo: ScreenSizeInfo): Dp = when {
        screenInfo.isCompact -> 20.dp
        else -> 25.dp
    }
}