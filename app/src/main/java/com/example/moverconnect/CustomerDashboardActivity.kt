package com.example.moverconnect

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moverconnect.ui.theme.MoverConnectTheme
import com.example.moverconnect.ui.screens.customer.CustomerHomeScreen
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import com.example.moverconnect.navigation.Screen

class CustomerDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoverConnectTheme {
                var drawerOpen by remember { mutableStateOf(false) }
                var showLogoutDialog by remember { mutableStateOf(false) }
                val activity = this@CustomerDashboardActivity
                val scope = rememberCoroutineScope()
                Box(Modifier.fillMaxSize()) {
                    CustomerHomeScreen()
                    IconButton(
                        onClick = { drawerOpen = true },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                    if (drawerOpen) {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.32f))
                                .clickable { drawerOpen = false }
                        ) {}
                        Box(
                            Modifier
                                .fillMaxHeight()
                                .width(300.dp)
                                .align(Alignment.CenterEnd)
                                .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.large)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Spacer(Modifier.height(32.dp))
                                    Text(
                                        text = "Menu",
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )
                                    // Add more drawer items here if needed
                                }
                                Button(
                                    onClick = { showLogoutDialog = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Icon(Icons.Default.Logout, contentDescription = "Logout")
                                    Spacer(Modifier.width(8.dp))
                                    Text("Logout")
                                }
                            }
                        }
                    }
                    if (showLogoutDialog) {
                        AlertDialog(
                            onDismissRequest = { showLogoutDialog = false },
                            title = { Text("Logout") },
                            text = { Text("Are you sure you want to logout?") },
                            confirmButton = {
                                TextButton(onClick = {
                                    showLogoutDialog = false
                                    SessionManager.logout(activity)
                                    val intent = Intent(activity, MainActivity::class.java).apply {
                                        putExtra("destination", Screen.Login.route)
                                    }
                                    activity.startActivity(intent)
                                    activity.finish()
                                }) {
                                    Text("Yes")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showLogoutDialog = false }) {
                                    Text("No")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDashboardScreen(
    onProfileClick: () -> Unit,
    onCreateMoveRequest: () -> Unit,
    onBrowseDrivers: () -> Unit,
    onMyRequests: () -> Unit
) {
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("Home", "Requests", "Profile", "Settings")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = when (index) {
                                    0 -> Icons.Default.Home
                                    1 -> Icons.Default.List
                                    2 -> Icons.Default.Person
                                    else -> Icons.Default.Settings
                                },
                                contentDescription = item
                            )
                        },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index }
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = onProfileClick
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "John Doe",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tap to edit profile",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Dashboard Actions
            DashboardActionCard(
                title = "Create Move Request",
                icon = Icons.Default.Add,
                onClick = onCreateMoveRequest
            )

            DashboardActionCard(
                title = "Browse Drivers",
                icon = Icons.Default.Search,
                onClick = onBrowseDrivers
            )

            DashboardActionCard(
                title = "My Requests",
                icon = Icons.Default.List,
                onClick = onMyRequests
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardActionCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
} 