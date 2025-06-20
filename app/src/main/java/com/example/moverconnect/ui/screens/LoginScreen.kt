package com.example.moverconnect.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.moverconnect.SessionManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.delay
import com.example.moverconnect.auth.FirebaseAuthService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLogin: (String, String) -> Unit,
    onRegister: () -> Unit,
    onForgotPassword: () -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }
    var useEmailLogin by remember { mutableStateOf(false) }
    
    var phoneError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var loginError by remember { mutableStateOf<String?>(null) }
    var showError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authService = remember { FirebaseAuthService() }

    // Animation for error message
    val errorOffset by animateFloatAsState(
        targetValue = if (showError) 0f else -100f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "Error Animation"
    )

    // Auto-hide error message after 3 seconds
    LaunchedEffect(showError) {
        if (showError) {
            delay(3000)
            showError = false
        }
    }

    // Auto-hide success message after 2 seconds
    LaunchedEffect(showSuccess) {
        if (showSuccess) {
            delay(2000)
            showSuccess = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome Back",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            if (!useEmailLogin) {
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { 
                        val filtered = it.filter { char -> char.isDigit() }
                        if (filtered.length <= 11) {
                            phoneNumber = filtered
                            phoneError = when {
                                filtered.isEmpty() -> "Phone number is required"
                                filtered.length < 11 -> "Phone number must be 11 digits"
                                !filtered.startsWith("0") -> "Phone number must start with 0"
                                else -> null
                            }
                        }
                    },
                    label = { Text("Phone Number") },
                    isError = phoneError != null,
                    supportingText = { phoneError?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = "Phone")
                    },
                    placeholder = { Text("03XXXXXXXXX") },
                    singleLine = true
                )
            } else {
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { 
                        phoneNumber = it
                        phoneError = if (!android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()) "Invalid email format" else null
                    },
                    label = { Text("Email") },
                    isError = phoneError != null,
                    supportingText = { phoneError?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = "Email")
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { 
                    password = it
                    passwordError = if (it.length < 6) "Password must be at least 6 characters" else null
                },
                label = { Text("Password") },
                isError = passwordError != null,
                supportingText = { passwordError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (showPassword) "Hide password" else "Show password"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it }
                    )
                    Text(
                        text = "Remember me",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                TextButton(onClick = onForgotPassword) {
                    Text(
                        text = "Forgot Password?",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = { 
                    useEmailLogin = !useEmailLogin
                    phoneNumber = ""
                    phoneError = null
                    loginError = null
                    showError = false
                }
            ) {
                Text(
                    text = if (useEmailLogin) "Login with Phone Number" else "Login with Email",
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (phoneError == null && passwordError == null) {
                        isLoading = true
                        scope.launch {
                            try {
                                val result = if (useEmailLogin) {
                                    authService.signInWithEmailAndPassword(phoneNumber, password)
                                } else {
                                    authService.signInWithPhoneNumber(phoneNumber, password)
                                }

                                result.fold(
                                    onSuccess = { (user, userType) ->
                                        // Save login state with the correct user type
                                        SessionManager.saveLogin(context, userType)
                                        
                                        // Save user information
                                        SessionManager.saveUserInfo(
                                            context = context,
                                            userId = user.uid,
                                            userName = user.displayName ?: "User",
                                            profileImageUrl = user.photoUrl?.toString() ?: ""
                                        )
                                        
                                        successMessage = "Login successful!"
                                        showSuccess = true
                                onLogin(phoneNumber, password)
                                    },
                                    onFailure = { exception ->
                                        loginError = when {
                                            exception.message?.contains("no user record") == true ->
                                                if (useEmailLogin) "No account found with this email" else "No account found with this phone number"
                                            exception.message?.contains("badly formatted") == true ->
                                                if (useEmailLogin) "Invalid email format" else "Invalid phone number format"
                                            exception.message?.contains("password is invalid") == true ->
                                                "Incorrect password"
                                            else -> "Login failed: ${exception.message}"
                                        }
                                showError = true
                            }
                                )
                            } catch (e: Exception) {
                                loginError = "An error occurred: ${e.message}"
                                showError = true
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = phoneError == null && passwordError == null && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                Text(
                    text = "Login",
                    fontSize = 18.sp
                )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onRegister) {
                Text(
                    text = "Don't have an account? Register",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Success message
        AnimatedVisibility(
            visible = showSuccess,
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
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = successMessage,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Error message
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
                        text = loginError ?: "",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
} 