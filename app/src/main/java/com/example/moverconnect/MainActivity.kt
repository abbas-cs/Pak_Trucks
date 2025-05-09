package com.example.moverconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.moverconnect.navigation.Screen
import com.example.moverconnect.navigation.UserType
import com.example.moverconnect.ui.screens.*
import com.example.moverconnect.ui.theme.MoverConnectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoverConnectTheme {
                val navController = rememberNavController()
                var selectedUserType by remember { mutableStateOf<UserType?>(null) }

                NavHost(
                    navController = navController,
                    startDestination = Screen.Splash.route
                ) {
                    composable(Screen.Splash.route) {
                        SplashScreen(
                            onSplashFinished = {
                                navController.navigate(Screen.Welcome.route) {
                                    popUpTo(Screen.Splash.route) { inclusive = true }
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
                            onLogin = {
                                // Show success message and navigate to main app
                                // For now, just show a success message
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
                }
            }
        }
    }
}