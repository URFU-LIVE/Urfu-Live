import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.Button
//import androidx.compose.material.ButtonDefaults
//import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import live.urfu.frontend.R
import live.urfu.frontend.ui.interests.Interest
import live.urfu.frontend.ui.interests.InterestsViewModel
import live.urfu.frontend.ui.theme.UrfuLiveTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun InterestsScreen(
    viewModel: InterestsViewModel = viewModel(),
    onLogoClick: () -> Unit,
    onNextClick: () -> Unit,
) {
    val interests by viewModel.selectedInterests.collectAsState()
    val allInterests = viewModel.allInterests

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var showError by remember { mutableStateOf(false) }

    val hasEnough = interests.size > 2

    // ✅ Один общий контейнер
    Box(modifier = Modifier.fillMaxSize()) {

        // ✅ Верхнее уведомление
        TopSnackbar(
            message = "Выберите хотя бы 3 интереса",
            visible = showError,
            onDismiss = { showError = false }
        )

        // ✅ Scaffold с нижним Snackbar
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        containerColor = Color(0xFFB00020),
                        contentColor = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        snackbarData = data
                    )
                }
            },
            containerColor = Color(0xFF0D0D0D)
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(vertical = 36.dp, horizontal = 26.5.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logowheart),
                    contentDescription = "Heart Logo",
                    modifier = Modifier.clickable { onLogoClick() }
                )

                Spacer(Modifier.height(31.dp))

                Text(
                    text = "Выберите, что вам интересно",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    modifier = Modifier.padding(start = 6.dp)
                )

                Spacer(Modifier.height(30.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    allInterests.forEach { interest ->
                        val isSelected = interests.contains(interest)
                        InterestChip(
                            interest = interest,
                            selected = isSelected,
                            onClick = { viewModel.onToggleInterest(interest) }
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(WindowInsets.navigationBars.asPaddingValues()),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            if (hasEnough) {
                                viewModel.saveInterests()
                                onNextClick()
                            } else {
                                showError = true // верхняя ошибка
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Выберите хотя бы 3 интереса") // нижняя ошибка
                                }
                            }
                        },
                        modifier = Modifier
                            .width(176.dp)
                            .height(42.dp),
                        shape = RoundedCornerShape(15.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (hasEnough) Color(0xFFFF6B3B) else Color.Gray,
                            contentColor = Color.Black
                        )
                    ) {
                        Text(
                            text = "Далее",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

    /**
     * Простой чип (кнопка), который меняет цвет при клике.
     */
    @Composable
    fun InterestChip(
        interest: Interest,
        selected: Boolean,
        onClick: () -> Unit
    ) {
        val backgroundColor = if (selected) interest.color else interest.backgroundColor
        val textColor = Color.Black

        Box(
            modifier = Modifier
                .clickable { onClick() }
                .background(backgroundColor, shape = RoundedCornerShape(52.dp))
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Text(
                text = interest.nameRu,
                color = textColor,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }

    @SuppressLint("ViewModelConstructorInComposable")
    @Preview(
        name = "Small screen (360x640)",
        device = "spec:width=360dp,height=640dp",
        backgroundColor = 10,
        showSystemUi = true
    )
    @Composable
    fun InterestsPreviewSmall() {
        UrfuLiveTheme {
            InterestsScreen(
                onLogoClick = {},
                onNextClick = {},
            )
        }
    }

    @SuppressLint("ViewModelConstructorInComposable")
    @Preview(
        name = "Default screen",
        showBackground = true,
        showSystemUi = true,
        backgroundColor = 10
    )
    @Composable
    fun InterestsPreviewDefault() {
        UrfuLiveTheme {
            InterestsScreen(
                onLogoClick = {},
                onNextClick = {},
            )
        }
    }

    @SuppressLint("ViewModelConstructorInComposable")
    @Preview(
        name = "Large screen (500x1000)",
        device = "spec:width=500dp,height=1000dp",
        showSystemUi = true
    )
    @Composable
    fun InterestsPreviewLarge() {
        UrfuLiveTheme {
            InterestsScreen(
                onLogoClick = {},
                onNextClick = {},
            )
        }
    }

    @Composable
    fun TopSnackbar(
        message: String,
        visible: Boolean,
        onDismiss: () -> Unit
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFB00020))
                    .padding(16.dp)
            ) {
                Text(
                    text = message,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Автоматически скрыть через 2 сек
            LaunchedEffect(Unit) {
                delay(2000)
                onDismiss()
            }
        }
    }
