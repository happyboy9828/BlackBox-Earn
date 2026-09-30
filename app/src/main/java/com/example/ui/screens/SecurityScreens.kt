package com.example.ui.screens

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.security.PinVerifyResult
import com.example.ui.theme.BlackBoxAmber
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
import kotlinx.coroutines.delay

@Composable
fun PinSetupScreen(
    onPinCreated: (String, String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(1) } // 1: Enter PIN, 2: Confirm PIN, 3: Optional Password
    var pinFirst by remember { mutableStateOf("") }
    var pinConfirm by remember { mutableStateOf("") }
    var optionalPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Apply FLAG_SECURE to prevent screenshot / screen recording during PIN creation
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val window = (context as? Activity)?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        onDispose {
            // Keep secure while in secure screens
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BlackBoxBg)
            .padding(24.dp)
            .testTag("pin_setup_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(BlackBoxEmerald.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = BlackBoxEmeraldLight,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "MASTER SECURITY SYSTEM",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = BlackBoxEmeraldLight,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = when (step) {
                    1 -> "Create 6-Digit App PIN"
                    2 -> "Confirm Your 6-Digit PIN"
                    else -> "Optional Backup Password"
                },
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = when (step) {
                    1 -> "Required to protect Adsterra & Monetag earnings data and vault credentials."
                    2 -> "Re-enter the 6 digits to verify."
                    else -> "Add an optional master password for secondary recovery."
                },
                fontSize = 12.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = errorMessage!!,
                    fontSize = 12.sp,
                    color = BlackBoxRose,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Content / PIN Dots
        if (step in 1..2) {
            val currentPin = if (step == 1) pinFirst else pinConfirm

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(vertical = 24.dp)
                ) {
                    for (i in 0 until 6) {
                        val isFilled = i < currentPin.length
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(if (isFilled) BlackBoxEmeraldLight else BlackBoxSurfaceVariant)
                                .border(
                                    1.5.dp,
                                    if (isFilled) BlackBoxEmerald else BlackBoxCardBorder,
                                    CircleShape
                                )
                        )
                    }
                }
            }

            // Numeric Keypad
            NumericKeypad(
                onNumberClick = { digit ->
                    errorMessage = null
                    if (step == 1) {
                        if (pinFirst.length < 6) {
                            pinFirst += digit
                            if (pinFirst.length == 6) {
                                step = 2
                            }
                        }
                    } else if (step == 2) {
                        if (pinConfirm.length < 6) {
                            pinConfirm += digit
                            if (pinConfirm.length == 6) {
                                if (pinConfirm == pinFirst) {
                                    step = 3
                                } else {
                                    errorMessage = "PINs do not match. Please try again."
                                    pinConfirm = ""
                                    pinFirst = ""
                                    step = 1
                                }
                            }
                        }
                    }
                },
                onBackspaceClick = {
                    errorMessage = null
                    if (step == 1 && pinFirst.isNotEmpty()) {
                        pinFirst = pinFirst.dropLast(1)
                    } else if (step == 2 && pinConfirm.isNotEmpty()) {
                        pinConfirm = pinConfirm.dropLast(1)
                    }
                }
            )
        } else {
            // Step 3: Optional Password & Confirm
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OutlinedTextField(
                    value = optionalPassword,
                    onValueChange = { optionalPassword = it },
                    label = { Text("Backup Password (Optional)", color = TextSecondary) },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle visibility",
                                tint = TextSecondary
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("setup_password_field"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = BlackBoxEmerald,
                        unfocusedBorderColor = BlackBoxCardBorder,
                        focusedContainerColor = BlackBoxSurface,
                        unfocusedContainerColor = BlackBoxSurface
                    ),
                    singleLine = true
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BlackBoxSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "SECURITY GUARANTEE:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BlackBoxEmeraldLight,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "PIN is stored using salted SHA-256 in EncryptedSharedPreferences (AES-256-GCM). Your credentials never leave this device.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 15.sp
                        )
                    }
                }

                Button(
                    onClick = {
                        onPinCreated(pinFirst, optionalPassword.ifBlank { null })
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BlackBoxEmerald),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_complete_pin_setup")
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = BlackBoxBg)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Secure Dashboard & Continue", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BlackBoxBg)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun LockScreen(
    onVerifyPin: (String) -> PinVerifyResult,
    onBiometricUnlock: () -> Unit,
    isBiometricAvailable: Boolean,
    isBiometricEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    var pinInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var lockoutSeconds by remember { mutableIntStateOf(0) }

    // Apply FLAG_SECURE on LockScreen
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val window = (context as? Activity)?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        onDispose {
            // Cleared when exiting lock
        }
    }

    // Lockout countdown timer
    LaunchedEffect(lockoutSeconds) {
        if (lockoutSeconds > 0) {
            delay(1000)
            lockoutSeconds -= 1
            if (lockoutSeconds == 0) {
                errorMessage = null
            }
        }
    }

    // Automatically trigger biometric fingerprint popup when app opens or LockScreen appears
    LaunchedEffect(isBiometricAvailable, isBiometricEnabled) {
        if (isBiometricAvailable && isBiometricEnabled && lockoutSeconds == 0) {
            delay(200)
            onBiometricUnlock()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BlackBoxBg)
            .padding(24.dp)
            .testTag("lock_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Icon & Title
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 40.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(BlackBoxEmerald.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = BlackBoxEmeraldLight,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "BlackBox Earn",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (lockoutSeconds > 0) "Locked for security: ${lockoutSeconds}s" else "Enter 6-Digit PIN to Unlock",
                fontSize = 13.sp,
                color = if (lockoutSeconds > 0) BlackBoxRose else TextSecondary,
                fontWeight = if (lockoutSeconds > 0) FontWeight.Bold else FontWeight.Normal
            )

            if (errorMessage != null && lockoutSeconds == 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    fontSize = 12.sp,
                    color = BlackBoxRose,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // PIN Dots
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(vertical = 20.dp)
        ) {
            for (i in 0 until 6) {
                val isFilled = i < pinInput.length
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(if (isFilled) BlackBoxEmeraldLight else BlackBoxSurfaceVariant)
                        .border(
                            1.5.dp,
                            if (isFilled) BlackBoxEmerald else BlackBoxCardBorder,
                            CircleShape
                        )
                )
            }
        }

        // Numeric Keypad
        NumericKeypad(
            enabled = lockoutSeconds == 0,
            showBiometric = isBiometricAvailable && isBiometricEnabled,
            onBiometricClick = onBiometricUnlock,
            onNumberClick = { digit ->
                if (pinInput.length < 6 && lockoutSeconds == 0) {
                    pinInput += digit
                    if (pinInput.length == 6) {
                        val result = onVerifyPin(pinInput)
                        when (result) {
                            is PinVerifyResult.Success -> {
                                errorMessage = null
                            }
                            is PinVerifyResult.Failed -> {
                                errorMessage = "Incorrect PIN. ${result.attemptsLeft} attempt(s) remaining."
                                pinInput = ""
                            }
                            is PinVerifyResult.LockedOut -> {
                                lockoutSeconds = result.secondsRemaining
                                errorMessage = "Too many failed attempts. Locked for 60s."
                                pinInput = ""
                            }
                            else -> {}
                        }
                    }
                }
            },
            onBackspaceClick = {
                if (pinInput.isNotEmpty() && lockoutSeconds == 0) {
                    pinInput = pinInput.dropLast(1)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun NumericKeypad(
    enabled: Boolean = true,
    showBiometric: Boolean = false,
    onNumberClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    onBiometricClick: (() -> Unit)? = null
) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("BIO", "0", "DEL")
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally)
            ) {
                row.forEach { key ->
                    when (key) {
                        "BIO" -> {
                            if (showBiometric && onBiometricClick != null) {
                                Box(
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(CircleShape)
                                        .background(BlackBoxSurfaceVariant)
                                        .clickable(enabled = enabled) { onBiometricClick() }
                                        .testTag("key_biometric"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Fingerprint,
                                        contentDescription = "Biometric",
                                        tint = BlackBoxCyan,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.size(68.dp))
                            }
                        }
                        "DEL" -> {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape)
                                    .background(BlackBoxSurfaceVariant)
                                    .clickable(enabled = enabled) { onBackspaceClick() }
                                    .testTag("key_backspace"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Backspace,
                                    contentDescription = "Backspace",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        else -> {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape)
                                    .background(BlackBoxSurface)
                                    .border(1.dp, BlackBoxCardBorder, CircleShape)
                                    .clickable(enabled = enabled) { onNumberClick(key) }
                                    .testTag("key_$key"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = key,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (enabled) TextPrimary else TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
