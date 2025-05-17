package com.example.moverconnect.ui.screens.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.example.moverconnect.ui.screens.driver.StatusBadge
import com.example.moverconnect.ui.screens.driver.DriverProfileViewScreen
import kotlinx.coroutines.launch
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import com.example.moverconnect.MainActivity
import com.example.moverconnect.SessionManager
import com.example.moverconnect.navigation.Screen

// Dummy data for move requests
private val mockRequests = listOf(
    MoveRequest(1, "Alice Smith", "Downtown", "Uptown", "2024-06-10 10:00", "Large", "$120", "Pending"),
    MoveRequest(2, "Bob Lee", "Eastside", "Westside", "2024-06-12 14:00", "Medium", "$90", "Active"),
    MoveRequest(3, "Carol King", "North Ave", "South Blvd", "2024-06-15 09:00", "Small", "$60", "Pending"),
    MoveRequest(4, "David Park", "Central Park", "Harbor", "2024-06-18 16:00", "Large", "$150", "Active")
)

// Dummy driver stats
private val driverStats = DriverStats(completedMoves = 12, rating = 4.8f)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverDashboardScreen(
    onProfileClick: () -> Unit,
    onBrowseRequests: () -> Unit,
    onRequestClick: (Int) -> Unit = {},
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    val navItems = listOf(
        NavBarItem("Requests", Icons.Default.List),
        NavBarItem("Search", Icons.Default.Search),
        NavBarItem("Ratings", Icons.Default.Star),
        NavBarItem("Profile", Icons.Default.Person)
    )
    var drawerOpen by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(navItems[selectedTab].label) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    actions = {
                        IconButton(onClick = { drawerOpen = true }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    navItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = selectedTab == index,
                            onClick = { selectedTab = index }
                        )
                    }
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                when (selectedTab) {
                    0 -> RequestsTab(onRequestClick)
                    1 -> SearchTab()
                    2 -> RatingsTab()
                    3 -> ProfileTab(onProfileClick)
                }
            }
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
                        SessionManager.logout(context)
                        val intent = Intent(context, MainActivity::class.java).apply {
                            putExtra("destination", Screen.Login.route)
                        }
                        context.startActivity(intent)
                        (context as? android.app.Activity)?.finish()
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

// Navigation bar item data class
private data class NavBarItem(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

// Requests Tab
@Composable
fun RequestsTab(onRequestClick: (Int) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Text(
            "Active & Pending Requests",
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            modifier = Modifier.padding(16.dp)
        )
        LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
            items(mockRequests) { request ->
                ProfessionalRequestCard(request, onClick = { onRequestClick(request.id) })
            }
        }
    }
}

// Professional request card
@Composable
fun ProfessionalRequestCard(request: MoveRequest, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(request.customerName, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${request.pickup} → ${request.dropoff}", color = MaterialTheme.colorScheme.primary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${request.dateTime} | ${request.jobSize}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Payment: ${request.payment}", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.secondary)
            }
            StatusBadge(request.status)
        }
    }
}

// Search Tab (placeholder for now)
@Composable
fun SearchTab() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Search Requests (Coming Soon)", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// Ratings Tab
@Composable
fun RatingsTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text("${driverStats.rating} / 5.0", fontWeight = FontWeight.Bold, fontSize = 28.sp)
        Text("${driverStats.completedMoves} Completed Moves", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// Profile Tab
@Composable
fun ProfileTab(onProfileClick: () -> Unit) {
    DriverProfileViewScreen(onEditProfile = onProfileClick)
}

// Data classes for dummy data
data class MoveRequest(
    val id: Int,
    val customerName: String,
    val pickup: String,
    val dropoff: String,
    val dateTime: String,
    val jobSize: String,
    val payment: String,
    val status: String
)
data class DriverStats(val completedMoves: Int, val rating: Float) 