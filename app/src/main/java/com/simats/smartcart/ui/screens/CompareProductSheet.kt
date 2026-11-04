package com.simats.smartcart.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.CompareArrows
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.smartcart.data.Product
import com.simats.smartcart.data.ProductCatalog

fun getProductCategoryIcon(product: Product): ImageVector {
    return when (product.category) {
        "Mobiles" -> Icons.Outlined.Smartphone
        "Laptops" -> Icons.Outlined.Laptop
        "Audio" -> if (product.subcategory == "Speakers") Icons.Outlined.Speaker else Icons.Outlined.Headphones
        "Fashion" -> if (product.needs.contains("Watches")) Icons.Outlined.Watch else if (product.needs.contains("Accessories")) Icons.Outlined.ShoppingBag else Icons.Outlined.Checkroom
        "Home Appliances" -> if (product.subcategory == "Televisions") Icons.Outlined.Tv else Icons.Outlined.Kitchen
        else -> Icons.Outlined.AutoAwesome
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareProductSheet(
    baseProduct: Product,
    sheetTitle: String? = null,
    onSwapPositions: (() -> Unit)? = null,
    onSelectProduct: (Product) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val categoryCandidates = remember(baseProduct) {
        ProductCatalog.getComparableProducts(baseProduct)
    }

    val displayCandidates = remember(baseProduct, searchQuery, categoryCandidates) {
        if (searchQuery.isBlank()) {
            categoryCandidates
        } else {
            val q = searchQuery.trim().lowercase()
            val tokens = q.split(" ").filter { it.isNotBlank() }
            categoryCandidates.filter { product ->
                tokens.all { token ->
                    product.name.lowercase().contains(token) ||
                    product.brand.lowercase().contains(token) ||
                    product.subcategory.lowercase().contains(token) ||
                    product.specs.lowercase().contains(token) ||
                    product.bestFor.lowercase().contains(token)
                }
            }.sortedByDescending { it.matchPercentage }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = sheetTitle ?: "Compare with ${baseProduct.name}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E2135),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Choose matching ${baseProduct.getComparisonCategoryLabel()} to compare specs & prices:",
                        fontSize = 12.sp,
                        color = Color(0xFF7A8398)
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Close",
                        tint = Color(0xFF7A8398),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (onSwapPositions != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    onClick = {
                        onSwapPositions()
                        onDismiss()
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF2F4FF),
                    border = BorderStroke(1.dp, Color(0xFFD8DEF5)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.CompareArrows,
                                contentDescription = "Swap positions",
                                tint = Color(0xFF5050F0),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Swap positions of both products",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF5050F0)
                            )
                        }
                        Text(
                            text = "Swap ⇄",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5050F0)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "Search ${baseProduct.getComparisonCategoryLabel()} to compare...",
                        fontSize = 13.sp,
                        color = Color(0xFF9AA2B1)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search",
                        tint = Color(0xFF7A8398),
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { searchQuery = "" },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Clear search",
                                tint = Color(0xFF7A8398),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFFAFBFF),
                    unfocusedContainerColor = Color(0xFFFAFBFF),
                    disabledContainerColor = Color(0xFFFAFBFF),
                    focusedBorderColor = Color(0xFF5050F0),
                    unfocusedBorderColor = Color(0xFFE2E6F5),
                    focusedTextColor = Color(0xFF1E2135),
                    unfocusedTextColor = Color(0xFF1E2135)
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (displayCandidates.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.SearchOff,
                            contentDescription = null,
                            tint = Color(0xFF9AA2B1),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No matching ${baseProduct.getComparisonCategoryLabel().lowercase()} found",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF7A8398)
                        )
                    }
                }
            } else {
                // Candidate Products List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp)
                ) {
                    items(displayCandidates) { candidate ->
                        val icon = getProductCategoryIcon(candidate)

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectProduct(candidate) },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFBFF)),
                            border = BorderStroke(1.dp, Color(0xFFE2E6F5))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Icon Box
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFEEF0FF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = candidate.name,
                                        tint = Color(0xFF5050F0),
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                // Details
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = candidate.name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E2135),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${candidate.brand} · ${candidate.specs}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF7A8398),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = candidate.price,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFF5050F0)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = candidate.matchScore,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1A8068)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Compare Button
                                Button(
                                    onClick = { onSelectProduct(candidate) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5050F0)),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Text("Compare", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
