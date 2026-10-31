package com.simats.smartcart.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.CompareArrows
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
import com.simats.smartcart.data.UserAccountRepository

@Composable
fun ProfileScreen(
    onTabSelected: (Int) -> Unit = {},
    onShoppingHistoryClick: () -> Unit = {},
    onSavedComparisonsClick: () -> Unit = {},
    onEditPreferencesClick: () -> Unit = {},
    onPrivacyAndDataClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {}
) {
    var selectedCategoryTab by remember { mutableIntStateOf(0) } // 0: Audio, 1: Mobiles, 2: Laptops
    var selectedBottomTab by remember { mutableIntStateOf(4) } // 4 is Profile tab

    Scaffold(
        bottomBar = {
            ProfileBottomNavigationBar(
                selectedTab = selectedBottomTab,
                onTabSelected = { tab ->
                    selectedBottomTab = tab
                    onTabSelected(tab)
                }
            )
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
                Column {
                    Text(
                        text = "Profile",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E2135)
                    )
                    Text(
                        text = "Your needs, remembered",
                        fontSize = 11.sp,
                        color = Color(0xFF7A8398)
                    )
                }

                IconButton(
                    onClick = { onEditPreferencesClick() },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings",
                        tint = Color(0xFF5050F0),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFECEFF8))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val currentUser = UserAccountRepository.currentUser
                    val displayName = currentUser?.name ?: "Aarav Mehta"
                    val displayEmail = currentUser?.email ?: "aarav.mehta@example.com"
                    val initials = if (displayName.isNotBlank()) {
                        displayName.split(" ")
                            .filter { it.isNotBlank() }
                            .take(2)
                            .map { it.first().uppercaseChar() }
                            .joinToString("")
                    } else "U"

                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF2F4FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF5050F0)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = displayName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E2135)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = displayEmail,
                            fontSize = 12.sp,
                            color = Color(0xFF7A8398)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Illustrative account · Edit profile",
                            fontSize = 11.sp,
                            color = Color(0xFF5050F0),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.clickable { onEditProfileClick() }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Your Shopping Preferences Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your shopping preferences",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E2135)
                )
                Text(
                    text = "Edit",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5050F0),
                    modifier = Modifier.clickable { onEditPreferencesClick() }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

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
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val categories = listOf("Audio", "Mobiles", "Laptops")
                        categories.forEachIndexed { index, title ->
                            val isSelected = selectedCategoryTab == index
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) Color(0xFFF2F4FF) else Color(0xFFF5F6FA))
                                    .border(
                                        width = if (isSelected) 1.dp else 0.dp,
                                        color = if (isSelected) Color(0xFFD0D7FF) else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedCategoryTab = index }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color(0xFF5050F0) else Color(0xFF7A8398)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Audio budget: ₹3,000",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1E2135)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Top features: clear mic, comfort, battery life",
                        fontSize = 11.sp,
                        color = Color(0xFF7A8398)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onEditPreferencesClick() },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Preferences",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5050F0)
                        )
                        Icon(
                            imageVector = Icons.Outlined.ChevronRight,
                            contentDescription = null,
                            tint = Color(0xFF5050F0),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Your Activity Section
            Text(
                text = "Your activity",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E2135)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFECEFF8))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ActivityRow(
                        icon = Icons.Outlined.History,
                        title = "Shopping history",
                        subtitle = "JBL Tune 510BT viewed · Today",
                        onClick = onShoppingHistoryClick
                    )

                    HorizontalDivider(color = Color(0xFFF0F2F8), thickness = 1.dp)

                    ActivityRow(
                        icon = Icons.AutoMirrored.Outlined.CompareArrows,
                        title = "Saved comparisons",
                        subtitle = "1 comparison · Online classes",
                        onClick = onSavedComparisonsClick
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Settings & Privacy Group
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFECEFF8))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ActivityRow(
                        icon = Icons.Outlined.Notifications,
                        title = "Notifications",
                        subtitle = "Price alerts on · Recommendations off",
                        onClick = { onEditPreferencesClick() }
                    )

                    HorizontalDivider(color = Color(0xFFF0F2F8), thickness = 1.dp)

                    ActivityRow(
                        icon = Icons.Outlined.Shield,
                        title = "Privacy & data",
                        subtitle = "Control your personalization",
                        onClick = { onPrivacyAndDataClick() }
                    )

                    HorizontalDivider(color = Color(0xFFF0F2F8), thickness = 1.dp)

                    ActivityRow(
                        icon = Icons.Outlined.PersonOutline,
                        title = "Account settings",
                        subtitle = "Security, sign-in and account removal",
                        onClick = { onEditProfileClick() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ActivityRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
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

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E2135)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color(0xFF7A8398)
            )
        }

        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = Color(0xFF9AA4B8),
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun ProfileBottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Home,
                    contentDescription = "Home"
                )
            },
            label = { Text("Home", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF5050F0),
                selectedTextColor = Color(0xFF5050F0),
                indicatorColor = Color(0xFFF2F4FF),
                unselectedIconColor = Color(0xFF9AA4B8),
                unselectedTextColor = Color(0xFF9AA4B8)
            )
        )

        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Explore,
                    contentDescription = "Discover"
                )
            },
            label = { Text("Discover", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF5050F0),
                selectedTextColor = Color(0xFF5050F0),
                indicatorColor = Color(0xFFF2F4FF),
                unselectedIconColor = Color(0xFF9AA4B8),
                unselectedTextColor = Color(0xFF9AA4B8)
            )
        )

        NavigationBarItem(
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.CompareArrows,
                    contentDescription = "Compare"
                )
            },
            label = { Text("Compare", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF5050F0),
                selectedTextColor = Color(0xFF5050F0),
                indicatorColor = Color(0xFFF2F4FF),
                unselectedIconColor = Color(0xFF9AA4B8),
                unselectedTextColor = Color(0xFF9AA4B8)
            )
        )

        NavigationBarItem(
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.FavoriteBorder,
                    contentDescription = "Wishlist"
                )
            },
            label = { Text("Wishlist", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF5050F0),
                selectedTextColor = Color(0xFF5050F0),
                indicatorColor = Color(0xFFF2F4FF),
                unselectedIconColor = Color(0xFF9AA4B8),
                unselectedTextColor = Color(0xFF9AA4B8)
            )
        )

        NavigationBarItem(
            selected = selectedTab == 4,
            onClick = { onTabSelected(4) },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.PersonOutline,
                    contentDescription = "Profile"
                )
            },
            label = { Text("Profile", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF5050F0),
                selectedTextColor = Color(0xFF5050F0),
                indicatorColor = Color(0xFFF2F4FF),
                unselectedIconColor = Color(0xFF9AA4B8),
                unselectedTextColor = Color(0xFF9AA4B8)
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}
