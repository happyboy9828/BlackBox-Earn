package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.DailyScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LockScreen
import com.example.ui.screens.PinSetupScreen
import com.example.ui.screens.PublisherViewModel
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.VaultScreen
import com.example.ui.theme.BlackBoxBg
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

data class NavTab(
    val title: String,
    val icon: ImageVector,
    val tag: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PubDashApp(
    viewModel: PublisherViewModel,
    onRequestBiometricPrompt: (() -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var currentTabIndex by remember { mutableIntStateOf(0) }
    var showVaultPinDialog by remember { mutableStateOf(false) }

    // If app is not unlocked yet
    if (!uiState.isUnlocked) {
        if (!uiState.isPinConfigured) {
            PinSetupScreen(
                onPinCreated = { pin, pw ->
                    viewModel.setupPin(pin, pw)
                }
            )
        } else {
            LockScreen(
                onVerifyPin = { pin ->
                    viewModel.verifyPin(pin)
                },
                onBiometricUnlock = {
                    if (onRequestBiometricPrompt != null) {
                        onRequestBiometricPrompt()
                    } else {
                        viewModel.unlockWithBiometrics()
                    }
                },
                isBiometricAvailable = uiState.isBiometricAvailable,
                isBiometricEnabled = uiState.isBiometricEnabled
            )
        }
        return
    }

    // If Vault is unlocked, show Vault Screen with FLAG_SECURE
    if (uiState.isVaultUnlocked) {
        BackHandler {
            viewModel.lockVault()
        }
        VaultScreen(
            adsterraToken = uiState.adsterraToken,
            monetagToken = uiState.monetagToken,
            monetagWithdrawals = uiState.monetagWithdrawals,
            vercelToken = uiState.vercelToken,
            adsterraStatus = uiState.adsterraStatus,
            monetagStatus = uiState.monetagStatus,
            vercelStatus = uiState.vercelStatus,
            isTestingAdsterra = uiState.isTestingAdsterra,
            isTestingMonetag = uiState.isTestingMonetag,
            isTestingVercel = uiState.isTestingVercel,
            onSaveVault = { ad, mon, withdr, ver ->
                viewModel.saveVaultSecrets(ad, mon, withdr, ver)
            },
            onTestAdsterra = { viewModel.testAdsterraToken(it) },
            onTestMonetag = { viewModel.testMonetagToken(it) },
            onTestVercel = { viewModel.testVercelToken(it) },
            onDeleteAdsterra = { viewModel.deleteAdsterraToken() },
            onDeleteMonetag = { viewModel.deleteMonetagToken() },
            onDeleteVercel = { viewModel.deleteVercelToken() },
            onLockVault = { viewModel.lockVault() }
        )
        return
    }

    // Main App Navigation
    val tabs = listOf(
        NavTab("Home", Icons.Default.Dashboard, "tab_home"),
        NavTab("Daily", Icons.Default.CalendarMonth, "tab_daily"),
        NavTab("Analytics", Icons.Default.Analytics, "tab_analytics"),
        NavTab("Settings", Icons.Default.Settings, "tab_settings")
    )

    BackHandler(enabled = currentTabIndex != 0) {
        currentTabIndex = 0
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().testTag("main_scaffold"),
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
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "BlackBox Earn",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(BlackBoxEmerald, CircleShape)
                                )
                            }
                            Text(
                                text = "ADSTERRA, MONETAG & VERCEL VAULT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                },
                actions = {
                    // Lock App button
                    IconButton(
                        onClick = { viewModel.lockApp() },
                        modifier = Modifier.testTag("appbar_lock_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock App", tint = TextSecondary, modifier = Modifier.size(20.dp))
                    }

                    // Refresh Button
                    IconButton(
                        onClick = { viewModel.refreshPublisherData() },
                        enabled = !uiState.isRefreshing,
                        modifier = Modifier.testTag("appbar_refresh_btn")
                    ) {
                        if (uiState.isRefreshing) {
                            CircularProgressIndicator(strokeWidth = 2.dp, color = BlackBoxEmerald, modifier = Modifier.size(18.dp))
                        } else {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh", tint = BlackBoxEmeraldLight, modifier = Modifier.size(20.dp))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BlackBoxBg,
                    titleContentColor = TextPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = BlackBoxSurface,
                tonalElevation = 0.dp,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                tabs.forEachIndexed { index, tab ->
                    val isSelected = currentTabIndex == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTabIndex = index },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BlackBoxBg,
                            selectedTextColor = BlackBoxEmeraldLight,
                            indicatorColor = BlackBoxEmerald,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag(tab.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BlackBoxBg)
        ) {
            when (currentTabIndex) {
                0 -> HomeScreen(
                    balance = uiState.balanceSummary,
                    domains = uiState.filteredConnectedDomains,
                    selectedSystemFilter = uiState.selectedSystemFilter,
                    onSystemFilterChange = { viewModel.setSystemFilter(it) },
                    isRefreshing = uiState.isRefreshing,
                    syncMessage = uiState.syncMessage,
                    hasAdsterraKey = uiState.adsterraToken.isNotBlank(),
                    hasMonetagKey = uiState.monetagToken.isNotBlank(),
                    hasVercelKey = uiState.vercelToken.isNotBlank(),
                    onRefresh = { viewModel.refreshPublisherData() },
                    onNavigateToVault = { showVaultPinDialog = true }
                )
                1 -> DailyScreen(
                    groupedStats = uiState.groupedStats,
                    selectedFilter = uiState.selectedFilter,
                    selectedGroupBy = uiState.selectedGroupBy,
                    onFilterChange = { viewModel.setFilter(it) },
                    onGroupByChange = { viewModel.setGroupBy(it) }
                )
                2 -> AnalyticsScreen(
                    dailyRows = uiState.dailyRows
                )
                3 -> SettingsScreen(
                    isBiometricAvailable = uiState.isBiometricAvailable,
                    isBiometricEnabled = uiState.isBiometricEnabled,
                    lockTimeoutMs = uiState.lockTimeoutMs,
                    appearance = uiState.appearance,
                    onOpenVaultRequest = { showVaultPinDialog = true },
                    onChangePinRequest = { oldP, newP -> viewModel.changePin(oldP, newP) },
                    onResetAllDataRequest = { pin -> viewModel.resetAllData(pin) },
                    onSetBiometric = { viewModel.setBiometric(it) },
                    onSetLockTimeout = { viewModel.setLockTimeout(it) },
                    onSetAppearance = { viewModel.setAppearance(it) },
                    onClearCache = { viewModel.clearCache() }
                )
            }
        }
    }

    // Vault PIN authentication dialog (Second Layer Security)
    if (showVaultPinDialog) {
        VaultAuthDialog(
            onDismiss = { showVaultPinDialog = false },
            onConfirmPin = { pin ->
                val ok = viewModel.unlockVault(pin)
                if (ok) {
                    showVaultPinDialog = false
                }
                ok
            }
        )
    }
}

@Composable
fun VaultAuthDialog(
    onDismiss: () -> Unit,
    onConfirmPin: (String) -> Boolean
) {
    var pin by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BlackBoxSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = BlackBoxEmeraldLight, modifier = Modifier.size(20.dp))
                Text("Enter PIN to unlock Vault", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Second-layer authentication required to view or edit Adsterra and Monetag API tokens.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                OutlinedTextField(
                    value = pin,
                    onValueChange = { if (it.length <= 6) pin = it },
                    label = { Text("6-Digit PIN", color = TextSecondary) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = BlackBoxEmerald,
                        unfocusedBorderColor = BlackBoxCardBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("vault_pin_input")
                )

                if (errorMsg != null) {
                    Text(errorMsg!!, fontSize = 11.sp, color = BlackBoxRose)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (pin.length != 6) {
                        errorMsg = "PIN must be 6 digits"
                        return@Button
                    }
                    val ok = onConfirmPin(pin)
                    if (!ok) {
                        errorMsg = "Incorrect PIN. Vault access denied."
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BlackBoxEmerald),
                modifier = Modifier.testTag("btn_confirm_vault_pin")
            ) {
                Text("Unlock Vault", color = Color(0xFF070B12), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
