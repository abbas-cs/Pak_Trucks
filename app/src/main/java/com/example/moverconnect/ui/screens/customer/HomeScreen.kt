package com.example.moverconnect.ui.screens.customer

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.moverconnect.MainActivity
import com.example.moverconnect.SessionManager
import com.example.moverconnect.navigation.Screen
import com.example.moverconnect.ui.viewmodels.ReviewViewModel
import com.example.moverconnect.data.repository.ReviewRepository

class CustomerHomeViewModel : ViewModel() {
    private val repository = DriverProfileRepository.getInstance()
    private val _drivers = MutableStateFlow<List<DriverProfile>>(emptyList())
    val drivers: StateFlow<List<DriverProfile>> = _drivers

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _searchSuggestions = MutableStateFlow<List<String>>(emptyList())
    val searchSuggestions: StateFlow<List<String>> = _searchSuggestions

    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive

    private val _selectedFilters = MutableStateFlow<Set<FilterOption>>(emptySet())
    val selectedFilters: StateFlow<Set<FilterOption>> = _selectedFilters

    private lateinit var application: Application

    fun init(application: Application) {
        this.application = application
        loadDrivers()
        loadSearchHistory()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isNotEmpty()) {
            generateSearchSuggestions(query)
            setSearchActive(true)
        } else {
            _searchSuggestions.value = emptyList()
            setSearchActive(false)
        }
        applyFilters()
    }

    fun performSearch(query: String) {
        _searchQuery.value = query
        _searchSuggestions.value = emptyList()
        setSearchActive(false)
        addToSearchHistory(query)
        filterDrivers()
    }

    fun setSearchActive(active: Boolean) {
        _isSearchActive.value = active
        if (!active) {
            _searchSuggestions.value = emptyList()
        }
    }

    fun addToSearchHistory(query: String) {
        if (query.isNotEmpty()) {
            val currentHistory = _searchHistory.value.toMutableList()
            // Remove if already exists
            currentHistory.remove(query)
            // Add to front
            currentHistory.add(0, query)
            // Keep only last 10 searches
            _searchHistory.value = currentHistory.take(10)
            saveSearchHistory()
        }
    }

    fun clearSearchHistory() {
        _searchHistory.value = emptyList()
        saveSearchHistory()
    }

    private fun saveSearchHistory() {
        viewModelScope.launch {
            try {
                val prefs = application.getSharedPreferences("search_prefs", Context.MODE_PRIVATE)
                prefs.edit().putStringSet("search_history", _searchHistory.value.toSet()).apply()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun loadSearchHistory() {
        viewModelScope.launch {
            try {
                val prefs = application.getSharedPreferences("search_prefs", Context.MODE_PRIVATE)
                val history = prefs.getStringSet("search_history", emptySet())?.toList() ?: emptyList()
                _searchHistory.value = history
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun generateSearchSuggestions(query: String) {
        val suggestions = mutableSetOf<String>()
        val lowercaseQuery = query.lowercase()

        // Add matching cities
        _drivers.value.map { it.city }.distinct()
            .filter { it.lowercase().contains(lowercaseQuery) }
            .forEach { suggestions.add(it) }

        // Add matching areas
        _drivers.value.map { it.area }.distinct()
            .filter { it.lowercase().contains(lowercaseQuery) }
            .forEach { suggestions.add(it) }

        // Add matching truck types
        _drivers.value.map { it.truckType }.distinct()
            .filter { it.lowercase().contains(lowercaseQuery) }
            .forEach { suggestions.add(it) }

        // Add matching driver names
        _drivers.value.map { it.fullName }.distinct()
            .filter { it.lowercase().contains(lowercaseQuery) }
            .forEach { suggestions.add(it) }

        _searchSuggestions.value = suggestions.take(5).toList()
    }

    private fun filterDrivers() {
        val query = _searchQuery.value.lowercase()
        viewModelScope.launch {
            try {
                repository.getAllActiveDriverProfiles().collect { allDrivers ->
                    if (query.isEmpty()) {
                        _drivers.value = allDrivers
                    } else {
                        _drivers.value = allDrivers.filter { driver ->
                            driver.fullName.lowercase().contains(query) ||
                            driver.city.lowercase().contains(query) ||
                            driver.area.lowercase().contains(query) ||
                            driver.truckType.lowercase().contains(query)
                        }
                    }
                }
            } catch (e: Exception) {
                _error.value = "Failed to filter drivers: ${e.message}"
            }
        }
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

    fun toggleFilter(filter: FilterOption) {
        val currentFilters = _selectedFilters.value.toMutableSet()
        if (currentFilters.contains(filter)) {
            currentFilters.remove(filter)
        } else {
            currentFilters.add(filter)
        }
        _selectedFilters.value = currentFilters
        applyFilters()
    }

    fun clearFilters() {
        _selectedFilters.value = emptySet()
        applyFilters()
    }

    private fun applyFilters() {
        viewModelScope.launch {
            try {
                repository.getAllActiveDriverProfiles().collect { allDrivers ->
                    val query = _searchQuery.value.lowercase()
                    val filters = _selectedFilters.value

                    // First apply text search if any
                    var filteredDrivers = if (query.isEmpty()) {
                        allDrivers
                    } else {
                        allDrivers.filter { driver ->
                            driver.fullName.lowercase().contains(query) ||
                            driver.city.lowercase().contains(query) ||
                            driver.area.lowercase().contains(query) ||
                            driver.truckType.lowercase().contains(query)
                        }
                    }

                    // Then apply filters if any
                    if (filters.isNotEmpty()) {
                        filteredDrivers = filteredDrivers.filter { driver ->
                            filters.all { filter ->
                                when (filter) {
                                    FilterOption.NEARBY -> true // TODO: Implement actual nearby logic
                                    FilterOption.TOP_RATED -> {
                                        // TODO: Replace with actual rating logic
                                        Random.nextFloat() > 0.3f
                                    }
                                    FilterOption.AVAILABLE_NOW -> {
                                        val currentHour = java.time.LocalTime.now().hour
                                        val workingHoursFrom = driver.workingHoursFrom.split(":").first().toInt()
                                        val workingHoursTo = driver.workingHoursTo.split(":").first().toInt()
                                        currentHour in workingHoursFrom until workingHoursTo
                                    }
                                    FilterOption.TRUCK -> driver.truckType.contains("Truck", ignoreCase = true)
                                    FilterOption.VAN -> driver.truckType.contains("Van", ignoreCase = true)
                                    else -> true
                                }
                            }
                        }
                    }

                    _drivers.value = filteredDrivers
                }
            } catch (e: Exception) {
                _error.value = "Failed to filter drivers: ${e.message}"
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit = {},
    onMenuClick: () -> Unit = {}
) {
    var drawerOpen by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val viewModel: CustomerHomeViewModel = viewModel()
    val reviewViewModel: ReviewViewModel = viewModel(
        factory = ReviewViewModel.ReviewViewModelFactory(ReviewRepository())
    )
    
    // Get current customer info from SessionManager
    val currentCustomerId = SessionManager.getCurrentUserId(context)
    val currentCustomerName = SessionManager.getCurrentUserName(context)
    val currentCustomerProfileImageUrl = SessionManager.getCurrentUserProfileImage(context)
    
    // Initialize ViewModel with Application context
    LaunchedEffect(Unit) {
        viewModel.init(context.applicationContext as Application)
    }

    var selectedDriver by remember { mutableStateOf<DriverProfile?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf<FilterOption?>(null) }
    var showFilters by remember { mutableStateOf(false) }

    val drivers by viewModel.drivers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val searchSuggestions by viewModel.searchSuggestions.collectAsState()
    val searchHistory by viewModel.searchHistory.collectAsState()
    val isSearchActive by viewModel.isSearchActive.collectAsState()
    val selectedFilters by viewModel.selectedFilters.collectAsState()

    // Update search query in ViewModel when it changes
    LaunchedEffect(searchQuery) {
        viewModel.updateSearchQuery(searchQuery)
    }

    if (selectedDriver != null) {
        DriverDetailsScreen(
            driver = selectedDriver!!,
            onBack = { selectedDriver = null },
            onBookNow = {
                onNavigate("booking/${selectedDriver!!.userId}")
            },
            reviewViewModel = reviewViewModel,
            currentCustomerId = currentCustomerId,
            currentCustomerName = currentCustomerName,
            currentCustomerProfileImageUrl = currentCustomerProfileImageUrl
        )
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                    Column {
                        // Top App Bar
                        TopAppBar(
                            title = { 
                                Text(
                                    "Find a Driver",
                                    style = MaterialTheme.typography.titleLarge
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = { drawerOpen = true }) {
                                    Icon(
                                        Icons.Default.Menu,
                                        contentDescription = "Menu"
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                titleContentColor = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        // Search and Filter Section
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            // Search Bar
                            Box {
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { 
                                        searchQuery = it
                                        viewModel.updateSearchQuery(it)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    placeholder = { 
                                        Text(
                                            "Search drivers by name or location",
                                            style = MaterialTheme.typography.bodyMedium
                                        ) 
                                    },
                                    textStyle = MaterialTheme.typography.bodyMedium,
                                    leadingIcon = { 
                                        Icon(
                                            Icons.Default.Search,
                                            contentDescription = "Search",
                                            modifier = Modifier.size(20.dp)
                                        ) 
                                    },
                            trailingIcon = {
                                        if (searchQuery.isNotEmpty()) {
                                            IconButton(
                                                onClick = { 
                                                    searchQuery = ""
                                                    viewModel.updateSearchQuery("")
                                                },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Clear,
                                                    contentDescription = "Clear search",
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        } else if (!isSearchActive) {
                                            IconButton(
                                                onClick = { showFilters = !showFilters },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                    Icon(
                                        if (showFilters) Icons.Default.FilterList else Icons.Outlined.FilterList,
                                        contentDescription = "Filter",
                                                    modifier = Modifier.size(18.dp),
                                        tint = if (showFilters) MaterialTheme.colorScheme.primary 
                                               else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                                    },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                        containerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    keyboardOptions = KeyboardOptions(
                                        imeAction = ImeAction.Search
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onSearch = {
                                            viewModel.performSearch(searchQuery)
                                        }
                                    )
                                )

                                // Filter Dropdown
                                if (showFilters && !isSearchActive) {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 4.dp)
                                            .offset(y = 48.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surface
                                        ),
                                        elevation = CardDefaults.cardElevation(
                                            defaultElevation = 4.dp
                                        )
                                    ) {
                            FilterChips(
                                            selectedFilters = selectedFilters,
                                            onFilterSelected = { 
                                                viewModel.toggleFilter(it)
                                                showFilters = false
                                            },
                                            onClearFilters = { 
                                                viewModel.clearFilters()
                                                showFilters = false
                                            }
                                        )
                                    }
                                }
                            }

                            // Selected Filter Chips
                            if (selectedFilters.isNotEmpty()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    selectedFilters.forEach { filter ->
                                        FilterChip(
                                            selected = true,
                                            onClick = { viewModel.toggleFilter(filter) },
                                            label = { Text(filter.title) },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = filter.icon,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            },
                                            trailingIcon = {
                                                Icon(
                                                    Icons.Default.Close,
                                                    contentDescription = "Remove filter",
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                                selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                                selectedTrailingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                bottomBar = {
                    CustomerBottomNavigation(
                        currentRoute = CustomerBottomNavItem.Home.route,
                        onNavigate = onNavigate
                    )
            }
        ) { padding ->
                Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                        .padding(padding)
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
                            Text(text = errorMessage)
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
}

@Composable
private fun FilterChips(
    selectedFilters: Set<FilterOption>,
    onFilterSelected: (FilterOption) -> Unit,
    onClearFilters: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Filter Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Filters",
                style = MaterialTheme.typography.titleMedium
            )
            if (selectedFilters.isNotEmpty()) {
                TextButton(onClick = onClearFilters) {
                    Text("Clear All")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Filter Categories in a more compact layout
        LazyColumn(
            modifier = Modifier.height(200.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
            FilterCategory.values().forEach { category ->
                val categoryFilters = FilterOption.values().filter { it.category == category }
                if (categoryFilters.isNotEmpty()) {
                    item {
                        Text(
                            category.name.lowercase().replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            categoryFilters.forEach { filter ->
            FilterChip(
                                    selected = selectedFilters.contains(filter),
                onClick = { onFilterSelected(filter) },
                                    label = { Text(filter.title) },
                leadingIcon = {
                    Icon(
                        imageVector = filter.icon,
                        contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                    )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                        selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                )
                            }
                        }
                    }
                }
            }
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
    val title: String,
    val icon: ImageVector,
    val category: FilterCategory
) {
    NEARBY("Nearby", Icons.Default.LocationOn, FilterCategory.LOCATION),
    TOP_RATED("Top Rated", Icons.Default.Star, FilterCategory.RATING),
    AVAILABLE_NOW("Available Now", Icons.Default.Schedule, FilterCategory.AVAILABILITY),
    TRUCK("Truck", Icons.Default.DirectionsCar, FilterCategory.VEHICLE),
    VAN("Van", Icons.Default.LocalShipping, FilterCategory.VEHICLE)
}

enum class FilterCategory {
    LOCATION,
    RATING,
    AVAILABILITY,
    VEHICLE
}

// Copy the DriverProfile data class from the driver package for now (in a real app, share the model)
data class DriverProfile(
    val userId: String,
    val fullName: String,
    val phoneNumber: String,
    val whatsappNumber: String,
    val yearsOfExperience: String,
    val workingHoursFrom: String,
    val workingHoursTo: String,
    val truckType: String,
    val truckCapacity: String,
    val city: String,
    val area: String,
    val profileImageUrl: String,
    val vehicleImageUrls: List<String>
) 

@Composable
private fun SearchHistoryItem(
    query: String,
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
            Icons.Default.History,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = query,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun SearchSuggestionItem(
    suggestion: String,
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
            Icons.Default.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = suggestion,
            style = MaterialTheme.typography.bodyLarge
        )
    }
} 