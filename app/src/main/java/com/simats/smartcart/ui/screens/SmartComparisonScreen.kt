package com.simats.smartcart.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.CompareArrows
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import com.simats.smartcart.data.WishlistRepository
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
import com.simats.smartcart.data.Product
import com.simats.smartcart.data.ProductCatalog

data class SpecRowData(
    val label: String,
    val value1: String,
    val value2: String,
    val isHighlighted: Boolean = false,
    val isBoldValue1: Boolean = false
)

private fun getCategorySpecRows(p1: Product, p2: Product): List<SpecRowData> {
    if (p1.isSpeaker && p2.isSpeaker) {
        val p1Specs = p1.specs.split("·").map { it.trim() }
        val p2Specs = p2.specs.split("·").map { it.trim() }
        return listOf(
            SpecRowData("Sound Output", p1Specs.getOrNull(0) ?: "Stereo Sound", p2Specs.getOrNull(0) ?: "Stereo Sound"),
            SpecRowData("Water Resistance", p1Specs.getOrNull(1) ?: "Water Resistant", p2Specs.getOrNull(1) ?: "Waterproof"),
            SpecRowData("Battery Playtime", p1.battery, p2.battery, isBoldValue1 = p1.battery == p2.battery || p1.battery > p2.battery),
            SpecRowData("Portability / Weight", p1.weight, p2.weight),
            SpecRowData("Primary Highlight", p1.bestFor, p2.bestFor),
            SpecRowData("Customer Rating", "★ ${p1.rating} (${p1.reviewCount})", "★ ${p2.rating} (${p2.reviewCount})", isBoldValue1 = p1.rating >= p2.rating),
            SpecRowData("Official Warranty", p1.warranty, p2.warranty),
            SpecRowData("Value (/10)", "${p1.valueScore}", "${p2.valueScore}", isHighlighted = true, isBoldValue1 = p1.valueScore >= p2.valueScore)
        )
    }

    return when (p1.category) {
        "Mobiles" -> {
            val p1Specs = p1.specs.split("·").map { it.trim() }
            val p2Specs = p2.specs.split("·").map { it.trim() }
            listOf(
                SpecRowData("RAM / Storage", p1Specs.getOrNull(0) ?: "8GB / 256GB", p2Specs.getOrNull(0) ?: "8GB / 128GB"),
                SpecRowData("Key Hardware", p1Specs.getOrNull(1) ?: p1.brand, p2Specs.getOrNull(1) ?: p2.brand),
                SpecRowData("Network / 5G", p1Specs.getOrNull(2) ?: "5G", p2Specs.getOrNull(2) ?: "5G"),
                SpecRowData("Primary Highlight", p1.bestFor, p2.bestFor),
                SpecRowData("Customer Rating", "★ ${p1.rating} (${p1.reviewCount})", "★ ${p2.rating} (${p2.reviewCount})", isBoldValue1 = p1.rating >= p2.rating),
                SpecRowData("Official Warranty", p1.warranty, p2.warranty),
                SpecRowData("Match Score", p1.matchScore, p2.matchScore, isBoldValue1 = p1.matchPercentage >= p2.matchPercentage),
                SpecRowData("Value (/10)", "${p1.valueScore}", "${p2.valueScore}", isHighlighted = true, isBoldValue1 = p1.valueScore >= p2.valueScore)
            )
        }
        "Laptops" -> {
            val p1Specs = p1.specs.split("·").map { it.trim() }
            val p2Specs = p2.specs.split("·").map { it.trim() }
            listOf(
                SpecRowData("Processor", p1Specs.getOrNull(0) ?: p1.brand, p2Specs.getOrNull(0) ?: p2.brand),
                SpecRowData("Memory & Storage", p1Specs.getOrNull(1) ?: "16GB / 512GB", p2Specs.getOrNull(1) ?: "8GB / 256GB"),
                SpecRowData("Display / Build", p1Specs.getOrNull(2) ?: p1.subcategory, p2Specs.getOrNull(2) ?: p2.subcategory),
                SpecRowData("Battery Life", p1.battery, p2.battery),
                SpecRowData("Weight", p1.weight, p2.weight),
                SpecRowData("Customer Rating", "★ ${p1.rating}", "★ ${p2.rating}"),
                SpecRowData("Warranty", p1.warranty, p2.warranty),
                SpecRowData("Value (/10)", "${p1.valueScore}", "${p2.valueScore}", isHighlighted = true, isBoldValue1 = p1.valueScore >= p2.valueScore)
            )
        }
        "Fashion" -> {
            listOf(
                SpecRowData("Brand & Group", "${p1.brand} · ${p1.subcategory}", "${p2.brand} · ${p2.subcategory}"),
                SpecRowData("Best For", p1.bestFor, p2.bestFor),
                SpecRowData("Fabric & Style", p1.specs, p2.specs),
                SpecRowData("Rating", "★ ${p1.rating}", "★ ${p2.rating}"),
                SpecRowData("Availability", p1.stockStatus, p2.stockStatus),
                SpecRowData("Return Policy", "7 Days Replacement", "7 Days Replacement"),
                SpecRowData("Value (/10)", "${p1.valueScore}", "${p2.valueScore}", isHighlighted = true, isBoldValue1 = p1.valueScore >= p2.valueScore)
            )
        }
        "Home Appliances" -> {
            val p1Specs = p1.specs.split("·").map { it.trim() }
            val p2Specs = p2.specs.split("·").map { it.trim() }
            listOf(
                SpecRowData("Capacity / Spec", p1Specs.getOrNull(0) ?: p1.subcategory, p2Specs.getOrNull(0) ?: p2.subcategory),
                SpecRowData("Efficiency / Tech", p1Specs.getOrNull(1) ?: p1.brand, p2Specs.getOrNull(1) ?: p2.brand),
                SpecRowData("Special Feature", p1.bestFor, p2.bestFor),
                SpecRowData("Rating", "★ ${p1.rating} (${p1.reviewCount})", "★ ${p2.rating} (${p2.reviewCount})"),
                SpecRowData("Official Warranty", p1.warranty, p2.warranty),
                SpecRowData("Value (/10)", "${p1.valueScore}", "${p2.valueScore}", isHighlighted = true, isBoldValue1 = p1.valueScore >= p2.valueScore)
            )
        }
        else -> { // Audio Headsets / Headphones and general fallback
            listOf(
                SpecRowData("Battery Life", p1.battery, p2.battery, isBoldValue1 = p1.battery != p2.battery),
                SpecRowData("Call Mic", p1.callMic, p2.callMic, isBoldValue1 = true),
                SpecRowData("Weight", p1.weight, p2.weight, isBoldValue1 = p1.weight != p2.weight),
                SpecRowData("Multipoint", p1.multipoint, p2.multipoint),
                SpecRowData("Active NC", p1.activeNC, p2.activeNC),
                SpecRowData("App EQ", p1.appEQ, p2.appEQ),
                SpecRowData("Warranty", p1.warranty, p2.warranty),
                SpecRowData("Value (/10)", "${p1.valueScore}", "${p2.valueScore}", isHighlighted = true, isBoldValue1 = p1.valueScore >= p2.valueScore)
            )
        }
    }
}

@Composable
fun SmartComparisonScreen(
    product1Name: String = "Samsung Galaxy S23",
    product2Name: String = "Apple iPhone 15",
    onBackClick: () -> Unit = {},
    onTabSelected: (Int) -> Unit = {},
    onSeeSummaryClick: (String, String) -> Unit = { _, _ -> }
) {
    var selectedBottomTab by remember { mutableIntStateOf(2) } // 2 is Compare tab

    var currentProd1Name by remember(product1Name) { mutableStateOf(product1Name) }
    var currentProd2Name by remember(product2Name, product1Name) {
        val p1 = ProductCatalog.getProductByName(product1Name)
        val candidate = ProductCatalog.getProductByName(product2Name)
        val initialP2 = if (ProductCatalog.isSameComparisonScope(p1, candidate) && candidate.id != p1.id) {
            candidate.name
        } else {
            ProductCatalog.getComparableProducts(p1).firstOrNull()?.name ?: candidate.name
        }
        mutableStateOf(initialP2)
    }

    val product1 = remember(currentProd1Name) { ProductCatalog.getProductByName(currentProd1Name) }
    val product2 = remember(currentProd2Name) { ProductCatalog.getProductByName(currentProd2Name) }

    var showChangeP1Sheet by remember { mutableStateOf(false) }
    var showChangeP2Sheet by remember { mutableStateOf(false) }

    val icon1 = remember(product1) { getProductCategoryIcon(product1) }
    val icon2 = remember(product2) { getProductCategoryIcon(product2) }

    val winner = if (product1.valueScore >= product2.valueScore) product1 else product2
    val loser = if (winner.id == product1.id) product2 else product1

    val specRows = remember(product1, product2) {
        getCategorySpecRows(product1, product2)
    }

    val otherCompetitors = remember(product1, product2) {
        ProductCatalog.getSimilarProducts(product1).filter { it.id != product2.id }
    }

    Scaffold(
        bottomBar = {
            CompareBottomNavigationBar(
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
                            text = "Smart Comparison",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E2135)
                        )
                        Text(
                            text = "${product1.getComparisonCategoryLabel()} · ${product1.brand} vs ${product2.brand}",
                            fontSize = 11.sp,
                            color = Color(0xFF7A8398),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                val isFav1 = WishlistRepository.isWishlisted(product1.name)
                IconButton(
                    onClick = {
                        WishlistRepository.toggleWishlist(product1)
                        WishlistRepository.toggleWishlist(product2)
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isFav1) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (isFav1) Color(0xFF5050F0) else Color(0xFF7A8398),
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

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar Button to pick/search any product to compare
            Surface(
                onClick = { showChangeP2Sheet = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E6F5))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search",
                        tint = Color(0xFF5050F0),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Search ${product1.getComparisonCategoryLabel()} to compare...",
                        fontSize = 13.sp,
                        color = Color(0xFF7A8398),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                        contentDescription = null,
                        tint = Color(0xFF5050F0),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Product Cards Header Row with Swap capability
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Product 1 Header Card
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E6F5))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF5F6FA)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon1,
                                    contentDescription = product1.name,
                                    tint = Color(0xFF2A2A2A),
                                    modifier = Modifier.size(46.dp)
                                )

                                // Swap chip inside box for Product 1
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.White.copy(alpha = 0.9f))
                                        .clickable { showChangeP1Sheet = true }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Swap",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF5050F0)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = product1.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E2135),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = product1.price,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF1E2135)
                                )
                                Text(
                                    text = product1.matchScore,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF5050F0)
                                )
                            }
                        }
                    }

                    // Product 2 Header Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showChangeP2Sheet = true },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E6F5))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF5F6FA)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon2,
                                    contentDescription = product2.name,
                                    tint = Color(0xFF2A2A2A),
                                    modifier = Modifier.size(46.dp)
                                )

                                // Swap chip inside box for Product 2
                                Surface(
                                    onClick = { showChangeP2Sheet = true },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(5.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.White,
                                    border = BorderStroke(1.dp, Color(0xFFE2E6F5)),
                                    shadowElevation = 1.dp
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Outlined.CompareArrows,
                                            contentDescription = "Swap product",
                                            tint = Color(0xFF5050F0),
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "Swap",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF5050F0)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = product2.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E2135),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = product2.price,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF1E2135)
                                )
                                Text(
                                    text = product2.matchScore,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF5050F0)
                                )
                            }
                        }
                    }
                }

                // Interactive Center Swap Button between both products
                Surface(
                    onClick = {
                        val temp = currentProd1Name
                        currentProd1Name = currentProd2Name
                        currentProd2Name = temp
                    },
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 3.dp,
                    border = BorderStroke(1.dp, Color(0xFFD8DEF5)),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.CompareArrows,
                            contentDescription = "Swap both products",
                            tint = Color(0xFF5050F0),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Quick Switcher Row
            if (otherCompetitors.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Compare ${product1.brand} with another ${product1.getComparisonCategoryLabel()}:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E2135)
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(otherCompetitors) { rival ->
                        SuggestionChip(
                            onClick = { currentProd2Name = rival.name },
                            label = {
                                Text(
                                    text = "+ ${rival.name} (${rival.price})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF5050F0)
                                )
                            },
                            border = BorderStroke(1.dp, Color(0xFFD8DEF5)),
                            shape = RoundedCornerShape(10.dp),
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = Color.White
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Specs Comparison Table Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFECEFF8))
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    specRows.forEachIndexed { index, row ->
                        val rowBg = if (row.isHighlighted) Color(0xFFF2F4FF) else if (index % 2 == 1) Color(0xFFF9FAFD) else Color.White

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(rowBg)
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = row.label,
                                fontSize = 11.sp,
                                color = Color(0xFF7A8398),
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1.1f)
                            )
                            Text(
                                text = row.value1,
                                fontSize = 11.sp,
                                color = if (row.isHighlighted) Color(0xFF5050F0) else Color(0xFF1E2135),
                                fontWeight = if (row.isBoldValue1 || row.isHighlighted) FontWeight.Bold else FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = row.value2,
                                fontSize = 11.sp,
                                color = Color(0xFF1E2135),
                                fontWeight = if (row.isHighlighted) FontWeight.Bold else FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (index < specRows.size - 1) {
                            HorizontalDivider(color = Color(0xFFF0F2F8), thickness = 1.dp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // AI Summary Verdict Box
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F7F2)),
                border = BorderStroke(1.dp, Color(0xFFC8ECE0))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFF1A8068),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${winner.name}: top overall value pick",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A8068)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${winner.name} (${winner.valueScore}/10) delivers greater value with ${winner.bestFor.lowercase()}. Consider ${loser.name} (${loser.price}) if you prioritize ${loser.bestFor.lowercase()}.",
                        fontSize = 12.sp,
                        color = Color(0xFF1A8068),
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footnotes
            Text(
                text = "¹ Illustrative listed specs; confirm terms with retailer.\n² Sample AI interpretation, not lab-test results.",
                fontSize = 10.sp,
                color = Color(0xFF9AA4B8),
                lineHeight = 14.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Link to AI summary & trade-offs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSeeSummaryClick(product1.name, product2.name) }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "See the AI summary & trade-offs",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5050F0)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                    contentDescription = null,
                    tint = Color(0xFF5050F0),
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Bottom Sheet to choose/swap Product 1
        if (showChangeP1Sheet) {
            CompareProductSheet(
                baseProduct = product2,
                sheetTitle = "Swap ${product1.name}",
                onSwapPositions = {
                    val temp = currentProd1Name
                    currentProd1Name = currentProd2Name
                    currentProd2Name = temp
                },
                onSelectProduct = { chosen ->
                    currentProd1Name = chosen.name
                    showChangeP1Sheet = false
                },
                onDismiss = { showChangeP1Sheet = false }
            )
        }

        // Bottom Sheet to choose/swap Product 2
        if (showChangeP2Sheet) {
            CompareProductSheet(
                baseProduct = product1,
                sheetTitle = "Swap ${product2.name}",
                onSwapPositions = {
                    val temp = currentProd1Name
                    currentProd1Name = currentProd2Name
                    currentProd2Name = temp
                },
                onSelectProduct = { chosen ->
                    currentProd2Name = chosen.name
                    showChangeP2Sheet = false
                },
                onDismiss = { showChangeP2Sheet = false }
            )
        }
    }
}

@Composable
private fun CompareBottomNavigationBar(
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
fun SmartComparisonScreenPreview() {
    SmartComparisonScreen()
}
