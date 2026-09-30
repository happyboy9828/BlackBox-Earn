package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val BlackBoxColorScheme = darkColorScheme(
    primary = BlackBoxEmerald,
    onPrimary = BlackBoxBg,
    primaryContainer = BlackBoxEmeraldBg,
    onPrimaryContainer = BlackBoxEmeraldLight,
    secondary = BlackBoxCyan,
    onSecondary = BlackBoxBg,
    secondaryContainer = BlackBoxSurfaceVariant,
    onSecondaryContainer = BlackBoxCyanLight,
    tertiary = BlackBoxAmber,
    onTertiary = BlackBoxBg,
    background = BlackBoxBg,
    onBackground = TextPrimary,
    surface = BlackBoxSurface,
    onSurface = TextPrimary,
    surfaceVariant = BlackBoxSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = BlackBoxCardBorder,
    error = BlackBoxRose,
    onError = TextPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // BlackBox Earn is purposefully engineered as an ultra-modern dark telemetry dashboard
    content: @Composable () -> Unit
) {
    val colorScheme = BlackBoxColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = BlackBoxBg.toArgb()
                window.navigationBarColor = BlackBoxBg.toArgb()
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = false
                controller.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
