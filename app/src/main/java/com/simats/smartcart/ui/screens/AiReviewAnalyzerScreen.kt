package com.simats.smartcart.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
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
fun AiReviewAnalyzerScreen(
    productName: String = "JBL Tune 510BT",
    onBackClick: () -> Unit = {}
) {
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
                            text = "AI Review Analyzer",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E2135)
                        )
                        Text(
                            text = productName,
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
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Info",
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

            // SYNTHETIC DEMO DATASET Rating Breakdown Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFECEFF8))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "SYNTHETIC DEMO DATASET",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF9AA4B8),
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "240 sample reviews · September 2026",
                        fontSize = 11.sp,
                        color = Color(0xFF7A8398)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Rating Score Column
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(80.dp)
                        ) {
                            Text(
                                text = "4.3",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1E2135)
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                repeat(4) {
                                    Icon(
                                        imageVector = Icons.Outlined.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFFFB800),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Outlined.StarOutline,
                                    contentDescription = null,
                                    tint = Color(0xFFFFB800),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Text(
                                text = "out of 5",
                                fontSize = 10.sp,
                                color = Color(0xFF7A8398)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Star Breakdown Bars Column
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            StarBreakdownRow(starLabel = "5", count = "144", progress = 0.75f)
                            StarBreakdownRow(starLabel = "4", count = "48", progress = 0.25f)
                            StarBreakdownRow(starLabel = "3", count = "24", progress = 0.12f)
                            StarBreakdownRow(starLabel = "2", count = "14", progress = 0.07f)
                            StarBreakdownRow(starLabel = "1", count = "10", progress = 0.05f)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // AI Summary of this sample Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F4FF)),
                border = BorderStroke(1.dp, Color(0xFFE2E6F5))
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
                            text = "AI summary of this sample",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5050F0)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Customers appreciate battery life and clear indoor calls, but some report ear pressure during long sessions and a weaker mic in noisy surroundings.",
                        fontSize = 12.sp,
                        color = Color(0xFF5050F0),
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Sample Sentiment Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sample sentiment",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E2135)
                )
                Text(
                    text = "AI-classified",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5050F0)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sentiment Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
            ) {
                Box(
                    modifier = Modifier
                        .weight(0.78f)
                        .fillMaxHeight()
                        .background(Color(0xFF1A8068))
                )
                Spacer(modifier = Modifier.width(2.dp))
                Box(
                    modifier = Modifier
                        .weight(0.14f)
                        .fillMaxHeight()
                        .background(Color(0xFFD6DDF5))
                )
                Spacer(modifier = Modifier.width(2.dp))
                Box(
                    modifier = Modifier
                        .weight(0.08f)
                        .fillMaxHeight()
                        .background(Color(0xFFE53935))
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sentiment Labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "78% positive",
                    fontSize = 11.sp,
                    color = Color(0xFF1A8068),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "14% neutral",
                    fontSize = 11.sp,
                    color = Color(0xFF7A8398),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "8% negative",
                    fontSize = 11.sp,
                    color = Color(0xFFE53935),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Common Praise & Complaints Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Praise Card
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
                        Text(
                            text = "Common praise",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A8068)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Long battery",
                            fontSize = 11.sp,
                            color = Color(0xFF1E2135),
                            lineHeight = 16.sp
                        )
                        Text(
                            text = "Lightweight build",
                            fontSize = 11.sp,
                            color = Color(0xFF1E2135),
                            lineHeight = 16.sp
                        )
                        Text(
                            text = "Easy device switching",
                            fontSize = 11.sp,
                            color = Color(0xFF1E2135),
                            lineHeight = 16.sp
                        )
                    }
                }

                // Complaints Card
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
                        Text(
                            text = "Common complaints",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB76E00)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Ear-cup pressure",
                            fontSize = 11.sp,
                            color = Color(0xFF1E2135),
                            lineHeight = 16.sp
                        )
                        Text(
                            text = "Weak outdoor mic",
                            fontSize = 11.sp,
                            color = Color(0xFF1E2135),
                            lineHeight = 16.sp
                        )
                        Text(
                            text = "Plastic build feel",
                            fontSize = 11.sp,
                            color = Color(0xFF1E2135),
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // "Watch for: long-session comfort" Card
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
                    Text(
                        text = "Watch for: long-session comfort",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E2135)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Ear pressure appears in 28 sample reviews; noisy calls in 19. Themes can overlap. This is not verified customer evidence.",
                        fontSize = 11.sp,
                        color = Color(0xFF7A8398),
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StarBreakdownRow(
    starLabel: String,
    count: String,
    progress: Float
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = starLabel,
            fontSize = 10.sp,
            color = Color(0xFF7A8398),
            modifier = Modifier.width(10.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .weight(1f)
                .height(5.dp)
                .clip(CircleShape),
            color = Color(0xFF5050F0),
            trackColor = Color(0xFFF0F2F8)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = count,
            fontSize = 10.sp,
            color = Color(0xFF7A8398),
            modifier = Modifier.width(24.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AiReviewAnalyzerScreenPreview() {
    AiReviewAnalyzerScreen()
}
