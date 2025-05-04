import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.urfulive.ui.settings.SettingsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost() {
    // Создаем контроллер навигации
    val navController: NavHostController = rememberNavController()

    // Определяем NavHost с указанием стартового экрана
    NavHost(
        navController = navController,
        startDestination = "login" // например, экран входа
    ) {
        // Определяем маршрут для экрана входа
        composable("login") {
            // Передаем callback для перехода на регистрацию
            LoginScreen(
                onRegisterClick = {
                    navController.navigate("registration")
                },
                onLoginClick = {
                    navController.navigate("main") {
                        // Clear back stack when going to main
                        popUpTo("login") { inclusive = true }
                    }
                },
                onLoginSuccess = {},
                onLoginError = {},
                onRestorePasswordClick = {
                    navController.navigate("main")
                }
            )
        }

        composable("registration") {
            RegistrationScreen(
                onLogoClick = {
                    // Переход обратно на экран входа при клике на логотип
                    navController.popBackStack()
                },
                onRegisterClick = {
                    navController.navigate("interests")
                },
                onRegisterSuccess = {},
                onRegisterError = {}
            )
        }

        composable("interests") {
            InterestsScreen(
                onLogoClick = {
                    navController.popBackStack()
                },
                onNextClick = {
                    navController.navigate("main") {
                        // Clear back stack when going to main
                        popUpTo("login") { inclusive = true }
                    }
                },
                onSkipClick = {
                    navController.navigate("main") {
                        // Clear back stack when going to main
                        popUpTo("login") { inclusive = true }
                    }
                },
            )
        }

        composable("main") {
            MainScreenWithOverlays(navController)
        }

        composable("settings") {
            SettingsScreen(
                onCloseOverlay = {navController.navigate("profile")}
            )
        }

        // Own profile route
        composable("profile") {
            ProfileScreen(
                onHomeClick = {
                    navController.navigate("main") {
                        popUpTo("main") { inclusive = true }
                    }
                },
                // Add navbar callbacks for user's own profile
                navbarCallbacks = NavbarCallbacks(
                    onHomeClick = {
                        navController.navigate("main") {
                            popUpTo("main") { inclusive = true }
                        }
                    },
                    onSavedClick = {
                        // Navigate to saved articles
                        navController.navigate("saved")
                    },
                    onCreateArticleClick = {

                    },
                    onMessagesClick = {
                        // Navigate to messages
                        navController.navigate("messages")
                    },
                    onProfileClick = {
                        // Already on profile, do nothing
                    }
                ),
                currentScreen = "profile",
                onSettingsClick = {navController.navigate("settings")}
            )
        }
    }
}

@Composable
fun MainScreenWithOverlays(navController: NavHostController) {
    // State to track author profile overlay visibility
    val showAuthorProfile = remember { mutableStateOf(false) }
    val authorUsername = remember { mutableStateOf("") }

    // Main content
    Box(modifier = Modifier.fillMaxSize()) {
        CarouselScreen(
            onProfileClick = {
                // Navigate to own profile via navbar
                navController.navigate("profile")
            },
            // Now instead of navigating, we show the overlay
            onAuthorClick = { authorName ->
                authorUsername.value = authorName
                showAuthorProfile.value = true
            },
            navController = navController,
            // Hide navbar when an article is expanded
            showNavBar = true  // This will be managed inside CarouselScreen
        )

        // Conditionally show author profile overlay
        if (showAuthorProfile.value) {
            // Semi-transparent background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable {
                        // Close on background click
                        showAuthorProfile.value = false
                    }
            )

            // Profile content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(enabled = false) { /* Prevent click through */ }
            ) {
                ProfileScreen(
                    userName = authorUsername.value,
                    isOwnProfile = false,
                    onHomeClick = {
                        showAuthorProfile.value = false
                        navController.navigate("main") {
                            popUpTo("main") { inclusive = true }
                        }
                    },
                    navbarCallbacks = NavbarCallbacks(
                        onHomeClick = {
                            showAuthorProfile.value = false
                            navController.navigate("main") {
                                popUpTo("main") { inclusive = true }
                            }
                        },
                        onSavedClick = {
                            showAuthorProfile.value = false
                            navController.navigate("saved")
                        },
                        onCreateArticleClick = {

                        },
                        onMessagesClick = {
                            showAuthorProfile.value = false
                            navController.navigate("messages")
                        },
                        onProfileClick = {
                            showAuthorProfile.value = false
                            navController.navigate("profile")
                        }
                    ),
                    currentScreen = "profile",
                    onCloseOverlay = {
                        showAuthorProfile.value = false
                    }
                )
            }
        }
    }
}

// Data class to hold navbar callback functions
data class NavbarCallbacks(
    val onHomeClick: () -> Unit,
    val onSavedClick: () -> Unit,
    val onCreateArticleClick: () -> Unit,
    val onMessagesClick: () -> Unit,
    val onProfileClick: () -> Unit
)