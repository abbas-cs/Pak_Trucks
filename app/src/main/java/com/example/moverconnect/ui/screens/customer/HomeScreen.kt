package com.example.moverconnect.ui.screens.customer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moverconnect.R
import com.example.moverconnect.ui.screens.driver.DriverProfileViewScreen
import coil.compose.rememberAsyncImagePainter
import com.example.moverconnect.data.model.DriverProfile
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlin.random.Random
import com.example.moverconnect.data.repository.DriverProfileRepository
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

class CustomerHomeViewModel : ViewModel() {
    private val repository = DriverProfileRepository.getInstance()
    private val _drivers = MutableStateFlow<List<DriverProfile>>(emptyList())
    val drivers: StateFlow<List<DriverProfile>> = _drivers

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadDrivers()
    }

    fun loadDrivers() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                // First, try to load cached data if available
                repository.getAllActiveDriverProfiles()
                    .collect { driverList ->
                        if (driverList.isNotEmpty()) {
                            _drivers.value = driverList
                            _error.value = null
                            _isLoading.value = false
                        } else {
                            _error.value = "No drivers available at the moment"
                            _isLoading.value = false
                        }
                    }
            } catch (e: Exception) {
                when {
                    e.message?.contains("permission-denied") == true -> {
                        _error.value = "Unable to access driver profiles. Please try again later."
                    }
                    e.message?.contains("network") == true -> {
                        _error.value = "Network error. Please check your connection."
                    }
                    else -> {
                        _error.value = "Failed to load drivers: ${e.message}"
                    }
                }
                // Load dummy data in case of error
                _drivers.value = getDummyDrivers()
                _isLoading.value = false
            }
        }
    }

    private fun getDummyDrivers(): List<DriverProfile> {
        return listOf(
    DriverProfile(
                userId = "dummy1",
                fullName = "John Doe",
                phoneNumber = "+1234567890",
                whatsappNumber = "+1234567890",
                yearsOfExperience = "5+ years",
                workingHoursFrom = "8:00 AM",
                workingHoursTo = "8:00 PM",
                truckType = "Truck",
                truckCapacity = "2 tons",
                city = "New York",
                area = "Manhattan, Brooklyn",
                profileImageUrl = "",
                vehicleImageUrls = emptyList()
    ),
    DriverProfile(
                userId = "dummy2",
                fullName = "Jane Smith",
                phoneNumber = "+1987654321",
                whatsappNumber = "+1987654321",
                yearsOfExperience = "3+ years",
                workingHoursFrom = "9:00 AM",
                workingHoursTo = "6:00 PM",
                truckType = "Van",
                truckCapacity = "1.5 tons",
                city = "Los Angeles",
                area = "Downtown, Hollywood",
                profileImageUrl = "",
                vehicleImageUrls = emptyList()
    )
)
    }
}

@Composable
fun CustomerBottomNav(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute == "customer_home",
            onClick = { onNavigate("customer_home") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.History, contentDescription = "Bookings") },
            label = { Text("Bookings") },
            selected = currentRoute == "customer_bookings",
            onClick = { onNavigate("customer_bookings") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = currentRoute == "customer_profile",
            onClick = { onNavigate("customer_profile") }
        )
    }
}

@Composable
fun CustomerMainScreen(
    navController: NavHostController = rememberNavController()
) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route ?: "customer_home"

    Scaffold(
        bottomBar = {
            CustomerBottomNav(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "customer_home",
            modifier = Modifier.padding(padding)
        ) {
            composable("customer_home") {
                CustomerHomeScreen()
            }
            composable("customer_bookings") {
                CustomerBookingsScreen()
            }
            composable("customer_profile") {
                CustomerProfileScreen()
            }
        }
    }
}

@Composable
fun CustomerBookingsScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Bookings Screen - Coming Soon")
    }
}

@Composable
fun CustomerProfileScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Profile Screen - Coming Soon")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerHomeScreen(
    viewModel: CustomerHomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var selectedDriver by remember { mutableStateOf<DriverProfile?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf<FilterOption?>(null) }
    var showFilters by remember { mutableStateOf(false) }

    val drivers by viewModel.drivers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    if (selectedDriver != null) {
        DriverProfileViewScreen(
            onEditProfile = { selectedDriver = null }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Search Bar
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = { },
                active = false,
                onActiveChange = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search drivers by name or location") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(
                            if (showFilters) Icons.Default.FilterList else Icons.Outlined.FilterList,
                            contentDescription = "Filter",
                            tint = if (showFilters) MaterialTheme.colorScheme.primary 
                                   else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            ) { }

            // Filter Chips
            if (showFilters) {
                FilterChips(
                    selectedFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                if (isLoading && drivers.isEmpty()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(drivers) { driver ->
                            DriverCard(
                                driver = driver,
                                onShowDetails = { selectedDriver = driver }
                            )
                        }
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
    }
}

@Composable
private fun FilterChips(
    selectedFilter: FilterOption?,
    onFilterSelected: (FilterOption) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterOption.values().forEach { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = { Text(filter.label) },
                leadingIcon = {
                    Icon(
                        imageVector = filter.icon,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            )
        }
    }
}

@Composable
fun DriverCard(driver: DriverProfile, onShowDetails: () -> Unit) {
    // Generate dummy data for unavailable fields
    val rating = remember { Random.nextFloat() * 2 + 3 } // Random rating between 3.0 and 5.0
    val completedMoves = remember { Random.nextInt(5, 50) } // Random number of completed moves

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .clickable(onClick = onShowDetails),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Image
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Image(
                    painter = if (driver.profileImageUrl.isNotEmpty()) {
                        rememberAsyncImagePainter(driver.profileImageUrl)
                    } else {
                        painterResource(id = R.drawable.ic_profile_placeholder)
                    },
                    contentDescription = "Driver Photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Driver Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = driver.fullName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = String.format("%.1f", rating),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        text = " • $completedMoves moves",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${driver.city}, ${driver.area}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoChip(
                        icon = Icons.Default.DirectionsCar,
                        text = driver.truckType,
                        modifier = Modifier.weight(1f)
                    )
                    InfoChip(
                        icon = Icons.Default.Scale,
                        text = driver.truckCapacity,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Arrow Icon
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "View Details",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun InfoChip(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            }
        }
    }

enum class FilterOption(
    val label: String,
    val icon: ImageVector
) {
    RATING_HIGH("High Rating", Icons.Default.Star),
    EXPERIENCE("Experience", Icons.Default.Work),
    AVAILABLE_NOW("Available Now", Icons.Default.AccessTime),
    NEARBY("Nearby", Icons.Default.LocationOn)
} 