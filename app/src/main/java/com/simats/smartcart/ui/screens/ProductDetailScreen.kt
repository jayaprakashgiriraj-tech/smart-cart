package com.simats.smartcart.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.smartcart.data.ProductCatalog

data class MatchProductDetail(
    val name: String,
    val price: String,
    val rating: String,
    val recIndex: String,
    val categoryBadge: String,
    val categoryIcon: ImageVector,
    val decisionTag: String,
    val decisionTagBg: Color,
    val decisionTagText: Color,
    val spec1: String,
    val spec2: String,
    val spec3: String,
    val matchPercentage: String,
    val matchProgress: Float,
    val valueScore: String,
    val whyText: String
)

val matchProductsList = listOf(
    MatchProductDetail(
        name = "JBL Tune 510BT",
        price = "₹2,799",
        rating = "4.3",
        recIndex = "Recommendation 1 of 3",
        categoryBadge = "BEST FOR ONLINE CLASSES",
        categoryIcon = Icons.Outlined.AutoAwesome,
        decisionTag = "BUY",
        decisionTagBg = Color(0xFFE6F5F0),
        decisionTagText = Color(0xFF1A8068),
        spec1 = "40 hours\nbattery",
        spec2 = "160 g\non-ear",
        spec3 = "Multi-point\nconnection",
        matchPercentage = "92%",
        matchProgress = 0.92f,
        valueScore = "8.6 /10",
        whyText = "Fits your budget, with a clear call mic and enough battery for a full week of classes."
    ),
    MatchProductDetail(
        name = "Soundcore H30i",
        price = "₹2,499",
        rating = "4.2",
        recIndex = "Recommendation 2 of 3",
        categoryBadge = "BATTERY LIFE PICK",
        categoryIcon = Icons.Outlined.BatteryChargingFull,
        decisionTag = "CONSIDER",
        decisionTagBg = Color(0xFFFFF7E6),
        decisionTagText = Color(0xFFB78103),
        spec1 = "70 hours\nbattery",
        spec2 = "183 g\non-ear",
        spec3 = "Multi-point\nconnection",
        matchPercentage = "88%",
        matchProgress = 0.88f,
        valueScore = "8.2 /10",
        whyText = "Best battery life here, but the mic is less consistent in noisy study spaces."
    ),
    MatchProductDetail(
        name = "boAt Rockerz 450",
        price = "₹1,499",
        rating = "4.1",
        recIndex = "Recommendation 3 of 3",
        categoryBadge = "BUDGET-FRIENDLY PICK",
        categoryIcon = Icons.Outlined.AccountBalanceWallet,
        decisionTag = "CONSIDER",
        decisionTagBg = Color(0xFFFFF7E6),
        decisionTagText = Color(0xFFB78103),
        spec1 = "15 hours\nbattery",
        spec2 = "168 g\non-ear",
        spec3 = "Single\nconnection",
        matchPercentage = "84%",
        matchProgress = 0.84f,
        valueScore = "7.6 /10",
        whyText = "Saves ₹1,301 from your budget; shorter battery life means charging more often."
    )
)

@Composable
fun ProductDetailScreen(
    productName: String = "JBL Tune 510BT",
    onBackClick: () -> Unit = {},
    onViewDetailsClick: (String) -> Unit = {},
    onCompareClick: () -> Unit = {}
) {
    val currentProductDetail = remember(productName) {
        val existing = matchProductsList.find { it.name.equals(productName, ignoreCase = true) }
        if (existing != null) {
            existing
        } else {
            val cat = ProductCatalog.products.find { it.name.equals(productName, ignoreCase = true) }
            if (cat != null) {
                val icon = when (cat.category) {
                    "Mobiles" -> Icons.Outlined.Smartphone
                    "Laptops" -> Icons.Outlined.Laptop
                    "Audio" -> if (cat.subcategory == "Speakers") Icons.Outlined.Speaker else Icons.Outlined.Headphones
                    "Fashion" -> if (cat.needs.contains("Watches")) Icons.Outlined.Watch else if (cat.needs.contains("Accessories")) Icons.Outlined.ShoppingBag else Icons.Outlined.Checkroom
                    "Home Appliances" -> Icons.Outlined.Kitchen
                    else -> Icons.Outlined.AutoAwesome
                }
                val specParts = cat.specs.split("·").map { it.trim() }
                val s1 = specParts.getOrNull(0) ?: cat.category
                val s2 = specParts.getOrNull(1) ?: cat.subcategory
                val s3 = specParts.getOrNull(2) ?: cat.brand
                MatchProductDetail(
                    name = cat.name,
                    price = cat.price,
                    rating = cat.rating,
                    recIndex = "Top Recommendation",
                    categoryBadge = cat.bestFor.uppercase(),
                    categoryIcon = icon,
                    decisionTag = "BUY",
                    decisionTagBg = Color(0xFFE6F5F0),
                    decisionTagText = Color(0xFF1A8068),
                    spec1 = s1,
                    spec2 = s2,
                    spec3 = s3,
                    matchPercentage = cat.matchScore,
                    matchProgress = cat.matchPercentage / 100f,
                    valueScore = "${cat.valueScore} /10",
                    whyText = cat.recommendationSummary
                )
            } else {
                matchProductsList.first()
            }
        }
    }

    var currentIndex by remember {
        val initial = matchProductsList.indexOfFirst { it.name.equals(productName, ignoreCase = true) }
        mutableIntStateOf(if (initial >= 0) initial else 0)
    }

    val product = if (matchProductsList.any { it.name.equals(productName, ignoreCase = true) }) {
        matchProductsList[currentIndex]
    } else {
        currentProductDetail
    }

    Scaffold(
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
                            text = "Your best matches",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E2135)
                        )
                        Text(
                            text = "Online classes - ${product.recIndex}",
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

            // Sub-header Description & Tags
            Text(
                text = "Wireless headphones that fit your priorities.",
                fontSize = 13.sp,
                color = Color(0xFF7A8398)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TagChip(text = "Under ₹3,000")
                TagChip(text = "Clear mic")
                TagChip(text = "Best match", isBestMatch = true)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Product Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFECEFF8))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Badge Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = product.categoryIcon,
                                contentDescription = null,
                                tint = Color(0xFF5050F0),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = product.categoryBadge,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF5050F0)
                            )
                        }

                        // Decision Tag Badge (BUY or CONSIDER)
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(product.decisionTagBg)
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (product.decisionTag == "BUY") "✓ BUY" else "— CONSIDER",
                                color = product.decisionTagText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Large Image Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFF5F6FA)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Headphones,
                            contentDescription = product.name,
                            tint = Color(0xFF2A2A2A),
                            modifier = Modifier.size(110.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Product Name
                    Text(
                        text = product.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E2135)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Price & Rating
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = product.price,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E2135)
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Star,
                                contentDescription = "Rating",
                                tint = Color(0xFFFFB800),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = product.rating,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E2135)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "sample rating",
                                fontSize = 11.sp,
                                color = Color(0xFF7A8398)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Feature Boxes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FeatureBox(text = product.spec1, modifier = Modifier.weight(1f))
                        FeatureBox(text = product.spec2, modifier = Modifier.weight(1f))
                        FeatureBox(text = product.spec3, modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Match Progress & Value Score
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Your match",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF5050F0)
                                )
                                Text(
                                    text = product.matchPercentage,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF5050F0)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { product.matchProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(CircleShape),
                                color = Color(0xFF5050F0),
                                trackColor = Color(0xFFEEF0FF)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = product.valueScore,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF5050F0)
                            )
                            Text(
                                text = "Value for money",
                                fontSize = 10.sp,
                                color = Color(0xFF7A8398)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Why it matches you
                    Text(
                        text = "Why it matches you",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E2135)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = product.whyText,
                        fontSize = 12.sp,
                        color = Color(0xFF7A8398),
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onViewDetailsClick(product.name) },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5050F0)),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("View Details", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }

                        OutlinedButton(
                            onClick = onCompareClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E6F5)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF5050F0))
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Compare", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer Match Navigation (Previous / Next)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentIndex > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { currentIndex-- }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Previous match",
                            tint = Color(0xFF5050F0),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Previous match",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5050F0)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                if (currentIndex < matchProductsList.size - 1) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { currentIndex++ }
                    ) {
                        Text(
                            text = "Next match",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5050F0)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                            contentDescription = "Next match",
                            tint = Color(0xFF5050F0),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onCompareClick() }
                    ) {
                        Text(
                            text = "Compare your shortlist",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5050F0)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                            contentDescription = "Compare shortlist",
                            tint = Color(0xFF5050F0),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun TagChip(text: String, isBestMatch: Boolean = false) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isBestMatch) Color.White else Color(0xFFEEF0FF),
        border = BorderStroke(
            1.dp,
            if (isBestMatch) Color(0xFFE2E6F5) else Color(0xFFEEF0FF)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isBestMatch) {
                Icon(
                    imageVector = Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = Color(0xFF5050F0),
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = text,
                fontSize = 11.sp,
                color = if (isBestMatch) Color(0xFF1E2135) else Color(0xFF5050F0),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun FeatureBox(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF7F8FC))
            .padding(vertical = 10.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            color = Color(0xFF1E2135),
            fontWeight = FontWeight.Medium,
            lineHeight = 14.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProductDetailScreenPreview() {
    ProductDetailScreen()
}
