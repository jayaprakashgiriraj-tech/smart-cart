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
import com.simats.smartcart.ai.SmartCartAiEngine
import com.simats.smartcart.data.ProductCatalog

@Composable
fun AiReviewAnalyzerScreen(
    productName: String = "JBL Tune 510BT",
    onBackClick: () -> Unit = {}
) {
    val product = remember(productName) {
        ProductCatalog.getProductByName(productName) ?: ProductCatalog.products.first()
    }
    val aiReviewSummary = remember(product) {
        SmartCartAiEngine.summarizeReviews(product)
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
                        text = "SYNTHETIC AI ANALYSIS DATASET",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF9AA4B8),
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${product.reviewCount} customer reviews analyzed · ${product.category}",
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
                                text = product.rating,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1E2135)
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                val rVal = product.rating.toDoubleOrNull() ?: 4.2
                                repeat(5) { starIndex ->
                                    Icon(
                                        imageVector = if (starIndex < rVal.toInt()) Icons.Outlined.Star else Icons.Outlined.StarOutline,
                                        contentDescription = null,
                                        tint = Color(0xFFFFB800),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
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
                            val posFrac = aiReviewSummary.positivePercent / 100f
                            val neuFrac = aiReviewSummary.neutralPercent / 100f
                            val critFrac = aiReviewSummary.criticalPercent / 100f
                            StarBreakdownRow(starLabel = "5", count = "${(posFrac * 120).toInt()}", progress = posFrac)
                            StarBreakdownRow(starLabel = "4", count = "${(posFrac * 40).toInt()}", progress = posFrac * 0.4f)
                            StarBreakdownRow(starLabel = "3", count = "${(neuFrac * 35).toInt()}", progress = neuFrac)
                            StarBreakdownRow(starLabel = "2", count = "${(critFrac * 20).toInt()}", progress = critFrac * 0.6f)
                            StarBreakdownRow(starLabel = "1", count = "${(critFrac * 10).toInt()}", progress = critFrac * 0.3f)
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
                            text = "AI Sentiment & Review Summary",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5050F0)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = aiReviewSummary.summaryText,
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
                    text = "Customer Sentiment",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E2135)
                )
                Text(
                    text = "AI NLP Classified",
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
                val pWeight = (aiReviewSummary.positivePercent.toFloat() / 100f).coerceAtLeast(0.05f)
                val nWeight = (aiReviewSummary.neutralPercent.toFloat() / 100f).coerceAtLeast(0.05f)
                val cWeight = (aiReviewSummary.criticalPercent.toFloat() / 100f).coerceAtLeast(0.05f)
                Box(
                    modifier = Modifier
                        .weight(pWeight)
                        .fillMaxHeight()
                        .background(Color(0xFF1A8068))
                )
                Spacer(modifier = Modifier.width(2.dp))
                Box(
                    modifier = Modifier
                        .weight(nWeight)
                        .fillMaxHeight()
                        .background(Color(0xFFD6DDF5))
                )
                Spacer(modifier = Modifier.width(2.dp))
                Box(
                    modifier = Modifier
                        .weight(cWeight)
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
                    text = "${aiReviewSummary.positivePercent}% positive",
                    fontSize = 11.sp,
                    color = Color(0xFF1A8068),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${aiReviewSummary.neutralPercent}% neutral",
                    fontSize = 11.sp,
                    color = Color(0xFF7A8398),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${aiReviewSummary.criticalPercent}% critical",
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
                            text = "Top Loved Features",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A8068)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        aiReviewSummary.topLovedFeatures.forEach { feat ->
                            Text(
                                text = "• $feat",
                                fontSize = 11.sp,
                                color = Color(0xFF1E2135),
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
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
                            text = "Reported Drawbacks",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB76E00)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        aiReviewSummary.commonComplaints.forEach { draw ->
                            Text(
                                text = "• $draw",
                                fontSize = 11.sp,
                                color = Color(0xFF1E2135),
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Buyer Verdict Takeaway Card
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
                        text = "SmartCart AI Buying Consensus",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB76E00)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = aiReviewSummary.recommendationVerdict,
                        fontSize = 11.sp,
                        color = Color(0xFF8A5300),
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
