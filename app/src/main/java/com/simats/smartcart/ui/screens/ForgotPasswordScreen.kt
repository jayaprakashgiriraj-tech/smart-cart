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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordScreen(
    onBackClick: () -> Unit = {},
    onResetSuccess: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    var showServerDialog by remember { mutableStateOf(false) }
    var serverInput by remember { mutableStateOf(AuthNetworkClient.getActiveUrl()) }

    val coroutineScope = rememberCoroutineScope()

    fun handleResetPassword() {
        val emailTrim = email.trim()

        if (emailTrim.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            errorMessage = "Please fill in all fields."
            successMessage = null
            return
        }

        if (!emailTrim.contains("@") || !emailTrim.contains(".")) {
            errorMessage = "Please enter a valid email address."
            successMessage = null
            return
        }

        if (newPassword.length < 8) {
            errorMessage = "New password must be at least 8 characters long."
            successMessage = null
            return
        }

        if (newPassword != confirmPassword) {
            errorMessage = "Passwords do not match."
            successMessage = null
            return
        }

        isLoading = true
        errorMessage = null
        successMessage = null

        coroutineScope.launch {
            val response = AuthNetworkClient.resetPassword(emailTrim, newPassword)
            isLoading = false

            if (response.isSuccess) {
                UserAccountRepository.resetPassword(emailTrim, newPassword)
                successMessage = response.message.ifEmpty { "Password reset successfully!" }
                errorMessage = null
                delay(1200)
                onResetSuccess()
            } else {
                // If backend was unreachable or returned failure, check local fallback
                val localSuccess = UserAccountRepository.resetPassword(emailTrim, newPassword)
                if (localSuccess) {
                    successMessage = "Password reset successfully (Offline Mode)!"
                    errorMessage = null
                    delay(1200)
                    onResetSuccess()
                } else {
                    errorMessage = response.message
                    successMessage = null
                }
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

            Spacer(modifier = Modifier.height(12.dp))

            // Icon with Gradient Glow
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
                Icon(
                    imageVector = Icons.Outlined.LockReset,
                    contentDescription = "Reset Password Icon",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title & Subtitle
            Text(
                text = "Reset Password",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1E2135)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Enter your registered email and a new password to restore your account.",
                fontSize = 13.sp,
                color = Color(0xFF7A8398),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Main Card
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
                    // Email
                    Text(
                        text = "Email address",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E2135)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // New Password
                    Text(
                        text = "New Password",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E2135)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        placeholder = {
                            Text("At least 8 characters", color = Color(0xFFA0A7B8), fontSize = 14.sp)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = "New Password Icon",
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // Confirm Password
                    Text(
                        text = "Confirm New Password",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E2135)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        placeholder = {
                            Text("Re-enter new password", color = Color(0xFFA0A7B8), fontSize = 14.sp)
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
                            color = Color(0xFF2E7D32),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Reset Password Button
                    Button(
                        onClick = { handleResetPassword() },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF5050F0),
                            contentColor = Color.White
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Reset Password",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Remember Password / Login Link
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Remember your password? ",
                    color = Color(0xFF7A8398),
                    fontSize = 13.sp
                )
                Text(
                    text = "Log In",
                    color = Color(0xFF5050F0),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onLoginClick() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Active Server Endpoint Indicator
            Text(
                text = "Server: ${AuthNetworkClient.getActiveUrl()} (Change)",
                color = Color(0xFF9AA4B8),
                fontSize = 10.sp,
                modifier = Modifier.clickable {
                    serverInput = AuthNetworkClient.getActiveUrl()
                    showServerDialog = true
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreenPreview() {
    ForgotPasswordScreen()
}

