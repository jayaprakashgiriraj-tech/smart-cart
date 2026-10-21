package com.simats.smartcart.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.CompareArrows
import androidx.compose.material.icons.automirrored.outlined.TrendingDown
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Headphones
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

data class OnboardingPageData(
    val category: String,
    val title: String,
    val description: String
)

val onboardingPages = listOf(
    OnboardingPageData(
        category = "MADE FOR YOUR NEEDS",
        title = "Find Products That\nMatch Your Needs",
        description = "Tell us what matters. We turn your budget, priorities\nand everyday needs into a shortlist."
    ),
    OnboardingPageData(
        category = "LESS RESEARCH. MORE CLARITY.",
        title = "Compare Products\nIntelligently",
        description = "See the differences that actually matter to you—not\njust a longer list of specifications."
    ),
    OnboardingPageData(
        category = "BUY WITH CONFIDENCE",
        title = "Shop Smarter\nBuy Better",
        description = "Get real-time price tracking, AI recommendations,\nand deal alerts for all your favorite stores."
    )
)

@Composable
fun OnboardingScreen(
    onNextClick: () -> Unit = {},
    onSkipClick: () -> Unit = {}
) {
    val pageCount = onboardingPages.size
    val pagerState = rememberPagerState(pageCount = { pageCount })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFBFF))
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.ShoppingCart,
                    contentDescription = "Logo",
                    tint = Color(0xFF5050F0),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SmartCart AI",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E2135),
                    fontSize = 16.sp
                )
            }
            TextButton(
                onClick = onSkipClick,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Skip", color = Color(0xFF7A8398), fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pager Content (Hero Graphic + Text Content)
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Hero Graphic Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.2f)
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color(0xFFF2F4FF)),
                    contentAlignment = Alignment.Center
                ) {
                    when (page) {
                        0 -> OnboardingGraphicPage1()
                        1 -> OnboardingGraphicPage2()
                        2 -> OnboardingGraphicPage3()
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Text Content
                val pageData = onboardingPages[page]
                Text(
                    text = pageData.category,
                    color = Color(0xFF5050F0),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = pageData.title,
                    color = Color(0xFF1E2135),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    lineHeight = 34.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = pageData.description,
                    color = Color(0xFF7A8398),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Page Indicators
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pageCount) { index ->
                val isSelected = pagerState.currentPage == index
                val width by animateDpAsState(
                    targetValue = if (isSelected) 24.dp else 6.dp,
                    label = "IndicatorWidth"
                )
                Box(
                    modifier = Modifier
                        .width(width)
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color(0xFF5050F0) else Color(0xFFD6DDF5))
                        .clickable {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Next / Get Started Button
        val isLastPage = pagerState.currentPage == pageCount - 1
        Button(
            onClick = {
                if (isLastPage) {
                    onNextClick()
                } else {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5050F0)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = if (isLastPage) "Get Started" else "Next",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                contentDescription = if (isLastPage) "Get Started" else "Next",
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Footer
        Text(
            text = "Shop Smarter. Buy Better.",
            color = Color(0xFF9AA4B8),
            fontSize = 11.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun OnboardingGraphicPage1() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(280.dp)
                .clip(CircleShape)
                .border(1.dp, Color(0xFFE5E8F8), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape)
                .border(1.dp, Color(0xFFE5E8F8), CircleShape)
        )

        Column(
            modifier = Modifier
                .size(150.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Headphones,
                contentDescription = "Headphones",
                modifier = Modifier.size(70.dp),
                tint = Color(0xFF2A2A2A)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "JBL Tune 510BT",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E2135)
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = 16.dp, y = 30.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1A8068))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "92% your match",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 24.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.AutoAwesome,
                contentDescription = null,
                tint = Color(0xFF5050F0),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Great calls. Under ₹3,000.",
                fontSize = 12.sp,
                color = Color(0xFF1E2135),
                fontWeight = FontWeight.Medium
            )
        }

        Text(
            text = "Illustrative recommendation",
            fontSize = 9.sp,
            color = Color(0xFFAAB2C8),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-16).dp)
        )
    }
}

@Composable
private fun OnboardingGraphicPage2() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(280.dp)
                .clip(CircleShape)
                .border(1.dp, Color(0xFFE5E8F8), CircleShape)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product 1 Card
            CardProductComparison(
                name = "JBL Tune 510BT",
                feature = "40 hours battery",
                score = "8.6",
                modifier = Modifier.weight(1f)
            )

            // Comparison Swap Icon
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF5050F0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.CompareArrows,
                    contentDescription = "Compare",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Product 2 Card
            CardProductComparison(
                name = "Soundcore H30i",
                feature = "70 hours battery",
                score = "8.2",
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            text = "Illustrative recommendation",
            fontSize = 9.sp,
            color = Color(0xFFAAB2C8),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-16).dp)
        )
    }
}

@Composable
private fun CardProductComparison(
    name: String,
    feature: String,
    score: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .padding(12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF5F6FA)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Headphones,
                contentDescription = name,
                modifier = Modifier.size(48.dp),
                tint = Color(0xFF2A2A2A)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = name,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E2135)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = feature,
            fontSize = 10.sp,
            color = Color(0xFF7A8398)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = score,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF5050F0)
            )
            Text(
                text = " / 10 value",
                fontSize = 10.sp,
                color = Color(0xFF7A8398)
            )
        }
    }
}

@Composable
private fun OnboardingGraphicPage3() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(280.dp)
                .clip(CircleShape)
                .border(1.dp, Color(0xFFE5E8F8), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape)
                .border(1.dp, Color(0xFFE5E8F8), CircleShape)
        )

        // Center Cart Card
        Column(
            modifier = Modifier
                .size(150.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.ShoppingCart,
                contentDescription = "Smart Cart",
                modifier = Modifier.size(64.dp),
                tint = Color(0xFF5050F0)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "SmartCart AI",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E2135)
            )
        }

        // Deal Badge
        Row(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = 12.dp, y = 36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1A8068))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.TrendingDown,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Saved ₹1,200",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Top AI Recommendation Badge
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 24.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.LocalOffer,
                contentDescription = null,
                tint = Color(0xFF5050F0),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Best price across 5 stores",
                fontSize = 12.sp,
                color = Color(0xFF1E2135),
                fontWeight = FontWeight.Medium
            )
        }

        Text(
            text = "Illustrative recommendation",
            fontSize = 9.sp,
            color = Color(0xFFAAB2C8),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-16).dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    OnboardingScreen()
}
