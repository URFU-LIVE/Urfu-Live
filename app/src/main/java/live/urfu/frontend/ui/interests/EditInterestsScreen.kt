package live.urfu.frontend.ui.interests

import InterestsScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun EditInterestsScreen(
    onBackClick: () -> Unit,
    onInterestsSaved: () -> Unit = {},
    viewModel: InterestsViewModel = viewModel()
) {
    var saveInProgress by remember { mutableStateOf(false) }

    InterestsScreen(
        viewModel = viewModel,
        onLogoClick = {},
        onNextClick = {
            saveInProgress = true
            onInterestsSaved()
            onBackClick()
        },
        onBackClick = onBackClick,
        isEditMode = true
    )

    if (saveInProgress) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = Color(0xFFFF6B3B),
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}