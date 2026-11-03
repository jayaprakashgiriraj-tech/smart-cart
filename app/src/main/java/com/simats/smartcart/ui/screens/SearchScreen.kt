package com.simats.smartcart.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SearchScreen(
    onBackClick: () -> Unit = {},
    onFindMatchesClick: (query: String, category: String, maxBudget: Int) -> Unit = { _, _, _ -> }
) {
    var selectedCategory by remember { mutableStateOf("Audio") }
    var maxBudget by remember { mutableFloatStateOf(3000f) }
    val selectedPreferences = remember { mutableStateListOf("Clear mic", "Comfort", "Battery life") }
    var followUpQuery by remember { mutableStateOf("") }

    val (budgetMin, budgetMax) = when (selectedCategory) {
        "Mobiles" -> 5000f to 100000f
        "Laptops" -> 20000f to 150000f
        "Fashion" -> 500f to 15000f
        "Home Appliances" -> 2000f to 60000f
        else -> 500f to 10000f
    }

    val options = when (selectedCategory) {
        "Mobiles" -> listOf("Pro camera", "Fast 5G", "Battery life", "Gaming speed", "AMOLED display")
        "Laptops" -> listOf("Fast processor", "Lightweight", "Long battery", "Gaming GPU", "Large SSD")
        "Fashion" -> listOf("Comfort fit", "Premium fabric", "Everyday wear", "Trendy design", "Ethnic wear")
        "Home Appliances" -> listOf("Energy saving", "Smart features", "Compact design", "Durability", "Fast cooling")
        else -> listOf("Clear mic", "Comfort", "Battery life", "Noise cancellation", "Deep bass")
    }

    Scaffold(
        bottomBar = {
            Surface(
                color = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                OutlinedTextField(
                    value = followUpQuery,
                    onValueChange = { followUpQuery = it },
                    placeholder = {
                        Text("Search a product (e.g. iPhone, Samsung)...", color = Color(0xFFA0A7B8), fontSize = 14.sp)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.AutoAwesome,
                            contentDescription = "AI",
                            tint = Color(0xFF5050F0),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (followUpQuery.isNotBlank()) {
                            IconButton(onClick = {
                                onFindMatchesClick(followUpQuery.trim(), selectedCategory, maxBudget.toInt())
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                                    contentDescription = "Find matches",
                                    tint = Color(0xFF5050F0),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Mic,
                                contentDescription = "Voice Input",
                                tint = Color(0xFF5050F0),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        onFindMatchesClick(followUpQuery.trim(), selectedCategory, maxBudget.toInt())
                    }),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFF5050F0),
                        unfocusedBorderColor = Color(0xFFE2E6F5)
                    )
                )
            }
        },
        containerColor = Color(0xFFFAFBFF)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "AI Shopping Assistant",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E2135)
                        )
                        Text(
                            text = "Your needs come first",
                            fontSize = 11.sp,
                            color = Color(0xFF7A8398)
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Outlined.AutoAwesome,
                    contentDescription = "AI",
                    tint = Color(0xFF5050F0),
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Disclaimer Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEEF0FF))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = Color(0xFF5050F0),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Illustrative data - not live retailer or review data",
                    fontSize = 11.sp,
                    color = Color(0xFF5050F0),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User Chat Bubble (Purple)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF5050F0))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = if (followUpQuery.isNotBlank()) {
                            "I'm searching for \"${followUpQuery.trim()}\"."
                        } else {
                            "I need $selectedCategory recommendations under ₹${maxBudget.toInt()}."
                        },
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "You · Just now",
                        color = Color(0xFFD6D3FF),
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // AI Assistant Response
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEEF0FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = "AI",
                        tint = Color(0xFF5050F0),
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (followUpQuery.isNotBlank()) {
                        "Got it. I'll search for \"${followUpQuery.trim()}\" and recommend best-matching products based on specs and value."
                    } else {
                        "Got it. I'll prioritize ${if (selectedPreferences.isNotEmpty()) selectedPreferences.joinToString(", ") else "top features"}. Let's fine-tune your shortlist."
                    },
                    fontSize = 13.sp,
                    color = Color(0xFF1E2135),
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Budget Slider Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFECEFF8))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Maximum budget",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E2135)
                        )
                        Text(
                            text = "₹${maxBudget.toInt()}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF5050F0)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = maxBudget.coerceIn(budgetMin, budgetMax),
                        onValueChange = { maxBudget = it },
                        valueRange = budgetMin..budgetMax,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF5050F0),
                            activeTrackColor = Color(0xFF5050F0),
                            inactiveTrackColor = Color(0xFFE8EBF4)
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("₹${budgetMin.toInt()}", fontSize = 11.sp, color = Color(0xFF7A8398))
                        Text("₹${budgetMax.toInt()}", fontSize = 11.sp, color = Color(0xFF7A8398))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Category Selection
            Text(
                text = "Category",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E2135)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CategoryChip(
                    title = "Audio",
                    icon = Icons.Outlined.Headphones,
                    isSelected = selectedCategory == "Audio",
                    onClick = {
                        selectedCategory = "Audio"
                        maxBudget = 3000f
                    }
                )
                CategoryChip(
                    title = "Mobiles",
                    icon = Icons.Outlined.Smartphone,
                    isSelected = selectedCategory == "Mobiles",
                    onClick = {
                        selectedCategory = "Mobiles"
                        maxBudget = 50000f
                    }
                )
                CategoryChip(
                    title = "Laptops",
                    icon = Icons.Outlined.Laptop,
                    isSelected = selectedCategory == "Laptops",
                    onClick = {
                        selectedCategory = "Laptops"
                        maxBudget = 70000f
                    }
                )
                CategoryChip(
                    title = "Fashion",
                    icon = Icons.Outlined.Checkroom,
                    isSelected = selectedCategory == "Fashion",
                    onClick = {
                        selectedCategory = "Fashion"
                        maxBudget = 3000f
                    }
                )
                CategoryChip(
                    title = "Home Appliances",
                    icon = Icons.Outlined.Kitchen,
                    isSelected = selectedCategory == "Home Appliances",
                    onClick = {
                        selectedCategory = "Home Appliances"
                        maxBudget = 25000f
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // What matters most? Selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "What matters most?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E2135)
                )
                Text(
                    text = "Pick up to 3",
                    fontSize = 11.sp,
                    color = Color(0xFF5050F0),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    options.take(3).forEach { option ->
                        PreferenceChip(
                            title = option,
                            isSelected = selectedPreferences.contains(option),
                            onClick = {
                                if (selectedPreferences.contains(option)) {
                                    selectedPreferences.remove(option)
                                } else if (selectedPreferences.size < 3) {
                                    selectedPreferences.add(option)
                                }
                            }
                        )
                    }
                }
                if (options.size > 3) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        options.drop(3).forEach { option ->
                            PreferenceChip(
                                title = option,
                                isSelected = selectedPreferences.contains(option),
                                onClick = {
                                    if (selectedPreferences.contains(option)) {
                                        selectedPreferences.remove(option)
                                    } else if (selectedPreferences.size < 3) {
                                        selectedPreferences.add(option)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Find my matches Button
            Button(
                onClick = {
                    onFindMatchesClick(followUpQuery.trim(), selectedCategory, maxBudget.toInt())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5050F0)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (followUpQuery.isNotBlank()) "Find matches for \"${followUpQuery.trim()}\"" else "Find my matches",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CategoryChip(
    title: String,
    icon: ImageVector? = null,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) Color(0xFFEEF0FF) else Color.White,
        border = BorderStroke(
            1.dp,
            if (isSelected) Color(0xFFC0C8F5) else Color(0xFFECEFF8)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) Color(0xFF5050F0) else Color(0xFF7A8398),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = title,
                fontSize = 12.sp,
                color = if (isSelected) Color(0xFF5050F0) else Color(0xFF1E2135),
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
private fun PreferenceChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) Color(0xFFEEF0FF) else Color.White,
        border = BorderStroke(
            1.dp,
            if (isSelected) Color(0xFFC0C8F5) else Color(0xFFECEFF8)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = null,
                    tint = Color(0xFF5050F0),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = title,
                fontSize = 12.sp,
                color = if (isSelected) Color(0xFF5050F0) else Color(0xFF1E2135),
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    SearchScreen()
}
