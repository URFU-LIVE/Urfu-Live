import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.urfulive.data.api.UserApiService
import com.example.urfulive.ui.comments.CommentsScreen
import com.example.urfulive.ui.main.PostViewModel
import com.example.urfulive.ui.profile.EditProfile
import com.example.urfulive.ui.profile.ExpandedPostOverlay
import com.example.urfulive.ui.profile.ProfileScreen
import com.example.urfulive.ui.profile.ProfileViewModel
import com.example.urfulive.ui.saved.SavedPostsScreen
import com.example.urfulive.ui.search.SearchScreen
import com.example.urfulive.ui.search.SearchViewModel
import com.example.urfulive.ui.settings.SettingsScreen
import com.example.urfulive.ui.settings.account.AccountSettings
import com.example.urfulive.ui.settings.notification.NotificationsSettings
import com.example.urfulive.ui.settings.privacy.PrivacySettings

sealed class AuthState {
    object Loading : AuthState()
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost() {
    val navController: NavHostController = rememberNavController()
    val profileViewModel: ProfileViewModel = viewModel()
    val userApiService = UserApiService()

    val sharedPostViewModel: PostViewModel = viewModel()

    val authState = remember { mutableStateOf<AuthState>(AuthState.Loading) }

    LaunchedEffect(Unit) {
        try {
            if (userApiService.refreshToken().isSuccess) {
                authState.value = AuthState.Authenticated
            } else {
                authState.value = AuthState.Unauthenticated
            }
        } catch (e: Exception) {
            authState.value = AuthState.Unauthenticated
        }
    }

    val startDestination = when (authState.value) {
        is AuthState.Loading -> "loading"
        is AuthState.Authenticated -> "main"
        is AuthState.Unauthenticated -> "login"
        else -> "loading"
    }


    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("loading") {
            Box(modifier = Modifier.fillMaxSize()) {
                // TODO: Add LoadingScreen()
            }
        }

        composable("login") {
            LoginScreen(
                onRegisterClick = { navController.navigate("registration") },
                onLoginClick = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onLoginError = {},
                onRestorePasswordClick = { navController.navigate("main") },
                postViewModel = sharedPostViewModel
            )
        }

        composable("registration") {
            RegistrationScreen(
                onLogoClick = { navController.popBackStack() },
                onRegisterClick = { navController.navigate("interests") },
                onRegisterSuccess = {},
                onRegisterError = {}
            )
        }

        composable("interests") {
            InterestsScreen(
                onLogoClick = { navController.popBackStack() },
                onNextClick = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
            )
        }

        composable("main") {
            MainScreenWithOverlays(
                navController = navController,
                profileViewModel = profileViewModel,
                sharedPostViewModel = sharedPostViewModel
            )
        }

        composable("savedPosts") {
            SavedPostsScreen(
                onProfileClick = { navController.navigate("profile") },
                onCreateArticleClick = {
                    // Implement article creation navigation
                },
                onHomeClick = {
                    navController.navigate("main") {
                        popUpTo("main") { inclusive = true }
                    }
                },
                onSavedClick = {
                    // Уже на экране сохраненных постов, ничего не делаем
                },
                onMessagesClick = {  },
                onPostClick = { post ->
                    // Навигация к развернутому посту
                },
                onSearchClick = {
                    // TODO: Implement search functionality
                },
                onRemoveFromSaved = { post ->
                    // TODO: Implement remove from saved functionality
                },
                currentScreen = "saved"
            )
        }

        composable(
            route = "settings?showAnimation={showAnimation}",
            arguments = listOf(
                navArgument("showAnimation") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val showAnimation = backStackEntry.arguments?.getBoolean("showAnimation") ?: false

            SettingsScreen(
                onCloseOverlay = { navController.navigate("profile") },
                onAccountClick = { navController.navigate("accountSettings") },
                onNotificationsClick = { navController.navigate("notificationsSettings") },
                onPrivacyClick = { navController.navigate("privacySettings") },
                onHomeClick = { navController.navigate("main") { popUpTo("main") { inclusive = true } } },
                navbarCallbacks = commonNavbarCallbacks(navController),
                onLeave = { navController.navigate("login") { popUpTo(0) { inclusive = true } } },
                enableAnimation = showAnimation
            )
        }

        composable("accountSettings") {
            AccountSettings(
                onClose = { navController.navigate("settings?showAnimation=false") },
                onHomeClick = { navController.navigate("main") { popUpTo("main") { inclusive = true } } },
                navbarCallbacks = commonNavbarCallbacks(navController)
            )
        }

        composable("notificationsSettings") {
            NotificationsSettings(
                onClose = { navController.navigate("settings?showAnimation=false") },
                onHomeClick = { navController.navigate("main") { popUpTo("main") { inclusive = true } } },
                navbarCallbacks = commonNavbarCallbacks(navController)
            )
        }

        composable("privacySettings") {
            PrivacySettings(
                onClose = { navController.navigate("settings?showAnimation=false") },
                onHomeClick = { navController.navigate("main") { popUpTo("main") { inclusive = true } } },
                navbarCallbacks = commonNavbarCallbacks(navController)
            )
        }

        composable("editProfile") {
            EditProfile(
                onClose = { navController.navigate("profile") },
                onHomeClick = { navController.navigate("main") { popUpTo("main") { inclusive = true } } },
                navbarCallbacks = commonNavbarCallbacks(navController)
            )
        }

        composable(
            route = "comments/{postId}",
            arguments = listOf(navArgument("postId") { type = NavType.LongType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getLong("postId") ?: 0
            CommentsScreen(
                postId = postId,
                onClose = { navController.popBackStack() }
            )
        }

        composable("profile") {
            LaunchedEffect(Unit) {
                profileViewModel.fetchProfile()
            }

            ProfileScreen(
                viewModel = profileViewModel,
                onHomeClick = { navController.navigate("main") { popUpTo("main") { inclusive = true } } },
                onEditProfileClick = { navController.navigate("editProfile") },
                navbarCallbacks = commonNavbarCallbacks(navController),
                currentScreen = "profile",
                onSettingsClick = {
                    navController.navigate("settings?showAnimation=true")
                },
                isOwnProfile = true,
                onSavedClick = { navController.navigate("savedPosts") },
                onCommentsClick = { postId ->
                    navController.navigate("comments/$postId")
                },
                sharedPostViewModel = sharedPostViewModel
            )
        }

        composable(
            route = "search?tag={tag}",
            arguments = listOf(navArgument("tag") {
                type = NavType.StringType
                defaultValue = ""
                nullable = true
            })
        ) { backStackEntry ->
            val selectedTag = backStackEntry.arguments?.getString("tag") ?: ""
            val searchViewModel: SearchViewModel = viewModel()
            SearchScreen(
                initialTag = selectedTag,
                onClose = { navController.popBackStack() },
                onPostClick = { post ->
                    // Этот колбэк больше не используется, так как посты раскрываются внутри SearchScreen
                },
                onAuthorClick = { authorId ->
                    // Навигация к профилю автора
                    navController.navigate("profile/$authorId")
                },
                onCommentsClick = { postId ->
                    // ✅ Навигация к экрану комментариев
                    navController.navigate("comments/$postId")
                },
                viewModel = searchViewModel,
                enableAnimations = true,
                postViewModel = sharedPostViewModel
            )
        }

        composable(
            route = "author/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.LongType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId") ?: return@composable
            val authorProfileViewModel: ProfileViewModel = viewModel()

            LaunchedEffect(userId) {
                authorProfileViewModel.clearData()
                authorProfileViewModel.fetchUserProfileById(userId)
            }

            ProfileScreen(
                viewModel = authorProfileViewModel,
                isOwnProfile = false,
                onHomeClick = { navController.navigate("main") { popUpTo("main") { inclusive = true } } },
                navbarCallbacks = commonNavbarCallbacks(navController),
                currentScreen = "author",
                onCloseOverlay = { navController.popBackStack() },
                onCommentsClick = { postId ->
                    navController.navigate("comments/$postId")
                },
                sharedPostViewModel = sharedPostViewModel
            )
        }
    }
}

@Composable
fun MainScreenWithOverlays(
    navController: NavHostController,
    profileViewModel: ProfileViewModel, // todo
    sharedPostViewModel: PostViewModel
) {
    Box(modifier = Modifier.fillMaxSize()) {
        CarouselScreen(
            navController = navController,
            onProfileClick = { navController.navigate("profile") },
            onAuthorClick = { authorId -> navController.navigate("author/$authorId") },
            showNavBar = true,
            onCommentsClick = { postId ->
                navController.navigate("comments/$postId")
            },
            onSavedPostsClick = {navController.navigate("savedPosts")},
            viewModel = sharedPostViewModel
        )
    }
}

fun commonNavbarCallbacks(navController: NavHostController) = NavbarCallbacks(
    onHomeClick = {
        navController.navigate("main") {
            popUpTo("main") { inclusive = true }
        }
    },
    onSavedClick = { navController.navigate("savedPosts") },
    onCreateArticleClick = {
        // Implement article creation
    },
    onMessagesClick = { navController.navigate("messages") },
    onProfileClick = { navController.navigate("profile") }
)

data class NavbarCallbacks(
    val onHomeClick: () -> Unit,
    val onSavedClick: () -> Unit,
    val onCreateArticleClick: () -> Unit,
    val onMessagesClick: () -> Unit,
    val onProfileClick: () -> Unit
)
