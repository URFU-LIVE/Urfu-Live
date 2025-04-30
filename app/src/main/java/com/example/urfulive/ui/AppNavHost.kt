import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


@OptIn(ExperimentalMaterialApi::class)
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
                }
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
            },)
        }

        composable("main") {
            CarouselScreen()}
    }
}
