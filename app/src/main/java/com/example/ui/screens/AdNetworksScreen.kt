package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AdNetworkEntity
import com.example.ui.components.GlassCard
import com.example.ui.components.NetworkMonogram
import com.example.ui.components.StatusChip
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
fun AdNetworksScreen(
    networks: List<AdNetworkEntity>,
    onToggleActive: (Int, Boolean) -> Unit,
    onSaveNetwork: (AdNetworkEntity) -> Unit,
    onDeleteNetwork: (AdNetworkEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") } // "All", "Active", "Bidding", "Waterfall"
    var editingNetwork by remember { mutableStateOf<AdNetworkEntity?>(null) }
    var isAddingNetwork by remember { mutableStateOf(false) }

    val filteredNetworks = networks.filter { network ->
        val matchesSearch = network.networkName.contains(searchQuery, ignoreCase = true) ||
                network.code.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (selectedFilter) {
            "Active" -> network.isActive
            "Bidding" -> network.mediationMode.contains("Bidding", ignoreCase = true)
            "Waterfall" -> network.mediationMode.contains("Waterfall", ignoreCase = true)
            else -> true
        }
        matchesSearch && matchesFilter
    }

    Box(modifier = modifier.fillMaxSize().testTag("ad_networks_screen")) {
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
                            text = "AD EXCHANGES",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Connected Networks (${networks.size})",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Button(
                        onClick = { isAddingNetwork = true },
                        colors = ButtonDefaults.buttonColors(containerColor = BlackBoxEmerald),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("add_network_top_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color(0xFF070B12),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Network", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF070B12))
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
                        .testTag("network_search_field"),
                    placeholder = { Text("Search by network name or code...", fontSize = 13.sp, color = TextMuted) },
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

            // Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(listOf("All", "Active", "Bidding", "Waterfall")) { filter ->
                        val isSelected = selectedFilter == filter
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) BlackBoxCyan.copy(alpha = 0.2f) else BlackBoxSurfaceVariant,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, BlackBoxCyan) else null,
                            modifier = Modifier.clickable { selectedFilter = filter }
                        ) {
                            Text(
                                text = filter,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) BlackBoxCyan else TextSecondary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Network Cards
            items(filteredNetworks, key = { it.id }) { network ->
                NetworkItemCard(
                    network = network,
                    onToggle = { onToggleActive(network.id, it) },
                    onEdit = { editingNetwork = network }
                )
            }
        }
    }

    // Edit/Configure Network Dialog
    editingNetwork?.let { network ->
        NetworkConfigDialog(
            network = network,
            onDismiss = { editingNetwork = null },
            onSave = { updated ->
                onSaveNetwork(updated)
                editingNetwork = null
            },
            onDelete = {
                onDeleteNetwork(network)
                editingNetwork = null
            }
        )
    }

    // Add Network Dialog
    if (isAddingNetwork) {
        NetworkConfigDialog(
            network = AdNetworkEntity(
                networkName = "",
                code = "",
                appId = "",
                apiKey = "",
                mediationMode = "Unified Bidding",
                waterfallTier = 1,
                ecpmFloor = 5.0,
                isActive = true,
                sdkVersion = "1.0.0",
                latencyMs = 50,
                fillRate = 95.0,
                ecpm = 10.0,
                impressions = 0,
                clicks = 0,
                revenue = 0.0,
                colorHex = 0xFF06B6D4
            ),
            isNew = true,
            onDismiss = { isAddingNetwork = false },
            onSave = { created ->
                onSaveNetwork(created)
                isAddingNetwork = false
            },
            onDelete = {}
        )
    }
}

@Composable
private fun NetworkItemCard(
    network: AdNetworkEntity,
    onToggle: (Boolean) -> Unit,
    onEdit: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("network_card_${network.code}"),
        onClick = onEdit
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Monogram, Name, Mode, and Active Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    NetworkMonogram(name = network.networkName, colorHex = network.colorHex, size = 40.dp)
                    Column {
                        Text(
                            text = network.networkName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "v${network.sdkVersion}",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                            Box(
                                modifier = Modifier
                                    .size(3.dp)
                                    .background(TextMuted, CircleShape)
                            )
                            Text(
                                text = network.mediationMode,
                                fontSize = 11.sp,
                                color = BlackBoxCyan,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusChip(isActive = network.isActive, latencyMs = network.latencyMs)
                    Switch(
                        checked = network.isActive,
                        onCheckedChange = onToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BlackBoxEmeraldLight,
                            checkedTrackColor = Color(0xFF064E3B),
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = BlackBoxSurfaceVariant
                        ),
                        modifier = Modifier.testTag("switch_${network.code}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stats Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BlackBoxSurfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(vertical = 10.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("eCPM", fontSize = 11.sp, color = TextMuted)
                    Text("$${String.format(Locale.US, "%.2f", network.ecpm)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Column {
                    Text("Floor", fontSize = 11.sp, color = TextMuted)
                    Text("$${String.format(Locale.US, "%.2f", network.ecpmFloor)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BlackBoxAmber)
                }
                Column {
                    Text("Fill Rate", fontSize = 11.sp, color = TextMuted)
                    Text("${String.format(Locale.US, "%.1f", network.fillRate)}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BlackBoxEmeraldLight)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Revenue", fontSize = 11.sp, color = TextMuted)
                    Text("$${String.format(Locale.US, "%,.2f", network.revenue)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BlackBoxEmerald)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom config action hint
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "App ID: ${if (network.appId.length > 20) network.appId.take(18) + "..." else network.appId}",
                    fontSize = 11.sp,
                    color = TextMuted
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Configure",
                        tint = BlackBoxCyan,
                        modifier = Modifier.size(13.dp)
                    )
                    Text("Configure", fontSize = 11.sp, color = BlackBoxCyan, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun NetworkConfigDialog(
    network: AdNetworkEntity,
    isNew: Boolean = false,
    onDismiss: () -> Unit,
    onSave: (AdNetworkEntity) -> Unit,
    onDelete: () -> Unit
) {
    var name by remember { mutableStateOf(network.networkName) }
    var code by remember { mutableStateOf(network.code) }
    var appId by remember { mutableStateOf(network.appId) }
    var apiKey by remember { mutableStateOf(network.apiKey) }
    var floorStr by remember { mutableStateOf(network.ecpmFloor.toString()) }
    var mode by remember { mutableStateOf(network.mediationMode) }
    var isActive by remember { mutableStateOf(network.isActive) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BlackBoxSurface,
        title = {
            Text(
                text = if (isNew) "Add Ad Network" else "Configure ${network.networkName}",
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
                    onValueChange = {
                        name = it
                        if (isNew) code = it.lowercase(Locale.US).replace(" ", "_")
                    },
                    label = { Text("Network Name", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth().testTag("dialog_network_name"),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = BlackBoxEmerald,
                        unfocusedBorderColor = BlackBoxCardBorder
                    )
                )

                OutlinedTextField(
                    value = appId,
                    onValueChange = { appId = it },
                    label = { Text("App ID / Exchange Key", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth().testTag("dialog_network_app_id"),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = BlackBoxEmerald,
                        unfocusedBorderColor = BlackBoxCardBorder
                    )
                )

                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    label = { Text("Reporting API Key", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth().testTag("dialog_network_api_key"),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = BlackBoxEmerald,
                        unfocusedBorderColor = BlackBoxCardBorder
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = floorStr,
                        onValueChange = { floorStr = it },
                        label = { Text("Floor Price ($)", color = TextSecondary) },
                        modifier = Modifier.weight(1f).testTag("dialog_network_floor"),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = BlackBoxEmerald,
                            unfocusedBorderColor = BlackBoxCardBorder
                        )
                    )

                    // Mediation Mode Selector
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Mediation Mode", fontSize = 11.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = BlackBoxSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    mode = when (mode) {
                                        "Unified Bidding" -> "Waterfall Tier"
                                        "Waterfall Tier" -> "Hybrid"
                                        else -> "Unified Bidding"
                                    }
                                }
                                .padding(vertical = 12.dp, horizontal = 10.dp)
                        ) {
                            Text(mode, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BlackBoxCyan)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val floor = floorStr.toDoubleOrNull() ?: network.ecpmFloor
                    onSave(
                        network.copy(
                            networkName = name.ifBlank { "Custom Network" },
                            code = if (isNew) code.ifBlank { "custom" } else network.code,
                            appId = appId,
                            apiKey = apiKey,
                            ecpmFloor = floor,
                            mediationMode = mode,
                            isActive = isActive
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = BlackBoxEmerald),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("dialog_save_network_btn")
            ) {
                Text("Save Network", color = Color(0xFF070B12), fontWeight = FontWeight.Bold)
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
