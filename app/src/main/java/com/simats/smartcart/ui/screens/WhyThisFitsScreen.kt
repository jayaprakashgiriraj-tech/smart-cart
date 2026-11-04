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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.smartcart.data.ProductCatalog
import com.simats.smartcart.data.WishlistRepository

@Composable
fun WhyThisFitsScreen(
    productName: String = "JBL Tune 510BT",
    onBackClick: () -> Unit = {},
    onWishlistClick: () -> Unit = {},
    onCompareClick: (String, String) -> Unit = { _, _ -> },
    onAlternativeClick: (String) -> Unit = {}
) {
    var showCompareSheet by remember { mutableStateOf(false) }

    val product = remember(productName) {
        ProductCatalog.getProductByName(productName) ?: ProductCatalog.products.first()
    }
    val isWishlisted = WishlistRepository.isWishlisted(product.name)

    val similarProduct = remember(product) {
        ProductCatalog.getSimilarProducts(product).firstOrNull() ?: ProductCatalog.products.first { it.id != product.id }
    }

    val similarIcon = remember(similarProduct) {
        when (similarProduct.category) {
            "Mobiles" -> Icons.Outlined.Smartphone
            "Laptops" -> Icons.Outlined.Laptop
            "Audio" -> if (similarProduct.subcategory == "Speakers") Icons.Outlined.Speaker else Icons.Outlined.Headphones
            "Fashion" -> if (similarProduct.needs.contains("Watches")) Icons.Outlined.Watch else if (similarProduct.needs.contains("Accessories")) Icons.Outlined.ShoppingBag else Icons.Outlined.Checkroom
            "Home Appliances" -> if (similarProduct.subcategory == "Televisions") Icons.Outlined.Tv else Icons.Outlined.Kitchen
            else -> Icons.Outlined.AutoAwesome
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
                            text = "Why this fits you",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E2135)
                        )
                        Text(
                            text = "${product.name} · Analysis continued",
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

            // "Why this product is right for you" Card
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
                    Text(
                        text = "Why this product is right for you",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E2135)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    WhyReasonItem(text = "Verdict: ${product.recommendationSummary}")
                    Spacer(modifier = Modifier.height(10.dp))
                    WhyReasonItem(text = "Best For: ${product.bestFor}")
                    Spacer(modifier = Modifier.height(10.dp))
                    WhyReasonItem(text = "Hardware: ${product.specs} with official ${product.warranty} warranty.")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Pros & Cons Side-by-Side Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Pros Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFECEFF8))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.AddCircleOutline,
                                contentDescription = null,
                                tint = Color(0xFF1A8068),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Pros",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A8068)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val specItems = product.specs.split("·").map { it.trim() }
                        ProConBullet(text = specItems.getOrNull(0) ?: "${product.brand} build quality")
                        ProConBullet(text = specItems.getOrNull(1) ?: "${product.category} performance")
                        ProConBullet(text = product.bestFor)
                        ProConBullet(text = "${product.warranty} official warranty")
                    }
                }

                // Cons Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFECEFF8))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.RemoveCircleOutline,
                                contentDescription = null,
                                tint = Color(0xFFB76E00),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Cons",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB76E00)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        when (product.category) {
                            "Mobiles" -> {
                                ProConBullet(text = "Fast charger often sold separately")
                                ProConBullet(text = "High refresh rate uses more battery in 5G gaming")
                                ProConBullet(text = "No microSD expandable storage")
                                ProConBullet(text = "Heavy camera usage needs daily charging")
                            }
                            "Laptops" -> {
                                ProConBullet(text = "Soldered RAM/SSD on thin variants")
                                ProConBullet(text = "Fan audible under peak prolonged loads")
                                ProConBullet(text = "Charger brick needed for fast gaming power")
                                ProConBullet(text = "Limited legacy port selection")
                            }
                            "Audio" -> {
                                ProConBullet(text = "On-ear fit may feel snug for glasses wearers")
                                ProConBullet(text = "No active noise cancelling on budget tier")
                                ProConBullet(text = "Microphone best in quiet environments")
                                ProConBullet(text = "Wired cable not included in box")
                            }
                            "Fashion" -> {
                                ProConBullet(text = "Care label instructions must be followed")
                                ProConBullet(text = "Natural dyes may fade in harsh direct sun")
                                ProConBullet(text = "Check size guide for optimal fit")
                                ProConBullet(text = "Gentle wash recommended for longevity")
                            }
                            else -> {
                                ProConBullet(text = "Requires dedicated grounded wall outlet")
                                ProConBullet(text = "Professional installation recommended")
                                ProConBullet(text = "Annual filter/maintenance suggested")
                                ProConBullet(text = "Delivery may take 2-4 business days")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // "Know the compromise" Card
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
                        text = "Know the compromise",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB76E00)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Every product has trade-offs. For ${product.name}, consider whether ${product.bestFor.lowercase()} fits your daily requirements and budget expectations.",
                        fontSize = 12.sp,
                        color = Color(0xFF8A5300),
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Alternative to consider Section
            Text(
                text = "Alternative to consider",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E2135)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Similar Alternative Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAlternativeClick(similarProduct.name) },
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
                            imageVector = similarIcon,
                            contentDescription = similarProduct.name,
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
                                text = similarProduct.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E2135),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = similarProduct.matchScore,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF5050F0)
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "${similarProduct.brand} · ${similarProduct.subcategory}",
                            fontSize = 11.sp,
                            color = Color(0xFF7A8398),
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = similarProduct.price,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E2135)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Star,
                                contentDescription = "Rating",
                                tint = Color(0xFFFFB800),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = similarProduct.rating,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E2135)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = similarProduct.specs,
                                fontSize = 11.sp,
                                color = Color(0xFF7A8398),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Choose ${similarProduct.name} for ${similarProduct.bestFor.lowercase()}. Value score: ${similarProduct.valueScore}/10.",
                fontSize = 11.sp,
                color = Color(0xFF7A8398),
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons (Wishlist & Compare)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        WishlistRepository.toggleWishlist(product)
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
                        imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
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
private fun WhyReasonItem(text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = Icons.Outlined.Check,
            contentDescription = null,
            tint = Color(0xFF5050F0),
            modifier = Modifier
                .size(16.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color(0xFF1E2135),
            fontWeight = FontWeight.Medium,
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun ProConBullet(text: String) {
    Text(
        text = "• $text",
        fontSize = 11.sp,
        color = Color(0xFF1E2135),
        lineHeight = 16.sp,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun WhyThisFitsScreenPreview() {
    WhyThisFitsScreen()
}
