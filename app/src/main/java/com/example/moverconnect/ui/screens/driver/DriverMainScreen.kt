package com.example.moverconnect.ui.screens.driver

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun DriverMainScreen(
    navController: NavHostController = rememberNavController(),
    onLogout: () -> Unit = {}
) {
    val TAG = "DriverMainScreen"
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route ?: "driver_home"
    val scope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            DriverBottomNav(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    scope.launch {
                        try {
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Navigation error", e)
                        }
                    }
                }
            )
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "driver_home",
            modifier = Modifier.padding(padding)
        ) {
            composable("driver_home") {
                DriverDashboardScreen(
                    onProfileClick = { 
                        scope.launch {
                            try {
                                navController.navigate("driver_profile")
                            } catch (e: Exception) {
                                Log.e(TAG, "Profile navigation error", e)
                            }
                        }
                    },
                    onBrowseRequests = { 
                        scope.launch {
                            try {
                                navController.navigate("driver_requests")
                            } catch (e: Exception) {
                                Log.e(TAG, "Requests navigation error", e)
                            }
                        }
                    },
                    onLogout = {
                        scope.launch {
                            try {
                                FirebaseAuth.getInstance().signOut()
                                onLogout()
                            } catch (e: Exception) {
                                Log.e(TAG, "Logout error", e)
                            }
                        }
                    }
                )
            }
            composable("driver_requests") {
                BrowseRequestsScreen(
                    onRequestClick = { requestId ->
                        scope.launch {
                            try {
                                navController.navigate("request_detail/$requestId")
                            } catch (e: Exception) {
                                Log.e(TAG, "Request detail navigation error", e)
                            }
                        }
                    }
                )
            }
            composable("driver_profile") {
                DriverProfileSetupScreen(
                    onSave = { 
                        scope.launch {
                            try {
                                navController.popBackStack()
                            } catch (e: Exception) {
                                Log.e(TAG, "Profile save navigation error", e)
                            }
                        }
                    }
                )
            }
            composable("request_detail/{requestId}") { backStackEntry ->
                val requestId = backStackEntry.arguments?.getString("requestId")?.toIntOrNull() ?: 0
                RequestDetailScreen(
                    requestId = requestId,
                    onBack = { 
                        scope.launch {
                            try {
                                navController.popBackStack()
                            } catch (e: Exception) {
                                Log.e(TAG, "Request detail back navigation error", e)
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun DriverBottomNav(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute == "driver_home",
            onClick = { onNavigate("driver_home") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.List, contentDescription = "Requests") },
            label = { Text("Requests") },
            selected = currentRoute == "driver_requests",
            onClick = { onNavigate("driver_requests") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = currentRoute == "driver_profile",
            onClick = { onNavigate("driver_profile") }
        )
    }
} 