package com.example.moverconnect.ui.screens.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import com.example.moverconnect.ui.screens.driver.StatusBadge

// Dummy data for move requests (reuse from dashboard)
private val allRequests = listOf(
    MoveRequest(1, "Alice Smith", "Downtown", "Uptown", "2024-06-10 10:00", "Large", "$120", "Pending"),
    MoveRequest(2, "Bob Lee", "Eastside", "Westside", "2024-06-12 14:00", "Medium", "$90", "Active"),
    MoveRequest(3, "Carol King", "North Ave", "South Blvd", "2024-06-15 09:00", "Small", "$60", "Pending"),
    MoveRequest(4, "David Park", "Central Park", "Harbor", "2024-06-18 16:00", "Large", "$150", "Active"),
    MoveRequest(5, "Eve Adams", "Hilltop", "Lakeside", "2024-06-20 11:30", "Medium", "$100", "Available"),
    MoveRequest(6, "Frank Moore", "Old Town", "New City", "2024-06-22 13:00", "Small", "$70", "Available")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestDetailScreen(requestId: Int, onBack: (() -> Unit)? = null) {
    val request = allRequests.find { it.id == requestId }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Request Details") },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (request == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Request not found", color = MaterialTheme.colorScheme.error)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Customer Info
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(request.customerName, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            StatusBadge(request.status)
                        }
                    }
                }
                // Locations
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Pickup: ", fontWeight = FontWeight.Medium)
                            Text(request.pickup, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Dropoff: ", fontWeight = FontWeight.Medium)
                            Text(request.dropoff, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
                // Date, Job Size, Payment
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Card(modifier = Modifier.weight(1f)) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Date/Time", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                                Text(request.dateTime, fontSize = 13.sp)
                            }
                        }
                    }
                    Card(modifier = Modifier.weight(1f)) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AttachMoney, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Payment", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                                Text(request.payment, fontSize = 13.sp)
                            }
                        }
                    }
                }
                // Job Size
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Job Size: ", fontWeight = FontWeight.Medium)
                        Text(request.jobSize)
                    }
                }
            }
        }
    }
}
// In a real app, this would fetch request details from a backend or ViewModel. 