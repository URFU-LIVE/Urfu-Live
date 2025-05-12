import android.annotation.SuppressLint
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.example.urfulive.R
import com.example.urfulive.ui.createarticle.CreateArticle
import com.example.urfulive.ui.theme.UrfuLiveTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InterestsScreen(
    viewModel: InterestsViewModel = viewModel(),
    onLogoClick: () -> Unit,
    onNextClick: () -> Unit,
    onSkipClick: () -> Unit,
) {
    val interests by viewModel.selectedInterests.collectAsState()
    val allInterests = viewModel.allInterests

    // Определяем, выбрано ли хоть что-то
    val hasSelection = interests.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D0D))
            .padding(vertical = 36.dp)
            .padding(start = 26.5.dp, end = 26.5.dp)

    ) {
        // Верхняя часть
        Image(
            painter = painterResource(id = R.drawable.logowheart),
            contentDescription = "Heart Logo",
            modifier = Modifier
                .clickable { onLogoClick() }
        )

        Spacer(Modifier.height(31.dp))

        // Заголовок
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
                val isSelected = interests.contains(interest.key)
                InterestChip(
                    text = interest.key,
                    selected = isSelected,
                    onClick = { viewModel.onToggleInterest(interest.key) },
                    interestColorMap = viewModel.interestColorMap
                )
            }
        }

        Spacer(Modifier.weight(1f))


        Row(
            modifier = Modifier.fillMaxWidth().padding(WindowInsets.navigationBars.asPaddingValues()),
            horizontalArrangement = Arrangement.End

        ) {
            Button(
                onClick = {
                    if (hasSelection) {
                        onNextClick()
                    } else {
                        onSkipClick()
                    }
                },
                modifier = Modifier
                    .width(176.dp)
                    .height(42.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (hasSelection) Color(0xFFFF6B3B) else Color.Gray,
                    contentColor = if (hasSelection) Color.White else Color.Black
                )
            ) {
                Text(
                    text = if (hasSelection) "Продолжить" else "Пропустить",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

/**
 * Простой чип (кнопка), который меняет цвет при клике.
 */
@Composable
fun InterestChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    interestColorMap: Map<String, Pair<Color, Color>>
) {
    val (selectedColor, unselectedColor) = interestColorMap[text] ?: (Color.Gray to Color.DarkGray)
    val backgroundColor = if (selected) selectedColor else unselectedColor
    val textColor = Color.Black

    Box(
        modifier = Modifier
            .clickable { onClick() }
            .background(backgroundColor, shape = RoundedCornerShape(52.dp))
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(name = "Small screen (360x640)", device = "spec:width=360dp,height=640dp", backgroundColor = 10, showSystemUi = true)
@Composable
fun InterestsPreviewSmall() {
    UrfuLiveTheme {
        InterestsScreen (
            onLogoClick = {},
            onNextClick = {},
            onSkipClick = {}
        )
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(name = "Default screen", showBackground = true, showSystemUi = true, backgroundColor = 10)
@Composable
fun InterestsPreviewDefault() {
    UrfuLiveTheme {
        InterestsScreen (
            onLogoClick = {},
            onNextClick = {},
            onSkipClick = {}
        )
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(name = "Large screen (500x1000)", device = "spec:width=500dp,height=1000dp", showSystemUi = true)
@Composable
fun InterestsPreviewLarge() {
    UrfuLiveTheme {
        InterestsScreen (
            onLogoClick = {},
            onNextClick = {},
            onSkipClick = {}
        )
    }
}