package com.simats.smartcart.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.CompareArrows
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.smartcart.data.Product
import com.simats.smartcart.data.ProductStoreComparison
import com.simats.smartcart.data.StoreComparisonRepository
import com.simats.smartcart.data.StorePriceInfo

@Composable
fun AmazonFlipkartCompareCard(
    product: Product,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var comparison by remember(product) {
        mutableStateOf(StoreComparisonRepository.getStoreComparison(product))
    }

    LaunchedEffect(product) {
        comparison = StoreComparisonRepository.getLiveStoreComparison(product)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E6F5))
    ) {
        var viewMode by remember { mutableIntStateOf(0) } // 0 = List Matrix (All Stores), 1 = Side-by-Side Cards

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Store Comparison Title & Live Sync Indicator (FIXED: Never squished)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFEEF0FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.CompareArrows,
                            contentDescription = null,
                            tint = Color(0xFF5050F0),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        Text(
                            text = "Multi-Store Price Matrix",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E2135),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Amazon · Flipkart · Croma · Reliance",
                            fontSize = 11.sp,
                            color = Color(0xFF7A8398),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Live Sync Pill (Never wraps or squishes)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE8F7F2),
                    border = BorderStroke(1.dp, Color(0xFFC8ECE0))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00C853))
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Live Sync",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1A8068),
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Best Deal Banner Highlight
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFF3FBF7),
                border = BorderStroke(1.dp, Color(0xFFCCEFE2))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFD4F3E6)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LocalOffer,
                            contentDescription = null,
                            tint = Color(0xFF1A8068),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "🔥 Best Deal on ${comparison.cheaperStore} · Save ${comparison.savingsFormatted}!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1A8068)
                        )
                        Text(
                            text = comparison.verdict,
                            fontSize = 11.sp,
                            color = Color(0xFF3F8A74),
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // View Mode Toggle (All Stores List vs Cards)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Live Store Prices (4 Stores)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E2135)
                )

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF0F2FA))
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (viewMode == 0) Color.White else Color.Transparent)
                            .clickable { viewMode = 0 }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "All Stores",
                            fontSize = 10.sp,
                            fontWeight = if (viewMode == 0) FontWeight.Bold else FontWeight.Medium,
                            color = if (viewMode == 0) Color(0xFF5050F0) else Color(0xFF7A8398)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (viewMode == 1) Color.White else Color.Transparent)
                            .clickable { viewMode = 1 }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Cards",
                            fontSize = 10.sp,
                            fontWeight = if (viewMode == 1) FontWeight.Bold else FontWeight.Medium,
                            color = if (viewMode == 1) Color(0xFF5050F0) else Color(0xFF7A8398)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (viewMode == 0) {
                // BUYHATKE ALL 4 STORES MATRIX (All visible at once)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    comparison.allStores.forEach { store ->
                        StorePriceRowItem(
                            storeInfo = store,
                            onBuyClick = {
                                StoreComparisonRepository.openStoreProduct(
                                    context = context,
                                    url = store.buyUrl,
                                    packageName = store.appPackage,
                                    productName = product.name
                                )
                            }
                        )
                    }
                }
            } else {
                // 4 E-Commerce Store Cards (Scrollable Row with Swipe Hint)
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        comparison.allStores.forEach { store ->
                            StoreDetailCard(
                                storeInfo = store,
                                onBuyClick = {
                                    StoreComparisonRepository.openStoreProduct(
                                        context = context,
                                        url = store.buyUrl,
                                        packageName = store.appPackage,
                                        productName = product.name
                                    )
                                },
                                modifier = Modifier.width(165.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "👉 Swipe horizontally to view Croma & Reliance Digital",
                        fontSize = 10.sp,
                        color = Color(0xFF7A8398),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Buyhatke-style Quick Price Comparison Bar
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF7F8FD),
                border = BorderStroke(1.dp, Color(0xFFE6EBF5)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    comparison.allStores.forEach { s ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = s.shortName,
                                fontSize = 10.sp,
                                fontWeight = if (s.isCheapest) FontWeight.ExtraBold else FontWeight.Medium,
                                color = if (s.isCheapest) Color(0xFF1A8068) else Color(0xFF7A8398)
                            )
                            Text(
                                text = s.formattedPrice,
                                fontSize = 11.sp,
                                fontWeight = if (s.isCheapest) FontWeight.ExtraBold else FontWeight.Bold,
                                color = if (s.isCheapest) Color(0xFF1A8068) else Color(0xFF1E2135)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StorePriceRowItem(
    storeInfo: StorePriceInfo,
    onBuyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val storeBrandColor = when (storeInfo.shortName) {
        "Amazon" -> Color(0xFFFF9900)
        "Flipkart" -> Color(0xFF2874F0)
        "Croma" -> Color(0xFF007E7E)
        else -> Color(0xFFE42529) // Reliance Digital
    }

    val storePillBg = when (storeInfo.shortName) {
        "Amazon" -> Color(0xFF131921)
        "Flipkart" -> Color(0xFF2874F0)
        "Croma" -> Color(0xFF007E7E)
        else -> Color(0xFFE42529)
    }

    val rowBg = if (storeInfo.isCheapest) Color(0xFFF3FBF7) else Color(0xFFFAFBFF)
    val rowBorder = if (storeInfo.isCheapest) Color(0xFF70CFAC) else Color(0xFFE5E9F5)

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = rowBg,
        border = BorderStroke(if (storeInfo.isCheapest) 1.5.dp else 1.dp, rowBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Store Brand Pill + Status
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(storePillBg)
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = storeInfo.shortName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    if (storeInfo.isCheapest) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFE6F7F0))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "LOWEST",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1A8068),
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = storeInfo.delivery,
                    fontSize = 10.5.sp,
                    color = Color(0xFF5A647A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Middle: Price & Discount
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = storeInfo.formattedPrice,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (storeInfo.isCheapest) Color(0xFF1A8068) else Color(0xFF1E2135)
                )
                if (storeInfo.originalPrice > storeInfo.price) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = storeInfo.formattedOriginalPrice,
                            fontSize = 9.5.sp,
                            color = Color(0xFF9AA2B5),
                            style = TextStyle(textDecoration = TextDecoration.LineThrough)
                        )
                        if (storeInfo.discountPercent.isNotBlank()) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = storeInfo.discountPercent,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD32F2F)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Right: 1-Tap Buy Button
            Button(
                onClick = onBuyClick,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = storeBrandColor),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Buy",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (storeInfo.shortName == "Amazon") Color(0xFF111111) else Color.White
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                        contentDescription = null,
                        tint = if (storeInfo.shortName == "Amazon") Color(0xFF111111) else Color.White,
                        modifier = Modifier.size(11.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StoreDetailCard(
    storeInfo: StorePriceInfo,
    onBuyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isAmazon = storeInfo.shortName == "Amazon"
    val isFlipkart = storeInfo.shortName == "Flipkart"
    val isCroma = storeInfo.shortName == "Croma"

    val hasApp = remember(storeInfo.shortName) {
        when (storeInfo.shortName) {
            "Amazon" -> StoreComparisonRepository.getInstalledAmazonPackage(context) != null
            "Flipkart" -> StoreComparisonRepository.isFlipkartInstalled(context)
            else -> false
        }
    }

    val storeBrandColor = when (storeInfo.shortName) {
        "Amazon" -> Color(0xFFFF9900)
        "Flipkart" -> Color(0xFF2874F0)
        "Croma" -> Color(0xFF007E7E)
        else -> Color(0xFFE42529) // Reliance Digital
    }

    val storePillBg = when (storeInfo.shortName) {
        "Amazon" -> Color(0xFF131921)
        "Flipkart" -> Color(0xFF2874F0)
        "Croma" -> Color(0xFF007E7E)
        else -> Color(0xFFE42529)
    }

    val cardBg = if (storeInfo.isCheapest) Color(0xFFF9FFFC) else Color(0xFFFAFBFF)
    val cardBorder = if (storeInfo.isCheapest) Color(0xFF70CFAC) else Color(0xFFE4E8F5)

    Card(
        modifier = modifier.clickable { onBuyClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(if (storeInfo.isCheapest) 1.5.dp else 1.dp, cardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header Row: Store Logo Pill + Best Deal Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Store Brand Badge
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(storePillBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = storeInfo.shortName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    if (hasApp) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "⚡",
                            fontSize = 10.sp
                        )
                    }
                }

                if (storeInfo.isCheapest) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFE6F7F0))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "LOWEST",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1A8068),
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Store Product Title Variant
            Text(
                text = storeInfo.productTitle,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E2135),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Price & MRP
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = storeInfo.formattedPrice,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (storeInfo.isCheapest) Color(0xFF1A8068) else Color(0xFF1E2135)
                )
                if (storeInfo.originalPrice > storeInfo.price) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = storeInfo.formattedOriginalPrice,
                        fontSize = 10.sp,
                        color = Color(0xFF9AA2B5),
                        style = TextStyle(textDecoration = TextDecoration.LineThrough)
                    )
                }
            }

            if (storeInfo.discountPercent.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = storeInfo.discountPercent,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD32F2F)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Rating & Review Count
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFB800),
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "${storeInfo.rating} (${storeInfo.ratingsCount})",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF5A647A)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Delivery Info
            Text(
                text = storeInfo.delivery,
                fontSize = 10.sp,
                color = Color(0xFF2C3E50),
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 13.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Bank / Card Offer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF1F3FB))
                    .padding(horizontal = 6.dp, vertical = 5.dp)
            ) {
                Text(
                    text = storeInfo.offer,
                    fontSize = 9.sp,
                    color = Color(0xFF4C556D),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Buy Action Button
            val buttonText = when {
                isAmazon && hasApp -> "Open Amazon App"
                isAmazon -> "Buy on Amazon"
                isFlipkart && hasApp -> "Open Flipkart App"
                isFlipkart -> "Buy on Flipkart"
                else -> "Buy on ${storeInfo.shortName}"
            }

            Button(
                onClick = onBuyClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = storeBrandColor
                ),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = buttonText,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isAmazon) Color(0xFF111111) else Color.White,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                        contentDescription = null,
                        tint = if (isAmazon) Color(0xFF111111) else Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmazonFlipkartCompareSheet(
    product: Product,
    onDismiss: () -> Unit
) {
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = modalBottomSheetState,
        containerColor = Color.White,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Compare Stores",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E2135)
                    )
                    Text(
                        text = product.name,
                        fontSize = 13.sp,
                        color = Color(0xFF7A8398)
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Close",
                        tint = Color(0xFF7A8398)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            AmazonFlipkartCompareCard(product = product)

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5050F0))
            ) {
                Text(
                    text = "Close",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
