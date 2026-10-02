package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.WindowManager
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.security.SecureStorageManager
import com.example.data.security.TokenStatus
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
fun StatusBadge(status: TokenStatus) {
    val (color, text, icon) = when (status) {
        TokenStatus.ACTIVE -> Triple(BlackBoxEmeraldLight, "Active", Icons.Default.CheckCircle)
        TokenStatus.INVALID -> Triple(BlackBoxRose, "Invalid", Icons.Default.Error)
        TokenStatus.CHECKING -> Triple(BlackBoxCyan, "Checking", Icons.Default.Refresh)
        TokenStatus.NOT_SET -> Triple(TextMuted, "Not Set", Icons.Default.HelpOutline)
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(12.dp))
            Text(text = text, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultScreen(
    adsterraToken: String,
    monetagToken: String,
    monetagWithdrawals: Double,
    vercelToken: String = "",
    adsterraStatus: TokenStatus,
    monetagStatus: TokenStatus,
    vercelStatus: TokenStatus = TokenStatus.NOT_SET,
    isTestingAdsterra: Boolean,
    isTestingMonetag: Boolean,
    isTestingVercel: Boolean = false,
    onSaveVault: (String, String, Double, String) -> Unit,
    onTestAdsterra: (String) -> Unit,
    onTestMonetag: (String) -> Unit,
    onTestVercel: (String) -> Unit = {},
    onDeleteAdsterra: () -> Unit,
    onDeleteMonetag: () -> Unit,
    onDeleteVercel: () -> Unit = {},
    onLockVault: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Apply FLAG_SECURE on VaultScreen to block screenshots & screen recordings
    DisposableEffect(Unit) {
        val window = (context as? Activity)?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        onDispose {
            // Screen exited
        }
    }

    var inputAdsterra by remember(adsterraToken) { mutableStateOf(adsterraToken) }
    var inputMonetag by remember(monetagToken) { mutableStateOf(monetagToken) }
    var inputVercel by remember(vercelToken) { mutableStateOf(vercelToken) }
    var inputWithdrawals by remember(monetagWithdrawals) { mutableStateOf(monetagWithdrawals.toString()) }

    var showAdsterraToken by remember { mutableStateOf(false) }
    var showMonetagToken by remember { mutableStateOf(false) }
    var showVercelToken by remember { mutableStateOf(false) }
    var saveSuccessMsg by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BlackBoxBg,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(BlackBoxEmerald.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = BlackBoxEmeraldLight,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Secure API Vault",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary,
                                maxLines = 1
                            )
                            Text(
                                text = "Hardware Encrypted • Zero-Trust",
                                fontSize = 10.sp,
                                color = BlackBoxEmeraldLight,
                                maxLines = 1
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onLockVault,
                        modifier = Modifier.testTag("vault_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onLockVault,
                        modifier = Modifier.testTag("btn_lock_vault")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Lock Vault",
                            tint = BlackBoxRose,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BlackBoxSurface,
                    titleContentColor = TextPrimary,
                    navigationIconContentColor = TextPrimary,
                    actionIconContentColor = TextPrimary
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .testTag("vault_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 0. Prominent Secure API Vault Header Banner
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = BlackBoxSurfaceVariant,
                    border = BorderStroke(1.dp, BlackBoxEmerald.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth().testTag("vault_header_card")
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .background(BlackBoxEmerald.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = BlackBoxEmeraldLight,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "SECURE API VAULT",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Biometric & Hardware Protected Credentials",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BlackBoxEmeraldLight
                            )
                        }
                    }
                }
            }

        // Encryption Banner
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = BlackBoxSurface,
                border = BorderStroke(1.dp, BlackBoxCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = BlackBoxCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Hardware-backed EncryptedSharedPreferences (AES-256-GCM). Keys are NEVER stored in Room DB and logs are strictly masked (****abcd).",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Section 1: Adsterra API Token
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth().testTag("adsterra_vault_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "1. ADSTERRA API TOKEN",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Header: X-API-Key",
                                fontSize = 10.sp,
                                color = BlackBoxCyan
                            )
                        }

                        StatusBadge(status = adsterraStatus)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = inputAdsterra,
                        onValueChange = { inputAdsterra = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_adsterra_token"),
                        placeholder = { Text("Enter Adsterra API Token...", fontSize = 12.sp, color = TextMuted) },
                        visualTransformation = if (showAdsterraToken) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showAdsterraToken = !showAdsterraToken }) {
                                Icon(
                                    imageVector = if (showAdsterraToken) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle visibility",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = BlackBoxSurfaceVariant,
                            unfocusedContainerColor = BlackBoxSurfaceVariant,
                            focusedBorderColor = BlackBoxEmerald,
                            unfocusedBorderColor = BlackBoxCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://beta.publishers.adsterra.com/api-token"))
                                context.startActivity(intent)
                            }
                        ) {
                            Text(
                                text = "Get API Token",
                                fontSize = 11.sp,
                                color = BlackBoxCyan,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Icon(Icons.Default.OpenInNew, contentDescription = null, tint = BlackBoxCyan, modifier = Modifier.size(12.dp))
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (inputAdsterra.isNotBlank()) {
                                IconButton(
                                    onClick = {
                                        inputAdsterra = ""
                                        onDeleteAdsterra()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = BlackBoxRose, modifier = Modifier.size(16.dp))
                                }
                            }

                            Button(
                                onClick = { onTestAdsterra(inputAdsterra) },
                                enabled = inputAdsterra.isNotBlank() && !isTestingAdsterra,
                                colors = ButtonDefaults.buttonColors(containerColor = BlackBoxSurfaceVariant),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("btn_test_adsterra")
                            ) {
                                if (isTestingAdsterra) {
                                    CircularProgressIndicator(strokeWidth = 2.dp, color = BlackBoxCyan, modifier = Modifier.size(14.dp))
                                } else {
                                    Text("Test Connection", fontSize = 11.sp, color = BlackBoxCyan, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Monetag API Token
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth().testTag("monetag_vault_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "2. MONETAG API TOKEN",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Header: X-API-Key / Bearer",
                                fontSize = 10.sp,
                                color = BlackBoxCyan
                            )
                        }

                        StatusBadge(status = monetagStatus)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = inputMonetag,
                        onValueChange = { inputMonetag = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_monetag_token"),
                        placeholder = { Text("Enter Monetag API Token...", fontSize = 12.sp, color = TextMuted) },
                        visualTransformation = if (showMonetagToken) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showMonetagToken = !showMonetagToken }) {
                                Icon(
                                    imageVector = if (showMonetagToken) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle visibility",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = BlackBoxSurfaceVariant,
                            unfocusedContainerColor = BlackBoxSurfaceVariant,
                            focusedBorderColor = BlackBoxEmerald,
                            unfocusedBorderColor = BlackBoxCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://publishers.monetag.com/profile"))
                                context.startActivity(intent)
                            }
                        ) {
                            Text(
                                text = "Get API Token",
                                fontSize = 11.sp,
                                color = BlackBoxCyan,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Icon(Icons.Default.OpenInNew, contentDescription = null, tint = BlackBoxCyan, modifier = Modifier.size(12.dp))
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (inputMonetag.isNotBlank()) {
                                IconButton(
                                    onClick = {
                                        inputMonetag = ""
                                        onDeleteMonetag()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = BlackBoxRose, modifier = Modifier.size(16.dp))
                                }
                            }

                            Button(
                                onClick = { onTestMonetag(inputMonetag) },
                                enabled = inputMonetag.isNotBlank() && !isTestingMonetag,
                                colors = ButtonDefaults.buttonColors(containerColor = BlackBoxSurfaceVariant),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("btn_test_monetag")
                            ) {
                                if (isTestingMonetag) {
                                    CircularProgressIndicator(strokeWidth = 2.dp, color = BlackBoxCyan, modifier = Modifier.size(14.dp))
                                } else {
                                    Text("Test Connection", fontSize = 11.sp, color = BlackBoxCyan, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 3: Vercel API Token (Connected Domains, Traffic & Bounce Rate)
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth().testTag("vercel_vault_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "3. VERCEL API TOKEN",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Header: Authorization: Bearer {token}",
                                fontSize = 11.sp,
                                color = BlackBoxPurple
                            )
                        }

                        StatusBadge(status = vercelStatus)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = inputVercel,
                        onValueChange = { inputVercel = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_vercel_token"),
                        placeholder = { Text("Enter Vercel Bearer Token...", fontSize = 12.sp, color = TextMuted) },
                        visualTransformation = if (showVercelToken) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showVercelToken = !showVercelToken }) {
                                Icon(
                                    imageVector = if (showVercelToken) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle visibility",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = BlackBoxSurfaceVariant,
                            unfocusedContainerColor = BlackBoxSurfaceVariant,
                            focusedBorderColor = BlackBoxEmerald,
                            unfocusedBorderColor = BlackBoxCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://vercel.com/account/tokens"))
                                context.startActivity(intent)
                            }
                        ) {
                            Text(
                                text = "Get Vercel Token",
                                fontSize = 11.sp,
                                color = BlackBoxPurple,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Icon(Icons.Default.OpenInNew, contentDescription = null, tint = BlackBoxPurple, modifier = Modifier.size(12.dp))
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (inputVercel.isNotBlank()) {
                                IconButton(
                                    onClick = {
                                        inputVercel = ""
                                        onDeleteVercel()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = BlackBoxRose, modifier = Modifier.size(16.dp))
                                }
                            }

                            Button(
                                onClick = { onTestVercel(inputVercel) },
                                enabled = inputVercel.isNotBlank() && !isTestingVercel,
                                colors = ButtonDefaults.buttonColors(containerColor = BlackBoxSurfaceVariant),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("btn_test_vercel")
                            ) {
                                if (isTestingVercel) {
                                    CircularProgressIndicator(strokeWidth = 2.dp, color = BlackBoxPurple, modifier = Modifier.size(14.dp))
                                } else {
                                    Text("Test Connection", fontSize = 11.sp, color = BlackBoxPurple, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 4: Monetag Total Withdrawals
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth().testTag("withdrawals_vault_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "4. MONETAG TOTAL WITHDRAWALS ($)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Used for accurate Lifetime Balance calculation: (Adsterra + Monetag - Withdrawals)",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = inputWithdrawals,
                        onValueChange = { inputWithdrawals = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_withdrawals"),
                        placeholder = { Text("0.00", fontSize = 12.sp, color = TextMuted) },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = BlackBoxSurfaceVariant,
                            unfocusedContainerColor = BlackBoxSurfaceVariant,
                            focusedBorderColor = BlackBoxEmerald,
                            unfocusedBorderColor = BlackBoxCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true
                    )
                }
            }
        }

        // Save Button
        item {
            Button(
                onClick = {
                    val w = inputWithdrawals.toDoubleOrNull() ?: 0.0
                    onSaveVault(inputAdsterra, inputMonetag, w, inputVercel)
                    saveSuccessMsg = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = BlackBoxEmerald),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_save_vault")
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = BlackBoxBg)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save & Sync With Official APIs", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BlackBoxBg)
            }
        }

        if (saveSuccessMsg) {
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
                        Text("Vault credentials securely encrypted and saved!", fontSize = 12.sp, color = TextPrimary)
                    }
                }
            }
        }
    }
}
}
