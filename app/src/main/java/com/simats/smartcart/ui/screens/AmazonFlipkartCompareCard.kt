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

private data class StatusBadgeConfig(
    val text: String,
    val dotColor: Color,
    val bgColor: Color,
    val borderColor: Color,
    val textColor: Color
)

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

        val amzLive = comparison.amazon.available &&
                (comparison.amazon.rawPrice ?: 0) > 0 &&
                !comparison.amazon.isStale &&
                (comparison.amazon.dataSource == "live_rapidapi" || comparison.amazon.dataSource == "live_amazon_paapi")

        val flpLive = comparison.flipkart.available &&
                (comparison.flipkart.rawPrice ?: 0) > 0 &&
                !comparison.flipkart.isStale &&
                comparison.flipkart.dataSource == "live_affiliate"

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

                // Authentic Dynamic Status Pill (Never wraps or squishes)
                val badge = when {
                    amzLive && flpLive -> StatusBadgeConfig("Live Sync", Color(0xFF00C853), Color(0xFFE8F7F2), Color(0xFFC8ECE0), Color(0xFF1A8068))
                    amzLive && !flpLive -> StatusBadgeConfig("Amazon Live", Color(0xFF00C853), Color(0xFFE8F7F2), Color(0xFFC8ECE0), Color(0xFF1A8068))
                    flpLive && !amzLive -> StatusBadgeConfig("Flipkart Live", Color(0xFF00C853), Color(0xFFE8F7F2), Color(0xFFC8ECE0), Color(0xFF1A8068))
                    comparison.amazon.isStale || comparison.flipkart.isStale -> StatusBadgeConfig("Outdated", Color(0xFFFFA000), Color(0xFFFFF8E7), Color(0xFFFFECB3), Color(0xFFD97706))
                    comparison.amazon.dataSource == "database_cache" || comparison.flipkart.dataSource == "database_cache" -> StatusBadgeConfig("Cached", Color(0xFF5050F0), Color(0xFFEEF0FF), Color(0xFFD8DCF8), Color(0xFF5050F0))
                    comparison.status == "offline" -> StatusBadgeConfig("Offline", Color(0xFF8A94A6), Color(0xFFF0F2FA), Color(0xFFE0E4F0), Color(0xFF5A6478))
                    comparison.status == "initial" -> StatusBadgeConfig("Checking...", Color(0xFF5050F0), Color(0xFFEEF0FF), Color(0xFFD8DCF8), Color(0xFF5050F0))
                    else -> StatusBadgeConfig("Unavailable", Color(0xFF9AA4B8), Color(0xFFF5F6FA), Color(0xFFE2E6F0), Color(0xFF7A8398))
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = badge.bgColor,
                    border = BorderStroke(1.dp, badge.borderColor)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(badge.dotColor)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = badge.text,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = badge.textColor,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Best Deal Banner (Strictly shown only when genuine live comparison verdict is available)
            val hasValidComparison = amzLive && flpLive &&
                    comparison.comparisonAvailable &&
                    comparison.isLiveComparison &&
                    !comparison.cheaperStoreNullable.isNullOrBlank() &&
                    comparison.cheaperStoreNullable != "None" &&
                    (comparison.savingsNullable ?: -1) >= 0 &&
                    (comparison.amazon.rawPrice ?: 0) > 0 &&
                    (comparison.flipkart.rawPrice ?: 0) > 0

            if (hasValidComparison) {
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
                                text = "🔥 Best Deal on ${comparison.cheaperStoreNullable} · Save ${comparison.savingsFormatted}!",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1A8068)
                            )
                            if (comparison.verdict.isNotBlank()) {
                                Text(
                                    text = comparison.verdict,
                                    fontSize = 11.sp,
                                    color = Color(0xFF3F8A74),
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            } else {
                // Neutral Informational Card when comparison is unavailable
                val infoTitle = when {
                    amzLive && !flpLive -> "Genuine Amazon Quote Available"
                    flpLive && !amzLive -> "Genuine Flipkart Quote Available"
                    comparison.amazon.available && comparison.flipkart.available -> "Store Quotes for Reference"
                    comparison.status == "offline" -> "Connection Offline"
                    comparison.status == "initial" -> "Checking Marketplace Prices..."
                    else -> "Marketplaces Currently Unavailable"
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF7F8FD),
                    border = BorderStroke(1.dp, Color(0xFFE2E6F5))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
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
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                                tint = Color(0xFF5050F0),
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = infoTitle,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E2135)
                            )
                            if (comparison.verdict.isNotBlank()) {
                                Text(
                                    text = comparison.verdict,
                                    fontSize = 11.sp,
                                    color = Color(0xFF5A6478),
                                    lineHeight = 15.sp
                                )
                            }
                        }
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
                    text = if (amzLive && flpLive && comparison.isLiveComparison) "Live Store Prices (4 Stores)" else "Store Price Quotes (4 Stores)",
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
                        val isLowestStore = hasValidComparison &&
                                store.isCheapest &&
                                store.available &&
                                store.isCheapestNullable == true &&
                                (store.rawPrice ?: 0) > 0

                        StorePriceRowItem(
                            storeInfo = store,
                            isLowest = isLowestStore,
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
                            val isLowestStore = hasValidComparison &&
                                    store.isCheapest &&
                                    store.available &&
                                    store.isCheapestNullable == true &&
                                    (store.rawPrice ?: 0) > 0

                            StoreDetailCard(
                                storeInfo = store,
                                isLowest = isLowestStore,
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

            // Authentic Quick Price Comparison Bar
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
                        val isCheapestStore = hasValidComparison &&
                                s.isCheapest &&
                                s.available &&
                                s.isCheapestNullable == true &&
                                (s.rawPrice ?: 0) > 0
                        val barPrice = if (s.available && s.rawPrice != null && s.rawPrice > 0) s.formattedPrice else "—"

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = s.shortName,
                                fontSize = 10.sp,
                                fontWeight = if (isCheapestStore) FontWeight.ExtraBold else FontWeight.Medium,
                                color = if (isCheapestStore) Color(0xFF1A8068) else Color(0xFF7A8398)
                            )
                            Text(
                                text = barPrice,
                                fontSize = 11.sp,
                                fontWeight = if (isCheapestStore) FontWeight.ExtraBold else if (s.available && (s.rawPrice ?: 0) > 0) FontWeight.Bold else FontWeight.Medium,
                                color = if (isCheapestStore) Color(0xFF1A8068) else if (s.available && (s.rawPrice ?: 0) > 0) Color(0xFF1E2135) else Color(0xFF9AA4B8)
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
    isLowest: Boolean,
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

    val isStoreAvailable = storeInfo.available && storeInfo.rawPrice != null && storeInfo.rawPrice > 0

    val isStoreLive = isStoreAvailable &&
            !storeInfo.isStale &&
            ((storeInfo.shortName == "Amazon" && (storeInfo.dataSource == "live_rapidapi" || storeInfo.dataSource == "live_amazon_paapi")) ||
             (storeInfo.shortName == "Flipkart" && storeInfo.dataSource == "live_affiliate"))

    val rowBg = if (isLowest) Color(0xFFF3FBF7) else if (!isStoreAvailable) Color(0xFFFAFAFC) else Color(0xFFFAFBFF)
    val rowBorder = if (isLowest) Color(0xFF70CFAC) else if (!isStoreAvailable) Color(0xFFE8EBF2) else Color(0xFFE5E9F5)

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = rowBg,
        border = BorderStroke(if (isLowest) 1.5.dp else 1.dp, rowBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Store Brand Pill + Status Badge
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

                    Spacer(modifier = Modifier.width(6.dp))

                    if (isLowest) {
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
                    } else if (!isStoreAvailable) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFEFF1F7))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "UNAVAILABLE",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF7A8398),
                                letterSpacing = 0.5.sp
                            )
                        }
                    } else if (storeInfo.isStale) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFFFF8E7))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "OUTDATED",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD97706),
                                letterSpacing = 0.5.sp
                            )
                        }
                    } else if (storeInfo.dataSource == "database_cache") {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFEEF0FF))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "CACHED",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF5050F0),
                                letterSpacing = 0.5.sp
                            )
                        }
                    } else if (isStoreLive) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFE8F7F2))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "LIVE",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A8068),
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                val deliveryText = if (!isStoreAvailable) {
                    if (storeInfo.shortName in listOf("Croma", "Reliance")) "Search directly on store" else "Currently unavailable on store"
                } else {
                    if (storeInfo.delivery.isNotBlank() && storeInfo.delivery != "—") storeInfo.delivery else "Delivery information unavailable"
                }

                Text(
                    text = deliveryText,
                    fontSize = 10.5.sp,
                    color = if (isStoreAvailable) Color(0xFF5A647A) else Color(0xFF8A94A6),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Middle: Price & Discount
            Column(horizontalAlignment = Alignment.End) {
                if (isStoreAvailable) {
                    Text(
                        text = storeInfo.formattedPrice,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isLowest) Color(0xFF1A8068) else Color(0xFF1E2135)
                    )
                    if (storeInfo.originalPrice > (storeInfo.rawPrice ?: 0) && storeInfo.formattedOriginalPrice != "—" && storeInfo.formattedOriginalPrice.isNotBlank()) {
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
                } else {
                    Text(
                        text = "Unavailable",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF8A94A6)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Right: Buy or Search Action Button
            val buttonLabel = if (isStoreAvailable) "Buy" else "Search"
            Button(
                onClick = onBuyClick,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (isStoreAvailable) storeBrandColor else Color(0xFF6B7280)),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = buttonLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isStoreAvailable && storeInfo.shortName == "Amazon") Color(0xFF111111) else Color.White
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                        contentDescription = null,
                        tint = if (isStoreAvailable && storeInfo.shortName == "Amazon") Color(0xFF111111) else Color.White,
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
    isLowest: Boolean,
    onBuyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isAmazon = storeInfo.shortName == "Amazon"
    val isFlipkart = storeInfo.shortName == "Flipkart"
    val isCroma = storeInfo.shortName == "Croma"

    val isStoreAvailable = storeInfo.available && storeInfo.rawPrice != null && storeInfo.rawPrice > 0

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

    val isStoreLive = isStoreAvailable &&
            !storeInfo.isStale &&
            ((storeInfo.shortName == "Amazon" && (storeInfo.dataSource == "live_rapidapi" || storeInfo.dataSource == "live_amazon_paapi")) ||
             (storeInfo.shortName == "Flipkart" && storeInfo.dataSource == "live_affiliate"))

    val cardBg = if (isLowest) Color(0xFFF9FFFC) else if (!isStoreAvailable) Color(0xFFFAFAFC) else Color(0xFFFAFBFF)
    val cardBorder = if (isLowest) Color(0xFF70CFAC) else if (!isStoreAvailable) Color(0xFFE8EBF2) else Color(0xFFE4E8F5)

    Card(
        modifier = modifier.clickable { onBuyClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(if (isLowest) 1.5.dp else 1.dp, cardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header Row: Store Logo Pill + Status Pill
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
                    if (hasApp && isStoreAvailable) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "⚡",
                            fontSize = 10.sp
                        )
                    }
                }

                if (isLowest) {
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
                } else if (!isStoreAvailable) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFEFF1F7))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "UNAVAILABLE",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7A8398),
                            letterSpacing = 0.5.sp
                        )
                    }
                } else if (storeInfo.isStale) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFFF8E7))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "OUTDATED",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD97706),
                            letterSpacing = 0.5.sp
                        )
                    }
                } else if (storeInfo.dataSource == "database_cache") {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFEEF0FF))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "CACHED",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5050F0),
                            letterSpacing = 0.5.sp
                        )
                    }
                } else if (isStoreLive) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFE8F7F2))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "LIVE",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
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
                if (isStoreAvailable) {
                    Text(
                        text = storeInfo.formattedPrice,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isLowest) Color(0xFF1A8068) else Color(0xFF1E2135)
                    )
                    if (storeInfo.originalPrice > (storeInfo.rawPrice ?: 0) && storeInfo.formattedOriginalPrice != "—" && storeInfo.formattedOriginalPrice.isNotBlank()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = storeInfo.formattedOriginalPrice,
                            fontSize = 10.sp,
                            color = Color(0xFF9AA2B5),
                            style = TextStyle(textDecoration = TextDecoration.LineThrough)
                        )
                    }
                } else {
                    Text(
                        text = "Unavailable",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8A94A6)
                    )
                }
            }

            if (isStoreAvailable && storeInfo.discountPercent.isNotBlank()) {
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
            if (isStoreAvailable && storeInfo.rating != "—" && storeInfo.rating.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFB800),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${storeInfo.rating}${if (storeInfo.ratingsCount.isNotBlank()) " (${storeInfo.ratingsCount})" else ""}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF5A647A)
                    )
                }
            } else {
                Text(
                    text = "Rating unavailable",
                    fontSize = 10.sp,
                    color = Color(0xFF9AA4B8)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Delivery Info
            val cardDelivery = if (!isStoreAvailable) {
                if (storeInfo.shortName in listOf("Croma", "Reliance")) "Search directly on store" else "Currently unavailable"
            } else {
                if (storeInfo.delivery.isNotBlank() && storeInfo.delivery != "—") storeInfo.delivery else "Delivery information unavailable"
            }

            Text(
                text = cardDelivery,
                fontSize = 10.sp,
                color = if (isStoreAvailable) Color(0xFF2C3E50) else Color(0xFF8A94A6),
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 13.sp
            )

            // Bank / Card Offer (Only show when genuine offer text exists)
            if (isStoreAvailable && storeInfo.offer.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
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
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Buy Action Button
            val buttonText = when {
                !isStoreAvailable -> "Search on ${storeInfo.shortName}"
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
                    containerColor = if (isStoreAvailable) storeBrandColor else Color(0xFF6B7280)
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
                        color = if (isStoreAvailable && isAmazon) Color(0xFF111111) else Color.White,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                        contentDescription = null,
                        tint = if (isStoreAvailable && isAmazon) Color(0xFF111111) else Color.White,
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
