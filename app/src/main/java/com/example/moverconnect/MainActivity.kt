package com.example.moverconnect

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.moverconnect.navigation.Screen
import com.example.moverconnect.navigation.UserType
import com.example.moverconnect.ui.screens.*
import com.example.moverconnect.ui.theme.MoverConnectTheme
import com.example.moverconnect.ui.screens.driver.DriverDashboardScreen
import com.example.moverconnect.ui.screens.driver.DriverProfileSetupScreen
import com.example.moverconnect.ui.screens.driver.BrowseRequestsScreen
import com.example.moverconnect.ui.screens.driver.RequestDetailScreen
import com.example.moverconnect.SessionManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoverConnectTheme {
                val navController = rememberNavController()
                var selectedUserType by remember { mutableStateOf<UserType?>(null) }

                // Check if we should navigate directly to login
                val destination = intent.getStringExtra("destination")
                LaunchedEffect(destination) {
                    if (destination == Screen.Login.route) {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = Screen.Splash.route
                ) {
                    composable(Screen.Splash.route) {
                        SplashScreen(
                            onSplashFinished = {
                                val context = this@MainActivity
                                if (SessionManager.isLoggedIn(context)) {
                                    when (SessionManager.getUserType(context)) {
                                        "customer" -> {
                                            navController.navigate(Screen.Dashboard.route) {
                                                popUpTo(Screen.Splash.route) { 
                                                    inclusive = true 
                                                }
                                                launchSingleTop = true
                                            }
                                        }
                                        "driver" -> {
                                            navController.navigate(Screen.DriverDashboard.route) {
                                                popUpTo(Screen.Splash.route) { 
                                                    inclusive = true 
                                                }
                                                launchSingleTop = true
                                            }
                                        }
                                        else -> {
                                            navController.navigate(Screen.Welcome.route) {
                                                popUpTo(Screen.Splash.route) { 
                                                    inclusive = true 
                                                }
                                                launchSingleTop = true
                                            }
                                        }
                                    }
                                } else {
                                    navController.navigate(Screen.Welcome.route) {
                                        popUpTo(Screen.Splash.route) { 
                                            inclusive = true 
                                        }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }

                    composable(Screen.Welcome.route) {
                        WelcomeScreen(
                            onGetStarted = {
                                navController.navigate(Screen.UserType.route)
                            },
                            onLogin = {
                                navController.navigate(Screen.Login.route)
                            }
                        )
                    }

                    composable(Screen.UserType.route) {
                        UserTypeScreen(
                            onUserTypeSelected = { type ->
                                selectedUserType = type
                            },
                            onContinue = {
                                selectedUserType?.let {
                                    navController.navigate(Screen.Register.route)
                                }
                            }
                        )
                    }

                    composable(Screen.Register.route) {
                        RegisterScreen(
                            userType = selectedUserType ?: UserType.Customer,
                            onRegister = {
                                // Show success message and navigate to login
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(Screen.Welcome.route) { inclusive = true }
                                }
                            },
                            onLogin = {
                                navController.navigate(Screen.Login.route)
                            }
                        )
                    }

                    composable(Screen.Login.route) {
                        LoginScreen(
                            onLogin = { email, password ->
                                // The actual login is handled in LoginScreen with Firebase
                                // Here we just handle navigation based on user type
                                when (SessionManager.getUserType(this@MainActivity)) {
                                    "customer" -> {
                                        navController.navigate(Screen.Dashboard.route) {
                                            popUpTo(Screen.Splash.route) { inclusive = true }
                                        }
                                    }
                                    "driver" -> {
                                        navController.navigate(Screen.DriverDashboard.route) {
                                            popUpTo(Screen.Splash.route) { inclusive = true }
                                        }
                                    }
                                    else -> {
                                        // This shouldn't happen, but just in case
                                        navController.navigate(Screen.Welcome.route) {
                                            popUpTo(Screen.Splash.route) { inclusive = true }
                                        }
                                    }
                                }
                            },
                            onRegister = {
                                navController.navigate(Screen.UserType.route)
                            },
                            onForgotPassword = {
                                navController.navigate(Screen.ForgotPassword.route)
                            }
                        )
                    }

                    composable(Screen.ForgotPassword.route) {
                        ForgotPasswordScreen(
                            onBackToLogin = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(Screen.Dashboard.route) {
                        CustomerDashboard()
                    }

                    // DRIVER FLOW
                    composable(Screen.DriverDashboard.route) {
                        DriverDashboardScreen(
                            onProfileClick = { navController.navigate(Screen.DriverProfileSetup.route) },
                            onBrowseRequests = { navController.navigate(Screen.BrowseRequests.route) },
                            onLogout = {
                                SessionManager.logout(this@MainActivity)
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(Screen.Splash.route) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(Screen.DriverProfileSetup.route) {
                        DriverProfileSetupScreen(onSave = { navController.popBackStack() })
                    }
                    composable(Screen.BrowseRequests.route) {
                        BrowseRequestsScreen(
                            onRequestClick = { requestId ->
                                navController.navigate(Screen.RequestDetail.createRoute(requestId))
                            }
                        )
                    }
                    composable(
                        route = Screen.RequestDetail.route,
                        arguments = listOf(navArgument("requestId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val requestId = backStackEntry.arguments?.getInt("requestId") ?: 0
                        RequestDetailScreen(
                            requestId = requestId,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}