package com.example.moverconnect

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.moverconnect.ui.navigation.CustomerNavigation
import com.example.moverconnect.ui.theme.MoverConnectTheme

@Composable
fun CustomerDashboard() {
    val navController = rememberNavController()
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
    CustomerNavigation(navController = navController)
    }
} 