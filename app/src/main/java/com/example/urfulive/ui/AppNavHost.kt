import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.urfulive.ui.createarticle.CreateArticle
import com.example.urfulive.ui.profile.Profile


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
                    navController.navigate("main")
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
                    navController.navigate("login")
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
                    navController.navigate("login")
                },
                onNextClick = {
                    navController.navigate("main")
                },
                onSkipClick = {
                    navController.navigate("main")
                },
            )
        }

        composable("main") {
            CarouselScreen(
                onProfileClick = {
                    navController.navigate("profile")
                }
//                createArticle = {
//                    navController.navigate("createarticle")
//                },
            )
        }

        composable("profile") {
            Profile()
        }

//        composable("createarticle") {
//            CreateArticle()}
    }
}

