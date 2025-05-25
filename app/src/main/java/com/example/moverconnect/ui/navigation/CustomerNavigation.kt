package com.example.moverconnect.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.moverconnect.ui.screens.customer.*
import androidx.compose.ui.platform.LocalContext

@Composable
fun CustomerNavigation(
    navController: NavHostController,
    startDestination: String = CustomerBottomNavItem.Home.route
) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(CustomerBottomNavItem.Home.route) {
            HomeScreen(
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(CustomerBottomNavItem.Home.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable(CustomerBottomNavItem.Bookings.route) {
            BookingsScreen(
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(CustomerBottomNavItem.Bookings.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                context = context
            )
        }

        composable(CustomerBottomNavItem.Messages.route) {
            MessagesScreen(
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(CustomerBottomNavItem.Messages.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                context = context
            )
        }

        composable(CustomerBottomNavItem.Profile.route) {
            ProfileScreen(
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(CustomerBottomNavItem.Profile.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        // Edit Profile Screen
        composable("edit_profile") {
            EditProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
} 