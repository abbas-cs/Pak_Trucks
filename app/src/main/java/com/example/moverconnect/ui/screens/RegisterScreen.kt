package com.example.moverconnect.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moverconnect.navigation.UserType
import com.example.moverconnect.auth.FirebaseAuthService
import kotlinx.coroutines.delay
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.shadow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    userType: UserType,
    onRegister: () -> Unit,
    onLogin: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    
    var fullNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    var isFormValid by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val authService = remember { FirebaseAuthService() }

    // Update form validity
    LaunchedEffect(fullName, email, phoneNumber, password, confirmPassword) {
        isFormValid = fullName.isNotBlank() && 
                     email.isNotBlank() && 
                     phoneNumber.isNotBlank() && 
                     password.isNotBlank() && 
                     confirmPassword.isNotBlank() &&
                     fullNameError == null && 
                     emailError == null && 
                     phoneError == null && 
                     passwordError == null && 
                     confirmPasswordError == null
    }

    // Auto-hide error message
    LaunchedEffect(showError) {
        if (showError) {
            delay(3000)
            showError = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with animation
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    Text(
                        text = "Create Account",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Registering as ${if (userType is UserType.Customer) "Customer" else "Driver"}",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            // Form fields with animations
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                Column {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { 
                            fullName = it
                            fullNameError = when {
                                it.isBlank() -> "Name is required"
                                it.length < 3 -> "Name must be at least 3 characters"
                                else -> null
                            }
                        },
                        label = { Text("Full Name") },
                        isError = fullNameError != null,
                        supportingText = { fullNameError?.let { Text(it) } },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = "Name")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { 
                            email = it
                            emailError = when {
                                it.isBlank() -> "Email is required"
                                !android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches() -> "Invalid email format"
                                else -> null
                            }
                        },
                        label = { Text("Email") },
                        isError = emailError != null,
                        supportingText = { emailError?.let { Text(it) } },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = "Email")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { 
                            val filtered = it.filter { char -> char.isDigit() }
                            if (filtered.isEmpty() || filtered.startsWith("0")) {
                                phoneNumber = filtered
                                phoneError = when {
                                    filtered.isBlank() -> "Phone number is required"
                                    filtered.length < 11 -> "Phone number must be 11 digits"
                                    filtered.length > 11 -> "Phone number cannot exceed 11 digits"
                                    !filtered.startsWith("0") -> "Phone number must start with 0"
                                    else -> null
                                }
                            }
                        },
                        label = { Text("Phone Number") },
                        isError = phoneError != null,
                        supportingText = { phoneError?.let { Text(it) } },
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = "Phone")
                        },
                        placeholder = { Text("03XXXXXXXXX") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { 
                            password = it
                            passwordError = when {
                                it.isBlank() -> "Password is required"
                                it.length < 6 -> "Password must be at least 6 characters"
                                !it.any { char -> char.isDigit() } -> "Password must contain at least one number"
                                !it.any { char -> char.isUpperCase() } -> "Password must contain at least one uppercase letter"
                                else -> null
                            }
                        },
                        label = { Text("Password") },
                        isError = passwordError != null,
                        supportingText = { passwordError?.let { Text(it) } },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = "Password")
                        },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (showPassword) "Hide password" else "Show password"
                                )
                            }
                        },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { 
                            confirmPassword = it
                            confirmPasswordError = when {
                                it.isBlank() -> "Please confirm your password"
                                it != password -> "Passwords do not match"
                                else -> null
                            }
                        },
                        label = { Text("Confirm Password") },
                        isError = confirmPasswordError != null,
                        supportingText = { confirmPasswordError?.let { Text(it) } },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = "Confirm Password")
                        },
                        trailingIcon = {
                            IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                                Icon(
                                    imageVector = if (showConfirmPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (showConfirmPassword) "Hide password" else "Show password"
                                )
                            }
                        },
                        visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Register button with animation
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                Button(
                    onClick = {
                        if (isFormValid) {
                            isLoading = true
                            scope.launch {
                                try {
                                    val result = authService.createUserWithEmailAndPassword(
                                        email = email,
                                        password = password,
                                        fullName = fullName,
                                        userType = if (userType is UserType.Customer) "customer" else "driver",
                                        phoneNumber = phoneNumber
                                    )

                                    result.fold(
                                        onSuccess = {
                                            onRegister()
                                        },
                                        onFailure = { exception ->
                                            errorMessage = when {
                                                exception.message?.contains("email address is already in use") == true ->
                                                    "An account with this email already exists"
                                                exception.message?.contains("badly formatted") == true ->
                                                    "Invalid email format"
                                                else -> "Registration failed: ${exception.message}"
                                            }
                                            showError = true
                                        }
                                    )
                                } catch (e: Exception) {
                                    errorMessage = "An error occurred: ${e.message}"
                                    showError = true
                                } finally {
                                    isLoading = false
                                }
                            }
                        } else {
                            errorMessage = "Please fix the errors above"
                            showError = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = isFormValid && !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            text = "Register",
                            fontSize = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onLogin) {
                Text(
                    text = "Already have an account? Login",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Error message with animation
        AnimatedVisibility(
            visible = showError,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .shadow(8.dp, RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
} 