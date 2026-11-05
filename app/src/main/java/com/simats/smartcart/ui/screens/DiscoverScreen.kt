package com.simats.smartcart.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.smartcart.data.Product
import com.simats.smartcart.data.ProductCatalog
import com.simats.smartcart.data.storeComparison

@Composable
fun DiscoverScreen(
    onTabSelected: (Int) -> Unit = {},
    onProductClick: (String) -> Unit = {},
    onCompareSelectedClick: (String, String) -> Unit = { _, _ -> }
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedNeedChip by remember { mutableStateOf<String?>(null) }
    var selectedBottomTab by remember { mutableIntStateOf(1) } // 1 is Discover tab

    val categories = listOf("All", "Mobiles", "Laptops", "Audio", "Fashion", "Home Appliances")

    val needChips = remember(selectedCategory) {
        when (selectedCategory) {
            "Mobiles" -> listOf("Pro Camera", "Under ₹25,000", "Flagship 5G", "Long battery", "Gaming")
            "Laptops" -> listOf("Online classes", "Student & Study", "Thin & Light", "High FPS Gaming", "Long battery")
            "Audio" -> listOf("Clear calls", "Long battery", "Under ₹3,000", "Travel audio", "Work-from-home")
            "Fashion" -> listOf("Dresses for Women", "Dresses for Girls", "Watches for Men", "Watches & Accessories", "Ethnic Wear")
            "Home Appliances" -> listOf("Home & Kitchen", "Smart TV", "Air Fryer", "Double Door Fridge", "Washing Machine")
            else -> listOf("Clear calls", "Long battery", "Under ₹3,000", "Online classes", "Pro Camera", "Gaming", "Dresses", "Watches & Accessories", "Home & Kitchen")
        }
    }

    val onChipClicked: (String) -> Unit = { chip ->
        if (selectedNeedChip == chip) {
            selectedNeedChip = null
            searchQuery = ""
        } else {
            selectedNeedChip = chip
            searchQuery = chip
        }
    }

    val filteredProducts: List<Product> = remember(searchQuery, selectedCategory) {
        val query = searchQuery.trim()
        if (query.isEmpty()) {
            if (selectedCategory == "All") {
                ProductCatalog.products
            } else {
                ProductCatalog.getProductsByCategory(selectedCategory)
            }
        } else {
            ProductCatalog.searchProducts(
                query = query,
                categoryFilter = if (selectedCategory == "All") null else selectedCategory
            )
        }
    }

    Scaffold(
        bottomBar = {
            DiscoverBottomNavigationBar(
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
                        text = "Discover",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E2135)
                    )
                    Text(
                        text = "A little less browsing. A lot more clarity.",
                        fontSize = 11.sp,
                        color = Color(0xFF7A8398)
                    )
                }

                IconButton(
                    onClick = {
                        searchQuery = ""
                        selectedNeedChip = null
                        selectedCategory = "All"
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Tune,
                        contentDescription = "Filter",
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

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    if (it != selectedNeedChip) {
                        selectedNeedChip = null
                    }
                },
                placeholder = {
                    Text("Search a need or a product", color = Color(0xFFA0A7B8), fontSize = 14.sp)
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
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery = ""
                            selectedNeedChip = null
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Clear search",
                                tint = Color(0xFF5050F0),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Mic,
                            contentDescription = "Voice Search",
                            tint = Color(0xFF5050F0),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
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

            Spacer(modifier = Modifier.height(14.dp))

            // Category Filter Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Surface(
                        onClick = {
                            selectedCategory = cat
                            selectedNeedChip = null
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) Color(0xFF5050F0) else Color.White,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) Color(0xFF5050F0) else Color(0xFFECEFF8)
                        )
                    ) {
                        Text(
                            text = when (cat) {
                                "Mobiles" -> "📱 Mobiles"
                                "Laptops" -> "💻 Laptops"
                                "Audio" -> "🎧 Audio"
                                "Fashion" -> "👗 Fashion"
                                "Home Appliances" -> "🏠 Appliances"
                                else -> "✨ All Categories"
                            },
                            fontSize = 12.sp,
                            color = if (isSelected) Color.White else Color(0xFF1E2135),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Featured Box or Dynamic AI Insight
            if (searchQuery.isBlank()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onChipClicked("Online classes") },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F4FF)),
                    border = BorderStroke(1.dp, Color(0xFFE2E6F5))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.School,
                                contentDescription = null,
                                tint = Color(0xFF5050F0),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "YOUR EVERYDAY, UPGRADED",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF5050F0),
                                letterSpacing = 0.5.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Better gear for online classes",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E2135)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Clear calls, lasting battery and sensible budgets. Start with what you need.",
                            fontSize = 12.sp,
                            color = Color(0xFF7A8398),
                            lineHeight = 18.sp
                        )
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F3FF)),
                    border = BorderStroke(1.dp, Color(0xFFD8E0FF))
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
                                text = "AI SPEC & NEED RECOMMENDATION",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF5050F0),
                                letterSpacing = 0.5.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Recommended for \"$searchQuery\"",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E2135)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "SmartCart AI matched ${filteredProducts.size} products based on specifications, use-case requirements, and price suitability.",
                            fontSize = 12.sp,
                            color = Color(0xFF5A6478),
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Explore by Need Section
            Text(
                text = "Explore by need",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E2135)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Need Chips Layout (Two rows scrollable or wrapped)
            val half = (needChips.size + 1) / 2
            val row1 = needChips.take(half)
            val row2 = needChips.drop(half)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row1.forEach { chipTitle ->
                        NeedChip(
                            title = chipTitle,
                            isSelected = selectedNeedChip == chipTitle,
                            onClick = { onChipClicked(chipTitle) }
                        )
                    }
                }

                if (row2.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row2.forEach { chipTitle ->
                            NeedChip(
                                title = chipTitle,
                                isSelected = selectedNeedChip == chipTitle,
                                onClick = { onChipClicked(chipTitle) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Shortlist Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when {
                        searchQuery.isNotEmpty() -> "Search & AI recommendations"
                        selectedCategory != "All" -> "Your $selectedCategory shortlist"
                        else -> "Your Curated shortlist"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E2135)
                )
                Text(
                    text = "${filteredProducts.size} ${if (filteredProducts.size == 1) "match" else "matches"}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5050F0)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Render Product List or Empty State
            if (filteredProducts.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFECEFF8))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.SearchOff,
                            contentDescription = null,
                            tint = Color(0xFF7A8398),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No products found",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E2135)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "No matches for \"$searchQuery\". Try searching by product name (e.g. \"Samsung\", \"boAt\", \"Sony\") or describe your need (e.g. \"gaming laptop\", \"dress for women\", \"pro camera under 25k\").",
                            fontSize = 12.sp,
                            color = Color(0xFF7A8398),
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedButton(
                            onClick = {
                                searchQuery = ""
                                selectedNeedChip = null
                                selectedCategory = "All"
                            },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFD0D7FF)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF5050F0))
                        ) {
                            Text("Clear search", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    filteredProducts.forEach { product ->
                        ShortlistProductCard(
                            product = product,
                            onClick = { onProductClick(product.name) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun NeedChip(
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
            if (isSelected) Color(0xFF5050F0) else Color(0xFFECEFF8)
        )
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            color = if (isSelected) Color(0xFF5050F0) else Color(0xFF1E2135),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        )
    }
}

@Composable
private fun ShortlistProductCard(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
            val icon = when (product.category) {
                "Mobiles" -> Icons.Outlined.Smartphone
                "Laptops" -> Icons.Outlined.Laptop
                "Audio" -> if (product.subcategory == "Speakers") Icons.Outlined.Speaker else Icons.Outlined.Headphones
                "Fashion" -> if (product.needs.contains("Watches")) Icons.Outlined.Watch else if (product.needs.contains("Accessories")) Icons.Outlined.ShoppingBag else Icons.Outlined.Checkroom
                "Home Appliances" -> if (product.subcategory == "Televisions") Icons.Outlined.Tv else Icons.Outlined.Kitchen
                else -> Icons.Outlined.ShoppingBag
            }

            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF5F6FA)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = product.name,
                    tint = Color(0xFF5050F0),
                    modifier = Modifier.size(40.dp)
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
                        text = product.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E2135),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = product.matchScore,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5050F0)
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${product.brand} · ${product.subcategory}",
                        fontSize = 11.sp,
                        color = Color(0xFF7A8398),
                        fontWeight = FontWeight.Medium
                    )
                    if (product.needs.contains("Dress")) {
                        Text(
                            text = " · 👗 Dress",
                            fontSize = 11.sp,
                            color = Color(0xFFE91E63),
                            fontWeight = FontWeight.SemiBold
                        )
                    } else if (product.needs.contains("Watches")) {
                        Text(
                            text = " · ⌚ Watch",
                            fontSize = 11.sp,
                            color = Color(0xFF5050F0),
                            fontWeight = FontWeight.SemiBold
                        )
                    } else if (product.needs.contains("Accessories")) {
                        Text(
                            text = " · 👜 Accessory",
                            fontSize = 11.sp,
                            color = Color(0xFF00897B),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.price,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E2135)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFB800),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = product.rating,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E2135)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                val specText = when {
                    product.specs.isNotBlank() -> product.specs
                    product.battery != "N/A" -> "${product.battery} battery"
                    else -> product.bestFor
                }
                Text(
                    text = specText,
                    fontSize = 11.sp,
                    color = Color(0xFF7A8398),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (product.bestFor.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "💡 ${product.bestFor}",
                        fontSize = 10.sp,
                        color = Color(0xFF5A6478),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Amazon vs Flipkart quick store pill
                val storeComp = product.storeComparison
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFF3F5FC))
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Amazon ${storeComp.amazon.formattedPrice}",
                            fontSize = 9.sp,
                            fontWeight = if (storeComp.amazon.isCheapest) FontWeight.Bold else FontWeight.Normal,
                            color = if (storeComp.amazon.isCheapest) Color(0xFF1A8068) else Color(0xFF5A6478)
                        )
                        Text(text = " · ", fontSize = 9.sp, color = Color(0xFF9AA4B8))
                        Text(
                            text = "Flipkart ${storeComp.flipkart.formattedPrice}",
                            fontSize = 9.sp,
                            fontWeight = if (storeComp.flipkart.isCheapest) FontWeight.Bold else FontWeight.Normal,
                            color = if (storeComp.flipkart.isCheapest) Color(0xFF1A8068) else Color(0xFF5A6478)
                        )
                    }
                    Text(
                        text = "Save ${storeComp.savingsFormatted}",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A8068)
                    )
                }
            }
        }
    }
}

@Composable
private fun DiscoverBottomNavigationBar(
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
fun DiscoverScreenPreview() {
    DiscoverScreen()
}
