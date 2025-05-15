package com.example.moverconnect.ui.screens.driver

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moverconnect.R
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material.icons.filled.Phone
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DriverProfileViewScreen(onEditProfile: () -> Unit) {
    val context = LocalContext.current
    // Dummy profile data
    val driver = DriverProfile(
        name = "John Doe",
        photoRes = R.drawable.ic_profile_placeholder, // Replace with real image in future
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
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 0.dp, vertical = 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile photo area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                    .padding(top = 32.dp, bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Image(
                        painter = painterResource(id = driver.photoRes),
                        contentDescription = "Driver Photo",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .shadow(8.dp, CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                    )
                    FloatingActionButton(
                        onClick = onEditProfile,
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White,
                        modifier = Modifier
                            .size(44.dp)
                            .offset(x = 8.dp, y = 8.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                    }
                }
            }
            // Name and rating
            Text(driver.name, fontWeight = FontWeight.Bold, fontSize = 26.sp, modifier = Modifier.padding(top = 8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(4.dp))
                Text("${driver.rating} / 5.0", fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.width(12.dp))
                Text("${driver.completedMoves} moves", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            // Contact section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SectionHeader("Contact")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${driver.contact}"))
                            context.startActivity(intent)
                        }) {
                            Icon(Icons.Default.Phone, contentDescription = "Call Driver", tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = {
                            val url = "https://wa.me/${driver.whatsapp.replace("+", "")}" // WhatsApp API
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_whatsapp),
                                contentDescription = "WhatsApp Driver",
                                tint = Color(0xFF25D366)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(driver.contact, fontWeight = FontWeight.Medium)
                        if (driver.whatsapp != driver.contact) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("WhatsApp: ${driver.whatsapp}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            // Profile details card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    SectionHeader("Personal Information")
                    ProfileDetailRow(label = "License Number", value = driver.licenseNumber)
                    if (driver.licenseImageUploaded) {
                        Text("License Image: Uploaded", color = MaterialTheme.colorScheme.primary)
                    }
                    ProfileDetailRow(label = "Experience", value = driver.experience)
                    if (driver.bio.isNotBlank()) {
                        ProfileDetailRow(label = "About", value = driver.bio)
                    }
                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                    SectionHeader("Vehicle Information")
                    ProfileDetailRow(label = "Type", value = driver.vehicleType)
                    ProfileDetailRow(label = "Registration", value = driver.registration)
                    ProfileDetailRow(label = "Capacity", value = driver.capacity)
                    ProfileDetailRow(label = "Model/Year", value = driver.modelYear)
                    ProfileDetailRow(label = "Insurance", value = driver.insuranceStatus)
                    if (driver.vehiclePhotoUploaded) {
                        Text("Vehicle Photo: Uploaded", color = MaterialTheme.colorScheme.primary)
                    }
                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                    SectionHeader("Area of Operation & Availability")
                    ProfileDetailRow(label = "City/Region", value = driver.city)
                    ProfileDetailRow(label = "Areas Covered", value = driver.areasCovered)
                    ProfileDetailRow(label = "Willing to Travel", value = if (driver.willingToTravel) "Yes" else "No")
                    ProfileDetailRow(label = "Working Hours", value = driver.workingHours)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ProfileDetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text("$label:", fontWeight = FontWeight.Medium, modifier = Modifier.width(130.dp))
        Text(value, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private data class DriverProfile(
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