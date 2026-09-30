package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.GroupedStat
import com.example.ui.components.GlassCard
import com.example.ui.theme.BlackBoxCardBorder
import com.example.ui.theme.BlackBoxCyan
import com.example.ui.theme.BlackBoxEmerald
import com.example.ui.theme.BlackBoxEmeraldLight
import com.example.ui.theme.BlackBoxSurface
import com.example.ui.theme.BlackBoxSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun DailyScreen(
    groupedStats: List<GroupedStat>,
    selectedFilter: String,
    selectedGroupBy: String,
    onFilterChange: (String) -> Unit,
    onGroupByChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val filters = listOf("7D", "30D", "90D")
    val groupOptions = listOf("Date", "Domain", "Country")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("daily_breakdown_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "REVENUE BREAKDOWN",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Daily & Segmented Logs",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }

        // Filter Controls Row
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Timeframe Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Timeframe", fontSize = 11.sp, color = TextMuted)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(filters) { f ->
                            val isSelected = selectedFilter == f
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) BlackBoxEmerald else BlackBoxSurfaceVariant,
                                modifier = Modifier.clickable { onFilterChange(f) }.testTag("filter_$f")
                            ) {
                                Text(
                                    text = f,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color(0xFF070B12) else TextSecondary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }

                // Group By Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Group By", fontSize = 11.sp, color = TextMuted)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(groupOptions) { g ->
                            val isSelected = selectedGroupBy == g
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) BlackBoxCyan.copy(alpha = 0.2f) else BlackBoxSurfaceVariant,
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, BlackBoxCyan) else null,
                                modifier = Modifier.clickable { onGroupByChange(g) }.testTag("group_$g")
                            ) {
                                Text(
                                    text = g,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) BlackBoxCyan else TextSecondary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Table Header
        item {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = BlackBoxSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(selectedGroupBy.uppercase(Locale.US), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary, modifier = Modifier.weight(1.3f))
                    Text("ADSTERRA", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF97316), modifier = Modifier.weight(1f))
                    Text("MONETAG", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF06B6D4), modifier = Modifier.weight(1f))
                    Text("TOTAL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BlackBoxEmeraldLight, modifier = Modifier.weight(1f))
                }
            }
        }

        // Data Rows
        items(groupedStats) { row ->
            GlassCard(
                modifier = Modifier.fillMaxWidth().testTag("row_${row.groupKey}")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = row.groupKey,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.weight(1.3f)
                        )
                        Text(
                            text = "$${String.format(Locale.US, "%.2f", row.adsterraRevenue)}",
                            fontSize = 12.sp,
                            color = Color(0xFFF97316),
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "$${String.format(Locale.US, "%.2f", row.monetagRevenue)}",
                            fontSize = 12.sp,
                            color = Color(0xFF06B6D4),
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "$${String.format(Locale.US, "%.2f", row.totalRevenue)}",
                            fontSize = 13.sp,
                            color = BlackBoxEmeraldLight,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Impressions: ${if (row.impressions >= 1000) "${String.format(Locale.US, "%.1f", row.impressions / 1000.0)}K" else "${row.impressions}"}",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                        Text(
                            text = "Blended CPM: $${String.format(Locale.US, "%.2f", row.cpm)}",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        }
    }
}
