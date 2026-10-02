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
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.CombinedBalanceSummary
import com.example.domain.model.DomainOverviewModel
import com.example.domain.model.NetworkTodaySummary
import com.example.ui.components.GlassCard
import com.example.ui.theme.BlackBoxAmber
import com.example.ui.theme.BlackBoxBg
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
fun HomeScreen(
    balance: CombinedBalanceSummary,
    domains: List<DomainOverviewModel>,
    selectedSystemFilter: String = "ALL",
    onSystemFilterChange: (String) -> Unit = {},
    isRefreshing: Boolean,
    syncMessage: String?,
    hasAdsterraKey: Boolean,
    hasMonetagKey: Boolean,
    hasVercelKey: Boolean = false,
    onRefresh: () -> Unit,
    onNavigateToVault: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDomainForDetails by remember { mutableStateOf<DomainOverviewModel?>(null) }
    var showHamburgerMenu by remember { mutableStateOf(false) }
    var selectedSystemForDetails by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top disclaimer
        item {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = BlackBoxSurfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, BlackBoxCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = BlackBoxCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Data is estimated, final after 3-4 days. Real-time multi-system sync enabled.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Vault Setup Alert (if any key is missing)
        if (!hasAdsterraKey || !hasMonetagKey || !hasVercelKey) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF59E0B).copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, BlackBoxAmber.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToVault() }
                        .testTag("vault_setup_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(BlackBoxAmber.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = BlackBoxAmber, modifier = Modifier.size(18.dp))
                            }
                            Column {
                                Text(
                                    text = "Connect All 3 Main Systems",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Add Adsterra, Monetag & Vercel API tokens in Secure Vault.",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = BlackBoxAmber, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        // Sync Notification message
        if (syncMessage != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF064E3B).copy(alpha = 0.8f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = BlackBoxEmeraldLight, modifier = Modifier.size(16.dp))
                        Text(text = syncMessage, fontSize = 12.sp, color = TextPrimary, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // 1. HAMBURGER HEADER & 3 SUB-BUTTONS (ADSTERRA, MONETAG, VERCEL)
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hamburger_header_card"),
                borderColor = if (showHamburgerMenu) BlackBoxEmerald.copy(alpha = 0.6f) else BlackBoxCardBorder
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            IconButton(
                                onClick = { showHamburgerMenu = !showHamburgerMenu },
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(
                                        if (showHamburgerMenu) BlackBoxEmerald.copy(alpha = 0.25f) else BlackBoxSurfaceVariant,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .testTag("home_hamburger_button")
                            ) {
                                Icon(
                                    imageVector = if (showHamburgerMenu) Icons.Default.Close else Icons.Default.Menu,
                                    contentDescription = "Toggle Systems Menu",
                                    tint = if (showHamburgerMenu) BlackBoxEmeraldLight else TextPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "SYSTEMS ARCHITECTURE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "3 Main Systems",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (showHamburgerMenu) BlackBoxEmerald.copy(alpha = 0.2f) else BlackBoxSurfaceVariant,
                            border = BorderStroke(1.dp, if (showHamburgerMenu) BlackBoxEmerald else BlackBoxCardBorder),
                            modifier = Modifier.clickable { showHamburgerMenu = !showHamburgerMenu }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = if (showHamburgerMenu) "Close Menu" else "Inspect (3)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (showHamburgerMenu) BlackBoxEmeraldLight else TextSecondary
                                )
                                Icon(
                                    imageVector = if (showHamburgerMenu) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = if (showHamburgerMenu) BlackBoxEmeraldLight else TextSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    // Expandable 3 Sub-buttons: Adsterra, Monetag, Vercel
                    AnimatedVisibility(visible = showHamburgerMenu) {
                        Column(
                            modifier = Modifier.padding(top = 14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "TAP A SYSTEM TO INSPECT ITS DEDICATED DETAILS:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BlackBoxEmeraldLight,
                                letterSpacing = 0.5.sp
                            )

                            // Subbutton 1: Adsterra (adsetad)
                            SystemSubButton(
                                name = "Adsterra Network",
                                tag = "adsetad",
                                brandColor = Color(0xFFF97316),
                                isActive = hasAdsterraKey,
                                statText = "Today: $${String.format(Locale.US, "%.2f", balance.adsterraToday.revenue)} • ${balance.adsterraToday.impressions} imps",
                                description = "Earnings, impressions, CPM, CTR & domain ad units",
                                onClick = { selectedSystemForDetails = "ADSTERRA" },
                                testTag = "subbutton_adsterra"
                            )

                            // Subbutton 2: Monetag (publisher)
                            SystemSubButton(
                                name = "Monetag Publisher",
                                tag = "publisher",
                                brandColor = Color(0xFF06B6D4),
                                isActive = hasMonetagKey,
                                statText = "Today: $${String.format(Locale.US, "%.2f", balance.monetagToday.revenue)} • ${balance.monetagToday.impressions} imps",
                                description = "Earnings, impressions, CPM, withdrawals & publisher stats",
                                onClick = { selectedSystemForDetails = "MONETAG" },
                                testTag = "subbutton_monetag"
                            )

                            // Subbutton 3: Vercel (traffic & bounce)
                            val totalVisitors = domains.sumOf { it.usersTraffic }
                            val avgBounce = if (domains.isNotEmpty()) domains.map { it.bounceRate }.average() else 34.2
                            SystemSubButton(
                                name = "Vercel Analytics",
                                tag = "traffic & bounce",
                                brandColor = BlackBoxPurple,
                                isActive = hasVercelKey,
                                statText = "${String.format(Locale.US, "%,d", totalVisitors)} Users • ${String.format(Locale.US, "%.1f", avgBounce)}% Avg Bounce",
                                description = "Connected domains, user traffic & bounce rate telemetry",
                                onClick = { selectedSystemForDetails = "VERCEL" },
                                testTag = "subbutton_vercel"
                            )
                        }
                    }
                }
            }
        }

        // 2. HERO CARD: Combined Balance (ONLY COMBINED STATS SHOWN ON HOME SCREEN)
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_balance_card"),
                borderColor = BlackBoxEmerald.copy(alpha = 0.4f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    BlackBoxEmerald.copy(alpha = 0.12f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
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
                                    text = "TOTAL COMBINED BALANCE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BlackBoxEmeraldLight,
                                    letterSpacing = 1.sp
                                )
                            }

                            IconButton(
                                onClick = onRefresh,
                                enabled = !isRefreshing,
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(BlackBoxSurfaceVariant, CircleShape)
                                    .testTag("btn_home_refresh")
                            ) {
                                if (isRefreshing) {
                                    CircularProgressIndicator(strokeWidth = 2.dp, color = BlackBoxEmerald, modifier = Modifier.size(16.dp))
                                } else {
                                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh", tint = BlackBoxEmeraldLight, modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Large Total Combined Balance
                        Text(
                            text = "$${String.format(Locale.US, "%,.2f", balance.totalCombinedBalance)}",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )

                        Text(
                            text = "(Adsterra Lifetime + Monetag Lifetime - Withdrawals)",
                            fontSize = 10.sp,
                            color = TextMuted
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Today & Yesterday combined split
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(BlackBoxSurfaceVariant.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Combined Today", fontSize = 11.sp, color = TextSecondary)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "$${String.format(Locale.US, "%,.2f", balance.totalToday)}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BlackBoxEmeraldLight
                                )
                            }

                            Box(modifier = Modifier.width(1.dp).height(36.dp).background(BlackBoxCardBorder))

                            Column {
                                Text("Combined Yesterday", fontSize = 11.sp, color = TextSecondary)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "$${String.format(Locale.US, "%,.2f", balance.totalYesterday)}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }

                            Box(modifier = Modifier.width(1.dp).height(36.dp).background(BlackBoxCardBorder))

                            Column(horizontalAlignment = Alignment.End) {
                                Text("Withdrawals", fontSize = 11.sp, color = TextSecondary)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "-$${String.format(Locale.US, "%,.2f", balance.totalWithdrawals)}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BlackBoxAmber
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. COMBINED METRICS GRID (ONLY COMBINED DETAILS ON HOME SCREEN)
        item {
            GlassCard(modifier = Modifier.fillMaxWidth().testTag("combined_metrics_card")) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "COMBINED PERFORMANCE METRICS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val combinedImps = balance.adsterraToday.impressions + balance.monetagToday.impressions
                    val combinedClicks = balance.adsterraToday.clicks + balance.monetagToday.clicks
                    val totalTrafficUsers = domains.sumOf { it.usersTraffic }
                    val avgBounceRate = if (domains.isNotEmpty()) domains.map { it.bounceRate }.average() else 34.2
                    val blendedCpm = if (combinedImps > 0) (balance.totalToday / combinedImps) * 1000.0 else 0.0

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CombinedMetricBox(
                            title = "Blended CPM",
                            value = "$${String.format(Locale.US, "%.2f", blendedCpm)}",
                            subtext = "Combined rate",
                            icon = Icons.Default.TrendingUp,
                            tint = BlackBoxEmeraldLight,
                            modifier = Modifier.weight(1f)
                        )
                        CombinedMetricBox(
                            title = "Combined Imps",
                            value = if (combinedImps >= 1000) "${String.format(Locale.US, "%.1f", combinedImps / 1000.0)}K" else "$combinedImps",
                            subtext = "$combinedClicks clicks",
                            icon = Icons.Default.Visibility,
                            tint = BlackBoxCyan,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CombinedMetricBox(
                            title = "Total Users / Traffic",
                            value = if (totalTrafficUsers >= 1000) "${String.format(Locale.US, "%.1f", totalTrafficUsers / 1000.0)}K" else "$totalTrafficUsers",
                            subtext = "${domains.size} live domains",
                            icon = Icons.Default.Group,
                            tint = BlackBoxPurple,
                            modifier = Modifier.weight(1f)
                        )
                        CombinedMetricBox(
                            title = "Avg Bounce Rate",
                            value = "${String.format(Locale.US, "%.1f", avgBounceRate)}%",
                            subtext = "Vercel telemetry",
                            icon = Icons.Default.Speed,
                            tint = BlackBoxAmber,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 4. CONNECTED DOMAIN ANALYTICS SECTION (Vercel + Adsterra + Monetag)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "CONNECTED DOMAINS & TRAFFIC",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Users & Bounce Rate (Vercel) + Views & Clicks (Adsterra / Monetag)",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = BlackBoxSurfaceVariant,
                        border = BorderStroke(1.dp, BlackBoxCardBorder)
                    ) {
                        Text(
                            text = "${domains.size} Domains",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BlackBoxEmeraldLight,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // System filter chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "ALL" to "All Systems (3)",
                        "ADSTERRA" to "Adsterra",
                        "MONETAG" to "Monetag",
                        "VERCEL" to "Vercel"
                    ).forEach { (code, label) ->
                        val isSelected = selectedSystemFilter == code
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSystemFilterChange(code) },
                            label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BlackBoxEmerald.copy(alpha = 0.2f),
                                selectedLabelColor = BlackBoxEmeraldLight,
                                containerColor = BlackBoxSurfaceVariant,
                                labelColor = TextSecondary
                            ),
                            border = BorderStroke(1.dp, if (isSelected) BlackBoxEmerald else BlackBoxCardBorder)
                        )
                    }
                }
            }
        }

        // List of Connected Domain Cards
        if (domains.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BlackBoxSurface,
                    border = BorderStroke(1.dp, BlackBoxCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Language, contentDescription = null, tint = TextMuted, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No domains found for selected system", color = TextSecondary, fontSize = 13.sp)
                        Text("Tap Refresh or check API keys in Secure Vault", color = TextMuted, fontSize = 11.sp)
                    }
                }
            }
        } else {
            items(domains, key = { it.domain }) { domainItem ->
                ConnectedDomainCard(
                    item = domainItem,
                    onClick = { selectedDomainForDetails = domainItem }
                )
            }
        }
    }

    // Interactive Domain Detail Dialog
    selectedDomainForDetails?.let { domain ->
        DomainDetailDialog(
            domain = domain,
            onDismiss = { selectedDomainForDetails = null }
        )
    }

    // Interactive System Detail Dialog (from Hamburger sub-buttons: Adsterra, Monetag, Vercel)
    selectedSystemForDetails?.let { system ->
        SystemDetailDialog(
            system = system,
            balance = balance,
            domains = domains,
            onDismiss = { selectedSystemForDetails = null },
            onNavigateToVault = onNavigateToVault
        )
    }
}

@Composable
fun SystemStatusChip(
    name: String,
    tag: String,
    color: Color,
    isActive: Boolean,
    subText: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = BlackBoxSurface,
        border = BorderStroke(1.dp, if (isActive) color.copy(alpha = 0.4f) else BlackBoxCardBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .background(if (isActive) color else TextMuted, CircleShape)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(subText, fontSize = 10.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
            Text(tag, fontSize = 9.sp, color = TextMuted)
        }
    }
}

@Composable
fun ConnectedDomainCard(
    item: DomainOverviewModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("domain_card_${item.domain}"),
        borderColor = BlackBoxCardBorder
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Domain name + connected systems tags
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
                            .size(28.dp)
                            .background(BlackBoxEmerald.copy(alpha = 0.15f), RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Public, contentDescription = null, tint = BlackBoxEmeraldLight, modifier = Modifier.size(16.dp))
                    }
                    Column {
                        Text(
                            text = item.domain,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Tap to view full telemetry & breakdown",
                            fontSize = 9.sp,
                            color = TextMuted
                        )
                    }
                }

                // Revenue Pill
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = BlackBoxEmerald.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, BlackBoxEmerald.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "$${String.format(Locale.US, "%,.2f", item.totalRevenue)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BlackBoxEmeraldLight,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Connected systems tags
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                item.connectedSystems.forEach { sys ->
                    val (tagColor, tagBg) = when {
                        sys.contains("ADSTERRA", ignoreCase = true) -> Color(0xFFF97316) to Color(0xFFF97316).copy(alpha = 0.12f)
                        sys.contains("MONETAG", ignoreCase = true) -> Color(0xFF06B6D4) to Color(0xFF06B6D4).copy(alpha = 0.12f)
                        else -> BlackBoxPurple to BlackBoxPurple.copy(alpha = 0.12f)
                    }
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = tagBg,
                        border = BorderStroke(0.5.dp, tagColor.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = sys.uppercase(Locale.US),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = tagColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4-Column Grid: Users Traffic | Bounce Rate | Views/Impressions | Clicks
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BlackBoxSurfaceVariant.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Vercel Users
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        Icon(Icons.Default.Group, contentDescription = null, tint = BlackBoxPurple, modifier = Modifier.size(11.dp))
                        Text("Users", fontSize = 10.sp, color = TextMuted)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (item.usersTraffic >= 1000) "${String.format(Locale.US, "%.1f", item.usersTraffic / 1000.0)}K" else "${item.usersTraffic}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                // Vercel Bounce Rate
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        Icon(Icons.Default.Speed, contentDescription = null, tint = BlackBoxAmber, modifier = Modifier.size(11.dp))
                        Text("Bounce", fontSize = 10.sp, color = TextMuted)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    val bounceColor = if (item.bounceRate < 40.0) BlackBoxEmeraldLight else BlackBoxAmber
                    Text(
                        text = "${String.format(Locale.US, "%.1f", item.bounceRate)}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = bounceColor
                    )
                }

                // Views / Impressions
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        Icon(Icons.Default.Visibility, contentDescription = null, tint = BlackBoxCyan, modifier = Modifier.size(11.dp))
                        Text("Views", fontSize = 10.sp, color = TextMuted)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    val totalViews = item.pageViews + item.totalImpressions
                    Text(
                        text = if (totalViews >= 1000) "${String.format(Locale.US, "%.1f", totalViews / 1000.0)}K" else "$totalViews",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                // Clicks
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        Icon(Icons.Default.AdsClick, contentDescription = null, tint = Color(0xFFF97316), modifier = Modifier.size(11.dp))
                        Text("Clicks", fontSize = 10.sp, color = TextMuted)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${item.totalClicks}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun DomainDetailDialog(
    domain: DomainOverviewModel,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BlackBoxSurface,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Language, contentDescription = null, tint = BlackBoxEmeraldLight, modifier = Modifier.size(22.dp))
                    Column {
                        Text(domain.domain, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Integrated Multi-System Telemetry", fontSize = 10.sp, color = TextMuted)
                    }
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary, modifier = Modifier.size(18.dp))
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Vercel Telemetry Card
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = BlackBoxPurple.copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, BlackBoxPurple.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("VERCEL TRAFFIC & USERS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BlackBoxPurple)
                            Text("Active / Healthy", fontSize = 10.sp, color = BlackBoxEmeraldLight)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Unique Users", fontSize = 10.sp, color = TextMuted)
                                Text("${String.format(Locale.US, "%,d", domain.usersTraffic)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Column {
                                Text("Page Views", fontSize = 10.sp, color = TextMuted)
                                Text("${String.format(Locale.US, "%,d", domain.pageViews)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Column {
                                Text("Bounce Rate", fontSize = 10.sp, color = TextMuted)
                                Text("${String.format(Locale.US, "%.1f", domain.bounceRate)}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BlackBoxEmeraldLight)
                            }
                        }
                    }
                }

                // Adsterra Breakdown
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF97316).copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, Color(0xFFF97316).copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("ADSTERRA EARNINGS & CPM", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF97316))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Revenue", fontSize = 10.sp, color = TextMuted)
                                Text("$${String.format(Locale.US, "%,.2f", domain.adsterraRevenue)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Column {
                                Text("Impressions", fontSize = 10.sp, color = TextMuted)
                                Text("${String.format(Locale.US, "%,d", domain.adsterraImpressions)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Column {
                                Text("Clicks", fontSize = 10.sp, color = TextMuted)
                                Text("${domain.adsterraClicks}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                        }
                    }
                }

                // Monetag Breakdown
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF06B6D4).copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, Color(0xFF06B6D4).copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("MONETAG EARNINGS & CPM", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF06B6D4))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Revenue", fontSize = 10.sp, color = TextMuted)
                                Text("$${String.format(Locale.US, "%,.2f", domain.monetagRevenue)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Column {
                                Text("Impressions", fontSize = 10.sp, color = TextMuted)
                                Text("${String.format(Locale.US, "%,d", domain.monetagImpressions)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Column {
                                Text("Clicks", fontSize = 10.sp, color = TextMuted)
                                Text("${domain.monetagClicks}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = BlackBoxEmerald)
            ) {
                Text("Close", color = BlackBoxBg, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun NetworkTodayCard(
    summary: NetworkTodaySummary,
    brandColor: Color,
    portalUrl: String,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier,
        borderColor = brandColor.copy(alpha = 0.35f)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = summary.network.uppercase(Locale.US),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = brandColor
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(brandColor, CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "$${String.format(Locale.US, "%,.2f", summary.revenue)}",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )

            Text(
                text = "Today's Est. Revenue",
                fontSize = 10.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BlackBoxSurfaceVariant.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("CPM", fontSize = 10.sp, color = TextMuted)
                    Text("$${String.format(Locale.US, "%.2f", summary.cpm)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Impressions", fontSize = 10.sp, color = TextMuted)
                    Text(if (summary.impressions >= 1000) "${String.format(Locale.US, "%.1f", summary.impressions / 1000.0)}K" else "${summary.impressions}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Clicks", fontSize = 10.sp, color = TextMuted)
                    Text("${summary.clicks}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            }
        }
    }
}

@Composable
fun SystemSubButton(
    name: String,
    tag: String,
    brandColor: Color,
    isActive: Boolean,
    statText: String,
    description: String,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = BlackBoxSurfaceVariant,
        border = BorderStroke(1.dp, if (isActive) brandColor.copy(alpha = 0.4f) else BlackBoxCardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(brandColor.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    val icon = when {
                        tag.contains("adsetad") -> Icons.Default.AdsClick
                        tag.contains("publisher") -> Icons.Default.TrendingUp
                        else -> Icons.Default.Language
                    }
                    Icon(imageVector = icon, contentDescription = null, tint = brandColor, modifier = Modifier.size(20.dp))
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (isActive) BlackBoxEmerald.copy(alpha = 0.15f) else BlackBoxSurface,
                            border = BorderStroke(0.5.dp, if (isActive) BlackBoxEmeraldLight.copy(alpha = 0.5f) else TextMuted)
                        ) {
                            Text(
                                text = if (isActive) "ACTIVE" else "NOT SET",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isActive) BlackBoxEmeraldLight else TextMuted,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Text(statText, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = brandColor)
                    Text(description, fontSize = 9.sp, color = TextMuted)
                }
            }

            Icon(Icons.Default.ChevronRight, contentDescription = "View Details", tint = brandColor, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun CombinedMetricBox(
    title: String,
    value: String,
    subtext: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = BlackBoxSurfaceVariant.copy(alpha = 0.6f),
        border = BorderStroke(1.dp, BlackBoxCardBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Medium)
                Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(14.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextPrimary)
            Text(subtext, fontSize = 9.sp, color = TextSecondary)
        }
    }
}

@Composable
fun SystemDetailDialog(
    system: String,
    balance: CombinedBalanceSummary,
    domains: List<DomainOverviewModel>,
    onDismiss: () -> Unit,
    onNavigateToVault: () -> Unit
) {
    val (title, brandColor, tag, portalUrl) = when (system) {
        "ADSTERRA" -> Quadruple("Adsterra System Details", Color(0xFFF97316), "adsetad", "beta.publishers.adsterra.com")
        "MONETAG" -> Quadruple("Monetag System Details", Color(0xFF06B6D4), "publisher", "publishers.monetag.com")
        else -> Quadruple("Vercel Analytics Details", BlackBoxPurple, "traffic & bounce", "vercel.com/analytics")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BlackBoxSurface,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(brandColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        val icon = when (system) {
                            "ADSTERRA" -> Icons.Default.AdsClick
                            "MONETAG" -> Icons.Default.TrendingUp
                            else -> Icons.Default.Language
                        }
                        Icon(imageVector = icon, contentDescription = null, tint = brandColor, modifier = Modifier.size(18.dp))
                    }
                    Column {
                        Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Official Network Breakdown ($portalUrl)", fontSize = 10.sp, color = brandColor)
                    }
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary, modifier = Modifier.size(18.dp))
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    // System KPI Overview Card
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = brandColor.copy(alpha = 0.1f),
                        border = BorderStroke(1.dp, brandColor.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            when (system) {
                                "ADSTERRA" -> {
                                    val summary = balance.adsterraToday
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column {
                                            Text("Today Revenue", fontSize = 10.sp, color = TextMuted)
                                            Text("$${String.format(Locale.US, "%,.2f", summary.revenue)}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = brandColor)
                                        }
                                        Column {
                                            Text("Today CPM", fontSize = 10.sp, color = TextMuted)
                                            Text("$${String.format(Locale.US, "%,.2f", summary.cpm)}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        }
                                        Column {
                                            Text("Impressions", fontSize = 10.sp, color = TextMuted)
                                            Text("${summary.impressions}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Lifetime Balance: $${String.format(Locale.US, "%,.2f", balance.adsterraLifetime)}", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                                        Text("Clicks: ${summary.clicks}", fontSize = 11.sp, color = TextSecondary)
                                    }
                                }
                                "MONETAG" -> {
                                    val summary = balance.monetagToday
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column {
                                            Text("Today Revenue", fontSize = 10.sp, color = TextMuted)
                                            Text("$${String.format(Locale.US, "%,.2f", summary.revenue)}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = brandColor)
                                        }
                                        Column {
                                            Text("Today CPM", fontSize = 10.sp, color = TextMuted)
                                            Text("$${String.format(Locale.US, "%,.2f", summary.cpm)}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        }
                                        Column {
                                            Text("Impressions", fontSize = 10.sp, color = TextMuted)
                                            Text("${summary.impressions}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Lifetime: $${String.format(Locale.US, "%,.2f", balance.monetagLifetime)}", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                                        Text("Withdrawals: -$${String.format(Locale.US, "%,.2f", balance.totalWithdrawals)}", fontSize = 11.sp, color = BlackBoxAmber, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                                else -> { // VERCEL
                                    val totalUsers = domains.sumOf { it.usersTraffic }
                                    val totalViews = domains.sumOf { it.pageViews }
                                    val avgBounce = if (domains.isNotEmpty()) domains.map { it.bounceRate }.average() else 34.0
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column {
                                            Text("Total Users", fontSize = 10.sp, color = TextMuted)
                                            Text("${String.format(Locale.US, "%,d", totalUsers)}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = brandColor)
                                        }
                                        Column {
                                            Text("Total Views", fontSize = 10.sp, color = TextMuted)
                                            Text("${String.format(Locale.US, "%,d", totalViews)}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        }
                                        Column {
                                            Text("Avg Bounce", fontSize = 10.sp, color = TextMuted)
                                            Text("${String.format(Locale.US, "%.1f", avgBounce)}%", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BlackBoxEmeraldLight)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Connected Projects/Domains: ${domains.size} live sites", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "PER-DOMAIN SYSTEM BREAKDOWN:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 0.5.sp
                    )
                }

                items(domains, key = { it.domain }) { d ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = BlackBoxSurfaceVariant,
                        border = BorderStroke(1.dp, BlackBoxCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(d.domain, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                when (system) {
                                    "ADSTERRA" -> Text("${d.adsterraImpressions} imps • ${d.adsterraClicks} clicks", fontSize = 10.sp, color = TextMuted)
                                    "MONETAG" -> Text("${d.monetagImpressions} imps • ${d.monetagClicks} clicks", fontSize = 10.sp, color = TextMuted)
                                    else -> Text("${String.format(Locale.US, "%.1f", d.bounceRate)}% bounce • ${d.pageViews} views", fontSize = 10.sp, color = TextMuted)
                                }
                            }

                            when (system) {
                                "ADSTERRA" -> Text("$${String.format(Locale.US, "%,.2f", d.adsterraRevenue)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = brandColor)
                                "MONETAG" -> Text("$${String.format(Locale.US, "%,.2f", d.monetagRevenue)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = brandColor)
                                else -> Text("${String.format(Locale.US, "%,d", d.usersTraffic)} users", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = brandColor)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        onDismiss()
                        onNavigateToVault()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BlackBoxSurfaceVariant),
                    border = BorderStroke(1.dp, brandColor.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = brandColor, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Manage Key", color = TextPrimary, fontSize = 12.sp)
                }
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = BlackBoxEmerald)
                ) {
                    Text("Close", color = BlackBoxBg, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    )
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

