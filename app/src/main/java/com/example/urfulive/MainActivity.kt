package com.example.urfulive

import AppNavHost
import TokenManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.example.urfulive.ui.theme.UrfuLiveTheme
import androidx.core.view.WindowCompat


class MainActivity : ComponentActivity() {
    private lateinit var tokenManager: TokenManager
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TokenManagerInstance.initialize(applicationContext)
        tokenManager = TokenManager(applicationContext)

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