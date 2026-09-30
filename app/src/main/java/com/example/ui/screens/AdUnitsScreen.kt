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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AdUnitEntity
import com.example.ui.components.FormatBadge
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
import java.util.Locale

@Composable
fun AdUnitsScreen(
    adUnits: List<AdUnitEntity>,
    onToggleActive: (Int, Boolean) -> Unit,
    onSaveAdUnit: (AdUnitEntity) -> Unit,
    onDeleteAdUnit: (AdUnitEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFormat by remember { mutableStateOf("All") }
    var editingAdUnit by remember { mutableStateOf<AdUnitEntity?>(null) }
    var isAddingAdUnit by remember { mutableStateOf(false) }

    val formats = listOf("All", "Rewarded", "Interstitial", "Banner", "App Open", "Native")

    val filteredUnits = adUnits.filter { unit ->
        val matchesSearch = unit.name.contains(searchQuery, ignoreCase = true) ||
                unit.adUnitId.contains(searchQuery, ignoreCase = true) ||
                unit.primaryNetwork.contains(searchQuery, ignoreCase = true)
        val matchesFormat = selectedFormat == "All" || unit.adFormat.equals(selectedFormat, ignoreCase = true)
        matchesSearch && matchesFormat
    }

    Box(modifier = modifier.fillMaxSize().testTag("ad_units_screen")) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header stats
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "AD INVENTORY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Placements (${adUnits.size})",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Button(
                        onClick = { isAddingAdUnit = true },
                        colors = ButtonDefaults.buttonColors(containerColor = BlackBoxEmerald),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("add_ad_unit_top_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color(0xFF070B12),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Ad Unit", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF070B12))
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ad_unit_search_field"),
                    placeholder = { Text("Search by unit name, placement ID, or network...", fontSize = 13.sp, color = TextMuted) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BlackBoxSurface,
                        unfocusedContainerColor = BlackBoxSurface,
                        focusedBorderColor = BlackBoxEmerald,
                        unfocusedBorderColor = BlackBoxCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )
            }

            // Format Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(formats) { format ->
                        val isSelected = selectedFormat == format
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) BlackBoxEmerald else BlackBoxSurfaceVariant,
                            modifier = Modifier.clickable { selectedFormat = format }
                        ) {
                            Text(
                                text = format,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color(0xFF070B12) else TextSecondary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Units List
            items(filteredUnits, key = { it.id }) { unit ->
                AdUnitItemCard(
                    unit = unit,
                    onToggle = { onToggleActive(unit.id, it) },
                    onEdit = { editingAdUnit = unit }
                )
            }
        }
    }

    // Edit Dialog
    editingAdUnit?.let { unit ->
        AdUnitConfigDialog(
            adUnit = unit,
            onDismiss = { editingAdUnit = null },
            onSave = { updated ->
                onSaveAdUnit(updated)
                editingAdUnit = null
            },
            onDelete = {
                onDeleteAdUnit(unit)
                editingAdUnit = null
            }
        )
    }

    // Add Dialog
    if (isAddingAdUnit) {
        AdUnitConfigDialog(
            adUnit = AdUnitEntity(
                name = "",
                adFormat = "Rewarded",
                adUnitId = "ca-app-pub-custom/unit-new",
                primaryNetwork = "Google AdMob",
                floorPrice = 10.0,
                fillRate = 96.0,
                ecpm = 15.0,
                impressions = 0,
                revenue = 0.0,
                isActive = true
            ),
            isNew = true,
            onDismiss = { isAddingAdUnit = false },
            onSave = { created ->
                onSaveAdUnit(created)
                isAddingAdUnit = false
            },
            onDelete = {}
        )
    }
}

@Composable
private fun AdUnitItemCard(
    unit: AdUnitEntity,
    onToggle: (Boolean) -> Unit,
    onEdit: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ad_unit_card_${unit.id}"),
        onClick = onEdit
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Name, Format, Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = unit.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        FormatBadge(format = unit.adFormat)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "ID: ${unit.adUnitId}",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Switch(
                    checked = unit.isActive,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = BlackBoxEmeraldLight,
                        checkedTrackColor = Color(0xFF064E3B),
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = BlackBoxSurfaceVariant
                    ),
                    modifier = Modifier.testTag("switch_unit_${unit.id}")
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Metrics row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BlackBoxSurfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(vertical = 10.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("eCPM", fontSize = 11.sp, color = TextMuted)
                    Text("$${String.format(Locale.US, "%.2f", unit.ecpm)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Column {
                    Text("Floor Price", fontSize = 11.sp, color = TextMuted)
                    Text("$${String.format(Locale.US, "%.2f", unit.floorPrice)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BlackBoxAmber)
                }
                Column {
                    Text("Fill Rate", fontSize = 11.sp, color = TextMuted)
                    Text("${String.format(Locale.US, "%.1f", unit.fillRate)}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BlackBoxCyan)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Revenue", fontSize = 11.sp, color = TextMuted)
                    Text("$${String.format(Locale.US, "%,.2f", unit.revenue)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BlackBoxEmerald)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Fill Rate progress bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Primary: ${unit.primaryNetwork}", fontSize = 11.sp, color = TextSecondary)
                    Text("Fill: ${String.format(Locale.US, "%.1f", unit.fillRate)}%", fontSize = 11.sp, color = BlackBoxEmeraldLight, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { (unit.fillRate / 100.0).toFloat().coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = BlackBoxEmerald,
                    trackColor = BlackBoxSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun AdUnitConfigDialog(
    adUnit: AdUnitEntity,
    isNew: Boolean = false,
    onDismiss: () -> Unit,
    onSave: (AdUnitEntity) -> Unit,
    onDelete: () -> Unit
) {
    var name by remember { mutableStateOf(adUnit.name) }
    var adFormat by remember { mutableStateOf(adUnit.adFormat) }
    var adUnitId by remember { mutableStateOf(adUnit.adUnitId) }
    var primaryNetwork by remember { mutableStateOf(adUnit.primaryNetwork) }
    var floorStr by remember { mutableStateOf(adUnit.floorPrice.toString()) }

    val formatOptions = listOf("Rewarded", "Interstitial", "Banner", "App Open", "Native")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BlackBoxSurface,
        title = {
            Text(
                text = if (isNew) "Create Ad Unit" else "Edit Ad Unit",
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
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Placement Name", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth().testTag("dialog_unit_name"),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = BlackBoxEmerald,
                        unfocusedBorderColor = BlackBoxCardBorder
                    )
                )

                // Format Picker Chips
                Text("Ad Format", fontSize = 11.sp, color = TextSecondary)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(formatOptions) { fmt ->
                        val isSelected = adFormat == fmt
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) BlackBoxEmerald else BlackBoxSurfaceVariant,
                            modifier = Modifier.clickable { adFormat = fmt }
                        ) {
                            Text(
                                text = fmt,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color(0xFF070B12) else TextSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = adUnitId,
                    onValueChange = { adUnitId = it },
                    label = { Text("Ad Unit / Placement ID", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth().testTag("dialog_unit_id"),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = BlackBoxEmerald,
                        unfocusedBorderColor = BlackBoxCardBorder
                    )
                )

                OutlinedTextField(
                    value = primaryNetwork,
                    onValueChange = { primaryNetwork = it },
                    label = { Text("Primary Ad Exchange", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth().testTag("dialog_unit_network"),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = BlackBoxEmerald,
                        unfocusedBorderColor = BlackBoxCardBorder
                    )
                )

                OutlinedTextField(
                    value = floorStr,
                    onValueChange = { floorStr = it },
                    label = { Text("Floor Price ($)", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth().testTag("dialog_unit_floor"),
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
                    val floor = floorStr.toDoubleOrNull() ?: adUnit.floorPrice
                    onSave(
                        adUnit.copy(
                            name = name.ifBlank { "New Placement" },
                            adFormat = adFormat,
                            adUnitId = adUnitId,
                            primaryNetwork = primaryNetwork,
                            floorPrice = floor
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = BlackBoxEmerald),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("dialog_save_unit_btn")
            ) {
                Text("Save Placement", color = Color(0xFF070B12), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Row {
                if (!isNew) {
                    TextButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.textButtonColors(contentColor = BlackBoxRose)
                    ) {
                        Text("Delete")
                    }
                }
                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary)
                ) {
                    Text("Cancel")
                }
            }
        }
    )
}
