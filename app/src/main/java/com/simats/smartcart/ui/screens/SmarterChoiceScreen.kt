package com.simats.smartcart.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
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

@Composable
fun SmarterChoiceScreen(
    product1Name: String = "JBL Tune 510BT",
    product2Name: String = "Soundcore H30i",
    onBackClick: () -> Unit = {},
    onTabSelected: (Int) -> Unit = {},
    onViewDetailsClick: (String) -> Unit = {},
    onSaveComparisonClick: () -> Unit = {}
) {
    var selectedBottomTab by remember { mutableIntStateOf(2) } // 2 is Compare tab
    var isSaved by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            SmarterChoiceBottomNavigationBar(
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
                            text = "The smarter choice",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E2135)
                        )
                        Text(
                            text = "Comparison continued · JBL vs Soundcore",
                            fontSize = 11.sp,
                            color = Color(0xFF7A8398)
                        )
                    }
                }

                IconButton(
                    onClick = {
                        isSaved = !isSaved
                        onSaveComparisonClick()
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Outlined.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = Color(0xFF5050F0),
                        modifier = Modifier.size(20.dp)
                    )
                }
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

            // AI SUMMARY · FOR YOUR NEEDS Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F4FF)),
                border = BorderStroke(1.dp, Color(0xFFE2E6F5))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFF5050F0),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "AI SUMMARY · FOR YOUR NEEDS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF5050F0),
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "JBL Tune 510BT wins for you.",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E2135)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Spend ₹300 more for a lighter headset and a better indoor call experience. Both cover a full day of classes comfortably on battery.",
                        fontSize = 13.sp,
                        color = Color(0xFF7A8398),
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Best for each priority Section
            Text(
                text = "Best for each priority",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E2135)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFECEFF8))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    PriorityRow(
                        icon = Icons.Outlined.Mic,
                        title = "Clear calls + lighter fit",
                        winner = "JBL"
                    )
                    HorizontalDivider(color = Color(0xFFF0F2F8), thickness = 1.dp)
                    PriorityRow(
                        icon = Icons.Outlined.BatteryChargingFull,
                        title = "Battery + app EQ",
                        winner = "Soundcore"
                    )
                    HorizontalDivider(color = Color(0xFFF0F2F8), thickness = 1.dp)
                    PriorityRow(
                        icon = Icons.Outlined.AccountBalanceWallet,
                        title = "Lower spend",
                        winner = "Soundcore"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // What you give up Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7E6)),
                border = BorderStroke(1.dp, Color(0xFFFFE8B8))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "What you give up",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB76E00)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Choose JBL: 30 fewer battery hours and no app EQ.",
                        fontSize = 12.sp,
                        color = Color(0xFF8A5300),
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Choose Soundcore: a heavier fit and less consistent calls in noisy rooms. Neither has active noise cancellation.",
                        fontSize = 12.sp,
                        color = Color(0xFF8A5300),
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Rubric Text
            Text(
                text = "Sample match rubric: call clarity 45%, comfort 30%, battery 15%, budget/value 10%. Change your priorities to change the recommendation.",
                fontSize = 10.sp,
                color = Color(0xFF9AA4B8),
                lineHeight = 14.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { onViewDetailsClick(product1Name) },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5050F0)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "View JBL details",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                OutlinedButton(
                    onClick = {
                        isSaved = !isSaved
                        onSaveComparisonClick()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E6F5)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF5050F0))
                ) {
                    Text(
                        text = if (isSaved) "Saved" else "Save comparison",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PriorityRow(
    icon: ImageVector,
    title: String,
    winner: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF7A8398),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color(0xFF1E2135),
                fontWeight = FontWeight.Medium
            )
        }
        Text(
            text = winner,
            fontSize = 13.sp,
            color = Color(0xFF5050F0),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SmarterChoiceBottomNavigationBar(
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
fun SmarterChoiceScreenPreview() {
    SmarterChoiceScreen()
}
