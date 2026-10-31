package com.simats.smartcart.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import com.simats.smartcart.data.Product
import com.simats.smartcart.data.ProductCatalog
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

    // Subcategory & filter states
    var selectedFashionGroup by remember { mutableStateOf("All") } // "All", "Men", "Women", "Boys", "Girls"
    var selectedFashionType by remember { mutableStateOf("All") } // "All", "Dress", "Watches & Accessories"
    var selectedAudioSubcategory by remember { mutableStateOf("All") } // "All", "Headsets", "Speakers"
    var selectedApplianceSubcategory by remember { mutableStateOf("All") } // "All", "Washing Machines", "Refrigerators", etc.
    var selectedMobileFilter by remember { mutableStateOf("All") } // "All", "Flagship", "Under ₹25,000", "Camera"
    var selectedLaptopFilter by remember { mutableStateOf("All") } // "All", "Student", "Thin & Light", "Gaming"

    val promptSuggestion = when (selectedCategory) {
        "Mobiles" -> "Try \"Best phone under ₹25,000 with a good camera\""
        "Laptops" -> "Try \"Lightweight laptop for college and coding\""
        "Audio" -> "Try \"Wireless headphones with long battery and clear mic\""
        "Fashion" -> when (selectedFashionGroup) {
            "Men" -> "Try \"Slim fit shirts or chronograph leather watches for men\""
            "Women" -> "Try \"Floral midi dresses or rose gold watches for women\""
            "Boys" -> "Try \"Polo t-shirts or digital sports watches for boys\""
            "Girls" -> "Try \"Party frocks or pink digital watches for girls\""
            else -> "Try \"Dresses for boys and girls, or watches & accessories\""
        }
        "Home Appliances" -> "Try \"5-star energy saving refrigerator or washing machine\""
        else -> "Try \"Best products with top AI match scores\""
    }

    val popularSearches = when (selectedCategory) {
        "Mobiles" -> listOf("Camera phones under ₹25k", "5G Flagships")
        "Laptops" -> listOf("Study laptops", "MacBook Air M2")
        "Audio" -> listOf("Headphones under ₹3k", "Bluetooth speakers")
        "Fashion" -> when (selectedFashionGroup) {
            "Men" -> listOf("Men's formal shirts", "Fossil leather watch")
            "Women" -> listOf("Floral summer dresses", "Titan Raga watches")
            "Boys" -> listOf("Boys polo & shorts", "Kids sports watches")
            "Girls" -> listOf("Girls party frocks", "Zoop pink watches")
            else -> listOf("Men & Women dresses", "Watches & accessories")
        }
        "Home Appliances" -> listOf("Front load washer", "Double door fridge")
        else -> listOf("Top deals", "Best match")
    }

    val recommendedProducts: List<Product> = remember(
        selectedCategory,
        selectedFashionGroup,
        selectedFashionType,
        selectedAudioSubcategory,
        selectedApplianceSubcategory,
        selectedMobileFilter,
        selectedLaptopFilter
    ) {
        when (selectedCategory) {
            "Mobiles" -> {
                val allMobiles = ProductCatalog.getProductsByCategory("Mobiles")
                when (selectedMobileFilter) {
                    "Flagship" -> allMobiles.filter { it.needs.contains("Flagship") }
                    "Under ₹25,000" -> allMobiles.filter { it.numericPrice <= 25000 }
                    "Camera" -> allMobiles.filter { it.needs.contains("Camera") }
                    else -> allMobiles
                }
            }
            "Laptops" -> {
                val allLaptops = ProductCatalog.getProductsByCategory("Laptops")
                when (selectedLaptopFilter) {
                    "Student" -> allLaptops.filter { it.needs.contains("Student") || it.needs.contains("Study") }
                    "Thin & Light" -> allLaptops.filter { it.subcategory.contains("Thin", ignoreCase = true) }
                    "Gaming" -> allLaptops.filter { it.needs.contains("Gaming") }
                    else -> allLaptops
                }
            }
            "Audio" -> {
                if (selectedAudioSubcategory == "All") {
                    ProductCatalog.getProductsByCategory("Audio")
                } else {
                    ProductCatalog.getProductsByCategory("Audio", selectedAudioSubcategory)
                }
            }
            "Fashion" -> {
                ProductCatalog.getFashionProducts(selectedFashionGroup, selectedFashionType)
            }
            "Home Appliances" -> {
                if (selectedApplianceSubcategory == "All") {
                    ProductCatalog.getProductsByCategory("Home Appliances")
                } else {
                    ProductCatalog.getProductsByCategory("Home Appliances", selectedApplianceSubcategory)
                }
            }
            else -> ProductCatalog.products.take(6)
        }
    }

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
                text = promptSuggestion,
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
                        selectedMobileFilter = "All"
                        onCategoryClick("Mobiles")
                    }
                )
                CategoryItem(
                    title = "Laptops",
                    icon = Icons.Outlined.Laptop,
                    isSelected = selectedCategory == "Laptops",
                    onClick = {
                        selectedCategory = "Laptops"
                        selectedLaptopFilter = "All"
                        onCategoryClick("Laptops")
                    }
                )
                CategoryItem(
                    title = "Audio",
                    icon = Icons.Outlined.Headphones,
                    isSelected = selectedCategory == "Audio",
                    onClick = {
                        selectedCategory = "Audio"
                        selectedAudioSubcategory = "All"
                        onCategoryClick("Audio")
                    }
                )
                CategoryItem(
                    title = "Fashion",
                    icon = Icons.Outlined.Checkroom,
                    isSelected = selectedCategory == "Fashion",
                    onClick = {
                        selectedCategory = "Fashion"
                        selectedFashionGroup = "All"
                        selectedFashionType = "All"
                        onCategoryClick("Fashion")
                    }
                )
                CategoryItem(
                    title = "Home\nAppliances",
                    icon = Icons.Outlined.Kitchen,
                    isSelected = selectedCategory == "Home Appliances",
                    onClick = {
                        selectedCategory = "Home Appliances"
                        selectedApplianceSubcategory = "All"
                        onCategoryClick("Home Appliances")
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subcategory Filters based on Selected Category
            when (selectedCategory) {
                "Fashion" -> {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Department",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7A8398)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                        ) {
                            listOf(
                                "All" to "All Fashion",
                                "Men" to "Men 👨",
                                "Women" to "Women 👩",
                                "Boys" to "Boys 👦",
                                "Girls" to "Girls 👧"
                            ).forEach { (key, label) ->
                                FilterChipItem(
                                    text = label,
                                    isSelected = selectedFashionGroup == key,
                                    onClick = { selectedFashionGroup = key }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Item Category",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7A8398)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                        ) {
                            listOf(
                                "All" to "All Items",
                                "Dress" to "👗 Dresses & Clothing",
                                "Watches & Accessories" to "⌚ Watches & Accessories"
                            ).forEach { (key, label) ->
                                FilterChipItem(
                                    text = label,
                                    isSelected = selectedFashionType == key,
                                    onClick = { selectedFashionType = key }
                                )
                            }
                        }
                    }
                }
                "Audio" -> {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Audio Type",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7A8398)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                        ) {
                            listOf(
                                "All" to "All Audio",
                                "Headsets" to "🎧 Headsets & Headphones",
                                "Speakers" to "🔊 Bluetooth Speakers"
                            ).forEach { (key, label) ->
                                FilterChipItem(
                                    text = label,
                                    isSelected = selectedAudioSubcategory == key,
                                    onClick = { selectedAudioSubcategory = key }
                                )
                            }
                        }
                    }
                }
                "Home Appliances" -> {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Appliance Type",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7A8398)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                        ) {
                            listOf(
                                "All" to "All Appliances",
                                "Washing Machines" to "🧺 Washing Machines",
                                "Refrigerators" to "🧊 Refrigerators",
                                "Kitchen Appliances" to "🍳 Kitchen",
                                "Air Purifiers" to "💨 Air Purifiers",
                                "Televisions" to "📺 Smart TVs"
                            ).forEach { (key, label) ->
                                FilterChipItem(
                                    text = label,
                                    isSelected = selectedApplianceSubcategory == key,
                                    onClick = { selectedApplianceSubcategory = key }
                                )
                            }
                        }
                    }
                }
                "Mobiles" -> {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Filter Mobiles",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7A8398)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                        ) {
                            listOf(
                                "All" to "All Mobiles",
                                "Flagship" to "🔥 Flagship",
                                "Under ₹25,000" to "💰 Under ₹25,000",
                                "Camera" to "📸 Pro Camera"
                            ).forEach { (key, label) ->
                                FilterChipItem(
                                    text = label,
                                    isSelected = selectedMobileFilter == key,
                                    onClick = { selectedMobileFilter = key }
                                )
                            }
                        }
                    }
                }
                "Laptops" -> {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Filter Laptops",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7A8398)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                        ) {
                            listOf(
                                "All" to "All Laptops",
                                "Student" to "🎓 Student & Study",
                                "Thin & Light" to "⚡ Thin & Light",
                                "Gaming" to "🎮 High Performance"
                            ).forEach { (key, label) ->
                                FilterChipItem(
                                    text = label,
                                    isSelected = selectedLaptopFilter == key,
                                    onClick = { selectedLaptopFilter = key }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Popular Searches Section
            Text(
                text = "Popular searches",
                fontSize = 16.sp,
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
                popularSearches.forEachIndexed { index, text ->
                    PopularSearchChip(
                        text = text,
                        icon = if (index == 0) Icons.AutoMirrored.Outlined.TrendingUp else null,
                        onClick = { onSearchClick(text) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Recommended for You Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = when (selectedCategory) {
                            "Mobiles" -> "Recommended Mobiles"
                            "Laptops" -> "Recommended Laptops"
                            "Audio" -> if (selectedAudioSubcategory == "Headsets") "Recommended Headsets" else if (selectedAudioSubcategory == "Speakers") "Recommended Speakers" else "Recommended Audio · Headsets & Speakers"
                            "Fashion" -> when (selectedFashionGroup) {
                                "Men" -> if (selectedFashionType == "Dress") "Men's Dresses & Clothing" else if (selectedFashionType.contains("Watch")) "Men's Watches & Accessories" else "Recommended for Men · Dresses, Watches & Accessories"
                                "Women" -> if (selectedFashionType == "Dress") "Women's Dresses & Clothing" else if (selectedFashionType.contains("Watch")) "Women's Watches & Accessories" else "Recommended for Women · Dresses, Watches & Accessories"
                                "Boys" -> if (selectedFashionType == "Dress") "Boys' Dresses & Clothing" else if (selectedFashionType.contains("Watch")) "Boys' Watches & Accessories" else "Recommended for Boys · Dresses & Watches"
                                "Girls" -> if (selectedFashionType == "Dress") "Girls' Dresses & Clothing" else if (selectedFashionType.contains("Watch")) "Girls' Watches & Accessories" else "Recommended for Girls · Dresses & Accessories"
                                else -> if (selectedFashionType == "Dress") "Fashion Dresses" else if (selectedFashionType.contains("Watch")) "Watches & Accessories" else "Recommended Fashion · Men, Women, Boys & Girls"
                            }
                            "Home Appliances" -> if (selectedApplianceSubcategory != "All") "Recommended $selectedApplianceSubcategory" else "Recommended Home Appliances"
                            else -> "Recommended for You"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E2135)
                    )
                    Text(
                        text = "${recommendedProducts.size} recommendations found",
                        fontSize = 11.sp,
                        color = Color(0xFF7A8398)
                    )
                }
                Text(
                    text = "See all",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5050F0),
                    modifier = Modifier.clickable { }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (recommendedProducts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No products found for this filter.",
                        color = Color(0xFF7A8398),
                        fontSize = 13.sp
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    recommendedProducts.forEach { product ->
                        RecommendationProductCard(
                            product = product,
                            onClick = { onProductClick(product.name) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sample suggestions based on your $selectedCategory preferences.",
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

@Composable
private fun FilterChipItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFF5050F0) else Color.White,
        border = BorderStroke(1.dp, if (isSelected) Color(0xFF5050F0) else Color(0xFFE2E6F5)),
        modifier = Modifier.padding(end = 8.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else Color(0xFF1E2135),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
        )
    }
}

@Composable
private fun RecommendationProductCard(
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
                    modifier = Modifier.size(38.dp)
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
                        fontSize = 13.sp,
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
                        fontSize = 14.sp,
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

                Text(
                    text = product.bestFor,
                    fontSize = 11.sp,
                    color = Color(0xFF5A6478),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
