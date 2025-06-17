import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import live.urfu.frontend.R
import kotlinx.coroutines.launch
import live.urfu.frontend.data.model.Interest
import live.urfu.frontend.ui.interests.InterestsViewModel

@Composable
fun InterestsScreen(
    viewModel: InterestsViewModel = viewModel(),
    onLogoClick: () -> Unit,
    onNextClick: () -> Unit,
    onBackClick: (() -> Unit)? = null,
    isEditMode: Boolean = false,
) {
    val interests by viewModel.selectedInterests.collectAsState()
    val allInterests = viewModel.allInterests

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val hasEnough = interests.size > 2

    val screenInfo = rememberScreenSizeInfo()
    val isLoading by viewModel.isLoading.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                ) { data -> Snackbar(
                            containerColor = Color(0xFFB00020),
                            contentColor = Color.White,
                            shape = RoundedCornerShape(12.dp),
                            snackbarData = data
                        )
                    }
                },
            bottomBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .systemBarsPadding()
                        .padding(bottom = 15.dp, end = 26.5.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Button(
                        onClick = {
                            if (hasEnough) {
                                viewModel.saveInterests()
                                onNextClick()
                            } else {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Выберите хотя бы 3 интереса")
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
            },
            containerColor = Color(0xFF0D0D0D)
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(
                        if (isEditMode) {
                            PaddingValues(top = 23.dp, bottom = 15.dp)
                        } else {
                            PaddingValues(vertical = 36.dp, horizontal = 26.5.dp)
                        }
                    )
            ) {
                if (isEditMode && onBackClick != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.chevron_left),
                            contentDescription = "Arrow",
                            modifier = Modifier
                                .clickable { onBackClick() }
                                .padding(start = 15.dp)
                        )
                        Text(
                            text = "Изменить интересы",
                            color = Color.White,
                            style = adaptiveTextStyle(
                                MaterialTheme.typography.headlineLarge,
                                screenInfo
                            ),
                            modifier = Modifier.padding(start = if (screenInfo.isCompact) 8.dp else 10.dp)
                        )
                    }
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.logowheart),
                        contentDescription = "Heart Logo",
                        modifier = Modifier.clickable { onLogoClick() }
                    )
                    Spacer(Modifier.height(31.dp))
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            if (isEditMode) {
                                PaddingValues(vertical = 36.dp, horizontal = 26.5.dp)
                            } else {
                                PaddingValues(0.dp)
                            }
                        )
                ) {
                    Text(
                        text = if (isEditMode) "Выберите интересы" else "Выберите, что вам интересно",
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

                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFFFF6B3B))
                        }
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
    onClick: () -> Unit,
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