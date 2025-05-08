import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.urfulive.ui.comments.Comments
import com.example.urfulive.ui.profile.EditProfile
import com.example.urfulive.ui.profile.ProfileScreen
import com.example.urfulive.ui.profile.ProfileViewModel
import com.example.urfulive.ui.settings.account.AccountSettings
import com.example.urfulive.ui.settings.notification.NotificationsSettings
import com.example.urfulive.ui.settings.privacy.PrivacySettings
import com.example.urfulive.ui.settings.SettingsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost() {
    // Создаем контроллер навигации
    val navController: NavHostController = rememberNavController()

    // Create a shared ViewModel instance for the profile to prevent recreation
    val profileViewModel: ProfileViewModel = viewModel()

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
                onLoginSuccess = {navController.navigate("main"){
                    // Clear back stack when going to main
                    popUpTo("login") { inclusive = true }
                }},
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
            // Продолжаем использовать MainScreenWithOverlays, но теперь он будет
            // навигировать на профиль автора вместо показа оверлея
            MainScreenWithOverlays(
                navController = navController,
                profileViewModel = profileViewModel
            )
        }

        composable("settings") {
            SettingsScreen(
                onCloseOverlay = {navController.navigate("profile")},
                onAccountClick = {navController.navigate("accountSettings")},
                onNotificationsClick = {navController.navigate("notificationsSettings")},
                onPrivacyClick = {navController.navigate("privacySettings")},
                onHomeClick = {navController.navigate("main") {
                    popUpTo("main") { inclusive = true }
                }},
                navbarCallbacks = NavbarCallbacks(
                    onHomeClick = {navController.navigate("main") {
                        popUpTo("main") { inclusive = true }
                    }},
                    onSavedClick = {
                        // Navigate to saved articles
                        navController.navigate("saved")
                    },
                    onCreateArticleClick = {
                        // Handle creation in the ProfileScreen
                    },
                    onMessagesClick = {
                        // Navigate to messages
                        navController.navigate("messages")
                    },
                    onProfileClick = {
                        // Already on profile, do nothing
                    }
                )
            )
        }

        composable("accountSettings") {
            AccountSettings(
                onClose = {navController.navigate("settings")},
                onHomeClick = {navController.navigate("main") {
                    popUpTo("main") { inclusive = true }
                }},
                navbarCallbacks = NavbarCallbacks(
                    onHomeClick = {navController.navigate("main") {
                        popUpTo("main") { inclusive = true }
                    }},
                    onSavedClick = {
                        // Navigate to saved articles
                        navController.navigate("saved")
                    },
                    onCreateArticleClick = {
                        // Handle creation in the ProfileScreen
                    },
                    onMessagesClick = {
                        // Navigate to messages
                        navController.navigate("messages")
                    },
                    onProfileClick = {
                        // Already on profile, do nothing
                    }
                )
            )
        }

        composable("notificationsSettings") {
            NotificationsSettings(
                onClose = {navController.navigate("settings")},
                onHomeClick = {navController.navigate("main") {
                    popUpTo("main") { inclusive = true }
                }},
                navbarCallbacks = NavbarCallbacks(
                    onHomeClick = {navController.navigate("main") {
                        popUpTo("main") { inclusive = true }
                    }},
                    onSavedClick = {
                        // Navigate to saved articles
                        navController.navigate("saved")
                    },
                    onCreateArticleClick = {
                        // Handle creation in the ProfileScreen
                    },
                    onMessagesClick = {
                        // Navigate to messages
                        navController.navigate("messages")
                    },
                    onProfileClick = {
                        // Already on profile, do nothing
                    }
                )
            )
        }

        composable("privacySettings") {
            PrivacySettings(
                onClose = {navController.navigate("settings")},
                onHomeClick = {navController.navigate("main") {
                    popUpTo("main") { inclusive = true }
                }},
                navbarCallbacks = NavbarCallbacks(
                    onHomeClick = {navController.navigate("main") {
                        popUpTo("main") { inclusive = true }
                    }},
                    onSavedClick = {
                        // Navigate to saved articles
                        navController.navigate("saved")
                    },
                    onCreateArticleClick = {
                        // Handle creation in the ProfileScreen
                    },
                    onMessagesClick = {
                        // Navigate to messages
                        navController.navigate("messages")
                    },
                    onProfileClick = {
                        // Already on profile, do nothing
                    }
                )
            )
        }
        composable("editProfile") {
            EditProfile(
                onClose = {navController.navigate("profile")},
                onHomeClick = {navController.navigate("main") {
                    popUpTo("main") { inclusive = true }
                }},
                navbarCallbacks = NavbarCallbacks(
                    onHomeClick = {navController.navigate("main") {
                        popUpTo("main") { inclusive = true }
                    }},
                    onSavedClick = {
                        // Navigate to saved articles
                        navController.navigate("saved")
                    },
                    onCreateArticleClick = {
                        // Handle creation in the ProfileScreen
                    },
                    onMessagesClick = {
                        // Navigate to messages
                        navController.navigate("messages")
                    },
                    onProfileClick = {
                        // Already on profile, do nothing
                    }
                )
            )
        }

        composable("comments") {
            Comments(
                onClose = {navController.navigate("main")}
            )
        }

        // Own profile route
        composable("profile") {
            // При переходе на собственный профиль загружаем данные текущего пользователя
            LaunchedEffect(Unit) {
                profileViewModel.fetchProfile()
            }

            ProfileScreen(
                viewModel = profileViewModel, // Pass the shared ViewModel
                onHomeClick = {
                    navController.navigate("main") {
                        popUpTo("main") { inclusive = true }
                    }
                },
                onEditProfileClick = {navController.navigate("editProfile")},
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
                        // Handle creation in the ProfileScreen
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
                onSettingsClick = {navController.navigate("settings")},
                isOwnProfile = true
            )
        }

        // Add author profile route with userId parameter
        composable(
            route = "author/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.LongType })
        ) { backStackEntry ->
            // Get the userId parameter from the route
            val userId = backStackEntry.arguments?.getLong("userId") ?: return@composable

            // Create a separate ViewModel for author profiles
            val authorProfileViewModel: ProfileViewModel = viewModel()

            // Load author profile data when this screen is shown
            LaunchedEffect(userId) {
                // Clear previous data
                authorProfileViewModel.clearData()
                // Load new data
                authorProfileViewModel.fetchUserProfileById(userId)
            }

            ProfileScreen(
                viewModel = authorProfileViewModel,
                isOwnProfile = false,
                onHomeClick = {
                    navController.navigate("main") {
                        popUpTo("main") { inclusive = true }
                    }
                },
                navbarCallbacks = NavbarCallbacks(
                    onHomeClick = {
                        navController.navigate("main") {
                            popUpTo("main") { inclusive = true }
                        }
                    },
                    onSavedClick = {
                        navController.navigate("saved")
                    },
                    onCreateArticleClick = {
                        // Handle in ProfileScreen
                    },
                    onMessagesClick = {
                        navController.navigate("messages")
                    },
                    onProfileClick = {
                        navController.navigate("profile")
                    }
                ),
                currentScreen = "author",
                onCloseOverlay = {
                    navController.popBackStack()
                }
            )
        }
    }
}
@Composable
fun MainScreenWithOverlays(
    navController: NavHostController,
    profileViewModel: ProfileViewModel // Принимаем общий ViewModel для профиля
) {
    Box(modifier = Modifier.fillMaxSize()) {
        CarouselScreen(
            onProfileClick = {
                // Навигация на собственный профиль через навбар
                navController.navigate("profile")
            },
            // Изменяем обработку нажатия на автора - теперь навигируем на отдельный экран
            onAuthorClick = { authorId ->
                // Навигация на профиль автора с передачей ID
                navController.navigate("author/$authorId")
            },
            navController = navController,
            showNavBar = true,
            onCommentsClick = {navController.navigate("comments")}
        )

    }
}
data class NavbarCallbacks(
    val onHomeClick: () -> Unit,
    val onSavedClick: () -> Unit,
    val onCreateArticleClick: () -> Unit,
    val onMessagesClick: () -> Unit,
    val onProfileClick: () -> Unit
)