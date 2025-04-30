//package com.example.urfulive.util
//
//import LoginViewModel
//import LoginScreen
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.urfulive.data.auth.UserSession
//import com.example.urfulive.data.model.UserRole
//import com.example.urfulive.ui.login.LoginViewModel
//import com.example.urfulive.ui.login.LoginUiState
//
///**
// * Класс с утилитами для проверки прав пользователя
// */
//object PermissionUtils {
//
//    /**
//     * Проверяет, имеет ли пользователь указанную роль или выше
//     */
//    fun hasRole(session: UserSession, requiredRole: UserRole): Boolean {
//        return when (requiredRole) {
//            UserRole.USER -> true // Все пользователи имеют как минимум роль USER
//            UserRole.WRITER -> session.hasModeratorRights() || session.hasAdminRights()
//            UserRole.ADMIN -> session.hasAdminRights()
//        }
//    }
//
//    /**
//     * Composable-функция для проверки, имеет ли текущий пользователь доступ к функциональности
//     */
//    @Composable
//    fun HasPermission(
//        requiredRole: UserRole,
//        loginViewModel: LoginViewModel = viewModel(),
//        content: @Composable () -> Unit,
//        fallback: @Composable () -> Unit = {}
//    ) {
//        val loginState by loginViewModel.loginState.collectAsState()
//
//        when (loginState) {
//            is LoginUiState.LoggedIn -> {
//                val user = (loginState as LoginUiState.LoggedIn).user
//                if (hasPermissionForRole(user.role, requiredRole)) {
//                    content()
//                } else {
//                    fallback()
//                }
//            }
//            else -> fallback()
//        }
//    }
//
//    /**
//     * Проверяет, удовлетворяет ли роль пользователя требуемой роли
//     */
//    private fun hasPermissionForRole(userRole: UserRole, requiredRole: UserRole): Boolean {
//        return when (requiredRole) {
//            UserRole.USER -> true
//            UserRole.WRITER -> userRole == UserRole.WRITER || userRole == UserRole.ADMIN
//            UserRole.ADMIN -> userRole == UserRole.ADMIN
//        }
//    }
//}