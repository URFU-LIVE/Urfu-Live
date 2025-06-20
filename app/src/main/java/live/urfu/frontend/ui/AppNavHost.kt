package live.urfu.frontend.ui

import InterestsScreen
import LoginScreen
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
import live.urfu.frontend.data.api.UserApiService
import live.urfu.frontend.ui.authentication.registration.RegistrationScreen
import live.urfu.frontend.ui.comments.CommentsScreen
import live.urfu.frontend.ui.main.PostViewModel
import live.urfu.frontend.ui.profile.edit.EditProfile
import live.urfu.frontend.ui.profile.ProfileScreen
import live.urfu.frontend.ui.profile.ProfileViewModel
import live.urfu.frontend.ui.savedPosts.SavedPostsScreen
import live.urfu.frontend.ui.search.SearchScreen
import live.urfu.frontend.ui.search.SearchViewModel
import live.urfu.frontend.ui.settings.SettingsScreen
import live.urfu.frontend.ui.settings.account.AccountSettings
import live.urfu.frontend.ui.interests.EditInterestsScreen
import live.urfu.frontend.ui.main.CarouselScreen
import live.urfu.frontend.ui.settings.notification.NotificationsSettings
import live.urfu.frontend.ui.settings.privacy.PrivacySettings
import java.net.URLEncoder

sealed class AuthState {
    data object Loading : AuthState()
    data object Authenticated : AuthState()
    data object Unauthenticated : AuthState()
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
                onRestorePasswordClick = { },
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onLoginError = {}
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

        composable("editInterests") {
            EditInterestsScreen(
                onBackClick = { navController.navigate("settings?showAnimation=false") },
                onInterestsSaved = {
                    // Опционально: показать уведомление об успешном сохранении
                }
            )
        }

        composable("main") {
            MainScreenWithOverlays(
                navController = navController,
                sharedPostViewModel = sharedPostViewModel
            )
        }

        composable("savedPosts") {
            SavedPostsScreen(
                onProfileClick = { navController.navigate("profile") },
                onHomeClick = {
                    navController.navigate("main") {
                        popUpTo("main") { inclusive = true }
                    }
                },
                onSavedClick = {
                    // Уже на экране сохраненных постов, ничего не делаем
                },
                onMessagesClick = { },
                onAuthorClick = { authorId ->
                    navController.navigate("author/$authorId")
                },
                onCommentsClick = { postId ->
                    navController.navigate("comments/$postId")
                },
                currentScreen = "saved",
                sharedPostViewModel = sharedPostViewModel,
                navController = navController
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
                onInterestsClick = { navController.navigate("editInterests") },
                onHomeClick = { navController.navigate("main") { popUpTo("main") { inclusive = true } } },
                navbarCallbacks = commonNavbarCallbacks(navController),
                onLeave = { navController.navigate("login") { popUpTo(0) { inclusive = true } } },
                enableAnimation = showAnimation,
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
                onClose = { navController.popBackStack() },
                onProfileClick = { authorId ->
                    navController.navigate("author/$authorId")
                },
                postViewModel = sharedPostViewModel
            )
        }

        composable("profile") {
            LaunchedEffect(Unit) {
                profileViewModel.fetchProfile()
            }

            ProfileScreen(
                viewModel = profileViewModel,
                isOwnProfile = true,
                onHomeClick = { navController.navigate("main") { popUpTo("main") { inclusive = true } } },
                onSavedClick = { navController.navigate("savedPosts") },
                onSettingsClick = {
                    navController.navigate("settings?showAnimation=true")
                },
                currentScreen = "profile",
                navbarCallbacks = commonNavbarCallbacks(navController),
                onEditProfileClick = { navController.navigate("editProfile") },
                onCommentsClick = { postId ->
                    navController.navigate("comments/$postId")
                },
                sharedPostViewModel = sharedPostViewModel,
                onAuthorClick = { authorId ->
                    navController.navigate("author/$authorId")
                },
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
            val searchViewModel: SearchViewModel = viewModel(
                viewModelStoreOwner = backStackEntry
            )
            SearchScreen(
                initialTag = selectedTag,
                onClose = {
                    navController.popBackStack()
                },
                onAuthorClick = { authorId ->
                    navController.navigate("author/$authorId")
                },
                onCommentsClick = { postId, tag ->
                    val e = URLEncoder.encode(tag, "UTF-8")
                    navController.navigate("comments/$postId?searchTag=$e")
                },
                viewModel = searchViewModel,
                enableAnimations = true,
                postViewModel = sharedPostViewModel,
                onTagSearch   = { tagToSearch ->
                    val e = URLEncoder.encode(tagToSearch, "UTF-8")
                    navController.navigate("search?tag=$e") {
                        launchSingleTop = true
                        restoreState     = true
                    }
                }
            )
        }

        composable(
            route = "comments/{postId}?searchTag={searchTag}",
            arguments = listOf(
                navArgument("postId") { type = NavType.LongType },
                navArgument("searchTag") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getLong("postId") ?: 0
            val searchTag = backStackEntry.arguments?.getString("searchTag") ?: ""

            CommentsScreen(
                postId = postId,
                onClose = {
                    if (searchTag.isNotEmpty()) {
                        val decodedTag = java.net.URLDecoder.decode(
                            searchTag,
                            "UTF-8"
                        )
                        val targetRoute = "search?tag=$decodedTag"
                        if (navController.popBackStack(targetRoute, /* inclusive = */ false)) {
                            // успешно вернулись
                        } else {
                            navController.popBackStack()
                        }
                    } else {
                        navController.popBackStack()
                    }
                },
                onProfileClick = { authorId ->
                    navController.navigate("author/$authorId")
                },
                postViewModel = sharedPostViewModel
            )
        }

        composable(
            route = "author/{authorId}",
            arguments = listOf(navArgument("authorId") { type = NavType.StringType })
        ) { backStackEntry ->
            val authorId = backStackEntry.arguments?.getString("authorId") ?: ""

            val authorProfileViewModel: ProfileViewModel = viewModel()

            LaunchedEffect(authorId) {
                try {
                    val userId = authorId.toLongOrNull()
                    if (userId != null) {
                        authorProfileViewModel.clearData()
                        authorProfileViewModel.fetchUserProfileById(userId)
                    } else {
                        navController.popBackStack()
                    }
                } catch (e: Exception) {
                    android.util.Log.e("AuthorProfile", "Error loading author profile", e)
                    navController.popBackStack()
                }
            }

            ProfileScreen(
                viewModel = authorProfileViewModel,
                isOwnProfile = false,
                onHomeClick = {
                    navController.navigate("main") {
                        popUpTo("main") { inclusive = true }
                    }
                },
                onSavedClick = { },
                onMessagesClick = { },
                onReportClick = {
                    // TODO: Реализовать жалобу на пользователя
                },
                onSettingsClick = { },
                currentScreen = "",
                navbarCallbacks = null,
                onCloseOverlay = { navController.popBackStack() },
                onEditProfileClick = { },
                onCommentsClick = { postId ->
                    navController.navigate("comments/$postId")
                },
                sharedPostViewModel = sharedPostViewModel,
                onAuthorClick = {
                    navController.navigate("author/$authorId")
                },
                onSubscriptionChanged = { targetAuthorId, isSubscribed ->
                    val currentUserIdInt = sharedPostViewModel.currentUserId.value?.toIntOrNull()
                    if (currentUserIdInt != null) {
                        sharedPostViewModel.updateAuthorSubscriptionState(
                            authorId = targetAuthorId,
                            currentUserId = currentUserIdInt,
                            isSubscribed = isSubscribed
                        )
                    }
                },
            )
        }
    }
}

@Composable
fun MainScreenWithOverlays(
    navController: NavHostController,
    sharedPostViewModel: PostViewModel,
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
            onSavedPostsClick = { navController.navigate("savedPosts") },
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

    },
    onMessagesClick = { navController.navigate("messages") },
    onProfileClick = { navController.navigate("profile") }
)

data class NavbarCallbacks(
    val onHomeClick: () -> Unit,
    val onSavedClick: () -> Unit,
    val onCreateArticleClick: () -> Unit,
    val onMessagesClick: () -> Unit,
    val onProfileClick: () -> Unit,
)
