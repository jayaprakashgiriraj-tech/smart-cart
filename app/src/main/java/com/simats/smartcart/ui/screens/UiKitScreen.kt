package com.simats.smartcart.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
fun UiKitScreen(
    onBackClick: () -> Unit = {}
) {
    var currentPage by remember { mutableIntStateOf(0) } // 0: Foundations & actions, 1: Products & intelligence, 2: Evidence & trade-offs, 3: Tracking & controls, 4: Navigation & feedback

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
                        onClick = {
                            if (currentPage > 0) currentPage -= 1 else onBackClick()
                        },
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
                    onClick = {
                        currentPage = (currentPage + 1) % 5
                    },
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

            when (currentPage) {
                0 -> FoundationsAndActionsContent(onNextClick = { currentPage = 1 })
                1 -> ProductsAndIntelligenceContent(onNextClick = { currentPage = 2 })
                2 -> EvidenceAndTradeOffsContent(onNextClick = { currentPage = 3 })
                3 -> TrackingAndControlsContent(onNextClick = { currentPage = 4 })
                4 -> NavigationAndFeedbackContent(onNextClick = { currentPage = 0 })
            }
        }
    }
}

@Composable
private fun FoundationsAndActionsContent(onNextClick: () -> Unit) {
    var searchText by remember { mutableStateOf("") }
    var inputText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
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

        Spacer(modifier = Modifier.height(16.dp))

        // Next Button
        NextButton(onClick = onNextClick)

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun ProductsAndIntelligenceContent(onNextClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Products & intelligence",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF1E2135)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Facts, fit and AI interpretation each have a distinct place.",
            fontSize = 12.sp,
            color = Color(0xFF7A8398),
            lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Section 1: PRODUCT CARD · RECOMMENDATION
        Text(
            text = "PRODUCT CARD · RECOMMENDATION",
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF5F6FA)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Headphones,
                            contentDescription = null,
                            tint = Color(0xFF2A2A2A),
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "JBL Tune 510BT",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E2135)
                            )
                            Text(
                                text = "92% match",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF5050F0)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "₹2,799",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E2135)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "★ 4.3  40 hours battery",
                            fontSize = 11.sp,
                            color = Color(0xFF7A8398)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "A clear call mic and 40-hour battery fit your class routine.",
                    fontSize = 12.sp,
                    color = Color(0xFF7A8398)
                )
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5050F0))
                    ) {
                        Text("View Details", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E6F5)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF5050F0))
                    ) {
                        Text("Compare", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 2: RATING · SCORE + DATASET
        Text(
            text = "RATING · SCORE + DATASET",
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.StarOutline,
                        contentDescription = null,
                        tint = Color(0xFFB78103),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "4.3",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E2135)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "240 sample reviews",
                        fontSize = 12.sp,
                        color = Color(0xFF7A8398)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Always label the source and sample status.",
                    fontSize = 11.sp,
                    color = Color(0xFF7A8398)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 3: AI VERDICTS · THREE DECISION STATES
        Text(
            text = "AI VERDICTS · THREE DECISION STATES",
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
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE6F5F0))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("✓ BUY", color = Color(0xFF1A8068), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFFF7E6))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("— CONSIDER", color = Color(0xFFB78103), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFFEBEB))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("✕ SKIP", color = Color(0xFFD32F2F), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "BUY: strong fit · CONSIDER: meaningful trade-offs · SKIP: poor fit for the stated needs",
                    fontSize = 11.sp,
                    color = Color(0xFF7A8398),
                    lineHeight = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 4: MATCH · EXPLAINABLE, PERSONALIZED
        Text(
            text = "MATCH · EXPLAINABLE, PERSONALIZED",
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Your match", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E2135))
                    Text("92%", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF5050F0))
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { 0.92f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFF5050F0),
                    trackColor = Color(0xFFEEF0FF)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text("For your budget, clear mic, comfort and battery.", fontSize = 11.sp, color = Color(0xFF7A8398))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 5: SCORES · DISTINCT DIMENSIONS
        Text(
            text = "SCORES · DISTINCT DIMENSIONS",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF7A8398),
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ScoreDimensionCard(score = "9.3/10", label = "Budget Match", modifier = Modifier.weight(1f))
            ScoreDimensionCard(score = "8.4/10", label = "Feature Match", modifier = Modifier.weight(1f))
            ScoreDimensionCard(score = "8.6/10", label = "Value for Money", modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Reusable local components · editable specimens", fontSize = 11.sp, color = Color(0xFF7A8398))
        Spacer(modifier = Modifier.height(2.dp))
        Text("All product, review, score and price data is illustrative.", fontSize = 11.sp, color = Color(0xFF9AA4B8))

        Spacer(modifier = Modifier.height(16.dp))

        NextButton(onClick = onNextClick)

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun EvidenceAndTradeOffsContent(onNextClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Evidence & trade-offs",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF1E2135)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Category-relevant comparison. Clear sources. No black-box verdicts.",
            fontSize = 12.sp,
            color = Color(0xFF7A8398),
            lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Section 1: COMPARISON TABLE · JBL / SOUNDCORE
        Text(
            text = "COMPARISON TABLE · JBL / SOUNDCORE",
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
            Column(modifier = Modifier.fillMaxWidth()) {
                SpecTableRow("Battery¹", "40 hours", "70 hours", isShaded = false)
                HorizontalDivider(color = Color(0xFFF0F2F8), thickness = 1.dp)
                SpecTableRow("Call mic²", "Clear indoors", "Mixed in noise", isShaded = true)
                HorizontalDivider(color = Color(0xFFF0F2F8), thickness = 1.dp)
                SpecTableRow("Weight", "160 g", "183 g", isShaded = false)
                HorizontalDivider(color = Color(0xFFF0F2F8), thickness = 1.dp)
                SpecTableRow("Multipoint", "Yes", "Yes", isShaded = true)
                HorizontalDivider(color = Color(0xFFF0F2F8), thickness = 1.dp)
                SpecTableRow("Active NC", "No", "No", isShaded = false)
                HorizontalDivider(color = Color(0xFFF0F2F8), thickness = 1.dp)
                SpecTableRow("App EQ", "No", "Yes", isShaded = true)
                HorizontalDivider(color = Color(0xFFF0F2F8), thickness = 1.dp)
                SpecTableRow("Warranty¹", "1 year", "1 year", isShaded = false)
                HorizontalDivider(color = Color(0xFFF0F2F8), thickness = 1.dp)
                SpecTableRow("Value /10²", "8.6", "8.2", isShaded = true, isPurpleValue = true)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 2: REVIEW CARD · SYNTHETIC DATASET
        Text(
            text = "REVIEW CARD · SYNTHETIC DATASET",
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFF5050F0),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "AI summary · 240 sample reviews",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5050F0)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Customers appreciate battery life and clear indoor calls, but some report ear pressure during long sessions and a weaker mic in noisy surroundings.",
                    fontSize = 12.sp,
                    color = Color(0xFF1E2135),
                    lineHeight = 17.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "AI interpretation - not verified customer evidence",
                    fontSize = 10.sp,
                    color = Color(0xFF7A8398)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 3: TRADE-OFF · CONSIDER BEFORE BUYING
        Text(
            text = "TRADE-OFF · CONSIDER BEFORE BUYING",
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Scale,
                        contentDescription = null,
                        tint = Color(0xFFB78103),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Better calls. Less battery.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB78103)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "JBL is lighter and stronger for indoor calls. Soundcore lasts 30 hours longer and adds app EQ. Neither offers ANC.",
                    fontSize = 12.sp,
                    color = Color(0xFF7A8398),
                    lineHeight = 17.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Table: battery / warranty are illustrative listed specs; call mic and value are sample AI assessments. Check product and warranty details before buying.",
            fontSize = 10.sp,
            color = Color(0xFF9AA4B8),
            lineHeight = 14.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("Reusable local components · editable specimens", fontSize = 11.sp, color = Color(0xFF7A8398))
        Spacer(modifier = Modifier.height(2.dp))
        Text("All product, review, score and price data is illustrative.", fontSize = 11.sp, color = Color(0xFF9AA4B8))

        Spacer(modifier = Modifier.height(16.dp))

        NextButton(onClick = onNextClick)

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun TrackingAndControlsContent(onNextClick: () -> Unit) {
    var budgetValue by remember { mutableFloatStateOf(3000f) }
    var audioFilterSelected by remember { mutableStateOf(true) }
    var budgetFilterSelected by remember { mutableStateOf(true) }
    var micFilterSelected by remember { mutableStateOf(false) }
    var targetPriceText by remember { mutableStateOf("₹2,499") }
    var priceDropToggleOn by remember { mutableStateOf(true) }
    var priceDropToggleOff by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Tracking & controls",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF1E2135)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Budget-aware choices, transparent history and deliberate alerts.",
            fontSize = 12.sp,
            color = Color(0xFF7A8398),
            lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Section 1: PRICE CHART · LABELED SAMPLE HISTORY
        Text(
            text = "PRICE CHART · LABELED SAMPLE HISTORY",
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
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                PriceHistoryGraph(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "7 illustrative observations · not retailer price history",
                    fontSize = 10.sp,
                    color = Color(0xFF9AA4B8)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Column {
                    Text(
                        text = "Lowest sample: ₹2,699 · 26 Sep",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A8068)
                    )
                    Text(
                        text = "Not a verified market low.",
                        fontSize = 10.sp,
                        color = Color(0xFF1A8068)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 2: FILTERS · SELECTED / DEFAULT
        Text(
            text = "FILTERS · SELECTED / DEFAULT",
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
                modifier = Modifier.fillMaxWidth().padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChipSpecimen(
                    label = "Audio",
                    isSelected = audioFilterSelected,
                    onClick = { audioFilterSelected = !audioFilterSelected }
                )
                FilterChipSpecimen(
                    label = "Under ₹3k",
                    isSelected = budgetFilterSelected,
                    hasCheck = false,
                    onClick = { budgetFilterSelected = !budgetFilterSelected }
                )
                FilterChipSpecimen(
                    label = "Clear mic",
                    isSelected = micFilterSelected,
                    hasCheck = false,
                    onClick = { micFilterSelected = !micFilterSelected }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 3: BUDGET SLIDER · ₹500–₹5,000
        Text(
            text = "BUDGET SLIDER · ₹500–₹5,000",
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
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Maximum budget", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E2135))
                    Text("₹${budgetValue.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF5050F0))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Slider(
                    value = budgetValue,
                    onValueChange = { budgetValue = it },
                    valueRange = 500f..5000f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF5050F0),
                        activeTrackColor = Color(0xFF5050F0),
                        inactiveTrackColor = Color(0xFFE5E8F5)
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("₹500", fontSize = 10.sp, color = Color(0xFF9AA4B8))
                    Text("₹5,000", fontSize = 10.sp, color = Color(0xFF9AA4B8))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 4: INPUT · TARGET PRICE
        Text(
            text = "INPUT · TARGET PRICE",
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
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF2F4FF))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Target price", fontSize = 13.sp, color = Color(0xFF7A8398))
                        Text(targetPriceText, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E2135))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 5: TOGGLES · ON / OFF
        Text(
            text = "TOGGLES · ON / OFF",
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
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Price-drop alerts", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E2135))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Switch(
                        checked = priceDropToggleOn,
                        onCheckedChange = { priceDropToggleOn = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF5050F0)
                        )
                    )
                    Switch(
                        checked = priceDropToggleOff,
                        onCheckedChange = { priceDropToggleOff = it },
                        colors = SwitchDefaults.colors(
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFE5E8F5)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Reusable local components · editable specimens", fontSize = 11.sp, color = Color(0xFF7A8398))
        Spacer(modifier = Modifier.height(2.dp))
        Text("All product, review, score and price data is illustrative.", fontSize = 11.sp, color = Color(0xFF9AA4B8))

        Spacer(modifier = Modifier.height(16.dp))

        NextButton(onClick = onNextClick)

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun NavigationAndFeedbackContent(onNextClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Navigation & feedback",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF1E2135)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Calm system states with a useful, clear next step.",
            fontSize = 12.sp,
            color = Color(0xFF7A8398),
            lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Section 1: LOADING · ANALYZING YOUR PRIORITIES
        Text(
            text = "LOADING · ANALYZING YOUR PRIORITIES",
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
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        color = Color(0xFF5050F0),
                        strokeWidth = 3.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Finding your best matches", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E2135))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Comparing fit, features and budget...", fontSize = 11.sp, color = Color(0xFF7A8398))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color(0xFFF0F2F8))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color(0xFFF0F2F8))
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 2: EMPTY · NO SAVED PRODUCTS
        Text(
            text = "EMPTY · NO SAVED PRODUCTS",
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
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Icon(
                    imageVector = Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                    tint = Color(0xFF5050F0),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "A little space for better choices",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E2135)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Save products to revisit their AI verdicts and compare them when you're ready.",
                    fontSize = 12.sp,
                    color = Color(0xFF7A8398),
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5050F0))
                ) {
                    Text("Find your first match", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 3: ERROR · RECOVERABLE
        Text(
            text = "ERROR · RECOVERABLE",
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
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Icon(
                    imageVector = Icons.Outlined.CloudOff,
                    contentDescription = null,
                    tint = Color(0xFFD32F2F),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Couldn't load your matches",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E2135)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Your preferences are safe. Check your connection and try again.",
                    fontSize = 12.sp,
                    color = Color(0xFF7A8398),
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E6F5)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF5050F0))
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = null,
                        tint = Color(0xFF5050F0),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Try again", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 4: TRUST NOTICE · DATA QUALIFICATION
        Text(
            text = "TRUST NOTICE · DATA QUALIFICATION",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF7A8398),
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F4FF)),
            border = BorderStroke(1.dp, Color(0xFFE2E6F5))
        ) {
            Box(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                Text(
                    text = "Illustrative data - not live retailer or review data. AI scores are decision aids, not guarantees.",
                    fontSize = 11.sp,
                    color = Color(0xFF5050F0),
                    lineHeight = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 5: BOTTOM NAVIGATION · FIVE DESTINATIONS
        Text(
            text = "BOTTOM NAVIGATION · FIVE DESTINATIONS",
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
            Box(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 0.dp
                ) {
                    NavigationBarItem(
                        selected = true,
                        onClick = { },
                        icon = { Icon(Icons.Outlined.Home, contentDescription = "Home") },
                        label = { Text("Home", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF5050F0),
                            selectedTextColor = Color(0xFF5050F0),
                            indicatorColor = Color(0xFFF2F4FF),
                            unselectedIconColor = Color(0xFF9AA4B8),
                            unselectedTextColor = Color(0xFF9AA4B8)
                        )
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { },
                        icon = { Icon(Icons.Outlined.Explore, contentDescription = "Discover") },
                        label = { Text("Discover", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            unselectedIconColor = Color(0xFF9AA4B8),
                            unselectedTextColor = Color(0xFF9AA4B8)
                        )
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { },
                        icon = { Icon(Icons.AutoMirrored.Outlined.CompareArrows, contentDescription = "Compare") },
                        label = { Text("Compare", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            unselectedIconColor = Color(0xFF9AA4B8),
                            unselectedTextColor = Color(0xFF9AA4B8)
                        )
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { },
                        icon = { Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Wishlist") },
                        label = { Text("Wishlist", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            unselectedIconColor = Color(0xFF9AA4B8),
                            unselectedTextColor = Color(0xFF9AA4B8)
                        )
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { },
                        icon = { Icon(Icons.Outlined.PersonOutline, contentDescription = "Profile") },
                        label = { Text("Profile", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            unselectedIconColor = Color(0xFF9AA4B8),
                            unselectedTextColor = Color(0xFF9AA4B8)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Reusable local components · editable specimens", fontSize = 11.sp, color = Color(0xFF7A8398))
        Spacer(modifier = Modifier.height(2.dp))
        Text("All product, review, score and price data is illustrative.", fontSize = 11.sp, color = Color(0xFF9AA4B8))

        Spacer(modifier = Modifier.height(16.dp))

        NextButton(onClick = onNextClick)

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun FilterChipSpecimen(
    label: String,
    isSelected: Boolean,
    hasCheck: Boolean = true,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0xFFEEF0FF) else Color.White)
            .border(
                width = 1.dp,
                color = if (isSelected) Color(0xFFD0D7FF) else Color(0xFFECEFF8),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isSelected && hasCheck) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = null,
                    tint = Color(0xFF5050F0),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color(0xFF5050F0) else Color(0xFF1E2135)
            )
        }
    }
}

@Composable
private fun NextButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(120.dp)
            .height(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF5050F0))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.AutoAwesome,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Next",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun ScoreDimensionCard(
    score: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F4FF)),
        border = BorderStroke(1.dp, Color(0xFFE2E6F5))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp)
        ) {
            Text(score, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF5050F0))
            Spacer(modifier = Modifier.height(2.dp))
            Text(label, fontSize = 10.sp, color = Color(0xFF7A8398))
        }
    }
}

@Composable
private fun SpecTableRow(
    spec: String,
    value1: String,
    value2: String,
    isShaded: Boolean = false,
    isPurpleValue: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isShaded) Color(0xFFF8FAFC) else Color.White)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(spec, fontSize = 12.sp, color = Color(0xFF7A8398), modifier = Modifier.weight(1.2f))
        Text(
            value1,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isPurpleValue) Color(0xFF5050F0) else Color(0xFF1E2135),
            modifier = Modifier.weight(1f)
        )
        Text(
            value2,
            fontSize = 12.sp,
            color = Color(0xFF1E2135),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun PriceHistoryGraph(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val leftMargin = 45.dp.toPx()
            val rightMargin = 15.dp.toPx()
            val topMargin = 10.dp.toPx()
            val bottomMargin = 20.dp.toPx()

            val graphWidth = width - leftMargin - rightMargin
            val graphHeight = height - topMargin - bottomMargin

            val prices = listOf(3200f, 3200f, 3000f, 3100f, 2900f, 2700f, 2800f)
            val minPrice = 2600f
            val maxPrice = 3200f

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

            val points = prices.mapIndexed { index, price ->
                val normX = index.toFloat() / (prices.size - 1)
                val normY = (price - minPrice) / (maxPrice - minPrice)

                val x = leftMargin + graphWidth * normX
                val y = topMargin + graphHeight * (1f - normY)
                Offset(x, y)
            }

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

            points.forEach { point ->
                drawCircle(
                    color = Color(0xFF5050F0),
                    radius = 3.5.dp.toPx(),
                    center = point
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(42.dp)
                .padding(bottom = 18.dp, top = 2.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text("₹3,200", fontSize = 9.sp, color = Color(0xFF9AA4B8))
            Text("₹2,900", fontSize = 9.sp, color = Color(0xFF9AA4B8))
            Text("₹2,600", fontSize = 9.sp, color = Color(0xFF9AA4B8))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(start = 45.dp, end = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("1 Sep", fontSize = 9.sp, color = Color(0xFF9AA4B8))
            Text("15 Sep", fontSize = 9.sp, color = Color(0xFF9AA4B8))
            Text("30 Sep", fontSize = 9.sp, color = Color(0xFF9AA4B8))
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
