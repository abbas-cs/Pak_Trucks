package com.example.moverconnect.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Welcome : Screen("welcome")
    object UserType : Screen("user_type")
    object Register : Screen("register")
    object Login : Screen("login")
    object ForgotPassword : Screen("forgot_password")
    object Dashboard : Screen("dashboard")
    object DriverDashboard : Screen("driver_dashboard")
    object DriverProfileSetup : Screen("driver_profile_setup")
    object BrowseRequests : Screen("browse_requests")
    object RequestDetail : Screen("request_detail/{requestId}") {
        fun createRoute(requestId: Int) = "request_detail/$requestId"
    }
}

sealed class UserType {
    object Customer : UserType()
    object Driver : UserType()
} 