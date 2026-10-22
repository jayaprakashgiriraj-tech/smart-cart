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
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    onTabSelected: (Int) -> Unit = {},
    onCategoryClick: (String) -> Unit = {},
    onProductClick: (String) -> Unit = {},
    onCompareClick: () -> Unit = {},
    onSearchClick: (String) -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedBottomTab by remember { mutableIntStateOf(0) }
    var selectedCategory by remember { mutableStateOf("Audio") }

    Scaffold(
        bottomBar = {
            HomeBottomNavigationBar(
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
                        text = "SmartCart AI",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E2135)
                    )
                    Text(
                        text = "Shop Smarter. Buy Better.",
                        fontSize = 11.sp,
                        color = Color(0xFF7A8398)
                    )
                }

                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, Color(0xFFECEFF8), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = Color(0xFF5050F0),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Greeting
            Text(
                text = "Hey there! What are you\nlooking for?",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1E2135),
                lineHeight = 32.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // AI Search Field
            Box(modifier = Modifier.fillMaxWidth().clickable { onSearchClick(searchQuery) }) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    readOnly = true,
                    enabled = false,
                    placeholder = {
                        Text("Describe what you need...", color = Color(0xFFA0A7B8), fontSize = 14.sp)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.AutoAwesome,
                            contentDescription = "AI Search",
                            tint = Color(0xFF5050F0),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Mic,
                            contentDescription = "Voice Search",
                            tint = Color(0xFF5050F0),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledContainerColor = Color.White,
                        disabledBorderColor = Color(0xFFE2E6F5),
                        disabledPlaceholderColor = Color(0xFFA0A7B8),
                        disabledLeadingIconColor = Color(0xFF5050F0),
                        disabledTrailingIconColor = Color(0xFF5050F0)
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Search Prompt Suggestion
            Text(
                text = "Try \"Best phone under ₹25,000 with a good camera\"",
                fontSize = 11.sp,
                color = Color(0xFF7A8398)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Category Icons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CategoryItem(
                    title = "Mobiles",
                    icon = Icons.Outlined.Smartphone,
                    isSelected = selectedCategory == "Mobiles",
                    onClick = {
                        selectedCategory = "Mobiles"
                        onCategoryClick("Mobiles")
                    }
                )
                CategoryItem(
                    title = "Laptops",
                    icon = Icons.Outlined.Laptop,
                    isSelected = selectedCategory == "Laptops",
                    onClick = {
                        selectedCategory = "Laptops"
                        onCategoryClick("Laptops")
                    }
                )
                CategoryItem(
                    title = "Audio",
                    icon = Icons.Outlined.Headphones,
                    isSelected = selectedCategory == "Audio",
                    onClick = {
                        selectedCategory = "Audio"
                        onCategoryClick("Audio")
                    }
                )
                CategoryItem(
                    title = "Fashion",
                    icon = Icons.Outlined.Checkroom,
                    isSelected = selectedCategory == "Fashion",
                    onClick = {
                        selectedCategory = "Fashion"
                        onCategoryClick("Fashion")
                    }
                )
                CategoryItem(
                    title = "Home\nAppliances",
                    icon = Icons.Outlined.Kitchen,
                    isSelected = selectedCategory == "Home Appliances",
                    onClick = {
                        selectedCategory = "Home Appliances"
                        onCategoryClick("Home Appliances")
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Popular Searches Section
            Text(
                text = "Popular searches",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E2135)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                PopularSearchChip(
                    text = "Headphones under ₹3k",
                    icon = Icons.AutoMirrored.Outlined.TrendingUp,
                    onClick = { onSearchClick("Headphones under ₹3k") }
                )
                PopularSearchChip(
                    text = "Study laptops",
                    onClick = { onSearchClick("Study laptops") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Recommended for You Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recommended for You",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E2135)
                )
                Text(
                    text = "See all",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5050F0),
                    modifier = Modifier.clickable { }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Recommendation Product Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onProductClick("JBL Tune 510BT") },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFECEFF8))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF5F6FA)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Headphones,
                            contentDescription = "JBL Tune 510BT",
                            tint = Color(0xFF2A2A2A),
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "JBL Tune 510BT",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E2135)
                            )
                            Text(
                                text = "92% match",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF5050F0)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "₹2,799",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E2135)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Star,
                                contentDescription = "Rating",
                                tint = Color(0xFFFFB800),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "4.3",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E2135)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "40 hours battery",
                                fontSize = 11.sp,
                                color = Color(0xFF7A8398)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Sample suggestion based on your Audio preferences.",
                fontSize = 10.sp,
                color = Color(0xFF9AA4B8)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Recently Compared Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recently compared",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E2135)
                )
                Text(
                    text = "View activity",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5050F0),
                    modifier = Modifier.clickable { onCompareClick() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Comparison History Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCompareClick() },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFECEFF8))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
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
                            imageVector = Icons.AutoMirrored.Outlined.CompareArrows,
                            contentDescription = "Compare",
                            tint = Color(0xFF5050F0),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "JBL Tune 510BT vs Soundcore H30i",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E2135),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Online classes · 2 products · Today",
                            fontSize = 11.sp,
                            color = Color(0xFF7A8398)
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        contentDescription = "Details",
                        tint = Color(0xFF7A8398),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CategoryItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(58.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (isSelected) Color(0xFF5050F0) else Color(0xFFF2F4FF))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) Color.White else Color(0xFF5050F0),
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            color = Color(0xFF1E2135),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
            lineHeight = 13.sp
        )
    }
}

@Composable
private fun PopularSearchChip(
    text: String,
    icon: ImageVector? = null,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFECEFF8))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF7A8398),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                fontSize = 12.sp,
                color = Color(0xFF1E2135),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun HomeBottomNavigationBar(
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
fun HomeScreenPreview() {
    HomeScreen()
}
