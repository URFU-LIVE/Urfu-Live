package live.urfu.frontend.ui.messages

import ScreenSizeInfo
import adaptiveSafeAreaPadding
import rememberScreenSizeInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import live.urfu.frontend.R
import live.urfu.frontend.ui.createarticle.CreateArticle
import live.urfu.frontend.ui.createarticle.CreateArticleViewModel
import live.urfu.frontend.ui.footer.BottomNavBar
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import live.urfu.frontend.ui.search.SearchViewModel

@Composable
fun MessagesScreen(
    onProfileClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onSavedClick: () -> Unit = {},
    onMessagesClick: () -> Unit = {},
    currentScreen: String = "messages",
    navController: NavController
) {
    val screenInfo = rememberScreenSizeInfo()

    var showCreateArticle by remember { mutableStateOf(false) }
    var showSearchBar by remember { mutableStateOf(false) }
    if (showCreateArticle) {
        CreateArticle(
            onClose = { showCreateArticle = false },
            onPostSuccess = {},
            onPostError = {},
            viewModel = CreateArticleViewModel()
        )
    }
    if (showSearchBar) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(300f)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { showSearchBar = false }
                    )
                }
        ) {
            val quickSearchViewModel: SearchViewModel = viewModel()
            val searchBarAdapter = remember(quickSearchViewModel) {
                SearchViewModel.SearchBarAdapter(quickSearchViewModel)
            }
            Box(
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { }
                            )
                        }
                ) {
                    live.urfu.frontend.ui.search.SearchBar(
                        onClose = { showSearchBar = false },
                        onTagSelected = { selectedTag ->
                            showSearchBar = false
                            val safeTag = selectedTag.replace(" ", "_")
                            navController.navigate("search?tag=${safeTag}")
                        },
                        screenInfo = screenInfo,
                        adapter = searchBarAdapter
                    )
                }
            }
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                onProfileClick = onProfileClick,
                onCreateArticleClick = { showCreateArticle = true },
                onHomeClick = onHomeClick,
                onSavedClick = onSavedClick,
                onMessagesClick = onMessagesClick,
                currentScreen = currentScreen
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF131313))
        ) {
            MessagesTopBar(screenInfo = screenInfo, onSearchClick = { showSearchBar = true })

            Spacer(Modifier.weight(0.15f))
                DoItSoonMessage()
            Spacer(Modifier.weight(0.5f))
            paddingValues
        }
    }
}

@Composable
fun MessagesTopBar(
    screenInfo: ScreenSizeInfo,
    onSearchClick: () -> Unit,
) {
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
fun DoItSoonMessage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Этот раздел пока не готов, мы реализуем его в будущем",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 32.sp, lineHeight = 32.sp
                ),
                textAlign = TextAlign.Center,
                color = Color.White
            )
        }
    }
}