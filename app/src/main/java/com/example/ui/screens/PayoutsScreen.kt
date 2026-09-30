package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PayoutEntity
import com.example.ui.components.GlassCard
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
fun PayoutsScreen(
    payouts: List<PayoutEntity>,
    totalRevenue: Double,
    onRequestPayout: (Double, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showRequestDialog by remember { mutableStateOf(false) }

    val pendingBalance = totalRevenue * 1.05
    val payoutThreshold = 1000.0
    val progressToThreshold = (pendingBalance / payoutThreshold).toFloat().coerceIn(0f, 1f)

    Box(modifier = modifier.fillMaxSize().testTag("payouts_screen")) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "EARNINGS & SETTLEMENTS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Consolidated Payouts",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Button(
                        onClick = { showRequestDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = BlackBoxEmerald),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("btn_request_payout")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Payment,
                            contentDescription = null,
                            tint = Color(0xFF070B12),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Withdraw", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF070B12))
                    }
                }
            }

            // Pending Balance & Threshold Card
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text(
                                    text = "Ready for Settlement",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$${String.format(Locale.US, "%,.2f", pendingBalance)}",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = BlackBoxEmeraldLight
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = BlackBoxEmerald.copy(alpha = 0.15f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = BlackBoxEmeraldLight,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "Threshold Met",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BlackBoxEmeraldLight
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Threshold Progress
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Payment Threshold ($1,000)", fontSize = 11.sp, color = TextMuted)
                                Text("Next payout: Oct 15, 2026", fontSize = 11.sp, color = BlackBoxCyan, fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { progressToThreshold },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = BlackBoxEmerald,
                                trackColor = BlackBoxSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Quick info row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(BlackBoxSurfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Default Method: Wire (Chase Bank)", fontSize = 11.sp, color = TextSecondary)
                            Text(
                                text = "Export Statement",
                                fontSize = 11.sp,
                                color = BlackBoxCyan,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "BlackBox Earn Statement:\nPending Balance: $${String.format(Locale.US, "%,.2f", pendingBalance)}\nTotal Paid Out: $182,290.00\nStatus: Verified Publisher"
                                        )
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Export Statement"))
                                }
                            )
                        }
                    }
                }
            }

            // Historical Invoices Section
            item {
                Text(
                    text = "SETTLEMENT HISTORY (${payouts.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
            }

            items(payouts, key = { it.id }) { payout ->
                PayoutItemCard(payout = payout)
            }
        }
    }

    if (showRequestDialog) {
        RequestPayoutDialog(
            defaultAmount = pendingBalance,
            onDismiss = { showRequestDialog = false },
            onConfirm = { amount, method, account ->
                onRequestPayout(amount, method, account)
                showRequestDialog = false
            }
        )
    }
}

@Composable
private fun PayoutItemCard(payout: PayoutEntity) {
    val (statusColor, statusIcon) = when (payout.status.lowercase(Locale.US)) {
        "completed" -> Pair(BlackBoxEmeraldLight, Icons.Default.CheckCircle)
        "scheduled" -> Pair(BlackBoxCyan, Icons.Default.DateRange)
        else -> Pair(BlackBoxAmber, Icons.Default.Schedule)
    }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("payout_card_${payout.payoutId}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(statusColor.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = statusIcon,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = payout.payoutId,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${payout.method} • ${payout.destinationAccount}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = payout.date,
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${String.format(Locale.US, "%,.2f", payout.amount)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = payout.status.uppercase(Locale.US),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RequestPayoutDialog(
    defaultAmount: Double,
    onDismiss: () -> Unit,
    onConfirm: (Double, String, String) -> Unit
) {
    var amountStr by remember { mutableStateOf(String.format(Locale.US, "%.2f", defaultAmount)) }
    var selectedMethod by remember { mutableStateOf("Bank Wire (SWIFT)") }
    var destinationAccount by remember { mutableStateOf("Chase Tech Holdings •••• 9104") }

    val methods = listOf("Bank Wire (SWIFT)", "USDT (TRC20)", "PayPal", "Stripe Connect")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BlackBoxSurface,
        title = {
            Text(
                text = "Request Settlement",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Amount ($ USD)", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth().testTag("dialog_payout_amount"),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = BlackBoxEmerald,
                        unfocusedBorderColor = BlackBoxCardBorder
                    )
                )

                Text("Settlement Method", fontSize = 11.sp, color = TextSecondary)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    methods.forEach { method ->
                        val isSelected = selectedMethod == method
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) BlackBoxEmerald.copy(alpha = 0.15f) else BlackBoxSurfaceVariant,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, BlackBoxEmerald) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedMethod = method }
                                .padding(vertical = 8.dp, horizontal = 10.dp)
                        ) {
                            Text(
                                text = method,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) BlackBoxEmeraldLight else TextPrimary
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = destinationAccount,
                    onValueChange = { destinationAccount = it },
                    label = { Text("Destination / Wallet Account", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth().testTag("dialog_payout_account"),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = BlackBoxEmerald,
                        unfocusedBorderColor = BlackBoxCardBorder
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountStr.toDoubleOrNull() ?: defaultAmount
                    onConfirm(amount, selectedMethod, destinationAccount)
                },
                colors = ButtonDefaults.buttonColors(containerColor = BlackBoxEmerald),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("dialog_confirm_payout_btn")
            ) {
                Text("Confirm Payout", color = Color(0xFF070B12), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary)
            ) {
                Text("Cancel")
            }
        }
    )
}
