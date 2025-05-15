package com.example.moverconnect.ui.screens.driver

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moverconnect.R
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import com.example.moverconnect.ui.screens.driver.SectionHeader
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DriverProfileSetupScreen(onSave: () -> Unit) {
    // Personal Info
    var name by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var whatsapp by remember { mutableStateOf("") }
    var sameAsContact by remember { mutableStateOf(false) }
    var licenseNumber by remember { mutableStateOf("") }
    var licenseImageUploaded by remember { mutableStateOf(false) }
    var experience by remember { mutableStateOf("Beginner") }
    var bio by remember { mutableStateOf("") }
    var profilePhotoUploaded by remember { mutableStateOf(false) }
    var expDropdownExpanded by remember { mutableStateOf(false) }
    val experienceLevels = listOf("Beginner", "1-3 years", "3-5 years", "5+ years", "Expert")

    // Vehicle Info
    var vehicleType by remember { mutableStateOf("") }
    var vehiclePhotoUploaded by remember { mutableStateOf(false) }
    var registration by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("") }
    var modelYear by remember { mutableStateOf("") }
    var insuranceStatus by remember { mutableStateOf("Insured") }
    val insuranceOptions = listOf("Insured", "Not Insured")
    var insuranceDropdownExpanded by remember { mutableStateOf(false) }
    val services = listOf("Furniture Moving", "Full Home Moving", "Packing", "Unpacking")
    val selectedServices = remember { mutableStateListOf<String>() }

    // Area of Operation
    var city by remember { mutableStateOf("") }
    var areasCovered by remember { mutableStateOf("") }
    var willingToTravel by remember { mutableStateOf(false) }
    var workingHours by remember { mutableStateOf("") }

    var showSuccess by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile photo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                .padding(top = 32.dp, bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                Image(
                    painter = painterResource(id = R.drawable.ic_profile_placeholder),
                    contentDescription = "Profile Photo",
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .shadow(8.dp, CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                )
                FloatingActionButton(
                    onClick = { profilePhotoUploaded = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    modifier = Modifier
                        .size(40.dp)
                        .offset(x = 8.dp, y = 8.dp)
                ) {
                    Icon(Icons.Default.AddAPhoto, contentDescription = "Upload Profile Photo")
                }
            }
        }
        // Personal Info Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                SectionHeader("Personal Information")
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = contact,
                    onValueChange = {
                        contact = it
                        if (sameAsContact) whatsapp = it
                    },
                    label = { Text("Contact Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = sameAsContact,
                        onCheckedChange = {
                            sameAsContact = it
                            if (it) whatsapp = contact
                        }
                    )
                    Text("WhatsApp number same as contact", modifier = Modifier.padding(start = 8.dp))
                }
                OutlinedTextField(
                    value = whatsapp,
                    onValueChange = { whatsapp = it; if (sameAsContact) contact = it },
                    label = { Text("WhatsApp Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !sameAsContact
                )
                OutlinedTextField(
                    value = licenseNumber,
                    onValueChange = { licenseNumber = it },
                    label = { Text("Driver's License Number") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = { licenseImageUploaded = true }) {
                        Icon(Icons.Default.AddAPhoto, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Upload License Image")
                    }
                    if (licenseImageUploaded) Text("Uploaded", color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 8.dp))
                }
                ExposedDropdownMenuBox(
                    expanded = expDropdownExpanded,
                    onExpandedChange = { expDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = experience,
                        onValueChange = {},
                        label = { Text("Experience Level") },
                        readOnly = true,
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expDropdownExpanded) }
                    )
                    ExposedDropdownMenu(
                        expanded = expDropdownExpanded,
                        onDismissRequest = { expDropdownExpanded = false }
                    ) {
                        experienceLevels.forEach { level ->
                            DropdownMenuItem(
                                text = { Text(level) },
                                onClick = {
                                    experience = level
                                    expDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Short Bio/About Me") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        }
        // Vehicle Info Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                SectionHeader("Vehicle Information")
                OutlinedTextField(
                    value = vehicleType,
                    onValueChange = { vehicleType = it },
                    label = { Text("Vehicle Type") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = { vehiclePhotoUploaded = true }) {
                        Icon(Icons.Default.AddAPhoto, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Upload Vehicle Photo")
                    }
                    if (vehiclePhotoUploaded) Text("Uploaded", color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 8.dp))
                }
                OutlinedTextField(
                    value = registration,
                    onValueChange = { registration = it },
                    label = { Text("Registration/Number Plate") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = capacity,
                    onValueChange = { capacity = it },
                    label = { Text("Vehicle Capacity") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = modelYear,
                    onValueChange = { modelYear = it },
                    label = { Text("Model/Year") },
                    modifier = Modifier.fillMaxWidth()
                )
                ExposedDropdownMenuBox(
                    expanded = insuranceDropdownExpanded,
                    onExpandedChange = { insuranceDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = insuranceStatus,
                        onValueChange = {},
                        label = { Text("Insurance Status") },
                        readOnly = true,
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = insuranceDropdownExpanded) }
                    )
                    ExposedDropdownMenu(
                        expanded = insuranceDropdownExpanded,
                        onDismissRequest = { insuranceDropdownExpanded = false }
                    ) {
                        insuranceOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    insuranceStatus = option
                                    insuranceDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
        // Area of Operation Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                SectionHeader("Area of Operation & Availability")
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("Primary City/Region") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = areasCovered,
                    onValueChange = { areasCovered = it },
                    label = { Text("Areas/Neighborhoods Covered") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = willingToTravel,
                        onCheckedChange = { willingToTravel = it }
                    )
                    Text("Willing to Travel Further?", modifier = Modifier.padding(start = 8.dp))
                }
                OutlinedTextField(
                    value = workingHours,
                    onValueChange = { workingHours = it },
                    label = { Text("Preferred Working Hours") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        // Save button
        Button(
            onClick = {
                if (name.isBlank() || contact.isBlank() || licenseNumber.isBlank() || vehicleType.isBlank() || registration.isBlank() || capacity.isBlank() || city.isBlank()) {
                    error = "Please fill all required fields."
                } else {
                    error = ""
                    showSuccess = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 16.dp)
        ) {
            Text("Save Profile")
        }
        if (error.isNotBlank()) {
            Text(error, color = Color.Red, modifier = Modifier.padding(8.dp))
        }
        if (showSuccess) {
            AlertDialog(
                onDismissRequest = { showSuccess = false; onSave() },
                title = { Text("Success") },
                text = { Text("Profile saved successfully!") },
                confirmButton = {
                    TextButton(onClick = { showSuccess = false; onSave() }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

// All UI state is local; in a real app, this would be connected to a ViewModel and backend. 