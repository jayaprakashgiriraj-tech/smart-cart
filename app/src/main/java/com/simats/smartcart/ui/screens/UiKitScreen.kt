package com.simats.smartcart.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.CompareArrows
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
fun UiKitScreen(
    onBackClick: () -> Unit = {}
) {
    var searchText by remember { mutableStateOf("") }
    var inputText by remember { mutableStateOf("") }

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
            // Header Row
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
                            tint = Color(0xFF5050F0)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "SMARTCART / UI KIT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF5050F0),
                        letterSpacing = 0.5.sp
                    )
                }

                IconButton(
                    onClick = { },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.GridView,
                        contentDescription = "Grid",
                        tint = Color(0xFF5050F0),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Foundations & actions",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1E2135)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "A quiet, trustworthy language. One clear primary action at a time.",
                fontSize = 12.sp,
                color = Color(0xFF7A8398),
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Section 1: COLOR · SEMANTIC PALETTE
            Text(
                text = "COLOR · SEMANTIC PALETTE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7A8398),
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

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
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ColorSwatch(color = Color(0xFF4F46E5), name = "Indigo", hex = "4F46E5")
                    ColorSwatch(color = Color(0xFF14B8A6), name = "Teal", hex = "14B8A6")
                    ColorSwatch(color = Color(0xFF15203B), name = "Ink", hex = "15203B")
                    ColorSwatch(color = Color(0xFFF8FAFC), name = "Canvas", hex = "F8FAFC", hasBorder = true)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section 2: INTER · TYPE & RHYTHM
            Text(
                text = "INTER · TYPE & RHYTHM",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7A8398),
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

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
                        text = "Decisions, made clearer.",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E2135)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "24 / 16 / 14 / 12 · 4 px spacing foundation",
                        fontSize = 12.sp,
                        color = Color(0xFF7A8398)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section 3: BUTTON · PRIMARY / DEFAULT
            Text(
                text = "BUTTON · PRIMARY / DEFAULT",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7A8398),
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFECEFF8))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5050F0))
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Find my matches",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section 4: BUTTON · SECONDARY / DEFAULT
            Text(
                text = "BUTTON · SECONDARY / DEFAULT",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7A8398),
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFECEFF8))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    OutlinedButton(
                        onClick = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E6F5)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF5050F0))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.CompareArrows,
                            contentDescription = null,
                            tint = Color(0xFF5050F0),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Compare products",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section 5: SEARCH · DEFAULT
            Text(
                text = "SEARCH · DEFAULT",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7A8398),
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFECEFF8))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF5050F0),
                            unfocusedBorderColor = Color(0xFFD0D7FF),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFF5050F0),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Mic,
                                contentDescription = null,
                                tint = Color(0xFF5050F0),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        placeholder = {
                            Text("Describe what you need...", color = Color(0xFF7A8398), fontSize = 13.sp)
                        },
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section 6: INPUT · TEXT + VOICE
            Text(
                text = "INPUT · TEXT + VOICE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7A8398),
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFECEFF8))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF5050F0),
                            unfocusedBorderColor = Color(0xFFD0D7FF),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFF5050F0),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Mic,
                                contentDescription = null,
                                tint = Color(0xFF5050F0),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        placeholder = {
                            Text("Ask about a trade-off...", color = Color(0xFF7A8398), fontSize = 13.sp)
                        },
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer notes
            Text(
                text = "Reusable local components · editable specimens",
                fontSize = 11.sp,
                color = Color(0xFF7A8398)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "All product, review, score and price data is illustrative.",
                fontSize = 11.sp,
                color = Color(0xFF9AA4B8)
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ColorSwatch(
    color: Color,
    name: String,
    hex: String,
    hasBorder: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.width(68.dp)
    ) {
        Box(
            modifier = Modifier
                .size(width = 68.dp, height = 38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(color)
                .then(
                    if (hasBorder) Modifier.border(1.dp, Color(0xFFE2E6F5), RoundedCornerShape(10.dp))
                    else Modifier
                )
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = name,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E2135)
        )
        Text(
            text = hex,
            fontSize = 9.sp,
            color = Color(0xFF7A8398)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UiKitScreenPreview() {
    UiKitScreen()
}
