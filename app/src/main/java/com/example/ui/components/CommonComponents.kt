package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyMetricEntity
import com.example.ui.theme.BlackBoxCardBorder
import com.example.ui.theme.BlackBoxCyan
import com.example.ui.theme.BlackBoxEmerald
import com.example.ui.theme.BlackBoxEmeraldBg
import com.example.ui.theme.BlackBoxEmeraldLight
import com.example.ui.theme.BlackBoxRose
import com.example.ui.theme.BlackBoxSurface
import com.example.ui.theme.BlackBoxSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    borderColor: Color = BlackBoxCardBorder,
    backgroundColor: Color = BlackBoxSurface,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(1.dp, borderColor)
    ) {
        content()
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subValue: String,
    isPositiveTrend: Boolean? = null,
    icon: ImageVector,
    iconTint: Color = BlackBoxEmerald,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    GlassCard(
        modifier = modifier.testTag(testTag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.uppercase(Locale.US),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(iconTint.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isPositiveTrend != null) {
                    Icon(
                        imageVector = if (isPositiveTrend) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = if (isPositiveTrend) BlackBoxEmeraldLight else BlackBoxRose,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                }
                Text(
                    text = subValue,
                    fontSize = 12.sp,
                    color = when (isPositiveTrend) {
                        true -> BlackBoxEmeraldLight
                        false -> BlackBoxRose
                        null -> TextSecondary
                    },
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun RevenueSplineChart(
    metrics: List<DailyMetricEntity>,
    modifier: Modifier = Modifier
) {
    if (metrics.isEmpty()) return

    var selectedIndex by remember { mutableStateOf<Int?>(metrics.size - 1) }

    Column(modifier = modifier) {
        // Inspection tooltip
        val activeMetric = selectedIndex?.let { if (it in metrics.indices) metrics[it] else null }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = activeMetric?.let { "${it.dayLabel} (${it.dateStr})" } ?: "Performance Trend",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Text(
                    text = activeMetric?.let { "$${String.format(Locale.US, "%,.2f", it.revenue)}" } ?: "$0.00",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BlackBoxEmeraldLight
                )
            }
            if (activeMetric != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text("eCPM", fontSize = 11.sp, color = TextMuted)
                        Text("$${String.format(Locale.US, "%.2f", activeMetric.ecpm)}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Fill Rate", fontSize = 11.sp, color = TextMuted)
                        Text("${String.format(Locale.US, "%.1f", activeMetric.fillRate)}%", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = BlackBoxCyan)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Chart canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(metrics) {
                        detectTapGestures { offset ->
                            val pointWidth = size.width / (metrics.size - 1).coerceAtLeast(1)
                            val index = ((offset.x + pointWidth / 2) / pointWidth).toInt().coerceIn(0, metrics.size - 1)
                            selectedIndex = index
                        }
                    }
            ) {
                val width = size.width
                val height = size.height - 30.dp.toPx()
                val bottomPadding = 25.dp.toPx()

                val minRev = (metrics.minOfOrNull { it.revenue } ?: 0.0) * 0.85
                val maxRev = (metrics.maxOfOrNull { it.revenue } ?: 1000.0) * 1.15
                val revRange = (maxRev - minRev).coerceAtLeast(1.0)

                // Draw background horizontal grid lines
                val gridLines = 3
                for (i in 0..gridLines) {
                    val y = height - (height * (i.toFloat() / gridLines))
                    drawLine(
                        color = Color(0xFF1E293B),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Compute points
                val points = metrics.mapIndexed { index, m ->
                    val x = if (metrics.size > 1) {
                        index * (width / (metrics.size - 1))
                    } else {
                        width / 2f
                    }
                    val normalizedY = ((m.revenue - minRev) / revRange).toFloat().coerceIn(0f, 1f)
                    val y = height - (normalizedY * height)
                    Offset(x, y)
                }

                // Build smooth spline path
                val strokePath = Path()
                val fillPath = Path()

                if (points.isNotEmpty()) {
                    strokePath.moveTo(points[0].x, points[0].y)
                    fillPath.moveTo(points[0].x, height)
                    fillPath.lineTo(points[0].x, points[0].y)

                    for (i in 0 until points.size - 1) {
                        val p0 = points[i]
                        val p1 = points[i + 1]
                        val controlX = (p0.x + p1.x) / 2
                        strokePath.cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
                        fillPath.cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
                    }

                    fillPath.lineTo(points.last().x, height)
                    fillPath.close()

                    // Draw gradient fill under curve
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                BlackBoxEmerald.copy(alpha = 0.35f),
                                BlackBoxEmerald.copy(alpha = 0.02f)
                            ),
                            startY = 0f,
                            endY = height
                        )
                    )

                    // Draw glowing stroke
                    drawPath(
                        path = strokePath,
                        color = BlackBoxEmerald,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw points
                    points.forEachIndexed { index, pt ->
                        val isSelected = index == selectedIndex
                        drawCircle(
                            color = if (isSelected) BlackBoxEmeraldLight else BlackBoxSurface,
                            radius = if (isSelected) 6.dp.toPx() else 3.5.dp.toPx(),
                            center = pt
                        )
                        drawCircle(
                            color = BlackBoxEmerald,
                            radius = if (isSelected) 6.dp.toPx() else 3.5.dp.toPx(),
                            center = pt,
                            style = Stroke(width = 2.dp.toPx())
                        )

                        // Vertical highlight line on selected point
                        if (isSelected) {
                            drawLine(
                                color = BlackBoxEmerald.copy(alpha = 0.4f),
                                start = Offset(pt.x, 0f),
                                end = Offset(pt.x, height),
                                strokeWidth = 1.5.dp.toPx()
                            )
                        }
                    }
                }
            }
        }

        // X-axis day labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            metrics.forEachIndexed { index, m ->
                val isSelected = index == selectedIndex
                Text(
                    text = m.dayLabel,
                    fontSize = 11.sp,
                    color = if (isSelected) BlackBoxEmeraldLight else TextMuted,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.clickable { selectedIndex = index }
                )
            }
        }
    }
}

@Composable
fun StatusChip(
    isActive: Boolean,
    latencyMs: Int,
    modifier: Modifier = Modifier
) {
    val statusColor = if (!isActive) TextMuted else if (latencyMs > 120) Color(0xFFF59E0B) else BlackBoxEmerald

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = statusColor.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(statusColor, CircleShape)
            )
            Text(
                text = if (!isActive) "Inactive" else "${latencyMs}ms",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = statusColor
            )
        }
    }
}

@Composable
fun FormatBadge(
    format: String,
    modifier: Modifier = Modifier
) {
    val (badgeBg, badgeBorder, badgeText) = when (format.lowercase(Locale.US)) {
        "rewarded" -> Triple(Color(0xFF8B5CF6).copy(alpha = 0.15f), Color(0xFF8B5CF6).copy(alpha = 0.4f), Color(0xFFA78BFA))
        "interstitial" -> Triple(Color(0xFF0284C7).copy(alpha = 0.15f), Color(0xFF0284C7).copy(alpha = 0.4f), Color(0xFF38BDF8))
        "app open" -> Triple(Color(0xFFF59E0B).copy(alpha = 0.15f), Color(0xFFF59E0B).copy(alpha = 0.4f), Color(0xFFFBBF24))
        "native" -> Triple(Color(0xFF10B981).copy(alpha = 0.15f), Color(0xFF10B981).copy(alpha = 0.4f), Color(0xFF34D399))
        else -> Triple(Color(0xFF64748B).copy(alpha = 0.15f), Color(0xFF64748B).copy(alpha = 0.4f), Color(0xFFCBD5E1))
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = badgeBg,
        border = BorderStroke(1.dp, badgeBorder)
    ) {
        Text(
            text = format.uppercase(Locale.US),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = badgeText,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp),
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun NetworkMonogram(
    name: String,
    colorHex: Long,
    size: Dp = 36.dp
) {
    val initials = name.split(" ")
        .mapNotNull { it.firstOrNull()?.toString() }
        .take(2)
        .joinToString("")
        .uppercase(Locale.US)

    Box(
        modifier = Modifier
            .size(size)
            .background(Color(colorHex).copy(alpha = 0.2f), RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            fontSize = (size.value * 0.38f).sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(colorHex)
        )
    }
}
