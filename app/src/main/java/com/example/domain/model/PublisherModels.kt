package com.example.domain.model

data class DailyCombinedRow(
    val date: String,
    val adsterraRevenue: Double,
    val adsterraImpressions: Long,
    val adsterraCpm: Double,
    val monetagRevenue: Double,
    val monetagImpressions: Long,
    val monetagCpm: Double,
    val totalRevenue: Double,
    val totalImpressions: Long,
    val combinedCpm: Double
)

data class NetworkTodaySummary(
    val network: String,
    val revenue: Double,
    val impressions: Long,
    val clicks: Long,
    val cpm: Double
)

data class CombinedBalanceSummary(
    val totalCombinedBalance: Double,
    val totalToday: Double,
    val totalYesterday: Double,
    val adsterraToday: NetworkTodaySummary,
    val monetagToday: NetworkTodaySummary,
    val adsterraLifetime: Double,
    val monetagLifetime: Double,
    val totalWithdrawals: Double
)

data class GroupedStat(
    val groupKey: String, // Date, Domain, or Country
    val adsterraRevenue: Double,
    val monetagRevenue: Double,
    val totalRevenue: Double,
    val impressions: Long,
    val cpm: Double
)

data class DomainOverviewModel(
    val domain: String,
    val connectedSystems: List<String>, // listOf("Adsterra", "Monetag", "Vercel")
    val usersTraffic: Long,             // Users / Visitors from Vercel
    val pageViews: Long,                // Page views from Vercel
    val bounceRate: Double,             // % bounce rate (e.g. 34.2%)
    val adsterraImpressions: Long = 0,
    val adsterraClicks: Long = 0,
    val adsterraRevenue: Double = 0.0,
    val monetagImpressions: Long = 0,
    val monetagClicks: Long = 0,
    val monetagRevenue: Double = 0.0,
    val totalRevenue: Double = 0.0,
    val totalImpressions: Long = 0,
    val totalClicks: Long = 0,
    val blendedCpm: Double = 0.0,
    val ctr: Double = 0.0,
    val status: String = "Active"
)

