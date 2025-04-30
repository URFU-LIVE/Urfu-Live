//package com.example.urfulive.di
//
//import android.content.Context
//import com.example.urfulive.data.api.UserApiService
//import com.example.urfulive.data.auth.UserSession
//import com.example.urfulive.data.repository.UserRepository
//
///**
// * Контейнер для Dependency Injection
// */
//class AppContainer(private val applicationContext: Context) {
//
//    // API Services
//    val userApiService: UserApiService by lazy {
//        UserApiService()
//    }
//
//    // Repositories
//    val userRepository: UserRepository by lazy {
//        UserRepository(applicationContext)
//    }
//
//    // Session
//    val userSession: UserSession by lazy {
//        UserSession(applicationContext)
//    }
//
//    companion object {
//        @Volatile
//        private var INSTANCE: AppContainer? = null
//
//        fun getInstance(context: Context): AppContainer {
//            return INSTANCE ?: synchronized(this) {
//                INSTANCE ?: AppContainer(context.applicationContext).also { INSTANCE = it }
//            }
//        }
//    }
//}
//
///**
// * Расширение для Application класса
// */
//val Context.appContainer: AppContainer
//    get() = AppContainer.getInstance(this)