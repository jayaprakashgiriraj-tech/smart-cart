package com.simats.smartcart.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.smartcart.data.PriceTrackingRepository
import com.simats.smartcart.data.Product
import com.simats.smartcart.data.ProductCatalog
import com.simats.smartcart.data.ProductPriceHistory

@Composable
fun PriceTrackingScreen(
    productName: String = "JBL Tune 510BT",
    currentPrice: String = "",
    sampleDate: String = "",
    onBackClick: () -> Unit = {},
    onSaveTargetPriceClick: () -> Unit = {}
) {
    val product = remember(productName) {
        ProductCatalog.getProductByName(productName)
    }

    var history by remember(product) {
        mutableStateOf(PriceTrackingRepository.getCachedOrLocal(product))
    }

    LaunchedEffect(product) {
        val live = PriceTrackingRepository.getLivePriceHistory(product)
        history = live
    }

    var notifyTargetPrice by remember { mutableStateOf(true) }
    var targetPriceText by remember(history) { mutableStateOf(history.suggestedTargetPriceFormatted) }
    var isSavedToastShown by remember { mutableStateOf(false) }

    val productIcon = remember(product) {
        getProductCategoryIcon(product)
    }

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
                            imageVector = if (isSavedToastShown) Icons.Outlined.Check else Icons.Outlined.Notifications,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isSavedToastShown) "Target price alert saved!" else "Save target price alert",
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00C853))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Live real-time market tracking",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF00C853)
                            )
                        }
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

            // Real-time Live Status Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE8F7F2))
                    .border(BorderStroke(1.dp, Color(0xFFC8ECE0)), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF1A8068),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Live store sync: Amazon, Flipkart & Croma · ${history.lastUpdatedText}",
                    fontSize = 11.sp,
                    color = Color(0xFF1A8068),
                    fontWeight = FontWeight.SemiBold
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
                            imageVector = productIcon,
                            contentDescription = product.name,
                            tint = Color(0xFF2A2A2A),
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = product.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E2135)
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "${product.brand} · ${product.getComparisonCategoryLabel()}",
                            fontSize = 11.sp,
                            color = Color(0xFF7A8398)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = history.currentPriceFormatted,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1E2135)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Live price today",
                                fontSize = 11.sp,
                                color = Color(0xFF00C853),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 3.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // LOWEST PRICE SOLD & HIGHEST PRICE SOLD STAT CARDS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Lowest Price Sold Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F7F2)),
                    border = BorderStroke(1.dp, Color(0xFFC8ECE0))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFCEEAD6)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ArrowDownward,
                                    contentDescription = null,
                                    tint = Color(0xFF1A8068),
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LOWEST SOLD",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1A8068),
                                letterSpacing = 0.5.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = history.lowestPriceFormatted,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1A8068)
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "Sold on ${history.lowestPriceDate}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1A8068)
                        )

                        Text(
                            text = "All-time recorded low",
                            fontSize = 10.sp,
                            color = Color(0xFF4A9E88)
                        )
                    }
                }

                // Highest Price Sold Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF2EE)),
                    border = BorderStroke(1.dp, Color(0xFFFFD8CE))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFFFD8D0)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ArrowUpward,
                                    contentDescription = null,
                                    tint = Color(0xFFC8422A),
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "HIGHEST SOLD",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFC8422A),
                                letterSpacing = 0.5.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = history.highestPriceFormatted,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFC8422A)
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "Sold on ${history.highestPriceDate}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFC8422A)
                        )

                        Text(
                            text = "Launch peak recorded",
                            fontSize = 10.sp,
                            color = Color(0xFFB06050)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Peak Discount / Drop Opportunity Callout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF3F4FD))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocalOffer,
                    contentDescription = null,
                    tint = Color(0xFF5050F0),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Current price is ${history.savingsFromPeakFormatted} (${history.savingsPercent}%) lower than the highest recorded peak price.",
                    fontSize = 11.sp,
                    color = Color(0xFF5050F0),
                    fontWeight = FontWeight.Medium,
                    lineHeight = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Price History Graph Card
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Real-Time Price History",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E2135)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00C853))
                            )
                        }

                        Text(
                            text = "30-Day Live",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5050F0)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Legend indicators for High, Low and Current
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFC8422A))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "High: ${history.highestPriceFormatted}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFC8422A)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF1A8068))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Low: ${history.lowestPriceFormatted}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1A8068)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF5050F0))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Today: ${history.currentPriceFormatted}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF5050F0)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Line Graph Canvas
                    PriceHistoryGraph(
                        history = history,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Real-time observations · Tracked across live retailers",
                        fontSize = 10.sp,
                        color = Color(0xFF9AA4B8)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Lowest Recorded Banner
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFE8F7F2))
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
                                text = "Lowest recorded price sold: ${history.lowestPriceFormatted}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A8068)
                            )
                            Text(
                                text = "${history.lowestPriceDate} · Save ${history.savingsFromPeakFormatted} from launch peak",
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
                                text = "Your target price alert",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E2135)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Instant alert when price hits or drops below this",
                                fontSize = 11.sp,
                                color = Color(0xFF7A8398)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF2F4FF))
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = targetPriceText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF5050F0)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick percentage threshold chips
                    Text(
                        text = "Quick thresholds:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF7A8398)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val lowChip = history.lowestPriceFormatted
                        val minus5 = ProductCatalog.formatIndianRupees((history.currentPrice * 0.95).toInt())
                        val minus10 = ProductCatalog.formatIndianRupees((history.currentPrice * 0.90).toInt())

                        TargetPriceChip(
                            label = "Lowest ($lowChip)",
                            isSelected = targetPriceText == lowChip,
                            onClick = { targetPriceText = lowChip },
                            modifier = Modifier.weight(1f)
                        )
                        TargetPriceChip(
                            label = "-5% ($minus5)",
                            isSelected = targetPriceText == minus5,
                            onClick = { targetPriceText = minus5 },
                            modifier = Modifier.weight(1f)
                        )
                        TargetPriceChip(
                            label = "-10% ($minus10)",
                            isSelected = targetPriceText == minus10,
                            onClick = { targetPriceText = minus10 },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

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

            // Forecast & Trend Card
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
                        text = "Price trend forecast: ${history.estimatedNextWeekMinFormatted}–${history.estimatedNextWeekMaxFormatted} expected range over upcoming days based on real-time market patterns for ${product.name}.",
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
private fun TargetPriceChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) Color(0xFF5050F0) else Color(0xFFF2F4FF))
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else Color(0xFF5050F0),
            maxLines = 1
        )
    }
}

@Composable
fun PriceHistoryGraph(
    history: ProductPriceHistory,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        val minPrice = history.lowestPrice.toFloat()
        val maxPrice = history.highestPrice.toFloat()
        val priceSpan = (maxPrice - minPrice).coerceAtLeast(1f)
        val midPrice = (history.highestPrice + history.lowestPrice) / 2

        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val leftMargin = 68.dp.toPx()
            val rightMargin = 20.dp.toPx()
            val topMargin = 16.dp.toPx()
            val bottomMargin = 24.dp.toPx()

            val graphWidth = width - leftMargin - rightMargin
            val graphHeight = height - topMargin - bottomMargin

            val dashPathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

            // Top line (corresponds to Highest Price Sold)
            drawLine(
                color = Color(0xFFECEFF8),
                start = Offset(leftMargin, topMargin),
                end = Offset(width - rightMargin, topMargin),
                strokeWidth = 1.dp.toPx(),
                pathEffect = dashPathEffect
            )

            // Mid line (corresponds to Midpoint Price)
            val midY = topMargin + graphHeight * 0.5f
            drawLine(
                color = Color(0xFFECEFF8),
                start = Offset(leftMargin, midY),
                end = Offset(width - rightMargin, midY),
                strokeWidth = 1.dp.toPx(),
                pathEffect = dashPathEffect
            )

            // Bottom line (corresponds to Lowest Price Sold)
            val bottomY = topMargin + graphHeight
            drawLine(
                color = Color(0xFFECEFF8),
                start = Offset(leftMargin, bottomY),
                end = Offset(width - rightMargin, bottomY),
                strokeWidth = 1.dp.toPx(),
                pathEffect = dashPathEffect
            )

            // Calculate points accurately aligned to graph bounds
            val points = history.pricePoints.mapIndexed { index, pt ->
                val normX = index.toFloat() / (history.pricePoints.size - 1)
                val normY = (pt.price.toFloat() - minPrice) / priceSpan
                val x = leftMargin + graphWidth * normX
                val y = topMargin + graphHeight * (1f - normY.coerceIn(0f, 1f))
                Offset(x, y)
            }

            // Draw gradient area under curve
            if (points.isNotEmpty()) {
                val fillPath = Path()
                fillPath.moveTo(points.first().x, points.first().y)

                for (i in 0 until points.size - 1) {
                    val p0 = points[i]
                    val p1 = points[i + 1]
                    val controlX1 = p0.x + (p1.x - p0.x) / 2f
                    val controlY1 = p0.y
                    val controlX2 = p0.x + (p1.x - p0.x) / 2f
                    val controlY2 = p1.y
                    fillPath.cubicTo(controlX1, controlY1, controlX2, controlY2, p1.x, p1.y)
                }

                fillPath.lineTo(points.last().x, bottomY)
                fillPath.lineTo(points.first().x, bottomY)
                fillPath.close()

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0x355050F0), Color(0x025050F0)),
                        startY = topMargin,
                        endY = bottomY
                    )
                )

                // Draw curve line
                val linePath = Path()
                linePath.moveTo(points.first().x, points.first().y)
                for (i in 0 until points.size - 1) {
                    val p0 = points[i]
                    val p1 = points[i + 1]
                    val controlX1 = p0.x + (p1.x - p0.x) / 2f
                    val controlY1 = p0.y
                    val controlX2 = p0.x + (p1.x - p0.x) / 2f
                    val controlY2 = p1.y
                    linePath.cubicTo(controlX1, controlY1, controlX2, controlY2, p1.x, p1.y)
                }

                drawPath(
                    path = linePath,
                    color = Color(0xFF5050F0),
                    style = Stroke(width = 2.8.dp.toPx())
                )

                // Draw dots at data points
                points.forEachIndexed { idx, point ->
                    val pt = history.pricePoints[idx]
                    when {
                        pt.isHighest -> {
                            // Highest point: coral halo & dot
                            drawCircle(
                                color = Color(0x33C8422A),
                                radius = 7.dp.toPx(),
                                center = point
                            )
                            drawCircle(
                                color = Color(0xFFC8422A),
                                radius = 4.5.dp.toPx(),
                                center = point
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 2.dp.toPx(),
                                center = point
                            )
                        }
                        pt.isLowest -> {
                            // Lowest point: emerald halo & dot
                            drawCircle(
                                color = Color(0x331A8068),
                                radius = 7.dp.toPx(),
                                center = point
                            )
                            drawCircle(
                                color = Color(0xFF1A8068),
                                radius = 4.5.dp.toPx(),
                                center = point
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 2.dp.toPx(),
                                center = point
                            )
                        }
                        idx == points.size - 1 -> {
                            // Current point today: active blue halo & dot
                            drawCircle(
                                color = Color(0x335050F0),
                                radius = 6.dp.toPx(),
                                center = point
                            )
                            drawCircle(
                                color = Color(0xFF5050F0),
                                radius = 4.dp.toPx(),
                                center = point
                            )
                        }
                        else -> {
                            drawCircle(
                                color = Color(0xFF8B92AB),
                                radius = 2.5.dp.toPx(),
                                center = point
                            )
                        }
                    }
                }
            }
        }

        // Overlay Axis Text Labels matching exactly the Highest Sold, Mid, and Lowest Sold prices
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(66.dp)
                .padding(bottom = 22.dp, top = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = history.highestPriceFormatted,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFC8422A)
            )
            Text(
                text = ProductCatalog.formatIndianRupees(midPrice),
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF9AA4B8)
            )
            Text(
                text = history.lowestPriceFormatted,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A8068)
            )
        }

        // Real-time dynamic date labels along X-axis
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(start = 68.dp, end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val p0 = history.pricePoints.getOrNull(0)?.label ?: "30d ago"
            val p2 = history.pricePoints.getOrNull(2)?.label ?: "18d ago"
            val p4 = history.pricePoints.getOrNull(4)?.label ?: "8d ago"
            val pLast = history.pricePoints.lastOrNull()?.label ?: "Today"

            Text(p0, fontSize = 10.sp, color = Color(0xFF9AA4B8), fontWeight = FontWeight.Medium)
            Text(p2, fontSize = 10.sp, color = Color(0xFF9AA4B8), fontWeight = FontWeight.Medium)
            Text(p4, fontSize = 10.sp, color = Color(0xFF9AA4B8), fontWeight = FontWeight.Medium)
            Text(pLast, fontSize = 10.sp, color = Color(0xFF5050F0), fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PriceTrackingScreenPreview() {
    PriceTrackingScreen()
}
