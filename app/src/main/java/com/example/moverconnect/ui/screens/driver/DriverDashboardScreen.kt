package com.example.moverconnect.ui.screens.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                    title = { 
                        Text(
                            text = navItems[selectedTab].label,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    navigationIcon = {
                        IconButton(onClick = { drawerOpen = true }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    modifier = Modifier.shadow(4.dp)
                )
            },
            bottomBar = {
                DriverBottomNavigation(
                    currentRoute = when (selectedTab) {
                        0 -> DriverBottomNavItem.Requests.route
                        1 -> DriverBottomNavItem.Search.route
                        2 -> DriverBottomNavItem.Ratings.route
                        3 -> DriverBottomNavItem.Profile.route
                        else -> DriverBottomNavItem.Requests.route
                    },
                    onNavigate = { route ->
                        when (route) {
                            DriverBottomNavItem.Requests.route -> selectedTab = 0
                            DriverBottomNavItem.Search.route -> selectedTab = 1
                            DriverBottomNavItem.Ratings.route -> selectedTab = 2
                            DriverBottomNavItem.Profile.route -> selectedTab = 3
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
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
                    .align(Alignment.CenterStart)
            ) {
                DriverDrawer(
                    onNavigateToProfile = { onProfileClick() },
                    onNavigateToHistory = { /* TODO: Navigate to job history */ },
                    onNavigateToEarnings = { /* TODO: Navigate to earnings */ },
                    onNavigateToReviews = { /* TODO: Navigate to reviews */ },
                    onNavigateToSettings = { /* TODO: Navigate to settings */ },
                    onNavigateToHelp = { /* TODO: Navigate to help */ },
                    onLogout = onLogout,
                    onCloseDrawer = { drawerOpen = false }
                )
            }
        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Logout") },
                text = { Text("Are you sure you want to logout?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                        showLogoutDialog = false
                            onLogout()
                        }
                    ) {
                        Text("Logout")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun RequestsTab(onRequestClick: (Int) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Stats Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        icon = Icons.Default.Star,
                        value = driverStats.rating.toString(),
                        label = "Rating"
                    )
                    StatItem(
                        icon = Icons.Default.CheckCircle,
                        value = driverStats.completedMoves.toString(),
                        label = "Completed"
                    )
                }
            }
        }

        // Requests List
            items(mockRequests) { request ->
            RequestCard(request = request, onClick = { onRequestClick(request.id) })
            }
        }
    }

@Composable
private fun StatItem(
    icon: ImageVector,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun RequestCard(
    request: MoveRequest,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
    ) {
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
                Text(
                    text = request.customerName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                StatusBadge(request.status)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "From: ${request.pickup}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "To: ${request.dropoff}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = request.payment,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = request.dateTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = request.jobSize,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun SearchTab() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "Search Requests (Coming Soon)",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun RatingsTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "${driverStats.rating} / 5.0",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${driverStats.completedMoves} Completed Moves",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
    }
}
    }
}

@Composable
private fun ProfileTab(onProfileClick: () -> Unit) {
    DriverProfileViewScreen(onEditProfile = onProfileClick)
}

@Composable
private fun MenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

data class NavBarItem(val label: String, val icon: ImageVector)
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