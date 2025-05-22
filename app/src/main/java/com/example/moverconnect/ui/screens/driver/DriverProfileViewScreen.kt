package com.example.moverconnect.ui.screens.driver

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.moverconnect.R
import android.content.Intent
import android.net.Uri
import com.example.moverconnect.data.model.DriverProfile
import android.content.Context

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverProfileViewScreen(
    onEditProfile: () -> Unit,
    viewModel: DriverProfileViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val profile by viewModel.profile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
                // Enhanced Profile Header with Parallax Effect
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                        .height(280.dp)
                ) {
                    // Background Image with Gradient Overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                    )
                                )
                            )
                    )

                    // Profile Content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Profile Image with Enhanced Design
                        Box(
                            contentAlignment = Alignment.BottomEnd,
                            modifier = Modifier
                                .size(140.dp)
                                .shadow(16.dp, CircleShape)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                        ) {
                            Image(
                                painter = if (profile?.profileImageUrl?.isNotEmpty() == true) {
                                    rememberAsyncImagePainter(profile?.profileImageUrl)
                                } else {
                                    painterResource(id = R.drawable.ic_profile_placeholder)
                                },
                                contentDescription = "Profile Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            
                            // Edit Profile Button
                    FloatingActionButton(
                        onClick = onEditProfile,
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier
                            .size(44.dp)
                            .offset(x = 8.dp, y = 8.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                    }
                }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Profile Name and Location
                        Text(
                            text = profile?.fullName ?: "Driver Name",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        )

                        Text(
                            text = profile?.city ?: "Location",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                            )
                        )
            }
                }

                // Quick Actions with Enhanced Design
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
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
                        ActionButton(
                            icon = Icons.Default.Phone,
                            label = "Call",
                            onClick = {
                                profile?.phoneNumber?.let { phoneNumber ->
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
                            context.startActivity(intent)
                                }
                            }
                        )
                        ActionButton(
                            icon = Icons.Default.Chat,
                            label = "WhatsApp",
                            onClick = {
                                profile?.whatsappNumber?.let { whatsappNumber ->
                                    val url = "https://wa.me/${whatsappNumber.replace("+", "")}"
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                                }
                            }
                        )
                        ActionButton(
                            icon = Icons.Default.LocationOn,
                            label = "Location",
                            onClick = { /* TODO: Implement location sharing */ }
                        )
                    }
                }

                // Stats Section with Enhanced Design
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Star,
                        value = "4.5",
                        label = "Rating",
                        color = MaterialTheme.colorScheme.primary
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.CheckCircle,
                        value = "0",
                        label = "Completed Moves",
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                // Profile Details with Enhanced Design
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Vehicle Information
                    DetailSection(
                        title = "Vehicle Information",
                        icon = Icons.Default.DirectionsCar,
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        DetailItem(
                            label = "Type",
                            value = profile?.truckType ?: "Not specified",
                            icon = Icons.Default.DirectionsCar
                        )
                        DetailItem(
                            label = "Capacity",
                            value = profile?.truckCapacity ?: "Not specified",
                            icon = Icons.Default.Scale
                        )
                        profile?.vehicleImageUrls?.let { urls ->
                            if (urls.isNotEmpty()) {
                                DetailItem(
                                    label = "Photos",
                                    value = "${urls.size} uploaded",
                                    icon = Icons.Default.PhotoLibrary
                            )
                        }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Working Hours
                    DetailSection(
                        title = "Working Hours",
                        icon = Icons.Default.Schedule,
                        color = MaterialTheme.colorScheme.secondary
                    ) {
                        DetailItem(
                            label = "Availability",
                            value = "${profile?.workingHoursFrom ?: "Not specified"} - ${profile?.workingHoursTo ?: "Not specified"}",
                            icon = Icons.Default.AccessTime
                        )
                        DetailItem(
                            label = "Experience",
                            value = "${profile?.yearsOfExperience ?: "Not specified"} years",
                            icon = Icons.Default.Work
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Service Area
                    DetailSection(
                        title = "Service Area",
                        icon = Icons.Default.LocationOn,
                        color = MaterialTheme.colorScheme.tertiary
                    ) {
                        DetailItem(
                            label = "City",
                            value = profile?.city ?: "Not specified",
                            icon = Icons.Default.LocationCity
                        )
                        DetailItem(
                            label = "Areas Covered",
                            value = profile?.area ?: "Not specified",
                            icon = Icons.Default.Map
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        error?.let { errorMessage ->
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text(errorMessage)
            }
        }
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
                        }
                    }

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    value: String,
    label: String,
    color: Color
) {
            Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = color.copy(alpha = 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DetailSection(
    title: String,
    icon: ImageVector,
    color: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = color.copy(alpha = 0.1f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )
            }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            content()
        }
    }
}

@Composable
private fun DetailItem(
    label: String,
    value: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
} 