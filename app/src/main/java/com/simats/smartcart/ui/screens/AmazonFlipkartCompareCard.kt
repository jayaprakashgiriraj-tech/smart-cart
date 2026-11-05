package com.simats.smartcart.ui.screens

import android.content.Intent
import android.net.Uri
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Store Comparison Title & Live Sync Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFEEF0FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.CompareArrows,
                            contentDescription = null,
                            tint = Color(0xFF5050F0),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Amazon vs Flipkart Price",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E2135)
                        )
                        Text(
                            text = "Live price comparison across stores",
                            fontSize = 11.sp,
                            color = Color(0xFF7A8398)
                        )
                    }
                }

                // Live Sync Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE8F7F2))
                        .border(BorderStroke(1.dp, Color(0xFFC8ECE0)), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00C853))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Live Store Sync",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A8068)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Best Deal Banner Highlight
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF3FBF7))
                    .border(BorderStroke(1.dp, Color(0xFFCCEFE2)), RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
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

                Column {
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

            Spacer(modifier = Modifier.height(16.dp))

            // Two Store Cards (Amazon & Flipkart)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Amazon Card
                StoreDetailCard(
                    storeInfo = comparison.amazon,
                    isAmazon = true,
                    onBuyClick = {
                        StoreComparisonRepository.openStoreProduct(
                            context = context,
                            url = comparison.amazon.buyUrl,
                            packageName = comparison.amazon.appPackage
                        )
                    },
                    modifier = Modifier.weight(1f)
                )

                // Flipkart Card
                StoreDetailCard(
                    storeInfo = comparison.flipkart,
                    isAmazon = false,
                    onBuyClick = {
                        StoreComparisonRepository.openStoreProduct(
                            context = context,
                            url = comparison.flipkart.buyUrl,
                            packageName = comparison.flipkart.appPackage
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StoreDetailCard(
    storeInfo: StorePriceInfo,
    isAmazon: Boolean,
    onBuyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val storeBrandColor = if (isAmazon) Color(0xFFFF9900) else Color(0xFF2874F0)
    val cardBg = if (storeInfo.isCheapest) Color(0xFFF9FFFC) else Color(0xFFFAFBFF)
    val cardBorder = if (storeInfo.isCheapest) Color(0xFF70CFAC) else Color(0xFFE4E8F5)

    Card(
        modifier = modifier,
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
                        .background(if (isAmazon) Color(0xFF131921) else Color(0xFF2874F0))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isAmazon) "Amazon" else "Flipkart",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
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
                    fontSize = 19.sp,
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

            // Verified Seller
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = if (isAmazon) Color(0xFFFF9900) else Color(0xFF2874F0),
                    modifier = Modifier.size(11.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = storeInfo.seller,
                    fontSize = 9.sp,
                    color = Color(0xFF6B7280),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

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
            Button(
                onClick = onBuyClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isAmazon) Color(0xFFFF9900) else Color(0xFF2874F0)
                ),
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isAmazon) "Buy Amazon" else "Buy Flipkart",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isAmazon) Color(0xFF111111) else Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
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
