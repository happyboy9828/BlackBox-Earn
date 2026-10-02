package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.theme.BlackBoxAmber
import com.example.ui.theme.BlackBoxCardBorder
import com.example.ui.theme.BlackBoxCyan
import com.example.ui.theme.BlackBoxEmerald
import com.example.ui.theme.BlackBoxEmeraldLight
import com.example.ui.theme.BlackBoxRose
import com.example.ui.theme.BlackBoxSurface
import com.example.ui.theme.BlackBoxSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SettingsScreen(
    isBiometricAvailable: Boolean,
    isBiometricEnabled: Boolean,
    lockTimeoutMs: Long,
    appearance: String,
    onOpenVaultRequest: () -> Unit,
    onChangePinRequest: (String, String) -> Boolean,
    onResetAllDataRequest: (String) -> Boolean,
    onSetBiometric: (Boolean) -> Unit,
    onSetLockTimeout: (Long) -> Unit,
    onSetAppearance: (String) -> Unit,
    onClearCache: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showChangePinDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showTimeoutDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "APPLICATION SETTINGS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Security & Preferences",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }

        // 1. SECURE API VAULT BANNER (Always locked)
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenVaultRequest() }
                    .testTag("settings_vault_item"),
                borderColor = BlackBoxEmerald.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(BlackBoxEmerald.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = BlackBoxEmeraldLight,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Secure API Vault",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Icon(Icons.Default.Lock, contentDescription = null, tint = BlackBoxEmeraldLight, modifier = Modifier.size(13.dp))
                            }
                            Text(
                                text = "Adsterra & Monetag Tokens (PIN Protected)",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Open",
                        tint = BlackBoxEmeraldLight,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // 2. SECURITY SECTION
        item {
            Text(
                text = "SECURITY CONTROLS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )
        }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    // Biometric
                    if (isBiometricAvailable) {
                        SettingsRowWithSwitch(
                            title = "Biometric Authentication",
                            subtitle = "Use fingerprint or face to unlock app",
                            icon = Icons.Default.Fingerprint,
                            isChecked = isBiometricEnabled,
                            onCheckedChange = onSetBiometric
                        )
                    }

                    // Change PIN
                    SettingsActionRow(
                        title = "Change App PIN",
                        subtitle = "Update your 6-digit master PIN",
                        icon = Icons.Default.LockReset,
                        onClick = { showChangePinDialog = true }
                    )

                    // Lock Timeout
                    val timeoutText = when (lockTimeoutMs) {
                        120_000L -> "2 minutes after app closed"
                        300_000L -> "5 minutes after app closed"
                        else -> "Immediately after app closed"
                    }
                    SettingsActionRow(
                        title = "Auto-Lock Timer",
                        subtitle = "Lock timer: $timeoutText",
                        icon = Icons.Default.Timer,
                        onClick = { showTimeoutDialog = true }
                    )
                }
            }
        }

        // 3. DATA & EXPORT
        item {
            Text(
                text = "DATA MANAGEMENT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )
        }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    SettingsActionRow(
                        title = "Export CSV Report",
                        subtitle = "Share raw earnings & impressions log",
                        icon = Icons.Default.Download,
                        onClick = {
                            val intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "BlackBox Earn CSV Export\nNetwork,Date,Revenue,Impressions,CPM\nAdsterra,Today,241.92,38400,6.30\nMonetag,Today,221.65,34100,6.50")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(intent, "Export Earnings CSV"))
                        }
                    )

                    SettingsActionRow(
                        title = "Clear Cached Stats",
                        subtitle = "Purges local Room DB table (does not delete tokens)",
                        icon = Icons.Default.Delete,
                        onClick = {
                            onClearCache()
                            Toast.makeText(context, "Local stats cache cleared", Toast.LENGTH_SHORT).show()
                        }
                    )

                    SettingsActionRow(
                        title = "Reset All Data",
                        subtitle = "Wipes all credentials, PIN, and cache (Requires PIN)",
                        icon = Icons.Default.Delete,
                        titleColor = BlackBoxRose,
                        onClick = { showResetDialog = true }
                    )
                }
            }
        }

        // 4. OFFICIAL API NOTICE
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = BlackBoxSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = BlackBoxCyan, modifier = Modifier.size(16.dp))
                        Text(
                            text = "OFFICIAL API INTEGRATION ONLY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BlackBoxCyan,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "This app strictly connects to official REST endpoints: https://api3.adsterratools.com/publisher and https://api.monetag.com/v5. No scraping or WebView logins are ever performed.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }

    // Change PIN Dialog
    if (showChangePinDialog) {
        ChangePinDialog(
            onDismiss = { showChangePinDialog = false },
            onConfirm = { oldPin, newPin ->
                val success = onChangePinRequest(oldPin, newPin)
                if (success) {
                    Toast.makeText(context, "PIN changed successfully", Toast.LENGTH_SHORT).show()
                    showChangePinDialog = false
                }
                success
            }
        )
    }

    // Reset All Data Dialog
    if (showResetDialog) {
        ResetDataDialog(
            onDismiss = { showResetDialog = false },
            onConfirm = { pin ->
                val success = onResetAllDataRequest(pin)
                if (success) {
                    Toast.makeText(context, "All data reset successfully", Toast.LENGTH_SHORT).show()
                    showResetDialog = false
                }
                success
            }
        )
    }

    // Timeout Dialog
    if (showTimeoutDialog) {
        AlertDialog(
            onDismissRequest = { showTimeoutDialog = false },
            containerColor = BlackBoxSurface,
            title = { Text("Auto-Lock Timeout", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val timeouts = listOf(
                        Pair(0L, "Immediately after app closed"),
                        Pair(120_000L, "2 Minutes after app closed"),
                        Pair(300_000L, "5 Minutes after app closed")
                    )
                    timeouts.forEach { (time, label) ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (lockTimeoutMs == time) BlackBoxEmerald.copy(alpha = 0.15f) else BlackBoxSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSetLockTimeout(time)
                                    showTimeoutDialog = false
                                }
                                .padding(12.dp)
                        ) {
                            Text(label, color = if (lockTimeoutMs == time) BlackBoxEmeraldLight else TextPrimary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showTimeoutDialog = false }) {
                    Text("Close", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun SettingsActionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    titleColor: Color = TextPrimary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = titleColor, modifier = Modifier.size(20.dp))
            Column {
                Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = titleColor)
                Text(text = subtitle, fontSize = 11.sp, color = TextSecondary)
            }
        }

        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
    }
}

@Composable
fun SettingsRowWithSwitch(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = BlackBoxCyan, modifier = Modifier.size(20.dp))
            Column {
                Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(text = subtitle, fontSize = 11.sp, color = TextSecondary)
            }
        }

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = BlackBoxEmeraldLight,
                checkedTrackColor = Color(0xFF064E3B)
            )
        )
    }
}

@Composable
fun ChangePinDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Boolean
) {
    var oldPin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BlackBoxSurface,
        title = { Text("Change 6-Digit PIN", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = oldPin,
                    onValueChange = { if (it.length <= 6) oldPin = it },
                    label = { Text("Old 6-Digit PIN", color = TextSecondary) },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = BlackBoxEmerald,
                        unfocusedBorderColor = BlackBoxCardBorder
                    )
                )

                OutlinedTextField(
                    value = newPin,
                    onValueChange = { if (it.length <= 6) newPin = it },
                    label = { Text("New 6-Digit PIN", color = TextSecondary) },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = BlackBoxEmerald,
                        unfocusedBorderColor = BlackBoxCardBorder
                    )
                )

                if (errorMsg != null) {
                    Text(errorMsg!!, fontSize = 11.sp, color = BlackBoxRose)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newPin.length != 6) {
                        errorMsg = "New PIN must be 6 digits"
                        return@Button
                    }
                    val ok = onConfirm(oldPin, newPin)
                    if (!ok) {
                        errorMsg = "Incorrect old PIN"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BlackBoxEmerald)
            ) {
                Text("Update PIN", color = Color(0xFF070B12), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        }
    )
}

@Composable
fun ResetDataDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Boolean
) {
    var pin by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BlackBoxSurface,
        title = { Text("Reset All Data", color = BlackBoxRose, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Warning: This will permanently delete your stored API keys, PIN hash, and local statistics database.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                OutlinedTextField(
                    value = pin,
                    onValueChange = { if (it.length <= 6) pin = it },
                    label = { Text("Enter Current PIN to Confirm", color = TextSecondary) },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = BlackBoxRose,
                        unfocusedBorderColor = BlackBoxCardBorder
                    )
                )

                if (errorMsg != null) {
                    Text(errorMsg!!, fontSize = 11.sp, color = BlackBoxRose)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val ok = onConfirm(pin)
                    if (!ok) {
                        errorMsg = "Incorrect PIN"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BlackBoxRose)
            ) {
                Text("Wipe Everything", color = TextPrimary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        }
    )
}
