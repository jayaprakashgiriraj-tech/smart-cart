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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WhyThisFitsScreen(
    productName: String = "JBL Tune 510BT",
    onBackClick: () -> Unit = {},
    onWishlistClick: () -> Unit = {},
    onCompareClick: () -> Unit = {},
    onAlternativeClick: (String) -> Unit = {}
) {
    var isWishlisted by remember { mutableStateOf(false) }

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
                            text = "Why this fits you",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E2135)
                        )
                        Text(
                            text = "$productName · Analysis continued",
                            fontSize = 11.sp,
                            color = Color(0xFF7A8398)
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

                    WhyReasonItem(text = "Budget: ₹201 below your ₹3,000 limit.")
                    Spacer(modifier = Modifier.height(10.dp))
                    WhyReasonItem(text = "Classes: simple mic controls and multipoint let you switch between phone and laptop.")
                    Spacer(modifier = Modifier.height(10.dp))
                    WhyReasonItem(text = "Routine: 40-hour battery cuts charging breaks.")
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

                        ProConBullet(text = "Lightweight, 160 g")
                        ProConBullet(text = "40-hour battery")
                        ProConBullet(text = "Multipoint pairing")
                        ProConBullet(text = "Fast USB-C charging")
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

                        ProConBullet(text = "No ANC")
                        ProConBullet(text = "No app EQ")
                        ProConBullet(text = "On-ear pressure")
                        ProConBullet(text = "Mic struggles outdoors")
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
                        text = "No active noise cancellation or wired fallback. Consider another option for crowded commutes or long, pressure-sensitive wear.",
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

            // Soundcore H30i Alternative Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAlternativeClick("Soundcore H30i") },
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
                            contentDescription = "Soundcore H30i",
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
                                text = "Soundcore H30i",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E2135)
                            )
                            Text(
                                text = "88% match",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF5050F0)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "₹2,499",
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
                                text = "4.2",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E2135)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "70 hours battery",
                                fontSize = 11.sp,
                                color = Color(0xFF7A8398)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Choose H30i for 70-hour battery and app EQ. Trade-off: a less consistent mic in noisy spaces.",
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
                    onClick = onCompareClick,
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
