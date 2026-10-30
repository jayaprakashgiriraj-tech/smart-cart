package com.simats.smartcart.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.smartcart.data.UserAccountRepository
import com.simats.smartcart.network.AuthNetworkClient
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(
    onBackClick: () -> Unit = {},
    onSignUpSuccess: (String) -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    var showServerDialog by remember { mutableStateOf(false) }
    var serverInput by remember { mutableStateOf(AuthNetworkClient.getActiveUrl()) }

    val coroutineScope = rememberCoroutineScope()

    fun handleSignUp() {
        val nameTrim = fullName.trim()
        val emailTrim = email.trim()

        if (nameTrim.isEmpty() || emailTrim.isEmpty() || password.isEmpty()) {
            errorMessage = "Please fill in all fields."
            successMessage = null
            return
        }

        if (!emailTrim.contains("@") || !emailTrim.contains(".")) {
            errorMessage = "Please enter a valid email address."
            successMessage = null
            return
        }

        if (password.length < 6) {
            errorMessage = "Password must be at least 6 characters long."
            successMessage = null
            return
        }

        if (confirmPassword.isNotEmpty() && password != confirmPassword) {
            errorMessage = "Passwords do not match."
            successMessage = null
            return
        }

        isLoading = true
        errorMessage = null
        successMessage = null

        coroutineScope.launch {
            // Attempt API registration to XAMPP backend
            val response = AuthNetworkClient.registerUser(nameTrim, emailTrim, password)
            isLoading = false

            if (response.isSuccess) {
                errorMessage = null
                successMessage = response.message.ifEmpty { "Account created successfully!" }
                if (response.user != null) {
                    UserAccountRepository.currentUser = response.user
                } else {
                    UserAccountRepository.registerUser(nameTrim, emailTrim, password)
                }
                onSignUpSuccess(emailTrim)
            } else {
                errorMessage = response.message
                successMessage = null
            }
        }
    }

    if (showServerDialog) {
        AlertDialog(
            onDismissRequest = { showServerDialog = false },
            title = {
                Text("Backend Server Settings", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            },
            text = {
                Column {
                    Text(
                        "Set the base URL for XAMPP backend:",
                        fontSize = 13.sp,
                        color = Color(0xFF555555)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = serverInput,
                        onValueChange = { serverInput = it },
                        label = { Text("Base URL") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Quick Presets (tap to apply):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "• USB Phone (adb reverse):\n  http://127.0.0.1:8080/smartcart_backend",
                        fontSize = 11.sp,
                        color = Color(0xFF5050F0),
                        modifier = Modifier.clickable {
                            serverInput = "http://127.0.0.1:8080/smartcart_backend"
                        }
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "• Local PC LAN IP:\n  http://172.23.49.142/smartcart_backend",
                        fontSize = 11.sp,
                        color = Color(0xFF5050F0),
                        modifier = Modifier.clickable {
                            serverInput = "http://172.23.49.142/smartcart_backend"
                        }
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "• Android Emulator:\n  http://10.0.2.2/smartcart_backend",
                        fontSize = 11.sp,
                        color = Color(0xFF5050F0),
                        modifier = Modifier.clickable {
                            serverInput = "http://10.0.2.2/smartcart_backend"
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    AuthNetworkClient.customBaseUrl = serverInput.trim().trimEnd('/')
                    showServerDialog = false
                    errorMessage = null
                }) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showServerDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        containerColor = Color(0xFFFAFBFF)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar with Back Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back to Login",
                        tint = Color(0xFF1E2135)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Top App Icon with Gradient Glow
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFFA59BFF), Color(0xFF6B5BF0))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(contentAlignment = Alignment.TopEnd) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingCart,
                        contentDescription = "SmartCart Logo",
                        tint = Color.White,
                        modifier = Modifier.size(34.dp)
                    )
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFFA5F3FC),
                        modifier = Modifier
                            .size(16.dp)
                            .offset(x = 6.dp, y = (-4).dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // App Name & Tagline
            Text(
                text = "SmartCart AI",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1E2135)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Shop Smarter. Buy Better.",
                fontSize = 12.sp,
                color = Color(0xFF7A8398)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Create Account Header
            Text(
                text = "Create your account",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1E2135)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Sign up to shop smarter.",
                fontSize = 13.sp,
                color = Color(0xFF7A8398)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Main Registration Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = BorderStroke(1.dp, Color(0xFFECEFF8))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Full Name Label & Field
                    Text(
                        text = "Full Name",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E2135)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        placeholder = {
                            Text("Enter your full name", color = Color(0xFFA0A7B8), fontSize = 14.sp)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = "Name Icon",
                                tint = Color(0xFF7A8398),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF7F8FC),
                            unfocusedContainerColor = Color(0xFFF7F8FC),
                            focusedBorderColor = Color(0xFF5050F0),
                            unfocusedBorderColor = Color(0xFFE8EBF4)
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Email Label & Field
                    Text(
                        text = "Email Address",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E2135)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = {
                            Text("you@example.com", color = Color(0xFFA0A7B8), fontSize = 14.sp)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Email,
                                contentDescription = "Email Icon",
                                tint = Color(0xFF7A8398),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF7F8FC),
                            unfocusedContainerColor = Color(0xFFF7F8FC),
                            focusedBorderColor = Color(0xFF5050F0),
                            unfocusedBorderColor = Color(0xFFE8EBF4)
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Password Label & Field
                    Text(
                        text = "Password",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E2135)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = {
                            Text("Create a password", color = Color(0xFFA0A7B8), fontSize = 14.sp)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = "Password Icon",
                                tint = Color(0xFF7A8398),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                    contentDescription = "Toggle password visibility",
                                    tint = Color(0xFF7A8398),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF7F8FC),
                            unfocusedContainerColor = Color(0xFFF7F8FC),
                            focusedBorderColor = Color(0xFF5050F0),
                            unfocusedBorderColor = Color(0xFFE8EBF4)
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Confirm Password Label & Field
                    Text(
                        text = "Confirm Password",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E2135)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        placeholder = {
                            Text("Confirm your password", color = Color(0xFFA0A7B8), fontSize = 14.sp)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = "Confirm Password Icon",
                                tint = Color(0xFF7A8398),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                                Icon(
                                    imageVector = if (isConfirmPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                    contentDescription = "Toggle confirm password visibility",
                                    tint = Color(0xFF7A8398),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF7F8FC),
                            unfocusedContainerColor = Color(0xFFF7F8FC),
                            focusedBorderColor = Color(0xFF5050F0),
                            unfocusedBorderColor = Color(0xFFE8EBF4)
                        )
                    )

                    // Error Message Callout
                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = errorMessage!!,
                            color = Color(0xFFD32F2F),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "⚙️ Configure Backend URL",
                            color = Color(0xFF5050F0),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                serverInput = AuthNetworkClient.getActiveUrl()
                                showServerDialog = true
                            }
                        )
                    }

                    // Success Message Callout
                    if (successMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = successMessage!!,
                            color = Color(0xFF1A8068),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Prominent Purple "Sign up" Button
                    Button(
                        onClick = { handleSignUp() },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5050F0)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Text(
                                text = "Sign up",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Footer Link: Already have an account? Log in
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Already have an account? ",
                    color = Color(0xFF7A8398),
                    fontSize = 13.sp
                )
                Text(
                    text = "Log in",
                    color = Color(0xFF5050F0),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onLoginClick() }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Server Status & Change Link
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Server: ${AuthNetworkClient.getActiveUrl()} (Change)",
                    color = Color(0xFFA0A8B8),
                    fontSize = 10.sp,
                    modifier = Modifier.clickable {
                        serverInput = AuthNetworkClient.getActiveUrl()
                        showServerDialog = true
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Your preferences stay yours.",
                color = Color(0xFF9AA4B8),
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    SignUpScreen()
}
