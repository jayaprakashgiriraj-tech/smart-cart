package com.simats.smartcart.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PriceTrackingScreen(
    productName: String = "JBL Tune 510BT",
    currentPrice: String = "₹2,799",
    sampleDate: String = "sample · 30 Sep",
    onBackClick: () -> Unit = {},
    onSaveTargetPriceClick: () -> Unit = {}
) {
    var notifyTargetPrice by remember { mutableStateOf(true) }
    var targetPriceText by remember { mutableStateOf("₹2,499") }
    var isSavedToastShown by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                tonalElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Button(
                        onClick = {
                            isSavedToastShown = true
                            onSaveTargetPriceClick()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5050F0))
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isSavedToastShown) "Target price saved!" else "Save target price",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
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
                            text = "Price Tracking",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E2135)
                        )
                        Text(
                            text = "A calmer way to watch your budget",
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
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
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
                    text = "Illustrative data · not live retailer or review data",
                    fontSize = 11.sp,
                    color = Color(0xFF5050F0),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Product Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                            .size(68.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF5F6FA)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Headphones,
                            contentDescription = productName,
                            tint = Color(0xFF2A2A2A),
                            modifier = Modifier.size(42.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = productName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E2135)
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = currentPrice,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1E2135)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = sampleDate,
                                fontSize = 11.sp,
                                color = Color(0xFF7A8398),
                                modifier = Modifier.padding(bottom = 3.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Price History Section
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Price history",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E2135)
                        )
                        Text(
                            text = "September 2026",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5050F0)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Line Graph Canvas
                    PriceHistoryGraph(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "7 illustrative observations · not retailer price history",
                        fontSize = 10.sp,
                        color = Color(0xFF9AA4B8)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Lowest Recorded Banner
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFE6F5F0))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFCEEAD6)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ArrowDownward,
                                contentDescription = null,
                                tint = Color(0xFF1A8068),
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "Lowest recorded in this sample: ₹2,699",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A8068)
                            )
                            Text(
                                text = "26 Sep · not a verified market low",
                                fontSize = 10.sp,
                                color = Color(0xFF1A8068)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Your Target Price Card
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Your target price",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E2135)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "₹300 below current sample",
                                fontSize = 11.sp,
                                color = Color(0xFF7A8398)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF2F4FF))
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = targetPriceText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1E2135)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Notify me at or below target",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1E2135)
                        )

                        Switch(
                            checked = notifyTargetPrice,
                            onCheckedChange = { notifyTargetPrice = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF5050F0)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Illustrative Estimate Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F4FF)),
                border = BorderStroke(1.dp, Color(0xFFE2E6F5))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFF5050F0),
                        modifier = Modifier
                            .size(18.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Illustrative estimate: ₹2,600–₹2,900 next week. Based only on sample observations; not guaranteed. Demo alerts do not send notifications.",
                        fontSize = 11.sp,
                        color = Color(0xFF5050F0),
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun PriceHistoryGraph(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val leftMargin = 55.dp.toPx()
            val rightMargin = 20.dp.toPx()
            val topMargin = 10.dp.toPx()
            val bottomMargin = 25.dp.toPx()

            val graphWidth = width - leftMargin - rightMargin
            val graphHeight = height - topMargin - bottomMargin

            // Sample data points (Date index 0..6, Price ₹2,600..₹3,200)
            // 0: ₹3,200 (1 Sep)
            // 1: ₹3,200
            // 2: ₹3,000
            // 3: ₹3,100 (15 Sep)
            // 4: ₹2,900
            // 5: ₹2,700 (26 Sep)
            // 6: ₹2,800 (30 Sep)
            val prices = listOf(3200f, 3200f, 3000f, 3100f, 2900f, 2700f, 2800f)
            val minPrice = 2600f
            val maxPrice = 3200f

            // Dashed Grid Lines for Y-axis (₹3,200, ₹2,900, ₹2,600)
            val gridPrices = listOf(3200f, 2900f, 2600f)
            val dashPathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

            gridPrices.forEach { price ->
                val normY = (price - minPrice) / (maxPrice - minPrice)
                val y = topMargin + graphHeight * (1f - normY)

                drawLine(
                    color = Color(0xFFECEFF8),
                    start = Offset(leftMargin, y),
                    end = Offset(width - rightMargin, y),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = dashPathEffect
                )
            }

            // Calculate points
            val points = prices.mapIndexed { index, price ->
                val normX = index.toFloat() / (prices.size - 1)
                val normY = (price - minPrice) / (maxPrice - minPrice)

                val x = leftMargin + graphWidth * normX
                val y = topMargin + graphHeight * (1f - normY)
                Offset(x, y)
            }

            // Draw line connecting points
            val linePath = Path()
            points.forEachIndexed { index, offset ->
                if (index == 0) {
                    linePath.moveTo(offset.x, offset.y)
                } else {
                    linePath.lineTo(offset.x, offset.y)
                }
            }

            drawPath(
                path = linePath,
                color = Color(0xFF5050F0),
                style = Stroke(width = 2.5.dp.toPx())
            )

            // Draw dots at data points
            points.forEach { point ->
                drawCircle(
                    color = Color(0xFF5050F0),
                    radius = 3.5.dp.toPx(),
                    center = point
                )
            }
        }

        // Overlay Axis Text Labels using Compose Text
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(50.dp)
                .padding(bottom = 22.dp, top = 2.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text("₹3,200", fontSize = 10.sp, color = Color(0xFF9AA4B8))
            Text("₹2,900", fontSize = 10.sp, color = Color(0xFF9AA4B8))
            Text("₹2,600", fontSize = 10.sp, color = Color(0xFF9AA4B8))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(start = 55.dp, end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("1 Sep", fontSize = 10.sp, color = Color(0xFF9AA4B8))
            Text("15 Sep", fontSize = 10.sp, color = Color(0xFF9AA4B8))
            Text("30 Sep", fontSize = 10.sp, color = Color(0xFF9AA4B8))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PriceTrackingScreenPreview() {
    PriceTrackingScreen()
}
