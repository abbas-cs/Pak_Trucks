package com.example.moverconnect.ui.screens.driver

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Dummy data for available move requests
private val availableRequests = listOf(
    MoveRequest(1, "Alice Smith", "Downtown", "Uptown", "2024-06-10 10:00", "Large", "$120", "Available"),
    MoveRequest(2, "Bob Lee", "Eastside", "Westside", "2024-06-12 14:00", "Medium", "$90", "Available"),
    MoveRequest(3, "Carol King", "North Ave", "South Blvd", "2024-06-15 09:00", "Small", "$60", "Available"),
    MoveRequest(4, "David Park", "Central Park", "Harbor", "2024-06-18 16:00", "Large", "$150", "Available"),
    MoveRequest(5, "Eve Adams", "Hilltop", "Lakeside", "2024-06-20 11:30", "Medium", "$100", "Available"),
    MoveRequest(6, "Frank Moore", "Old Town", "New City", "2024-06-22 13:00", "Small", "$70", "Available")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseRequestsScreen(onRequestClick: (Int) -> Unit) {
    var filterJobSize by remember { mutableStateOf("") }
    var filterLocation by remember { mutableStateOf("") }
    var filterDate by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val jobSizes = listOf("", "Small", "Medium", "Large")

    // Filtering logic
    val filteredRequests = availableRequests.filter {
        (filterJobSize.isBlank() || it.jobSize == filterJobSize) &&
        (filterLocation.isBlank() || it.pickup.contains(filterLocation, true) || it.dropoff.contains(filterLocation, true)) &&
        (filterDate.isBlank() || it.dateTime.contains(filterDate))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Browse Requests") },
                actions = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Filter options
            if (expanded) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        OutlinedTextField(
                            value = filterLocation,
                            onValueChange = { filterLocation = it },
                            label = { Text("Location") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = filterDate,
                            onValueChange = { filterDate = it },
                            label = { Text("Date (YYYY-MM-DD)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        // Job size dropdown (fixed pattern)
                        var jobSizeExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = jobSizeExpanded,
                            onExpandedChange = { jobSizeExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = filterJobSize,
                                onValueChange = {}, // No-op, value set via dropdown
                                label = { Text("Job Size") },
                                readOnly = true,
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = jobSizeExpanded) }
                            )
                            ExposedDropdownMenu(
                                expanded = jobSizeExpanded,
                                onDismissRequest = { jobSizeExpanded = false }
                            ) {
                                jobSizes.forEach { size ->
                                    DropdownMenuItem(
                                        text = { Text(if (size.isBlank()) "All" else size) },
                                        onClick = {
                                            filterJobSize = size
                                            jobSizeExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            // List of requests
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredRequests) { request ->
                    BrowseRequestCard(request, onClick = { onRequestClick(request.id) })
                }
            }
        }
    }
}

@Composable
fun BrowseRequestCard(request: MoveRequest, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("${request.customerName}", fontWeight = FontWeight.Bold)
            Text("From: ${request.pickup} → To: ${request.dropoff}")
            Text("When: ${request.dateTime}")
            Text("Job Size: ${request.jobSize} | Payment: ${request.payment}")
        }
    }
}
// In a real app, requests would come from a backend or ViewModel. 