package com.example.moverconnect.ui.screens.customer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moverconnect.R
import com.example.moverconnect.ui.screens.driver.DriverProfileViewScreen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star

// Dummy driver data
private val activeDrivers = listOf(
    DriverProfile(
        name = "John Doe",
        photoRes = R.drawable.ic_profile_placeholder,
        contact = "+1234567890",
        whatsapp = "+1234567890",
        licenseNumber = "DL-123456789",
        licenseImageUploaded = true,
        experience = "Expert",
        bio = "I've been helping people move for 8 years. Reliable and friendly!",
        vehicleType = "Truck",
        vehiclePhotoUploaded = true,
        registration = "ABC-1234",
        capacity = "2 tons",
        modelYear = "2020",
        insuranceStatus = "Insured",
        city = "Metropolis",
        areasCovered = "Downtown, Uptown, Eastside",
        willingToTravel = true,
        workingHours = "8am - 8pm",
        rating = 4.8f,
        completedMoves = 24
    ),
    DriverProfile(
        name = "Jane Smith",
        photoRes = R.drawable.ic_profile_placeholder,
        contact = "+1987654321",
        whatsapp = "+1987654321",
        licenseNumber = "DL-987654321",
        licenseImageUploaded = true,
        experience = "3-5 years",
        bio = "Professional and careful driver.",
        vehicleType = "Van",
        vehiclePhotoUploaded = true,
        registration = "XYZ-5678",
        capacity = "1.5 tons",
        modelYear = "2019",
        insuranceStatus = "Insured",
        city = "Gotham",
        areasCovered = "Midtown, Downtown",
        willingToTravel = false,
        workingHours = "9am - 6pm",
        rating = 4.6f,
        completedMoves = 15
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerHomeScreen() {
    var selectedDriver by remember { mutableStateOf<DriverProfile?>(null) }

    if (selectedDriver != null) {
        // Show driver profile details
        DriverProfileViewScreen(
            onEditProfile = { selectedDriver = null }
        )
    } else {
        // Show list of drivers
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Available Drivers") })
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(padding),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(activeDrivers) { driver ->
                    DriverCard(driver = driver, onShowDetails = { selectedDriver = driver })
                }
            }
        }
    }
}

@Composable
fun DriverCard(driver: DriverProfile, onShowDetails: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = driver.photoRes),
                contentDescription = "Driver Photo",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(driver.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${driver.rating} / 5.0", fontSize = 14.sp)
                }
                Text(driver.vehicleType, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                Text("${driver.completedMoves} moves", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
            }
            Button(onClick = onShowDetails) {
                Text("Show Details")
            }
        }
    }
}

// Copy the DriverProfile data class from the driver package for now (in a real app, share the model)
data class DriverProfile(
    val name: String,
    val photoRes: Int,
    val contact: String,
    val whatsapp: String,
    val licenseNumber: String,
    val licenseImageUploaded: Boolean,
    val experience: String,
    val bio: String,
    val vehicleType: String,
    val vehiclePhotoUploaded: Boolean,
    val registration: String,
    val capacity: String,
    val modelYear: String,
    val insuranceStatus: String,
    val city: String,
    val areasCovered: String,
    val willingToTravel: Boolean,
    val workingHours: String,
    val rating: Float,
    val completedMoves: Int
) 