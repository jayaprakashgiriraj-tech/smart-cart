package com.simats.smartcart.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
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
fun LoginScreen(
    onLoginClick: (String, String) -> Unit = { _, _ -> },
    onSignUpClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onGoogleSignInClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    var showServerDialog by remember { mutableStateOf(false) }
    var serverInput by remember { mutableStateOf(AuthNetworkClient.getActiveUrl()) }

    fun handleLogin() {
        val emailTrim = email.trim()
        if (emailTrim.isEmpty() || password.isEmpty()) {
            errorMessage = "Please enter both email and password."
            return
        }

        if (!emailTrim.contains("@") || !emailTrim.contains(".")) {
            errorMessage = "Please enter a valid email address."
            return
        }

        isLoading = true
        errorMessage = null

        coroutineScope.launch {
            val response = AuthNetworkClient.loginUser(emailTrim, password)
            isLoading = false

            if (response.isSuccess) {
                errorMessage = null
                if (response.user != null) {
                    UserAccountRepository.currentUser = response.user
                }
                onLoginClick(emailTrim, password)
            } else {
                errorMessage = response.message
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFBFF))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top App Icon with Gradient Glow
        Box(
            modifier = Modifier
                .size(72.dp)
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
                    modifier = Modifier.size(36.dp)
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

        Spacer(modifier = Modifier.height(16.dp))

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

        Spacer(modifier = Modifier.height(24.dp))

        // Welcome Back Header
        Text(
            text = "Welcome back",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF1E2135)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Log in to find your next best choice.",
            fontSize = 13.sp,
            color = Color(0xFF7A8398)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Main Login Card
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
                // Email Label
                Text(
                    text = "Email address",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E2135)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Email Input
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

                // Password Label
                Text(
                    text = "Password",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E2135)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Password Input
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = {
                        Text("Enter your password", color = Color(0xFFA0A7B8), fontSize = 14.sp)
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

                Spacer(modifier = Modifier.height(10.dp))

                // Forgot Password Link
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = "Forgot password?",
                        color = Color(0xFF5050F0),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { onForgotPasswordClick() }
                            .padding(vertical = 6.dp, horizontal = 4.dp)
                    )
                }

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

                Spacer(modifier = Modifier.height(16.dp))

                // Log In Button
                Button(
                    onClick = { handleLogin() },
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
                            text = "Log in",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Divider: "or continue with"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFECEFF8),
                        thickness = 1.dp
                    )
                    Text(
                        text = "or continue with",
                        color = Color(0xFF9AA4B8),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFECEFF8),
                        thickness = 1.dp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sign Up Link
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "New to SmartCart AI? ",
                        color = Color(0xFF7A8398),
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Sign up",
                        color = Color(0xFF5050F0),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onSignUpClick() }
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

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
