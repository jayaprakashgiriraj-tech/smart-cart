package com.simats.smartcart.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SavedDecisionsScreen(
    onTabSelected: (Int) -> Unit = {},
    onCompareClick: () -> Unit = {},
    onOpenComparisonClick: () -> Unit = {},
    onPriceDropClick: (String) -> Unit = {}
) {
    var selectedTopTab by remember { mutableIntStateOf(0) } // 0 is Wishlist, 1 is Comparisons
    var selectedBottomTab by remember { mutableIntStateOf(3) } // 3 is Wishlist tab
    var priceDropJbl by remember { mutableStateOf(true) }
    var priceDropSoundcore by remember { mutableStateOf(true) }

    Scaffold(
        bottomBar = {
            SavedDecisionsBottomNavigationBar(
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
                        text = "Your saved decisions",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E2135)
                    )
                    Text(
                        text = "Wishlist & Saved Comparisons",
                        fontSize = 11.sp,
                        color = Color(0xFF7A8398)
                    )
                }

                IconButton(
                    onClick = { },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search",
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

            // Segmented Tab Switcher (Wishlist · 2 vs Comparisons · 1)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(4.dp)
            ) {
                Button(
                    onClick = { selectedTopTab = 0 },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTopTab == 0) Color(0xFF5050F0) else Color.Transparent,
                        contentColor = if (selectedTopTab == 0) Color.White else Color(0xFF7A8398)
                    ),
                    elevation = null
                ) {
                    Text("Wishlist · 2", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { selectedTopTab = 1 },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTopTab == 1) Color(0xFF5050F0) else Color.Transparent,
                        contentColor = if (selectedTopTab == 1) Color.White else Color(0xFF7A8398)
                    ),
                    elevation = null
                ) {
                    Text("Comparisons · 1", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Wishlist Item 1: JBL Tune 510BT
            WishlistItemCard(
                name = "JBL Tune 510BT",
                price = "₹2,799",
                originalPrice = "₹2,999",
                decisionTag = "BUY",
                isBuyTag = true,
                priceDropChecked = priceDropJbl,
                onPriceDropChange = {
                    priceDropJbl = it
                },
                onPriceDropClick = { onPriceDropClick("JBL Tune 510BT") },
                onCompareClick = onCompareClick
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Wishlist Item 2: Soundcore H30i
            WishlistItemCard(
                name = "Soundcore H30i",
                price = "₹2,499",
                originalPrice = "₹2,699",
                decisionTag = "CONSIDER",
                isBuyTag = false,
                priceDropChecked = priceDropSoundcore,
                onPriceDropChange = {
                    priceDropSoundcore = it
                },
                onPriceDropClick = { onPriceDropClick("Soundcore H30i") },
                onCompareClick = onCompareClick
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Saved Comparisons Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Saved comparisons",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E2135)
                )
                Text(
                    text = "1 saved",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5050F0)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Saved Comparison Item
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenComparisonClick() },
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
                            color = Color(0xFF1E2135)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Online classes · Saved today",
                            fontSize = 11.sp,
                            color = Color(0xFF7A8398)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onOpenComparisonClick() }
                    ) {
                        Text(
                            text = "Open",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5050F0)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.Outlined.ChevronRight,
                            contentDescription = "Open",
                            tint = Color(0xFF5050F0),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Demo alerts are visual only. Prices are sample observations, not live retailer offers.",
                fontSize = 10.sp,
                color = Color(0xFF9AA4B8),
                lineHeight = 14.sp
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun WishlistItemCard(
    name: String,
    price: String,
    originalPrice: String,
    decisionTag: String,
    isBuyTag: Boolean,
    priceDropChecked: Boolean,
    onPriceDropChange: (Boolean) -> Unit,
    onPriceDropClick: () -> Unit = {},
    onCompareClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFECEFF8))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                        contentDescription = name,
                        tint = Color(0xFF2A2A2A),
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E2135)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = price,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E2135)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = originalPrice,
                            fontSize = 11.sp,
                            color = Color(0xFF9AA4B8),
                            textDecoration = TextDecoration.LineThrough
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Now / previously recorded · sample",
                        fontSize = 10.sp,
                        color = Color(0xFF7A8398)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isBuyTag) Color(0xFFE6F5F0) else Color(0xFFFFF7E6))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isBuyTag) "✓ BUY" else "— CONSIDER",
                                color = if (isBuyTag) Color(0xFF1A8068) else Color(0xFFB78103),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Saved AI verdict",
                            fontSize = 10.sp,
                            color = Color(0xFF7A8398)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF0F2F8), thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Action Controls Row: Switch Price drops | Compare | Remove
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(
                        checked = priceDropChecked,
                        onCheckedChange = {
                            onPriceDropChange(it)
                        },
                        modifier = Modifier.scale(0.7f),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF5050F0)
                        )
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "Price drops",
                        fontSize = 11.sp,
                        color = Color(0xFF5050F0),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onPriceDropClick() }
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onCompareClick() }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.CompareArrows,
                        contentDescription = "Compare",
                        tint = Color(0xFF5050F0),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Compare",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5050F0)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Remove",
                        tint = Color(0xFF7A8398),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Remove",
                        fontSize = 11.sp,
                        color = Color(0xFF7A8398)
                    )
                }
            }
        }
    }
}

@Composable
private fun SavedDecisionsBottomNavigationBar(
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
                    imageVector = Icons.Outlined.Favorite,
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
fun SavedDecisionsScreenPreview() {
    SavedDecisionsScreen()
}
