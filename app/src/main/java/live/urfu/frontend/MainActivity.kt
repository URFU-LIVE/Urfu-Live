package live.urfu.frontend

import AppNavHost
import live.urfu.frontend.data.manager.TokenManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import live.urfu.frontend.ui.theme.UrfuLiveTheme
import androidx.core.view.WindowCompat
import live.urfu.frontend.data.manager.InterestManager
import live.urfu.frontend.data.manager.InterestManagerInstance
import live.urfu.frontend.data.manager.PostManager
import live.urfu.frontend.data.manager.PostManagerInstance
import live.urfu.frontend.data.manager.TokenManagerInstance


class MainActivity : ComponentActivity() {
    private lateinit var tokenManager: TokenManager
    private lateinit var interestManager: InterestManager
    private lateinit var postsManager: PostManager
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TokenManagerInstance.initialize(applicationContext)
        tokenManager = TokenManager(applicationContext)

        InterestManagerInstance.initialize(applicationContext)
        interestManager = InterestManager(applicationContext)

        PostManagerInstance.initialize(applicationContext)
        postsManager = PostManager(applicationContext)


        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        window.isNavigationBarContrastEnforced = false // КОНТРАСТНОСТЬ НА ТЕЛЕФОНАХ SAMSUNG
        setContent {
            UrfuLiveTheme {
                // Показываем сразу экран регистрации
                AppNavHost()
            }
        }
    }
}