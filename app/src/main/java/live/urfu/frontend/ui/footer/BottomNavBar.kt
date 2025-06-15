package urfu.live.frontend.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import live.urfu.frontend.R

@Composable
fun BottomNavBar(
    onProfileClick: () -> Unit,
    onCreateArticleClick: () -> Unit,
    onHomeClick: () -> Unit,
    onSavedClick: () -> Unit,
    onMessagesClick: () -> Unit,
    currentScreen: String,
    modifier: Modifier = Modifier,
    containerWidth: Dp = 400.dp,
    containerHeight: Dp = 110.dp,
    horizontalPadding: Dp = 16.dp,
    verticalPadding: Dp = 12.dp
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp)
            .systemBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .width(containerWidth)
                .height(containerHeight)
                .background(Color(0xFF292929), shape = RoundedCornerShape(52.dp))
                .padding(horizontal = horizontalPadding, vertical = verticalPadding)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavItem(
                    iconRes = R.drawable.home_icon,
                    isSelected = currentScreen == "home",
                    onClick = onHomeClick,
                    iconSize = 42.dp
                )

                NavItem(
                    iconRes = R.drawable.savenew,
                    isSelected = currentScreen == "saved",
                    onClick = onSavedClick,
                    iconSize = 40.dp
                )

                NavItem(
                    iconRes = R.drawable.resource_new,
                    isSelected = currentScreen == "create",
                    onClick = onCreateArticleClick,
                    iconSize = 60.dp
                )

                NavItem(
                    iconRes = R.drawable.messagenew,
                    isSelected = currentScreen == "messages",
                    onClick = onMessagesClick,
                    iconSize = 40.dp
                )

                NavItem(
                    iconRes = R.drawable.profilenew,
                    isSelected = currentScreen == "profile",
                    onClick = onProfileClick,
                    iconSize = 42.dp
                )
            }
        }
    }
}

@Composable
private fun NavItem(
    iconRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    iconSize: Dp = 24.dp
) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(
                if (isSelected) Color.White
                else Color.Transparent
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(iconSize),
            colorFilter = ColorFilter.tint(
                if (isSelected) Color.Black else Color.White
            )
        )
    }
}