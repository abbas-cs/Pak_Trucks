package com.example.moverconnect.ui.screens.customer

import android.content.Context
import android.content.Intent
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
import java.text.SimpleDateFormat
import java.util.*
import com.example.moverconnect.MainActivity
import com.example.moverconnect.SessionManager
import com.example.moverconnect.navigation.Screen

data class Booking(
    val id: String,
    val driverName: String,
    val driverPhoto: String,
    val date: Date,
    val status: BookingStatus,
    val pickupLocation: String,
    val dropoffLocation: String,
    val price: Double
)

enum class BookingStatus {
    PENDING, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingsScreen(
    onNavigate: (String) -> Unit = {},
    context: Context
) {
    var drawerOpen by remember { mutableStateOf(false) }

    // Dummy data for demonstration
    val bookings = remember {
        listOf(
            Booking(
                id = "1",
                driverName = "John Doe",
                driverPhoto = "",
                date = Date(),
                status = BookingStatus.CONFIRMED,
                pickupLocation = "123 Main St, New York",
                dropoffLocation = "456 Park Ave, New York",
                price = 150.0
            ),
            Booking(
                id = "2",
                driverName = "Jane Smith",
                driverPhoto = "",
                date = Date(System.currentTimeMillis() + 86400000),
                status = BookingStatus.PENDING,
                pickupLocation = "789 Broadway, New York",
                dropoffLocation = "321 5th Ave, New York",
                price = 200.0
            )
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            "My Bookings",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { drawerOpen = true }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            bottomBar = {
                CustomerBottomNavigation(
                    currentRoute = CustomerBottomNavItem.Bookings.route,
                    onNavigate = onNavigate
                )
            }
        ) { padding ->
            if (bookings.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No Bookings Yet",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Your upcoming and past bookings will appear here",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(bookings) { booking ->
                        BookingCard(booking = booking)
                    }
                }
            }
        }

        // Drawer
        if (drawerOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.32f))
                    .clickable { drawerOpen = false }
            ) {}
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(300.dp)
                    .align(Alignment.CenterStart)
            ) {
                CustomerDrawer(
                    onNavigateToProfile = { onNavigate(CustomerBottomNavItem.Profile.route) },
                    onNavigateToHistory = { onNavigate(CustomerBottomNavItem.Bookings.route) },
                    onNavigateToPayments = { /* TODO: Navigate to payments */ },
                    onNavigateToFavorites = { /* TODO: Navigate to favorites */ },
                    onNavigateToReviews = { /* TODO: Navigate to reviews */ },
                    onNavigateToSettings = { /* TODO: Navigate to settings */ },
                    onNavigateToHelp = { /* TODO: Navigate to help */ },
                    onLogout = {
                        SessionManager.logout(context)
                        val intent = Intent(context, MainActivity::class.java).apply {
                            putExtra("destination", Screen.Login.route)
                        }
                        context.startActivity(intent)
                        (context as? android.app.Activity)?.finish()
                    },
                    onCloseDrawer = { drawerOpen = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingCard(booking: Booking) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateFormat.format(booking.date),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
                BookingStatusChip(status = booking.status)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Driver Info
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = booking.driverName,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Locations
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "From: ${booking.pickupLocation}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "To: ${booking.dropoffLocation}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Price
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.AttachMoney,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$${booking.price}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

@Composable
fun BookingStatusChip(status: BookingStatus) {
    val (backgroundColor, textColor) = when (status) {
        BookingStatus.PENDING -> MaterialTheme.colorScheme.tertiary to MaterialTheme.colorScheme.onTertiary
        BookingStatus.CONFIRMED -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        BookingStatus.IN_PROGRESS -> MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.onSecondary
        BookingStatus.COMPLETED -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        BookingStatus.CANCELLED -> MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.onError
    }

    Surface(
        color = backgroundColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = status.name.replace("_", " ").lowercase().capitalize(),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
} 