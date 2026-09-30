package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ad_networks")
data class AdNetworkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val networkName: String,
    val code: String,
    val appId: String,
    val apiKey: String,
    val mediationMode: String, // "Unified Bidding", "Waterfall Tier", "Hybrid"
    val waterfallTier: Int, // 1 to 4
    val ecpmFloor: Double,
    val isActive: Boolean,
    val sdkVersion: String,
    val latencyMs: Int,
    val fillRate: Double,
    val ecpm: Double,
    val impressions: Long,
    val clicks: Long,
    val revenue: Double,
    val colorHex: Long
)

@Entity(tableName = "ad_units")
data class AdUnitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val adFormat: String, // "Rewarded", "Interstitial", "Banner", "App Open", "Native"
    val adUnitId: String,
    val primaryNetwork: String,
    val floorPrice: Double,
    val fillRate: Double,
    val ecpm: Double,
    val impressions: Long,
    val revenue: Double,
    val isActive: Boolean
)

@Entity(tableName = "daily_metrics")
data class DailyMetricEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val dateStr: String,
    val dayLabel: String,
    val revenue: Double,
    val impressions: Long,
    val clicks: Long,
    val ecpm: Double,
    val fillRate: Double
)

@Entity(tableName = "payouts")
data class PayoutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val payoutId: String,
    val amount: Double,
    val method: String, // "Bank Wire", "PayPal", "USDT TRC20", "Stripe"
    val status: String, // "Completed", "Processing", "Scheduled"
    val date: String,
    val destinationAccount: String
)

@Entity(tableName = "mediation_tiers")
data class MediationTierEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tierName: String,
    val orderIndex: Int,
    val floorPrice: Double,
    val networkCodes: String, // comma-separated code list
    val estimatedFill: Double
)

data class NetworkShare(
    val networkName: String,
    val code: String,
    val revenue: Double,
    val sharePercent: Float,
    val colorHex: Long
)

data class TelemetryAlert(
    val id: String,
    val title: String,
    val description: String,
    val type: AlertType,
    val timeAgo: String
)

enum class AlertType {
    SURGE,
    OPTIMIZATION,
    LATENCY,
    THRESHOLD
}
