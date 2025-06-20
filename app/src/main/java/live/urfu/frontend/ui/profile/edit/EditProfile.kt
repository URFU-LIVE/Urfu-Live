package live.urfu.frontend.ui.profile.edit

import NavbarCallbacks
import live.urfu.frontend.ui.snackBar.TopSnackBarWithDismiss
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
import coil.compose.AsyncImage
import live.urfu.frontend.R
import live.urfu.frontend.ui.footer.BottomNavBar
import live.urfu.frontend.ui.createarticle.CreateArticle
import live.urfu.frontend.ui.createarticle.CreateArticleViewModel
import live.urfu.frontend.ui.settings.ArrowSettingsItem
import live.urfu.frontend.ui.snackBar.SnackBarManager
import live.urfu.frontend.ui.snackBar.SnackBarMessage
import live.urfu.frontend.ui.snackBar.SnackBarType

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

    val userState by viewModel.user.collectAsState()

    val showBackgroundSuccess by viewModel.showBackgroundSuccess.collectAsState()
    val showAvatarSuccess by viewModel.showAvatarSuccess.collectAsState()
    val showUsernameSuccess by viewModel.showUsernameSuccess.collectAsState()
    val showDescriptionSuccess by viewModel.showDescriptionSuccess.collectAsState()

    val snackBarManager = remember { SnackBarManager() }
    val currentSnackBar by snackBarManager.currentMessage.collectAsState()

    val avatarGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onAvatarImageSelected(context, it) }
    }

    val backgroundGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onBackgroundImageSelected(context, it) }
    }

    LaunchedEffect(showBackgroundSuccess) {
        if (showBackgroundSuccess) {
            snackBarManager.showMessage(
                SnackBarMessage(
                    "Фоновое изображение успешно установлено",
                    SnackBarType.SUCCESS
                )
            )
            viewModel.resetBackgroundSuccessFlag()
        }
    }

    LaunchedEffect(showAvatarSuccess) {
        if (showAvatarSuccess) {
            snackBarManager.showMessage(
                SnackBarMessage(
                    "Аватарка успешно обновлена",
                    SnackBarType.SUCCESS
                )
            )
            viewModel.resetAvatarSuccessFlag()
        }
    }

    LaunchedEffect(showUsernameSuccess) {
        if (showUsernameSuccess) {
            snackBarManager.showMessage(
                SnackBarMessage(
                    "Имя пользователя обновлено",
                    SnackBarType.SUCCESS
                )
            )
            viewModel.resetUsernameSuccessFlag()
        }
    }

    LaunchedEffect(showDescriptionSuccess) {
        if (showDescriptionSuccess) {
            snackBarManager.showMessage(
                SnackBarMessage(
                    "Описание обновлено",
                    SnackBarType.SUCCESS
                )
            )
            viewModel.resetDescriptionSuccessFlag()
        }
    }



    if (showUsernameDialog) {
        EditTextDialog(
            title = "Введите новое имя пользователя",
            initialValue = userState?.username ?: "username",
            onDismiss = { showUsernameDialog = false },
            onConfirm = { newUsername ->
                viewModel.updateUsername(newUsername)
                showUsernameDialog = false
            }
        )
    }

    if (showDescriptionDialog) {
        EditTextDialog(
            title = "Введите новое описание",
            initialValue = userState?.description ?: "",
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
                    if (userState != null) {
                        AsyncImage(
                            model = userState!!.avatarUrl,
                            contentDescription = "Аватар пользователя",
                            modifier = Modifier
                                .size(84.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color.White, CircleShape)
                                .clickable { avatarGalleryLauncher.launch("image/*") },
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(R.drawable.ava),
                            error = painterResource(R.drawable.ava)
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
                    } else {
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
                }

                Text(
                    text = "Изменить фото",
                    style = MaterialTheme.typography.headlineMedium.copy(color = Color.White),
                    modifier = Modifier.padding(top = 8.dp)
                )

                ArrowSettingsItem(
                    title = "Имя пользователя",
                    currentValue =  userState?.username ?: "username",
                    onClick = { showUsernameDialog = true },
                )

                ArrowSettingsItem(
                    title = "Описание",
                    onClick = { showDescriptionDialog = true },
                )

                ArrowSettingsItem(
                    title = "Изменить фон",
                    onClick = {
                        backgroundGalleryLauncher.launch("image/*") },
                )
            }
        }

        currentSnackBar?.let { snackBar ->
            TopSnackBarWithDismiss(
                message = snackBar.message,
                visible = true,
                onDismiss = { snackBarManager.dismissCurrent() },
                backgroundColor = when (snackBar.type) {
                    SnackBarType.SUCCESS -> Color(0xFF4CAF50)
                    SnackBarType.ERROR -> Color(0xFFB00020)
                    SnackBarType.INFO -> Color(0xFF2196F3)
                },
                autoHideDuration = snackBar.duration,
            )
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