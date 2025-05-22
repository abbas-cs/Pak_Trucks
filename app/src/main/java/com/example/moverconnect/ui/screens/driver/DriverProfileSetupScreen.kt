package com.example.moverconnect.ui.screens.driver

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import com.example.moverconnect.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moverconnect.data.model.DriverProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverProfileSetupScreen(
    onSave: () -> Unit,
    viewModel: DriverProfileViewModel = viewModel()
) {
    // Basic Info
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var fullName by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf<LocalDate?>(null) }
    var yearsOfExperience by remember { mutableStateOf("") }
    var showExperienceMenu by remember { mutableStateOf(false) }

    // Contact Info
    var phoneNumber by remember { mutableStateOf("") }
    var whatsappNumber by remember { mutableStateOf("") }
    var useSameNumber by remember { mutableStateOf(false) }
    var workingHoursFrom by remember { mutableStateOf("") }
    var workingHoursTo by remember { mutableStateOf("") }
    var workingHoursFromPeriod by remember { mutableStateOf("AM") }
    var workingHoursToPeriod by remember { mutableStateOf("PM") }
    var showFromTimeMenu by remember { mutableStateOf(false) }
    var showToTimeMenu by remember { mutableStateOf(false) }
    var showFromPeriodMenu by remember { mutableStateOf(false) }
    var showToPeriodMenu by remember { mutableStateOf(false) }

    // Truck Information
    var truckType by remember { mutableStateOf("") }
    var truckCapacity by remember { mutableStateOf("") }
    var vehicleImages by remember { mutableStateOf<List<Uri>>(emptyList()) }

    // Location
    var city by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var citySearchQuery by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTruckTypeMenu by remember { mutableStateOf(false) }
    var showCityMenu by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    var selectedImageIndex by remember { mutableStateOf<Int?>(null) }
    var showImagePreview by remember { mutableStateOf(false) }

    val truckTypes = listOf(
        "Mini Truck (1-2 tons)",
        "Pickup (0.5-1 ton)",
        "Shehzore (2-3 tons)",
        "Mazda (3-5 tons)",
        "Container (20-40 ft)",
        "Truck (5-10 tons)",
        "Trailer (10-20 tons)",
        "Refrigerated Truck",
        "Flatbed Truck",
        "Box Truck"
    )
    
    // List of major cities in Pakistan
    val pakistanCities = listOf(
        "Abbottabad", "Bahawalpur", "Faisalabad", "Gujranwala", "Hyderabad",
        "Islamabad", "Karachi", "Lahore", "Larkana", "Multan",
        "Mardan", "Mingora", "Mirpur Khas", "Nawabshah", "Okara",
        "Peshawar", "Quetta", "Rahim Yar Khan", "Rawalpindi", "Sargodha",
        "Sialkot", "Sukkur", "Turbat", "Wah Cantonment", "Zhob"
    ).sorted()

    val filteredCities = remember(citySearchQuery) {
        if (citySearchQuery.isEmpty()) {
            pakistanCities
        } else {
            pakistanCities.filter { it.contains(citySearchQuery, ignoreCase = true) }
        }
    }

    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { profileImageUri = it }
    }

    val vehicleImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            if (vehicleImages.size < 5) {
                vehicleImages = vehicleImages + it
            }
        }
    }

    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = dateOfBirth?.toEpochDay()?.times(24 * 60 * 60 * 1000)
    )

    val experienceLevels = listOf(
        "1+ years",
        "2+ years",
        "3+ years",
        "5+ years",
        "8+ years",
        "10+ years",
        "15+ years",
        "20+ years"
    )

    val timeOptions = (1..12).map { it.toString().padStart(2, '0') }
    val periodOptions = listOf("AM", "PM")

    // Load profile data when screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    // Observe profile data
    val profile by viewModel.profile.collectAsState()

    // Update form fields when profile is loaded
    LaunchedEffect(profile) {
        profile?.let { p ->
            fullName = p.fullName
            try {
                dateOfBirth = if (p.dateOfBirth.isNotEmpty()) {
                    LocalDate.parse(p.dateOfBirth)
                } else {
                    null
                }
            } catch (e: Exception) {
                dateOfBirth = null
            }
            yearsOfExperience = p.yearsOfExperience
            phoneNumber = p.phoneNumber
            whatsappNumber = p.whatsappNumber
            workingHoursFrom = p.workingHoursFrom
            workingHoursTo = p.workingHoursTo
            truckType = p.truckType
            truckCapacity = p.truckCapacity
            city = p.city
            area = p.area
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Driver Profile Setup") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
            )
        }
    ) { paddingValues ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .verticalScroll(scrollState)
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
            // Profile Picture Section with enhanced design
        Box(
            modifier = Modifier
                .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
                    .padding(vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                    modifier = Modifier
                            .size(140.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                            .shadow(12.dp, CircleShape)
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (profileImageUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(profileImageUri),
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Add Profile Picture",
                                modifier = Modifier.size(56.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                FloatingActionButton(
                        onClick = { imagePickerLauncher.launch("image/*") },
                    containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                            .size(44.dp)
                        .offset(x = 8.dp, y = 8.dp)
                ) {
                        Icon(Icons.Default.AddAPhoto, contentDescription = "Add Profile Picture")
                    }
                }
            }

            // Form Sections with enhanced design
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
        ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(
                        "Basic Information",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { 
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            ) 
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                )

                OutlinedTextField(
                        value = dateOfBirth?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "",
                        onValueChange = { },
                        label = { Text("Date of Birth") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { 
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            ) 
                        },
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(
                                    Icons.Default.DateRange,
                                    contentDescription = "Select Date",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        readOnly = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    ExposedDropdownMenuBox(
                        expanded = showExperienceMenu,
                        onExpandedChange = { showExperienceMenu = it }
                    ) {
                        OutlinedTextField(
                            value = yearsOfExperience,
                            onValueChange = {},
                            label = { Text("Years of Experience") },
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            leadingIcon = { 
                                Icon(
                                    Icons.Default.Work,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                ) 
                            },
                            trailingIcon = { 
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = showExperienceMenu
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = showExperienceMenu,
                            onDismissRequest = { showExperienceMenu = false }
                        ) {
                            experienceLevels.forEach { level ->
                                DropdownMenuItem(
                                    text = { Text(level) },
                                    onClick = {
                                        yearsOfExperience = level
                                        showExperienceMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Contact Info Section with enhanced design
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(
                        "Contact Information",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = phoneNumber,
                    onValueChange = {
                                if (it.length <= 15 && it.all { char -> char.isDigit() || char == '+' }) {
                                    phoneNumber = it
                                    if (useSameNumber) {
                                        whatsappNumber = it
                                    }
                                }
                    },
                            label = { Text("Phone Number") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            leadingIcon = { 
                                Icon(
                                    Icons.Default.Phone,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                ) 
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            OutlinedTextField(
                                value = whatsappNumber,
                                onValueChange = { 
                                    if (it.length <= 15 && it.all { char -> char.isDigit() || char == '+' }) {
                                        whatsappNumber = it
                                    }
                                },
                                label = { Text("WhatsApp Number") },
                                modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                leadingIcon = { 
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_whatsapp),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    ) 
                                },
                                enabled = !useSameNumber,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )
                            
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                    Checkbox(
                                    checked = useSameNumber,
                                    onCheckedChange = { checked ->
                                        useSameNumber = checked
                                        if (checked) {
                                            whatsappNumber = phoneNumber
                                        }
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = MaterialTheme.colorScheme.primary,
                                        uncheckedColor = MaterialTheme.colorScheme.outline
                                    )
                                )
                                Text(
                                    "Use same number as phone",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Working Hours Section with enhanced design
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(
                        "Working Hours",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // From time
                            ExposedDropdownMenuBox(
                                expanded = showFromTimeMenu,
                                onExpandedChange = { showFromTimeMenu = it },
                                modifier = Modifier.weight(1f)
                            ) {
                                OutlinedTextField(
                                    value = workingHoursFrom,
                                    onValueChange = {},
                                    label = { Text("From") },
                                    readOnly = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    leadingIcon = { 
                                        Icon(
                                            Icons.Default.Schedule,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        ) 
                                    },
                                    trailingIcon = { 
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = showFromTimeMenu
                                        )
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = showFromTimeMenu,
                                    onDismissRequest = { showFromTimeMenu = false }
                                ) {
                                    timeOptions.forEach { time ->
                                        DropdownMenuItem(
                                            text = { Text(time) },
                                            onClick = {
                                                workingHoursFrom = time
                                                showFromTimeMenu = false
                                            }
                                        )
                                    }
                                }
                            }

                            // From period
                            ExposedDropdownMenuBox(
                                expanded = showFromPeriodMenu,
                                onExpandedChange = { showFromPeriodMenu = it },
                                modifier = Modifier.width(100.dp)
                            ) {
                OutlinedTextField(
                                    value = workingHoursFromPeriod,
                                    onValueChange = {},
                                    readOnly = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    trailingIcon = { 
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = showFromPeriodMenu
                                        )
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = showFromPeriodMenu,
                                    onDismissRequest = { showFromPeriodMenu = false }
                                ) {
                                    periodOptions.forEach { period ->
                                        DropdownMenuItem(
                                            text = { Text(period) },
                                            onClick = {
                                                workingHoursFromPeriod = period
                                                showFromPeriodMenu = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                    modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // To time
                            ExposedDropdownMenuBox(
                                expanded = showToTimeMenu,
                                onExpandedChange = { showToTimeMenu = it },
                                modifier = Modifier.weight(1f)
                            ) {
                                OutlinedTextField(
                                    value = workingHoursTo,
                                    onValueChange = {},
                                    label = { Text("To") },
                                    readOnly = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    leadingIcon = { 
                                        Icon(
                                            Icons.Default.Schedule,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        ) 
                                    },
                                    trailingIcon = { 
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = showToTimeMenu
                                        )
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = showToTimeMenu,
                                    onDismissRequest = { showToTimeMenu = false }
                                ) {
                                    timeOptions.forEach { time ->
                                        DropdownMenuItem(
                                            text = { Text(time) },
                                            onClick = {
                                                workingHoursTo = time
                                                showToTimeMenu = false
                                            }
                                        )
                                    }
                                }
                            }

                            // To period
                            ExposedDropdownMenuBox(
                                expanded = showToPeriodMenu,
                                onExpandedChange = { showToPeriodMenu = it },
                                modifier = Modifier.width(100.dp)
                            ) {
                OutlinedTextField(
                                    value = workingHoursToPeriod,
                                    onValueChange = {},
                                    readOnly = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    trailingIcon = { 
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = showToPeriodMenu
                                        )
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = showToPeriodMenu,
                                    onDismissRequest = { showToPeriodMenu = false }
                                ) {
                                    periodOptions.forEach { period ->
                                        DropdownMenuItem(
                                            text = { Text(period) },
                                            onClick = {
                                                workingHoursToPeriod = period
                                                showToPeriodMenu = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Truck Information Section with enhanced design
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(
                        "Truck Information",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                ExposedDropdownMenuBox(
                        expanded = showTruckTypeMenu,
                        onExpandedChange = { showTruckTypeMenu = it }
                ) {
                    OutlinedTextField(
                            value = truckType,
                        onValueChange = {},
                            label = { Text("Truck Type") },
                        readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            leadingIcon = { 
                                Icon(
                                    Icons.Default.DirectionsCar,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                ) 
                            },
                            trailingIcon = { 
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = showTruckTypeMenu
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                    )
                    ExposedDropdownMenu(
                            expanded = showTruckTypeMenu,
                            onDismissRequest = { showTruckTypeMenu = false }
                    ) {
                            truckTypes.forEach { type ->
                            DropdownMenuItem(
                                    text = { Text(type) },
                                onClick = {
                                        truckType = type
                                        showTruckTypeMenu = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                        value = truckCapacity,
                        onValueChange = { truckCapacity = it },
                        label = { Text("Truck Capacity") },
                    modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = { 
                            Icon(
                                Icons.Default.Scale,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            ) 
                        },
                        trailingIcon = { 
                            Text(
                                "tons",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ) 
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                )
            }
        }

            // Vehicle Images Section with enhanced design
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
        ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Vehicle Images",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "${vehicleImages.size}/5",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (vehicleImages.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { vehicleImagePickerLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Default.AddAPhoto,
                                    contentDescription = "Add Vehicle Image",
                                    modifier = Modifier.size(56.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    "Add Vehicle Images",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    "Upload up to 5 images",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            items(vehicleImages) { imageUri ->
                                VehicleImageItem(
                                    imageUri = imageUri,
                                    index = vehicleImages.indexOf(imageUri),
                                    onImageClick = { index ->
                                        selectedImageIndex = index
                                        showImagePreview = true
                                    },
                                    onDeleteClick = { index ->
                                        vehicleImages = vehicleImages.filterIndexed { i, _ -> i != index }
                                    }
                                )
                            }
                            if (vehicleImages.size < 5) {
                                item {
                                    AddImageButton(
                                        onClick = { vehicleImagePickerLauncher.launch("image/*") }
                            )
                        }
                    }
                }
            }
        }
            }

            // Location Section with enhanced design
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
        ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(
                        "Location",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    ExposedDropdownMenuBox(
                        expanded = showCityMenu,
                        onExpandedChange = { showCityMenu = it }
                    ) {
                OutlinedTextField(
                    value = city,
                            onValueChange = { 
                                citySearchQuery = it
                                city = it
                                if (!showCityMenu) showCityMenu = true
                            },
                            label = { Text("City") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            leadingIcon = { 
                                Icon(
                                    Icons.Default.LocationCity,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                ) 
                            },
                            trailingIcon = { 
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = showCityMenu
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = showCityMenu,
                            onDismissRequest = { showCityMenu = false }
                        ) {
                            filteredCities.forEach { cityName ->
                                DropdownMenuItem(
                                    text = { Text(cityName) },
                                    onClick = {
                                        city = cityName
                                        citySearchQuery = cityName
                                        showCityMenu = false
                                    }
                                )
                            }
                        }
                    }

                OutlinedTextField(
                        value = area,
                        onValueChange = { area = it },
                        label = { Text("Area or Region") },
                    modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { 
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            ) 
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
            }

            // Save Button with loading state
            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        errorMessage = null
                        
                        try {
                            val profile = DriverProfile(
                                fullName = fullName,
                                dateOfBirth = dateOfBirth?.format(DateTimeFormatter.ISO_DATE) ?: "",
                                yearsOfExperience = yearsOfExperience,
                                phoneNumber = phoneNumber,
                                whatsappNumber = whatsappNumber,
                                workingHoursFrom = "$workingHoursFrom $workingHoursFromPeriod",
                                workingHoursTo = "$workingHoursTo $workingHoursToPeriod",
                                truckType = truckType,
                                truckCapacity = truckCapacity,
                                city = city,
                                area = area
                            )
                            
                            viewModel.saveProfile(profile)
                            onSave()
                        } catch (e: Exception) {
                            errorMessage = e.message ?: "An error occurred while saving the profile"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        "Save Profile",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            errorMessage?.let { message ->
                Text(
                    message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    if (showDatePicker) {
        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            properties = DialogProperties(usePlatformDefaultWidth = false),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(28.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Select Date of Birth",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                DatePicker(
                    state = datePickerState,
                    showModeToggle = false,
                    colors = DatePickerDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        headlineContentColor = MaterialTheme.colorScheme.onSurface,
                        weekdayContentColor = MaterialTheme.colorScheme.onSurface,
                        subheadContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        yearContentColor = MaterialTheme.colorScheme.onSurface,
                        currentYearContentColor = MaterialTheme.colorScheme.primary,
                        selectedYearContainerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { showDatePicker = false }
                    ) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                dateOfBirth = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                            }
                            showDatePicker = false
                        }
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }

    // Image Preview Bottom Sheet
    if (showImagePreview && selectedImageIndex != null) {
        ModalBottomSheet(
            onDismissRequest = { showImagePreview = false },
            modifier = Modifier.fillMaxHeight(),
            sheetState = rememberModalBottomSheetState()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { showImagePreview = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                        Text(
                            "Image ${selectedImageIndex!! + 1} of ${vehicleImages.size}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        IconButton(
                            onClick = {
                                vehicleImages = vehicleImages.filterIndexed { i, _ -> i != selectedImageIndex }
                                showImagePreview = false
                            }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Image")
                        }
                    }

                    // Image
                    Image(
                        painter = rememberAsyncImagePainter(vehicleImages[selectedImageIndex!!]),
                        contentDescription = "Vehicle Image Preview",
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}

@Composable
private fun VehicleImageItem(
    imageUri: Uri,
    index: Int,
    onImageClick: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .width(300.dp)
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onImageClick(index) }
    ) {
        Image(
            painter = rememberAsyncImagePainter(imageUri),
            contentDescription = "Vehicle Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        IconButton(
            onClick = { onDeleteClick(index) },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                    shape = CircleShape
                )
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Delete Image",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun AddImageButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(300.dp)
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.AddAPhoto,
                contentDescription = "Add Vehicle Image",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Add Another Image",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private fun validateForm(
    fullName: String,
    dateOfBirth: LocalDate?,
    yearsOfExperience: String,
    phoneNumber: String,
    whatsappNumber: String,
    workingHoursFrom: String,
    workingHoursTo: String,
    truckType: String,
    truckCapacity: String,
    city: String,
    area: String
): Boolean {
    return fullName.isNotBlank() &&
            dateOfBirth != null &&
            yearsOfExperience.isNotBlank() &&
            phoneNumber.isNotBlank() &&
            whatsappNumber.isNotBlank() &&
            workingHoursFrom.isNotBlank() &&
            workingHoursTo.isNotBlank() &&
            truckType.isNotBlank() &&
            truckCapacity.isNotBlank() &&
            city.isNotBlank() &&
            area.isNotBlank()
} 