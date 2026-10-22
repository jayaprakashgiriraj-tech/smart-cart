package com.simats.smartcart.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PreferencesScreen(
    onBackClick: () -> Unit = {},
    onSavePreferencesClick: () -> Unit = {}
) {
    var audioSelected by remember { mutableStateOf(true) }
    var mobilesSelected by remember { mutableStateOf(true) }
    var laptopsSelected by remember { mutableStateOf(true) }
    var fashionSelected by remember { mutableStateOf(false) }
    var homeAppliancesSelected by remember { mutableStateOf(false) }

    var audioBudget by remember { mutableFloatStateOf(3000f) }

    var priceDropAlerts by remember { mutableStateOf(true) }
    var newRecommendations by remember { mutableStateOf(false) }
    var useShoppingHistory by remember { mutableStateOf(true) }

    var isSavedToastShown by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                tonalElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Button(
                        onClick = {
                            isSavedToastShown = true
                            onSavePreferencesClick()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5050F0))
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isSavedToastShown) "Preferences saved!" else "Save preferences",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
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
                            text = "Preferences",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E2135)
                        )
                        Text(
                            text = "Profile / Preferences",
                            fontSize = 11.sp,
                            color = Color(0xFF7A8398)
                        )
                    }
                }

                IconButton(
                    onClick = { },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Tune,
                        contentDescription = "Tune",
                        tint = Color(0xFF5050F0),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Intro text
            Text(
                text = "Tell us what matters. Your defaults shape future recommendations, and you can change them anytime.",
                fontSize = 12.sp,
                color = Color(0xFF7A8398),
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Favorite Categories Section
            Text(
                text = "Favorite categories",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E2135)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category Chips Grid
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CategoryChip(
                        label = "Audio",
                        isSelected = audioSelected,
                        onClick = { audioSelected = !audioSelected }
                    )
                    CategoryChip(
                        label = "Mobiles",
                        isSelected = mobilesSelected,
                        onClick = { mobilesSelected = !mobilesSelected }
                    )
                    CategoryChip(
                        label = "Laptops",
                        isSelected = laptopsSelected,
                        onClick = { laptopsSelected = !laptopsSelected }
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CategoryChip(
                        label = "Fashion",
                        isSelected = fashionSelected,
                        onClick = { fashionSelected = !fashionSelected }
                    )
                    CategoryChip(
                        label = "Home Appliances",
                        isSelected = homeAppliancesSelected,
                        onClick = { homeAppliancesSelected = !homeAppliancesSelected }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Audio Budget Card
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
                            text = "Audio budget",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E2135)
                        )
                        Icon(
                            imageVector = Icons.Outlined.Headphones,
                            contentDescription = null,
                            tint = Color(0xFF5050F0),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

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
                            text = "₹${audioBudget.toInt()}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF5050F0)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = audioBudget,
                        onValueChange = { audioBudget = it },
                        valueRange = 500f..5000f,
                        steps = 44,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF5050F0),
                            activeTrackColor = Color(0xFF5050F0),
                            inactiveTrackColor = Color(0xFFE5E8F5)
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("₹500", fontSize = 10.sp, color = Color(0xFF9AA4B8))
                        Text("₹5,000", fontSize = 10.sp, color = Color(0xFF9AA4B8))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Mobiles: ₹25,000 · Laptops: ₹60,000",
                        fontSize = 11.sp,
                        color = Color(0xFF7A8398)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Audio Feature Priorities Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Audio feature priorities",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E2135)
                )
                Text(
                    text = "Ranked for you",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5050F0)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PriorityChip(number = "1", label = "Clear mic")
                PriorityChip(number = "2", label = "Comfort")
                PriorityChip(number = "3", label = "Battery")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "For online classes. Price/value always stays in the mix. Update priorities for a different use case.",
                fontSize = 11.sp,
                color = Color(0xFF7A8398),
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Toggles Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFECEFF8))
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    ToggleRow(
                        icon = Icons.Outlined.Notifications,
                        title = "Price-drop alerts",
                        checked = priceDropAlerts,
                        onCheckedChange = { priceDropAlerts = it }
                    )

                    HorizontalDivider(color = Color(0xFFF0F2F8), thickness = 1.dp)

                    ToggleRow(
                        icon = Icons.Outlined.AutoAwesome,
                        title = "New recommendations",
                        checked = newRecommendations,
                        onCheckedChange = { newRecommendations = it }
                    )

                    HorizontalDivider(color = Color(0xFFF0F2F8), thickness = 1.dp)

                    ToggleRow(
                        icon = Icons.Outlined.Shield,
                        title = "Use shopping history",
                        subtitle = "For personalized suggestions",
                        checked = useShoppingHistory,
                        onCheckedChange = { useShoppingHistory = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun CategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0xFFEEF0FF) else Color.White)
            .border(
                width = 1.dp,
                color = if (isSelected) Color(0xFFD0D7FF) else Color(0xFFECEFF8),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
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
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color(0xFF5050F0) else Color(0xFF1E2135)
            )
        }
    }
}

@Composable
private fun PriorityChip(
    number: String,
    label: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFEEF0FF))
            .border(1.dp, Color(0xFFD0D7FF), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = "$number  $label",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF5050F0)
        )
    }
}

@Composable
private fun ToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF2F4FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color(0xFF5050F0),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E2135)
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = Color(0xFF7A8398)
                    )
                }
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF5050F0)
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreferencesScreenPreview() {
    PreferencesScreen()
}
