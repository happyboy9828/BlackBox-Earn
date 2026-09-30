package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AlertType
import com.example.ui.components.GlassCard
import com.example.ui.components.MetricCard
import com.example.ui.components.NetworkMonogram
import com.example.ui.components.RevenueSplineChart
import com.example.ui.theme.BlackBoxAmber
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
import com.example.ui.viewmodel.DashboardUiState
import com.example.ui.viewmodel.TimeframeFilter
import java.util.Locale

@Composable
fun DashboardScreen(
    uiState: DashboardUiState,
    onTimeframeChange: (TimeframeFilter) -> Unit,
    onSyncTelemetry: () -> Unit,
    onOptimizeWaterfall: () -> Unit,
    onNavigateToNetworks: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card with Cyber Banner
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.blackbox_hero_banner),
                        contentDescription = "BlackBox Telemetry Grid",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF070B12).copy(alpha = 0.4f),
                                        Color(0xFF070B12).copy(alpha = 0.92f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
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
                                        .size(8.dp)
                                        .background(BlackBoxEmerald, CircleShape)
                                )
                                Text(
                                    text = "BLACKBOX REAL-TIME MEDIATION",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = BlackBoxEmeraldLight
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = BlackBoxEmerald.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "${uiState.activeNetworksCount} Active Exchanges",
                                    fontSize = 11.sp,
                                    color = BlackBoxEmeraldLight,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(
                                    text = "Estimated Yield",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "$${String.format(Locale.US, "%,.2f", uiState.totalRevenue)}",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = onOptimizeWaterfall,
                                    colors = ButtonDefaults.buttonColors(containerColor = BlackBoxEmerald),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("action_optimize_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ElectricBolt,
                                        contentDescription = null,
                                        tint = Color(0xFF070B12),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Auto-Tune", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF070B12))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Notification Banner (if any)
        item {
            AnimatedVisibility(
                visible = uiState.optimizationNotification != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                uiState.optimizationNotification?.let { msg ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF064E3B).copy(alpha = 0.8f)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = BlackBoxEmeraldLight,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = msg,
                                fontSize = 12.sp,
                                color = TextPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Timeframe Selector & Sync Action
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(TimeframeFilter.values()) { filter ->
                        val isSelected = uiState.selectedTimeframe == filter
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) BlackBoxEmerald else BlackBoxSurfaceVariant,
                            modifier = Modifier
                                .clickable { onTimeframeChange(filter) }
                                .testTag("timeframe_${filter.name.lowercase(Locale.US)}")
                        ) {
                            Text(
                                text = filter.label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color(0xFF070B12) else TextSecondary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = onSyncTelemetry,
                    modifier = Modifier
                        .size(36.dp)
                        .background(BlackBoxSurfaceVariant, CircleShape)
                        .testTag("sync_telemetry_btn")
                ) {
                    if (uiState.isSyncing) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            color = BlackBoxEmerald,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Sync Telemetry",
                            tint = BlackBoxEmeraldLight,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // 2x2 Primary KPI Metric Cards
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Avg eCPM",
                        value = "$${String.format(Locale.US, "%.2f", uiState.avgEcpm)}",
                        subValue = "+8.4% vs 7d avg",
                        isPositiveTrend = true,
                        icon = Icons.Default.AutoGraph,
                        iconTint = BlackBoxEmerald,
                        modifier = Modifier.weight(1f),
                        testTag = "kpi_ecpm"
                    )
                    MetricCard(
                        title = "Impressions",
                        value = if (uiState.totalImpressions >= 1000) "${String.format(Locale.US, "%.1f", uiState.totalImpressions / 1000.0)}K" else "${uiState.totalImpressions}",
                        subValue = "+14.2% traffic",
                        isPositiveTrend = true,
                        icon = Icons.Default.Visibility,
                        iconTint = BlackBoxCyan,
                        modifier = Modifier.weight(1f),
                        testTag = "kpi_impressions"
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Fill Rate",
                        value = "${String.format(Locale.US, "%.1f", uiState.avgFillRate)}%",
                        subValue = "98.9% target",
                        isPositiveTrend = uiState.avgFillRate >= 95.0,
                        icon = Icons.Default.Percent,
                        iconTint = BlackBoxAmber,
                        modifier = Modifier.weight(1f),
                        testTag = "kpi_fill_rate"
                    )
                    MetricCard(
                        title = "Network Health",
                        value = "${uiState.activeNetworksCount}/${uiState.networks.size}",
                        subValue = "All pings <95ms",
                        isPositiveTrend = true,
                        icon = Icons.Default.Speed,
                        iconTint = BlackBoxPurple,
                        modifier = Modifier.weight(1f),
                        testTag = "kpi_health"
                    )
                }
            }
        }

        // Interactive Revenue Spline Chart
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("chart_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "REVENUE TRAJECTORY",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "7-Day eCPM & Traffic Arc",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        }

                        IconButton(
                            onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "BlackBox Earn Telemetry Snapshot:\nRevenue: $${String.format(Locale.US, "%,.2f", uiState.totalRevenue)}\neCPM: $${String.format(Locale.US, "%.2f", uiState.avgEcpm)}\nFill Rate: ${String.format(Locale.US, "%.1f", uiState.avgFillRate)}%"
                                    )
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Share Telemetry Report")
                                context.startActivity(shareIntent)
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    RevenueSplineChart(
                        metrics = uiState.dailyMetrics,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Revenue Share by Network Breakdown
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("network_share_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "EXCHANGE REVENUE SHARE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Distribution by Ad Source",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        }

                        Text(
                            text = "Manage",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BlackBoxCyan,
                            modifier = Modifier.clickable { onNavigateToNetworks() }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Multi-segment horizontal progress bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                    ) {
                        uiState.networkShares.forEach { share ->
                            Box(
                                modifier = Modifier
                                    .weight(share.sharePercent.coerceAtLeast(1f))
                                    .background(Color(share.colorHex))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Network share details list
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        uiState.networkShares.take(5).forEach { share ->
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
                                            .size(10.dp)
                                            .background(Color(share.colorHex), CircleShape)
                                    )
                                    Text(
                                        text = share.networkName,
                                        fontSize = 13.sp,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "$${String.format(Locale.US, "%,.2f", share.revenue)}",
                                        fontSize = 13.sp,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${String.format(Locale.US, "%.1f", share.sharePercent)}%",
                                        fontSize = 12.sp,
                                        color = TextMuted,
                                        modifier = Modifier.width(42.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Live Telemetry & Alerts Feed
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("telemetry_alerts_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = BlackBoxAmber,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "LIVE TELEMETRY ALERTS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            text = "Auto-Resolved",
                            fontSize = 11.sp,
                            color = BlackBoxEmeraldLight
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        uiState.alerts.forEach { alert ->
                            val tint = when (alert.type) {
                                AlertType.SURGE -> BlackBoxEmeraldLight
                                AlertType.OPTIMIZATION -> BlackBoxCyan
                                AlertType.LATENCY -> BlackBoxPurple
                                AlertType.THRESHOLD -> BlackBoxAmber
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = BlackBoxSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(tint, CircleShape)
                                            .padding(top = 4.dp)
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = alert.title,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = alert.timeAgo,
                                                fontSize = 10.sp,
                                                color = TextMuted
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = alert.description,
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
