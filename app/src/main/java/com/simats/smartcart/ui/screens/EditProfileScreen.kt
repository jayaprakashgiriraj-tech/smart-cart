package com.simats.smartcart.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.smartcart.data.UserAccountRepository
import com.simats.smartcart.network.AuthNetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit = {},
    onSaveSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val currentUser = UserAccountRepository.currentUser

    var name by remember { mutableStateOf(currentUser?.name ?: "Jayaprakash G") }
    var email by remember { mutableStateOf(currentUser?.email ?: "jayaprakashgiriraj@gmail.com") }

    var profileBitmap by remember {
        val existingPhotoPath = currentUser?.profilePhoto
        val initialBitmap = if (!existingPhotoPath.isNullOrBlank()) {
            try {
                val f = File(existingPhotoPath)
                if (f.exists()) BitmapFactory.decodeFile(f.absolutePath) else null
            } catch (_: Exception) {
                null
            }
        } else null
        mutableStateOf<Bitmap?>(initialBitmap)
    }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    var showServerDialog by remember { mutableStateOf(false) }
    var serverInput by remember { mutableStateOf(AuthNetworkClient.getActiveUrl()) }

    // System Photo Picker launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val bmp = BitmapFactory.decodeStream(stream)
                    if (bmp != null) {
                        profileBitmap = bmp
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Failed to load selected photo."
            }
        }
    }

    // Fallback Image Picker
    val fallbackGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val bmp = BitmapFactory.decodeStream(stream)
                    if (bmp != null) {
                        profileBitmap = bmp
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Failed to load selected photo."
            }
        }
    }

    fun openGallery() {
        try {
            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } catch (_: Exception) {
            fallbackGalleryLauncher.launch("image/*")
        }
    }

    fun handleSave() {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) {
            errorMessage = "Name cannot be empty."
            successMessage = null
            return
        }

        isLoading = true
        errorMessage = null
        successMessage = null

        coroutineScope.launch {
            var savedPhotoPath = currentUser?.profilePhoto

            // Save selected gallery image to app's internal files directory
            if (profileBitmap != null && selectedImageUri != null) {
                savedPhotoPath = withContext(Dispatchers.IO) {
                    try {
                        val file = File(context.filesDir, "profile_photo.jpg")
                        FileOutputStream(file).use { out ->
                            profileBitmap?.compress(Bitmap.CompressFormat.JPEG, 92, out)
                        }
                        file.absolutePath
                    } catch (e: Exception) {
                        currentUser?.profilePhoto
                    }
                }
            }

            // Immediately update in-memory user repository
            UserAccountRepository.updateProfile(trimmedName, email.trim(), savedPhotoPath)

            // Sync with backend if connected
            withContext(Dispatchers.IO) {
                try {
                    AuthNetworkClient.updateProfile(trimmedName, email.trim())
                    if (savedPhotoPath != null) {
                        val photoFile = File(savedPhotoPath)
                        if (photoFile.exists()) {
                            AuthNetworkClient.uploadProfilePhoto(email.trim(), photoFile)
                        }
                    }
                } catch (_: Exception) {
                    // Local changes are preserved even if offline
                }
            }

            isLoading = false
            successMessage = "Profile updated successfully!"
            delay(700)
            onSaveSuccess()
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
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF1E2135)
                    )
                }

                Text(
                    text = "Edit Profile",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1E2135)
                )

                TextButton(
                    onClick = { handleSave() },
                    enabled = !isLoading
                ) {
                    Text(
                        text = "Save",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5050F0)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Profile Photo with Camera Badge (Tap to open Gallery)
            Box(
                modifier = Modifier
                    .size(108.dp)
                    .clickable { openGallery() },
                contentAlignment = Alignment.BottomEnd
            ) {
                if (profileBitmap != null) {
                    Image(
                        bitmap = profileBitmap!!.asImageBitmap(),
                        contentDescription = "Profile Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(104.dp)
                            .clip(CircleShape)
                            .border(3.dp, Color(0xFF5050F0), CircleShape)
                    )
                } else {
                    val initials = if (name.isNotBlank()) {
                        name.split(" ")
                            .filter { it.isNotBlank() }
                            .take(2)
                            .map { it.first().uppercaseChar() }
                            .joinToString("")
                    } else "U"

                    Box(
                        modifier = Modifier
                            .size(104.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFFE8EAFE), Color(0xFFD4D8FC))
                                )
                            )
                            .border(2.dp, Color(0xFF5050F0), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF5050F0)
                        )
                    }
                }

                // Camera Badge Button
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF5050F0))
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PhotoCamera,
                        contentDescription = "Change Photo",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tap photo button
            TextButton(onClick = { openGallery() }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.AddPhotoAlternate,
                        contentDescription = null,
                        tint = Color(0xFF5050F0),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (profileBitmap != null) "Change Profile Photo" else "Add Profile Photo",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5050F0)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Input Fields Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFECEFF8))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Name Field
                    Text(
                        text = "Full Name",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E2135)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text("Enter your full name", color = Color(0xFFA0A7B8), fontSize = 14.sp) },
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
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            keyboardType = KeyboardType.Text
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF7F8FC),
                            unfocusedContainerColor = Color(0xFFF7F8FC),
                            focusedBorderColor = Color(0xFF5050F0),
                            unfocusedBorderColor = Color(0xFFE8EBF4)
                        )
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Email Field (Display only or editable)
                    Text(
                        text = "Email Address",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E2135)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("your.email@example.com", color = Color(0xFFA0A7B8), fontSize = 14.sp) },
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
                        readOnly = true, // Primary identifier in database
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF1F3F9),
                            unfocusedContainerColor = Color(0xFFF1F3F9),
                            focusedBorderColor = Color(0xFFD5DBEC),
                            unfocusedBorderColor = Color(0xFFE8EBF4)
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Email is linked to your SmartCart AI account.",
                        fontSize = 11.sp,
                        color = Color(0xFF9AA4B8)
                    )

                    // Error Callout
                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = errorMessage!!,
                            color = Color(0xFFD32F2F),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Success Callout
                    if (successMessage != null) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = successMessage!!,
                            color = Color(0xFF2E7D32),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Save Changes Button
                    Button(
                        onClick = { handleSave() },
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
                                text = "Save Changes",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Backend Server Settings Link
            Text(
                text = "Server: ${AuthNetworkClient.getActiveUrl()} (Change)",
                color = Color(0xFF9AA4B8),
                fontSize = 10.sp,
                modifier = Modifier.clickable {
                    serverInput = AuthNetworkClient.getActiveUrl()
                    showServerDialog = true
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditProfileScreenPreview() {
    EditProfileScreen()
}

