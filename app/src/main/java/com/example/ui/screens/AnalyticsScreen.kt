package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.DailyCombinedRow
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
fun AnalyticsScreen(
    dailyRows: List<DailyCombinedRow>,
    modifier: Modifier = Modifier
) {
    val reversedRows = dailyRows.take(7).reversed()

    val adsterraTotal = dailyRows.sumOf { it.adsterraRevenue }
    val monetagTotal = dailyRows.sumOf { it.monetagRevenue }
    val grandTotal = (adsterraTotal + monetagTotal).coerceAtLeast(1.0)

    val adsterraShare = ((adsterraTotal / grandTotal) * 100.0).toFloat()
    val monetagShare = ((monetagTotal / grandTotal) * 100.0).toFloat()

    val adsterraColor = Color(0xFFF97316) // Orange
    val monetagColor = Color(0xFF06B6D4)  // Cyan

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("analytics_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "NETWORK ANALYTICS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Adsterra vs Monetag Yield",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }

        // Dual Line Chart
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth().testTag("chart_revenue_trend")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Revenue Trends",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        // Legend
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(modifier = Modifier.size(8.dp).background(adsterraColor, CircleShape))
                                Text("Adsterra", fontSize = 11.sp, color = adsterraColor, fontWeight = FontWeight.Bold)
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(modifier = Modifier.size(8.dp).background(monetagColor, CircleShape))
                                Text("Monetag", fontSize = 11.sp, color = monetagColor, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Line Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height - 20.dp.toPx()

                            if (reversedRows.size < 2) return@Canvas

                            val maxVal = reversedRows.maxOfOrNull { maxOf(it.adsterraRevenue, it.monetagRevenue) } ?: 100.0
                            val scale = (maxVal * 1.2).coerceAtLeast(1.0)

                            // Background grid lines
                            for (i in 0..3) {
                                val y = h * (i / 3f)
                                drawLine(
                                    color = Color(0xFF1E293B),
                                    start = Offset(0f, y),
                                    end = Offset(w, y),
                                    strokeWidth = 1.dp.toPx()
                                )
                            }

                            // Points for Adsterra
                            val adPoints = reversedRows.mapIndexed { idx, item ->
                                val x = idx * (w / (reversedRows.size - 1))
                                val y = h - ((item.adsterraRevenue / scale).toFloat() * h)
                                Offset(x, y)
                            }

                            // Points for Monetag
                            val monPoints = reversedRows.mapIndexed { idx, item ->
                                val x = idx * (w / (reversedRows.size - 1))
                                val y = h - ((item.monetagRevenue / scale).toFloat() * h)
                                Offset(x, y)
                            }

                            // Draw Adsterra Path
                            val adPath = Path().apply {
                                moveTo(adPoints.first().x, adPoints.first().y)
                                for (i in 1 until adPoints.size) {
                                    lineTo(adPoints[i].x, adPoints[i].y)
                                }
                            }
                            drawPath(adPath, color = adsterraColor, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
                            adPoints.forEach { pt ->
                                drawCircle(color = adsterraColor, radius = 4.dp.toPx(), center = pt)
                            }

                            // Draw Monetag Path
                            val monPath = Path().apply {
                                moveTo(monPoints.first().x, monPoints.first().y)
                                for (i in 1 until monPoints.size) {
                                    lineTo(monPoints[i].x, monPoints[i].y)
                                }
                            }
                            drawPath(monPath, color = monetagColor, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
                            monPoints.forEach { pt ->
                                drawCircle(color = monetagColor, radius = 4.dp.toPx(), center = pt)
                            }
                        }
                    }

                    // Dates axis
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        reversedRows.forEach { item ->
                            Text(
                                text = item.date.takeLast(5),
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }

        // Comparative Bar Chart: Adsterra vs Monetag
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth().testTag("chart_comparison_bars")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Daily Comparative Yield",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        reversedRows.forEach { row ->
                            val rowTotal = (row.adsterraRevenue + row.monetagRevenue).coerceAtLeast(1.0)
                            val adWidthFrac = (row.adsterraRevenue / rowTotal).toFloat()
                            val monWidthFrac = (row.monetagRevenue / rowTotal).toFloat()

                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(row.date, fontSize = 11.sp, color = TextSecondary)
                                    Text("$${String.format(Locale.US, "%.2f", row.totalRevenue)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                ) {
                                    Box(modifier = Modifier.weight(adWidthFrac.coerceAtLeast(0.05f)).fillMaxSize().background(adsterraColor))
                                    Box(modifier = Modifier.weight(monWidthFrac.coerceAtLeast(0.05f)).fillMaxSize().background(monetagColor))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Market Share Bar
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth().testTag("share_summary_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Total Network Revenue Share",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                    ) {
                        Box(modifier = Modifier.weight(adsterraShare.coerceAtLeast(1f)).fillMaxSize().background(adsterraColor))
                        Box(modifier = Modifier.weight(monetagShare.coerceAtLeast(1f)).fillMaxSize().background(monetagColor))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(modifier = Modifier.size(10.dp).background(adsterraColor, CircleShape))
                            Text("Adsterra: ${String.format(Locale.US, "%.1f", adsterraShare)}% ($${String.format(Locale.US, "%,.2f", adsterraTotal)})", fontSize = 11.sp, color = TextSecondary)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(modifier = Modifier.size(10.dp).background(monetagColor, CircleShape))
                            Text("Monetag: ${String.format(Locale.US, "%.1f", monetagShare)}% ($${String.format(Locale.US, "%,.2f", monetagTotal)})", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }
            }
        }
    }
}
