package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.AdNetworkEntity
import com.example.data.model.AdUnitEntity
import com.example.data.model.AlertType
import com.example.data.model.DailyMetricEntity
import com.example.data.model.MediationTierEntity
import com.example.data.model.NetworkShare
import com.example.data.model.PayoutEntity
import com.example.data.model.TelemetryAlert
import com.example.data.repository.BlackBoxRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class TimeframeFilter(val label: String) {
    TODAY("Today"),
    YESTERDAY("Yesterday"),
    LAST_7D("7 Days"),
    LAST_30D("30 Days"),
    ALL_TIME("All Time")
}

data class DashboardUiState(
    val networks: List<AdNetworkEntity> = emptyList(),
    val adUnits: List<AdUnitEntity> = emptyList(),
    val dailyMetrics: List<DailyMetricEntity> = emptyList(),
    val payouts: List<PayoutEntity> = emptyList(),
    val mediationTiers: List<MediationTierEntity> = emptyList(),
    val selectedTimeframe: TimeframeFilter = TimeframeFilter.LAST_7D,
    val searchQuery: String = "",
    val isSyncing: Boolean = false,
    val optimizationNotification: String? = null,
    val alerts: List<TelemetryAlert> = listOf(
        TelemetryAlert("1", "eCPM Spike Detected", "Unity Ads Rewarded format increased by +18.4% over benchmark", AlertType.SURGE, "12m ago"),
        TelemetryAlert("2", "Waterfall Floor Auto-Tuned", "AppLovin MAX floor raised to $14.50 to reduce backfill latency", AlertType.OPTIMIZATION, "45m ago"),
        TelemetryAlert("3", "AdMob Fill Rate 99.2%", "Zero unfilled request dropouts detected in the last 4 hours", AlertType.THRESHOLD, "2h ago"),
        TelemetryAlert("4", "Low Latency Benchmark", "All unified bidding networks responded under 65ms avg latency", AlertType.LATENCY, "3h ago")
    )
) {
    val totalRevenue: Double
        get() = when (selectedTimeframe) {
            TimeframeFilter.TODAY -> networks.filter { it.isActive }.sumOf { it.revenue * 0.38 }
            TimeframeFilter.YESTERDAY -> networks.filter { it.isActive }.sumOf { it.revenue * 0.36 }
            TimeframeFilter.LAST_7D -> networks.filter { it.isActive }.sumOf { it.revenue }
            TimeframeFilter.LAST_30D -> networks.filter { it.isActive }.sumOf { it.revenue * 4.2 }
            TimeframeFilter.ALL_TIME -> networks.filter { it.isActive }.sumOf { it.revenue * 18.5 }
        }

    val totalImpressions: Long
        get() = when (selectedTimeframe) {
            TimeframeFilter.TODAY -> (networks.filter { it.isActive }.sumOf { it.impressions } * 0.38).toLong()
            TimeframeFilter.YESTERDAY -> (networks.filter { it.isActive }.sumOf { it.impressions } * 0.36).toLong()
            TimeframeFilter.LAST_7D -> networks.filter { it.isActive }.sumOf { it.impressions }
            TimeframeFilter.LAST_30D -> (networks.filter { it.isActive }.sumOf { it.impressions } * 4.2).toLong()
            TimeframeFilter.ALL_TIME -> (networks.filter { it.isActive }.sumOf { it.impressions } * 18.5).toLong()
        }

    val avgEcpm: Double
        get() {
            val imps = totalImpressions
            return if (imps > 0) (totalRevenue / imps) * 1000.0 else 0.0
        }

    val avgFillRate: Double
        get() {
            val active = networks.filter { it.isActive }
            return if (active.isNotEmpty()) active.map { it.fillRate }.average() else 0.0
        }

    val activeNetworksCount: Int
        get() = networks.count { it.isActive }

    val networkShares: List<NetworkShare>
        get() {
            val total = networks.filter { it.isActive }.sumOf { it.revenue }
            if (total <= 0.0) return emptyList()
            return networks.filter { it.isActive }.map {
                NetworkShare(
                    networkName = it.networkName,
                    code = it.code,
                    revenue = it.revenue,
                    sharePercent = ((it.revenue / total) * 100.0).toFloat(),
                    colorHex = it.colorHex
                )
            }.sortedByDescending { it.revenue }
        }
}

class BlackBoxViewModel(
    private val repository: BlackBoxRepository
) : ViewModel() {

    private val _selectedTimeframe = MutableStateFlow(TimeframeFilter.LAST_7D)
    private val _searchQuery = MutableStateFlow("")
    private val _isSyncing = MutableStateFlow(false)
    private val _optimizationNotification = MutableStateFlow<String?>(null)

    private data class DatabaseData(
        val networks: List<AdNetworkEntity> = emptyList(),
        val adUnits: List<AdUnitEntity> = emptyList(),
        val dailyMetrics: List<DailyMetricEntity> = emptyList(),
        val payouts: List<PayoutEntity> = emptyList(),
        val mediationTiers: List<MediationTierEntity> = emptyList()
    )

    private val dbDataFlow = combine(
        repository.allNetworks,
        repository.allAdUnits,
        repository.allDailyMetrics,
        repository.allPayouts,
        repository.allMediationTiers
    ) { networks, adUnits, dailyMetrics, payouts, tiers ->
        DatabaseData(networks, adUnits, dailyMetrics, payouts, tiers)
    }

    private data class ControlState(
        val timeframe: TimeframeFilter,
        val search: String,
        val syncing: Boolean,
        val notif: String?
    )

    private val controlFlow = combine(
        _selectedTimeframe,
        _searchQuery,
        _isSyncing,
        _optimizationNotification
    ) { timeframe, search, syncing, notif ->
        ControlState(timeframe, search, syncing, notif)
    }

    val uiState: StateFlow<DashboardUiState> = combine(
        dbDataFlow,
        controlFlow
    ) { db, ctrl ->
        DashboardUiState(
            networks = db.networks,
            adUnits = db.adUnits,
            dailyMetrics = db.dailyMetrics,
            payouts = db.payouts,
            mediationTiers = db.mediationTiers,
            selectedTimeframe = ctrl.timeframe,
            searchQuery = ctrl.search,
            isSyncing = ctrl.syncing,
            optimizationNotification = ctrl.notif
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )

    fun setTimeframe(timeframe: TimeframeFilter) {
        _selectedTimeframe.value = timeframe
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleNetwork(id: Int, isActive: Boolean) {
        viewModelScope.launch {
            repository.toggleNetwork(id, isActive)
        }
    }

    fun toggleAdUnit(id: Int, isActive: Boolean) {
        viewModelScope.launch {
            repository.toggleAdUnit(id, isActive)
        }
    }

    fun syncAllTelemetry() {
        viewModelScope.launch {
            _isSyncing.value = true
            val currentNets = uiState.value.networks
            repository.pingAllNetworks(currentNets)
            delay(1200) // smooth visual sync feedback
            _isSyncing.value = false
            _optimizationNotification.value = "Telemetry synchronized with all connected ad exchanges."
            delay(4000)
            _optimizationNotification.value = null
        }
    }

    fun runBlackBoxOptimization() {
        viewModelScope.launch {
            _isSyncing.value = true
            delay(1000)
            val lift = repository.optimizeWaterfall(uiState.value.mediationTiers, uiState.value.networks)
            _isSyncing.value = false
            _optimizationNotification.value = "BlackBox Engine optimized! Projected +$lift% yield lift."
            delay(4500)
            _optimizationNotification.value = null
        }
    }

    fun saveNetwork(network: AdNetworkEntity) {
        viewModelScope.launch {
            if (network.id == 0) {
                repository.insertNetwork(network)
            } else {
                repository.updateNetwork(network)
            }
        }
    }

    fun deleteNetwork(network: AdNetworkEntity) {
        viewModelScope.launch {
            repository.deleteNetwork(network)
        }
    }

    fun saveAdUnit(adUnit: AdUnitEntity) {
        viewModelScope.launch {
            if (adUnit.id == 0) {
                repository.insertAdUnit(adUnit)
            } else {
                repository.updateAdUnit(adUnit)
            }
        }
    }

    fun deleteAdUnit(adUnit: AdUnitEntity) {
        viewModelScope.launch {
            repository.deleteAdUnit(adUnit)
        }
    }

    fun requestPayout(amount: Double, method: String, account: String) {
        viewModelScope.launch {
            val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date())
            val newPayout = PayoutEntity(
                payoutId = "PAY-${System.currentTimeMillis() % 100000}",
                amount = amount,
                method = method,
                status = "Processing",
                date = dateStr,
                destinationAccount = account
            )
            repository.insertPayout(newPayout)
            _optimizationNotification.value = "Payout request for $$amount submitted successfully!"
            delay(4000)
            _optimizationNotification.value = null
        }
    }

    fun dismissNotification() {
        _optimizationNotification.value = null
    }
}

class BlackBoxViewModelFactory(
    private val repository: BlackBoxRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BlackBoxViewModel::class.java)) {
            return BlackBoxViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
