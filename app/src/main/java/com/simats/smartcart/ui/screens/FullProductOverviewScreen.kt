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
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.smartcart.data.ProductCatalog

@Composable
fun FullProductOverviewScreen(
    productName: String = "JBL Tune 510BT",
    onBackClick: () -> Unit = {},
    onWishlistClick: () -> Unit = {},
    onCompareClick: (String, String) -> Unit = { _, _ -> },
    onWhyItFitsClick: () -> Unit = {}
) {
    var selectedImageIndex by remember { mutableIntStateOf(0) }
    var isWishlisted by remember { mutableStateOf(false) }
    var showCompareSheet by remember { mutableStateOf(false) }

    val product = remember(productName) {
        ProductCatalog.getProductByName(productName) ?: ProductCatalog.products.first()
    }

    val primaryIcon = remember(product) {
        when (product.category) {
            "Mobiles" -> Icons.Outlined.Smartphone
            "Laptops" -> Icons.Outlined.Laptop
            "Audio" -> if (product.subcategory == "Speakers") Icons.Outlined.Speaker else Icons.Outlined.Headphones
            "Fashion" -> if (product.needs.contains("Watches")) Icons.Outlined.Watch else if (product.needs.contains("Accessories")) Icons.Outlined.ShoppingBag else Icons.Outlined.Checkroom
            "Home Appliances" -> if (product.subcategory == "Televisions") Icons.Outlined.Tv else Icons.Outlined.Kitchen
            else -> Icons.Outlined.AutoAwesome
        }
    }

    val galleryIcons = remember(product, primaryIcon) {
        when (product.category) {
            "Mobiles" -> listOf(primaryIcon, Icons.Outlined.CameraAlt, Icons.Outlined.BatteryChargingFull)
            "Laptops" -> listOf(primaryIcon, Icons.Outlined.Memory, Icons.Outlined.BatteryChargingFull)
            "Audio" -> listOf(primaryIcon, Icons.Outlined.GraphicEq, Icons.Outlined.Bluetooth)
            "Fashion" -> listOf(primaryIcon, Icons.Outlined.Checkroom, Icons.Outlined.Style)
            "Home Appliances" -> listOf(primaryIcon, Icons.Outlined.Power, Icons.Outlined.Speed)
            else -> listOf(primaryIcon, Icons.Outlined.Star, Icons.Outlined.AutoAwesome)
        }
    }

    val specTriad = remember(product) {
        val parts = product.specs.split("·").map { it.trim() }
        val s1 = parts.getOrNull(0) ?: (if (product.battery != "N/A") "${product.battery} battery" else product.category)
        val s2 = parts.getOrNull(1) ?: product.subcategory
        val s3 = parts.getOrNull(2) ?: product.brand
        Triple(s1, s2, s3)
    }

    val specIcons = remember(product) {
        when (product.category) {
            "Mobiles" -> Triple(Icons.Outlined.Memory, Icons.Outlined.Storage, Icons.Outlined.Wifi)
            "Laptops" -> Triple(Icons.Outlined.Memory, Icons.Outlined.Storage, Icons.Outlined.BatteryChargingFull)
            "Audio" -> Triple(Icons.Outlined.BatteryChargingFull, Icons.Outlined.Bluetooth, Icons.Outlined.ShoppingBag)
            "Fashion" -> Triple(Icons.Outlined.Checkroom, Icons.Outlined.ShoppingBag, Icons.Outlined.Verified)
            "Home Appliances" -> Triple(Icons.Outlined.Power, Icons.Outlined.Speed, Icons.Outlined.Kitchen)
            else -> Triple(Icons.Outlined.AutoAwesome, Icons.Outlined.Star, Icons.Outlined.ShoppingBag)
        }
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
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                            text = "Product Details",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E2135)
                        )
                        Text(
                            text = "${product.name} · Overview",
                            fontSize = 11.sp,
                            color = Color(0xFF7A8398),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                IconButton(
                    onClick = { },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Share",
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

            // Product Gallery Section (Main Image + Thumbnail Column)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Main Big Image
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(190.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFFF5F6FA)),
                    contentAlignment = Alignment.Center
                ) {
                    val currentIcon = galleryIcons.getOrElse(selectedImageIndex) { primaryIcon }
                    Icon(
                        imageVector = currentIcon,
                        contentDescription = product.name,
                        tint = Color(0xFF2A2A2A),
                        modifier = Modifier.size(110.dp)
                    )

                    // Page counter badge (e.g. 1 / 3)
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.85f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${selectedImageIndex + 1} / ${galleryIcons.size}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E2135)
                        )
                    }
                }

                // Thumbnail Column
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    galleryIcons.forEachIndexed { index, iconItem ->
                        val isSelected = selectedImageIndex == index
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFF5F6FA))
                                .border(
                                    BorderStroke(
                                        if (isSelected) 2.dp else 1.dp,
                                        if (isSelected) Color(0xFF5050F0) else Color(0xFFE2E6F5)
                                    ),
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { selectedImageIndex = index },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = iconItem,
                                contentDescription = null,
                                tint = if (isSelected) Color(0xFF5050F0) else Color(0xFF2A2A2A),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Title
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
                        text = "${product.reviewCount} customer reviews",
                        fontSize = 11.sp,
                        color = Color(0xFF7A8398)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Spec Highlights Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val (s1, s2, s3) = specTriad
                val (i1, i2, i3) = specIcons
                OverviewSpecBox(
                    icon = i1,
                    text = s1,
                    modifier = Modifier.weight(1f)
                )
                OverviewSpecBox(
                    icon = i2,
                    text = s2,
                    modifier = Modifier.weight(1f)
                )
                OverviewSpecBox(
                    icon = i3,
                    text = s3,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // SMARTCART AI VERDICT CARD
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "SMARTCART AI VERDICT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1A8068),
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Check,
                                    contentDescription = null,
                                    tint = Color(0xFF1A8068),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "BUY",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF1A8068)
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${product.valueScore}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1A8068)
                            )
                            Text(
                                text = " /10",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A8068),
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = product.recommendationSummary,
                        fontSize = 12.sp,
                        color = Color(0xFF1A8068),
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sub-scores Row (3 cards)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val budgetScore = "${((product.valueScore + 0.3).coerceIn(8.0, 9.8)).let { "%.1f".format(it) }}/10"
                val featureScore = "${((product.matchPercentage / 10.0).coerceIn(8.2, 9.9)).let { "%.1f".format(it) }}/10"
                val valueScore = "${product.valueScore}/10"
                SubScoreCard(
                    score = budgetScore,
                    label = "Budget Match",
                    modifier = Modifier.weight(1f)
                )
                SubScoreCard(
                    score = featureScore,
                    label = "Feature Match",
                    modifier = Modifier.weight(1f)
                )
                SubScoreCard(
                    score = valueScore,
                    label = "Value for Money",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Pros, cons & alternatives link
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onWhyItFitsClick() }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Why it fits, pros, cons & alternatives",
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

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Illustrative AI scores. Specs and prices should be checked with the retailer before purchase.",
                fontSize = 10.sp,
                color = Color(0xFF9AA4B8),
                lineHeight = 14.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons (Wishlist & Compare)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        isWishlisted = !isWishlisted
                        onWishlistClick()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E6F5)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF5050F0))
                ) {
                    Icon(
                        imageVector = if (isWishlisted) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Wishlist",
                        tint = if (isWishlisted) Color(0xFFE53935) else Color(0xFF5050F0),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Wishlist",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = { showCompareSheet = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5050F0)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.CompareArrows,
                        contentDescription = "Compare",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Compare",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (showCompareSheet) {
            CompareProductSheet(
                baseProduct = product,
                onSelectProduct = { chosen ->
                    showCompareSheet = false
                    onCompareClick(product.name, chosen.name)
                },
                onDismiss = { showCompareSheet = false }
            )
        }
    }
}

@Composable
private fun OverviewSpecBox(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFECEFF8))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF5050F0),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1E2135),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SubScoreCard(
    score: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F4FF)),
        border = BorderStroke(1.dp, Color(0xFFE2E6F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = score,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF5050F0)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                color = Color(0xFF7A8398),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FullProductOverviewScreenPreview() {
    FullProductOverviewScreen()
}
