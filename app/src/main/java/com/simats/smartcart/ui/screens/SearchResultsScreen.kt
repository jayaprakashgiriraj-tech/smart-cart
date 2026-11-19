package com.simats.smartcart.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.horizontalScroll
import com.simats.smartcart.ai.AiSearchResultItem
import com.simats.smartcart.ai.SmartCartAiEngine
import com.simats.smartcart.data.Product
import com.simats.smartcart.data.ProductCatalog
import com.simats.smartcart.data.storeComparison

@Composable
fun SearchResultsScreen(
    initialQuery: String = "",
    initialCategory: String = "All",
    initialMaxBudget: Int = 0,
    onBackClick: () -> Unit = {},
    onSeeFullRecommendationsClick: () -> Unit = {},
    onProductClick: (String) -> Unit = {}
) {
    var currentQuery by remember { mutableStateOf(initialQuery) }
    var followUpQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(initialCategory) }

    // Execute SmartCart Custom On-Device AI Search
    val aiSearchResponse = remember(currentQuery, selectedCategory, initialMaxBudget) {
        val query = currentQuery.trim()
        val queryToRun = if (query.isNotBlank()) query else if (selectedCategory != "All") selectedCategory else "Top Deals"
        SmartCartAiEngine.search(
            query = queryToRun,
            categoryFilter = if (selectedCategory == "All") null else selectedCategory,
            maxPriceFilter = if (initialMaxBudget > 0) initialMaxBudget else null
        )
    }

    val matchingResults = aiSearchResponse.results
    val parsedQuery = aiSearchResponse.parsedQuery
    val aiSummaryText = aiSearchResponse.aiSummary

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
                        Text("Search another product or ask a trade-off...", color = Color(0xFFA0A7B8), fontSize = 14.sp)
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
                                currentQuery = followUpQuery.trim()
                                selectedCategory = "All"
                                followUpQuery = ""
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                                    contentDescription = "Search",
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
                        if (followUpQuery.isNotBlank()) {
                            currentQuery = followUpQuery.trim()
                            selectedCategory = "All"
                            followUpQuery = ""
                        }
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
                            text = if (currentQuery.isNotBlank()) "Search results for \"$currentQuery\"" else "Recommendations · conversation continued",
                            fontSize = 11.sp,
                            color = Color(0xFF7A8398),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
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

            // AI Parsed Intent Chips Row
            if (parsedQuery.category != null || parsedQuery.maxBudget != null || parsedQuery.detectedBrand != null || parsedQuery.detectedFeatures.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (parsedQuery.detectedBrand != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF2F4FF),
                            border = BorderStroke(1.dp, Color(0xFFD6DDF8))
                        ) {
                            Text(
                                text = "🏢 ${parsedQuery.detectedBrand}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF5050F0),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    if (parsedQuery.category != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF2F4FF),
                            border = BorderStroke(1.dp, Color(0xFFD6DDF8))
                        ) {
                            Text(
                                text = "🏷️ ${parsedQuery.category}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF5050F0),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    if (parsedQuery.maxBudget != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFE8F7F2),
                            border = BorderStroke(1.dp, Color(0xFFC8ECE0))
                        ) {
                            Text(
                                text = "💰 Under ₹${ProductCatalog.formatIndianRupees(parsedQuery.maxBudget)}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A8068),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    parsedQuery.detectedFeatures.forEach { feat ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFFF8E1),
                            border = BorderStroke(1.dp, Color(0xFFFFECB3))
                        ) {
                            Text(
                                text = "⚡ ${feat.replaceFirstChar { it.uppercase() }}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFB76E00),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // AI Recommendation Summary Card
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFF5050F0),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SmartCart AI Search",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5050F0)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = aiSummaryText,
                        fontSize = 13.sp,
                        color = Color(0xFF1E2135),
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Shortlist Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (currentQuery.isNotBlank()) "Matches for \"$currentQuery\"" else "Your $selectedCategory shortlist",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E2135),
                    modifier = Modifier.weight(1f, fill = false),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${matchingResults.size} ${if (matchingResults.size == 1) "match" else "matches"}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5050F0)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Render Product Cards or Empty State
            if (matchingResults.isEmpty()) {
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
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No products found",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E2135)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "No matching products found for \"$currentQuery\". Try searching for \"iPhone\", \"Samsung\", \"boAt\", \"Dell\", or describe what you need.",
                            fontSize = 12.sp,
                            color = Color(0xFF7A8398),
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedButton(
                            onClick = {
                                currentQuery = ""
                                selectedCategory = "All"
                            },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFD0D7FF)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF5050F0))
                        ) {
                            Text("Reset search", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    matchingResults.forEach { item ->
                        SearchResultProductCard(
                            product = item.product,
                            onClick = { onProductClick(item.product.id) },
                            resultItem = item
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // See full recommendations Button
            Button(
                onClick = onSeeFullRecommendationsClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5050F0)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "See full recommendations",
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
private fun SearchResultProductCard(
    product: Product,
    onClick: () -> Unit,
    resultItem: AiSearchResultItem
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

                Text(
                    text = "${product.brand} · ${product.subcategory}",
                    fontSize = 11.sp,
                    color = Color(0xFF7A8398),
                    fontWeight = FontWeight.Medium
                )

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

@Preview(showBackground = true)
@Composable
fun SearchResultsScreenPreview() {
    SearchResultsScreen()
}
