package com.example.moverconnect.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Welcome : Screen("welcome")
    object UserType : Screen("user_type")
    object Register : Screen("register")
    object Login : Screen("login")
    object ForgotPassword : Screen("forgot_password")
}

sealed class UserType {
    object Customer : UserType()
    object Driver : UserType()
} 