package com.simats.smartcart.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAFBFF)) // Very light blueish white background
    ) {
        // Soft background glow
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(300.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x155050F0), // Very light transparent blue
                            Color.Transparent
                        )
                    )
                )
        )
        
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            
            // Logo
            LogoBox()
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "SmartCart AI",
                color = Color(0xFF1E2135), // Dark navy
                fontSize = 28.sp,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Shop Smarter. Buy Better.",
                color = Color(0xFF7A8398), // Gray text
                fontSize = 16.sp
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Bottom Indicator and Text
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Active dot
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF5050F0))
                )
                // Inactive dots
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD6DDF5))
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD6DDF5))
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "YOUR NEEDS. YOUR BEST CHOICE.",
                color = Color(0xFF9AA4B8),
                fontSize = 10.sp,
                letterSpacing = 1.2.sp,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun LogoBox() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFF5050F0)), // Bright Blue
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.ShoppingCart,
            contentDescription = "Shopping Cart",
            tint = Color.White,
            modifier = Modifier.size(48.dp)
        )
        
        // Green badge at top right of the cart
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-16).dp, y = 16.dp) // Adjust based on visual placement
                .size(24.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFF26D380)), // Green
            contentAlignment = Alignment.Center
        ) {
            // A simple sparkle/cross made of four small white squares
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Box(modifier = Modifier.size(3.dp).background(Color.Transparent))
                    Box(modifier = Modifier.size(3.dp).background(Color.White))
                    Box(modifier = Modifier.size(3.dp).background(Color.Transparent))
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Box(modifier = Modifier.size(3.dp).background(Color.White))
                    Box(modifier = Modifier.size(3.dp).background(Color.Transparent))
                    Box(modifier = Modifier.size(3.dp).background(Color.White))
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Box(modifier = Modifier.size(3.dp).background(Color.Transparent))
                    Box(modifier = Modifier.size(3.dp).background(Color.White))
                    Box(modifier = Modifier.size(3.dp).background(Color.Transparent))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen()
}
