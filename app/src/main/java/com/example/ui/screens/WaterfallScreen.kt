package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AdNetworkEntity
import com.example.data.model.MediationTierEntity
import com.example.ui.components.GlassCard
import com.example.ui.components.NetworkMonogram
import com.example.ui.theme.BlackBoxAmber
import com.example.ui.theme.BlackBoxCardBorder
import com.example.ui.theme.BlackBoxCyan
import com.example.ui.theme.BlackBoxEmerald
import com.example.ui.theme.BlackBoxEmeraldLight
import com.example.ui.theme.BlackBoxPurple
import com.example.ui.theme.BlackBoxRose
import com.example.ui.theme.BlackBoxSurface
import com.example.ui.theme.BlackBoxSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun WaterfallScreen(
    tiers: List<MediationTierEntity>,
    networks: List<AdNetworkEntity>,
    isOptimizing: Boolean,
    onRunOptimization: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Waterfall Tiers, 1: A/B Experiment

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("waterfall_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Column {
                Text(
                    text = "BLACKBOX ENGINE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Mediation & Waterfall Strategy",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }

        // Sub Navigation Tabs
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = BlackBoxSurface,
                contentColor = BlackBoxEmerald,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = BlackBoxEmerald
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("Waterfall Tiers", fontWeight = FontWeight.Bold)
                        }
                    },
                    selectedContentColor = BlackBoxEmerald,
                    unselectedContentColor = TextSecondary
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Science, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("A/B Simulator", fontWeight = FontWeight.Bold)
                        }
                    },
                    selectedContentColor = BlackBoxEmerald,
                    unselectedContentColor = TextSecondary
                )
            }
        }

        if (selectedTab == 0) {
            // Auto-Optimization Banner Card
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = BlackBoxEmerald.copy(alpha = 0.5f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(BlackBoxEmerald.copy(alpha = 0.2f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ElectricBolt,
                                        contentDescription = null,
                                        tint = BlackBoxEmeraldLight,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "BlackBox Auto-Tuning",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "AI-Driven Dynamic Floor Allocation",
                                        fontSize = 11.sp,
                                        color = BlackBoxEmeraldLight
                                    )
                                }
                            }

                            Button(
                                onClick = onRunOptimization,
                                enabled = !isOptimizing,
                                colors = ButtonDefaults.buttonColors(containerColor = BlackBoxEmerald),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("btn_auto_optimize_waterfall")
                            ) {
                                if (isOptimizing) {
                                    CircularProgressIndicator(
                                        strokeWidth = 2.dp,
                                        color = Color(0xFF070B12),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Tuning...", color = Color(0xFF070B12), fontWeight = FontWeight.Bold)
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color(0xFF070B12),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Re-Order", color = Color(0xFF070B12), fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "BlackBox calculates real-time clearing prices across unified bidding auctions and waterfall floors, dynamically ranking exchanges by historical 7-day eCPM to maximize ARPDAU.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            // Waterfall Tiers Visual List
            items(tiers, key = { it.id }) { tier ->
                WaterfallTierCard(
                    tier = tier,
                    networks = networks
                )
            }
        } else {
            // A/B Experiment Simulator
            item {
                ExperimentSimulatorCard()
            }
        }
    }
}

@Composable
private fun WaterfallTierCard(
    tier: MediationTierEntity,
    networks: List<AdNetworkEntity>
) {
    val tierNetworks = remember(tier.networkCodes, networks) {
        val codes = tier.networkCodes.split(",").map { it.trim().lowercase(Locale.US) }
        networks.filter { it.code.lowercase(Locale.US) in codes }
    }

    val tierColor = when (tier.orderIndex) {
        0 -> BlackBoxCyan
        1 -> BlackBoxEmerald
        2 -> BlackBoxAmber
        3 -> BlackBoxPurple
        else -> TextMuted
    }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("tier_card_${tier.orderIndex}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(tierColor.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${tier.orderIndex}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = tierColor
                        )
                    }
                    Text(
                        text = tier.tierName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BlackBoxSurfaceVariant,
                    border = BorderStroke(1.dp, tierColor.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = if (tier.floorPrice > 0.0) "$${String.format(Locale.US, "%.2f", tier.floorPrice)} Floor" else "Unified Bid",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = tierColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Assigned Networks chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (tierNetworks.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = BlackBoxSurfaceVariant
                    ) {
                        Text(
                            text = "House Ads (100% Backfill)",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else {
                    tierNetworks.forEach { net ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(net.colorHex).copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, Color(net.colorHex).copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(Color(net.colorHex), CircleShape)
                                )
                                Text(
                                    text = net.networkName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Estimated fill rate bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Traffic Share", fontSize = 11.sp, color = TextMuted)
                    Text("${String.format(Locale.US, "%.1f", tier.estimatedFill)}% requests resolved", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { (tier.estimatedFill / 100.0).toFloat().coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = tierColor,
                    trackColor = BlackBoxSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ExperimentSimulatorCard() {
    var isStrategyBActive by remember { mutableStateOf(true) }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("experiment_simulator_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "A/B MEDIATION EXPERIMENT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Unified Bidding vs Traditional Waterfall",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BlackBoxEmerald.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "Live 50/50 Split",
                        fontSize = 11.sp,
                        color = BlackBoxEmeraldLight,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Side by Side comparison cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Group A
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BlackBoxSurfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, if (!isStrategyBActive) BlackBoxCyan else BlackBoxCardBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { isStrategyBActive = false }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("CONTROL (A)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Text("Static Waterfall", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("eCPM", fontSize = 10.sp, color = TextMuted)
                        Text("$14.20", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Fill Rate", fontSize = 10.sp, color = TextMuted)
                        Text("91.4%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Avg Latency", fontSize = 10.sp, color = TextMuted)
                        Text("185ms", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BlackBoxRose)
                    }
                }

                // Group B
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BlackBoxSurfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, if (isStrategyBActive) BlackBoxEmerald else BlackBoxCardBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { isStrategyBActive = true }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("TEST (B)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BlackBoxEmeraldLight)
                            Text("+18.6% YIELD", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = BlackBoxEmerald)
                        }
                        Text("BlackBox Hybrid", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("eCPM", fontSize = 10.sp, color = TextMuted)
                        Text("$18.42", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BlackBoxEmeraldLight)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Fill Rate", fontSize = 10.sp, color = TextMuted)
                        Text("98.8%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BlackBoxEmeraldLight)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Avg Latency", fontSize = 10.sp, color = TextMuted)
                        Text("48ms", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BlackBoxCyan)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = BlackBoxEmerald.copy(alpha = 0.12f)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.TrendingUp, contentDescription = null, tint = BlackBoxEmeraldLight, modifier = Modifier.size(18.dp))
                    Text(
                        text = "Variant B (Hybrid Bidding) won with 99% statistical confidence (+ $1,420 estimated monthly boost).",
                        fontSize = 11.sp,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}
