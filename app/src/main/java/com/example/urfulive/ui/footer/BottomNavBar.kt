package com.example.urfulive.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.urfulive.R

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
    containerHeight: Dp = 80.dp,
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
                    onClick = onHomeClick
                )

                NavItem(
                    iconRes = R.drawable.savenew,
                    isSelected = currentScreen == "saved",
                    onClick = onSavedClick
                )

                NavItem(
                    iconRes = R.drawable.resource_new,
                    isSelected = currentScreen == "create",
                    onClick = onCreateArticleClick,
                    iconSize = 28.dp
                )

                NavItem(
                    iconRes = R.drawable.messagenew,
                    isSelected = currentScreen == "messages",
                    onClick = onMessagesClick
                )

                NavItem(
                    iconRes = R.drawable.profilenew,
                    isSelected = currentScreen == "profile",
                    onClick = onProfileClick,
                    iconSize = 30.dp
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
            .size(44.dp)
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