package com.example.moverconnect

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.moverconnect.ui.screens.LoginScreen
import com.example.moverconnect.ui.screens.customer.CustomerMainScreen
import com.example.moverconnect.ui.screens.driver.DriverMainScreen
import com.example.moverconnect.ui.theme.MoverConnectTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoverConnectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    val db = FirebaseFirestore.getInstance()
                    var userType by remember { mutableStateOf("customer") }
                    val scope = rememberCoroutineScope()

                    // Fetch user type
                    LaunchedEffect(currentUser) {
                        try {
                            currentUser?.let { user ->
                                val userDoc = db.collection("users").document(user.uid).get().await()
                                userType = userDoc.getString("userType") ?: "customer"
                                Log.d(TAG, "User type fetched: $userType")
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error fetching user type", e)
                            userType = "customer"
                        }
                    }

                    NavHost(
                        navController = navController,
                        startDestination = if (currentUser != null) "main" else "login"
                    ) {
                        composable("login") {
                            LoginScreen(
                                onLogin = { email, password ->
                                    scope.launch {
                                        try {
                                            // Handle login
                                            navController.navigate("main") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        } catch (e: Exception) {
                                            Log.e(TAG, "Login error", e)
                                        }
                                    }
                                },
                                onRegister = {
                                    // Handle register navigation
                                },
                                onForgotPassword = {
                                    // Handle forgot password
                                }
                            )
                        }
                        composable("main") {
                            if (userType == "driver") {
                                DriverMainScreen(
                                    onLogout = {
                                        scope.launch {
                                            try {
                                                FirebaseAuth.getInstance().signOut()
                                                navController.navigate("login") {
                                                    popUpTo("main") { inclusive = true }
                                                }
                                            } catch (e: Exception) {
                                                Log.e(TAG, "Logout error", e)
                                            }
                                        }
                                    }
                                )
                            } else {
                                CustomerMainScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}