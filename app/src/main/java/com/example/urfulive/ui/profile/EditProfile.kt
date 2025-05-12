package com.example.urfulive.ui.profile

import NavbarCallbacks
import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.urfulive.R
import com.example.urfulive.components.BottomNavBar
import com.example.urfulive.ui.createarticle.CreateArticle
import com.example.urfulive.ui.createarticle.CreateArticleViewModel
import com.example.urfulive.ui.settings.ArrowSettingsItem

@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun EditProfile(
    onClose: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onSavedClick: () -> Unit = {},
    onMessagesClick: () -> Unit = {},
    currentScreen: String = "profile",
    navbarCallbacks: NavbarCallbacks? = null,
    viewModel: EditProfileViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
) {
    val context = LocalContext.current
    var showCreateArticle by remember { mutableStateOf(false) }
    var showUsernameDialog by remember { mutableStateOf(false) }
    var showDescriptionDialog by remember { mutableStateOf(false) }

    // Лаунчер для выбора аватарки
    val avatarGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onAvatarImageSelected(context, it) }
    }

    // Лаунчер для выбора фона
    val backgroundGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onBackgroundImageSelected(context, it) }
    }

    if (showUsernameDialog) {
        EditTextDialog(
            title = "Change username",
            initialValue = "@username", // You should get this from viewModel
            onDismiss = { showUsernameDialog = false },
            onConfirm = { newUsername ->
                viewModel.updateUsername(newUsername)
                showUsernameDialog = false
            }
        )
    }

    if (showDescriptionDialog) {
        EditTextDialog(
            title = "Change description",
            initialValue = "", // You should get this from viewModel
            onDismiss = { showDescriptionDialog = false },
            onConfirm = { newDescription ->
                viewModel.updateDescription(newDescription)
                showDescriptionDialog = false
            }
        )
    }

    if (showCreateArticle) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(300f)
        ) {
            CreateArticle(
                onClose = { showCreateArticle = false },
                onPostSuccess = {},
                onPostError = {},
                viewModel = CreateArticleViewModel()
            )
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
                .systemBarsPadding()
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
                        text = "Редактировать профиль",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }
            }

            // Content
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Блок аватарки
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .clickable {
                            avatarGalleryLauncher.launch("image/*")
                        }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ava),
                        contentDescription = "Аватар по умолчанию",
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.White, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.8f))
                            .size(110.dp),
                    )
                    Image(
                        painter = painterResource(id = R.drawable.camera),
                        contentDescription = "Значок камеры",
                        modifier = Modifier.size(48.dp)
                    )
                }

                Text(
                    text = "Изменить фото",
                    style = MaterialTheme.typography.headlineMedium.copy(color = Color.White),
                    modifier = Modifier.padding(top = 8.dp)
                )

                ArrowSettingsItem(
                    title = "Имя пользователя",
                    currentValue = "@username",
                    onClick = { showUsernameDialog = true },
                )

                ArrowSettingsItem(
                    title = "Описание",
                    onClick = { showDescriptionDialog = true },
                )

                ArrowSettingsItem(
                    title = "Изменить фон",
                    onClick = { backgroundGalleryLauncher.launch("image/*") },
                )
            }
        }

        BottomNavBar(
            onProfileClick = onClose,
            onCreateArticleClick = { showCreateArticle = true },
            onHomeClick = navbarCallbacks?.onHomeClick ?: onHomeClick,
            onSavedClick = onSavedClick,
            onMessagesClick = onMessagesClick,
            currentScreen = currentScreen,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun EditTextDialog(
    title: String,
    initialValue: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var textValue by remember { mutableStateOf(initialValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = {
            TextField(
                value = textValue,
                onValueChange = { textValue = it },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(textValue) }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}