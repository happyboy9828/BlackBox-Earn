package com.example.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.Room.PublisherStatEntity
import com.example.data.security.PinManager
import com.example.data.security.PinVerifyResult
import com.example.data.security.SecureStorageManager
import com.example.data.security.TokenStatus
import com.example.domain.model.CombinedBalanceSummary
import com.example.domain.model.DailyCombinedRow
import com.example.domain.model.DomainOverviewModel
import com.example.domain.model.GroupedStat
import com.example.domain.model.NetworkTodaySummary
import com.example.domain.repository.PublisherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PubDashUiState(
    val isUnlocked: Boolean = false,
    val isPinConfigured: Boolean = false,
    val isVaultUnlocked: Boolean = false,
    val balanceSummary: CombinedBalanceSummary = CombinedBalanceSummary(
        totalCombinedBalance = 0.0,
        totalToday = 0.0,
        totalYesterday = 0.0,
        adsterraToday = NetworkTodaySummary("Adsterra", 0.0, 0, 0, 0.0),
        monetagToday = NetworkTodaySummary("Monetag", 0.0, 0, 0, 0.0),
        adsterraLifetime = 0.0,
        monetagLifetime = 0.0,
        totalWithdrawals = 0.0
    ),
    val dailyRows: List<DailyCombinedRow> = emptyList(),
    val allStats: List<PublisherStatEntity> = emptyList(),
    val connectedDomains: List<DomainOverviewModel> = emptyList(),
    val selectedSystemFilter: String = "ALL", // "ALL", "ADSTERRA", "MONETAG", "VERCEL"
    val isRefreshing: Boolean = false,
    val syncMessage: String? = null,
    val selectedFilter: String = "7D", // "7D", "30D", "90D"
    val selectedGroupBy: String = "Date", // "Date", "Domain", "Country"
    val adsterraToken: String = "",
    val monetagToken: String = "",
    val monetagWithdrawals: Double = 0.0,
    val vercelToken: String = "",
    val adsterraStatus: TokenStatus = TokenStatus.NOT_SET,
    val monetagStatus: TokenStatus = TokenStatus.NOT_SET,
    val vercelStatus: TokenStatus = TokenStatus.NOT_SET,
    val isTestingAdsterra: Boolean = false,
    val isTestingMonetag: Boolean = false,
    val isTestingVercel: Boolean = false,
    val isBiometricEnabled: Boolean = false,
    val isBiometricAvailable: Boolean = false,
    val lockTimeoutMs: Long = 60_000L,
    val appearance: String = "Dark"
) {
    val filteredDailyRows: List<DailyCombinedRow>
        get() {
            val limit = when (selectedFilter) {
                "7D" -> 7
                "30D" -> 30
                "90D" -> 90
                else -> 7
            }
            return dailyRows.take(limit)
        }

    val filteredConnectedDomains: List<DomainOverviewModel>
        get() {
            return when (selectedSystemFilter) {
                "ADSTERRA" -> connectedDomains.filter { it.connectedSystems.any { s -> s.contains("ADSTERRA", ignoreCase = true) } }
                "MONETAG" -> connectedDomains.filter { it.connectedSystems.any { s -> s.contains("MONETAG", ignoreCase = true) } }
                "VERCEL" -> connectedDomains.filter { it.connectedSystems.any { s -> s.contains("VERCEL", ignoreCase = true) } }
                else -> connectedDomains
            }
        }

    val groupedStats: List<GroupedStat>
        get() {
            val limit = when (selectedFilter) {
                "7D" -> 7
                "30D" -> 30
                "90D" -> 90
                else -> 7
            }
            val filtered = allStats.take(limit * 2)

            return when (selectedGroupBy) {
                "Domain" -> {
                    filtered.groupBy { it.domain }.map { (domain, items) ->
                        val adRev = items.filter { it.network == "ADSTERRA" }.sumOf { it.revenue }
                        val monRev = items.filter { it.network == "MONETAG" }.sumOf { it.revenue }
                        val imps = items.sumOf { it.impressions }
                        val totRev = adRev + monRev
                        val cpm = if (imps > 0) (totRev / imps) * 1000.0 else 0.0
                        GroupedStat(domain, adRev, monRev, totRev, imps, cpm)
                    }.sortedByDescending { it.totalRevenue }
                }
                "Country" -> {
                    filtered.groupBy { it.country }.map { (country, items) ->
                        val adRev = items.filter { it.network == "ADSTERRA" }.sumOf { it.revenue }
                        val monRev = items.filter { it.network == "MONETAG" }.sumOf { it.revenue }
                        val imps = items.sumOf { it.impressions }
                        val totRev = adRev + monRev
                        val cpm = if (imps > 0) (totRev / imps) * 1000.0 else 0.0
                        GroupedStat(country, adRev, monRev, totRev, imps, cpm)
                    }.sortedByDescending { it.totalRevenue }
                }
                else -> { // Date
                    filteredDailyRows.map { row ->
                        GroupedStat(row.date, row.adsterraRevenue, row.monetagRevenue, row.totalRevenue, row.totalImpressions, row.combinedCpm)
                    }
                }
            }
        }
}

class PublisherViewModel(
    private val repository: PublisherRepository,
    private val pinManager: PinManager,
    private val secureStorage: SecureStorageManager
) : ViewModel() {

    private val _isUnlocked = MutableStateFlow(false)
    private val _isPinConfigured = MutableStateFlow(pinManager.isPinConfigured())
    private val _isVaultUnlocked = MutableStateFlow(false)
    private val _isRefreshing = MutableStateFlow(false)
    private val _syncMessage = MutableStateFlow<String?>(null)
    private val _selectedFilter = MutableStateFlow("7D")
    private val _selectedGroupBy = MutableStateFlow("Date")
    private val _isTestingAdsterra = MutableStateFlow(false)
    private val _isTestingMonetag = MutableStateFlow(false)
    private val _isTestingVercel = MutableStateFlow(false)
    private val _selectedSystemFilter = MutableStateFlow("ALL")
    private val _appearance = MutableStateFlow("Dark")

    private val _vaultData = MutableStateFlow(
        VaultState(
            adsterraToken = secureStorage.getAdsterraToken(),
            monetagToken = secureStorage.getMonetagToken(),
            monetagWithdrawals = secureStorage.getMonetagWithdrawals(),
            vercelToken = secureStorage.getVercelToken(),
            adsterraStatus = secureStorage.getAdsterraStatus(),
            monetagStatus = secureStorage.getMonetagStatus(),
            vercelStatus = secureStorage.getVercelStatus()
        )
    )

    private data class VaultState(
        val adsterraToken: String,
        val monetagToken: String,
        val monetagWithdrawals: Double,
        val vercelToken: String,
        val adsterraStatus: TokenStatus,
        val monetagStatus: TokenStatus,
        val vercelStatus: TokenStatus
    )

    private val _securityState = MutableStateFlow(
        SecurityPrefs(
            biometricEnabled = pinManager.isBiometricEnabled(),
            biometricAvailable = pinManager.isBiometricAvailable(),
            lockTimeoutMs = pinManager.getLockTimeout()
        )
    )

    private data class SecurityPrefs(
        val biometricEnabled: Boolean,
        val biometricAvailable: Boolean,
        val lockTimeoutMs: Long
    )

    val uiState: StateFlow<PubDashUiState> = combine(
        repository.balanceSummary,
        repository.combinedDailyRows,
        repository.allStats,
        repository.connectedDomains,
        _selectedSystemFilter,
        _isUnlocked,
        _isPinConfigured,
        _isVaultUnlocked,
        _isRefreshing,
        _syncMessage,
        _selectedFilter,
        _selectedGroupBy,
        _vaultData,
        _securityState,
        _isTestingAdsterra,
        _isTestingMonetag,
        _isTestingVercel,
        _appearance
    ) { args: Array<Any?> ->
        val balance = args[0] as CombinedBalanceSummary
        val daily = args[1] as List<DailyCombinedRow>
        val stats = args[2] as List<PublisherStatEntity>
        @Suppress("UNCHECKED_CAST")
        val domains = args[3] as List<DomainOverviewModel>
        val sysFilter = args[4] as String
        val unlocked = args[5] as Boolean
        val pinConfigured = args[6] as Boolean
        val vaultUnlocked = args[7] as Boolean
        val refreshing = args[8] as Boolean
        val syncMsg = args[9] as String?
        val filter = args[10] as String
        val groupBy = args[11] as String
        val vault = args[12] as VaultState
        val sec = args[13] as SecurityPrefs
        val testAd = args[14] as Boolean
        val testMon = args[15] as Boolean
        val testVer = args[16] as Boolean
        val app = args[17] as String

        PubDashUiState(
            isUnlocked = unlocked,
            isPinConfigured = pinConfigured,
            isVaultUnlocked = vaultUnlocked,
            balanceSummary = balance,
            dailyRows = daily,
            allStats = stats,
            connectedDomains = domains,
            selectedSystemFilter = sysFilter,
            isRefreshing = refreshing,
            syncMessage = syncMsg,
            selectedFilter = filter,
            selectedGroupBy = groupBy,
            adsterraToken = vault.adsterraToken,
            monetagToken = vault.monetagToken,
            monetagWithdrawals = vault.monetagWithdrawals,
            vercelToken = vault.vercelToken,
            adsterraStatus = vault.adsterraStatus,
            monetagStatus = vault.monetagStatus,
            vercelStatus = vault.vercelStatus,
            isTestingAdsterra = testAd,
            isTestingMonetag = testMon,
            isTestingVercel = testVer,
            isBiometricEnabled = sec.biometricEnabled,
            isBiometricAvailable = sec.biometricAvailable,
            lockTimeoutMs = sec.lockTimeoutMs,
            appearance = app
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PubDashUiState()
    )

    fun verifyPin(pin: String): PinVerifyResult {
        val result = pinManager.verifyPin(pin)
        if (result is PinVerifyResult.Success) {
            _isUnlocked.value = true
        }
        return result
    }

    fun setupPin(pin: String, password: String?): Boolean {
        val success = pinManager.setupPin(pin, password)
        if (success) {
            _isPinConfigured.value = true
            _isUnlocked.value = true
        }
        return success
    }

    fun unlockWithBiometrics() {
        if (pinManager.isBiometricEnabled()) {
            _isUnlocked.value = true
        }
    }

    fun lockApp() {
        _isUnlocked.value = false
        _isVaultUnlocked.value = false
    }

    fun unlockVault(pin: String): Boolean {
        val result = pinManager.verifyPin(pin)
        val success = result is PinVerifyResult.Success
        if (success) {
            _isVaultUnlocked.value = true
        }
        return success
    }

    fun lockVault() {
        _isVaultUnlocked.value = false
    }

    fun saveVaultSecrets(adsterra: String, monetag: String, withdrawals: Double, vercel: String = secureStorage.getVercelToken()) {
        secureStorage.saveAdsterraToken(adsterra)
        secureStorage.saveMonetagToken(monetag)
        secureStorage.saveMonetagWithdrawals(withdrawals)
        secureStorage.saveVercelToken(vercel)
        _vaultData.value = VaultState(
            adsterraToken = adsterra,
            monetagToken = monetag,
            monetagWithdrawals = withdrawals,
            vercelToken = vercel,
            adsterraStatus = secureStorage.getAdsterraStatus(),
            monetagStatus = secureStorage.getMonetagStatus(),
            vercelStatus = secureStorage.getVercelStatus()
        )
        refreshPublisherData()
    }

    fun testAdsterraToken(token: String) {
        viewModelScope.launch {
            _isTestingAdsterra.value = true
            val status = repository.testAdsterraConnection(token)
            _isTestingAdsterra.value = false
            _vaultData.value = _vaultData.value.copy(adsterraStatus = status)
        }
    }

    fun testMonetagToken(token: String) {
        viewModelScope.launch {
            _isTestingMonetag.value = true
            val status = repository.testMonetagConnection(token)
            _isTestingMonetag.value = false
            _vaultData.value = _vaultData.value.copy(monetagStatus = status)
        }
    }

    fun testVercelToken(token: String) {
        viewModelScope.launch {
            _isTestingVercel.value = true
            val status = repository.testVercelConnection(token)
            _isTestingVercel.value = false
            _vaultData.value = _vaultData.value.copy(vercelStatus = status)
        }
    }

    fun deleteAdsterraToken() {
        secureStorage.deleteAdsterraToken()
        _vaultData.value = _vaultData.value.copy(
            adsterraToken = "",
            adsterraStatus = TokenStatus.NOT_SET
        )
    }

    fun deleteMonetagToken() {
        secureStorage.deleteMonetagToken()
        _vaultData.value = _vaultData.value.copy(
            monetagToken = "",
            monetagStatus = TokenStatus.NOT_SET
        )
    }

    fun deleteVercelToken() {
        secureStorage.deleteVercelToken()
        _vaultData.value = _vaultData.value.copy(
            vercelToken = "",
            vercelStatus = TokenStatus.NOT_SET
        )
    }

    fun setSystemFilter(filter: String) {
        _selectedSystemFilter.value = filter
    }

    fun refreshPublisherData() {
        viewModelScope.launch {
            _isRefreshing.value = true
            val result = repository.refreshData(daysBack = 3)
            _isRefreshing.value = false
            _syncMessage.value = result.getOrNull() ?: "Sync complete"
            _vaultData.value = _vaultData.value.copy(
                adsterraStatus = secureStorage.getAdsterraStatus(),
                monetagStatus = secureStorage.getMonetagStatus(),
                vercelStatus = secureStorage.getVercelStatus()
            )
        }
    }

    fun dismissSyncMessage() {
        _syncMessage.value = null
    }

    fun setFilter(filter: String) {
        _selectedFilter.value = filter
    }

    fun setGroupBy(groupBy: String) {
        _selectedGroupBy.value = groupBy
    }

    fun setBiometric(enabled: Boolean) {
        pinManager.setBiometricEnabled(enabled)
        _securityState.value = _securityState.value.copy(biometricEnabled = enabled)
    }

    fun setLockTimeout(timeout: Long) {
        pinManager.setLockTimeout(timeout)
        _securityState.value = _securityState.value.copy(lockTimeoutMs = timeout)
    }

    fun changePin(oldPin: String, newPin: String): Boolean {
        return pinManager.changePin(oldPin, newPin)
    }

    fun resetAllData(oldPin: String): Boolean {
        val success = pinManager.resetAllSecurity(oldPin)
        if (success) {
            secureStorage.clearAllSecrets()
            viewModelScope.launch {
                repository.clearCache()
            }
            _isPinConfigured.value = false
            _isUnlocked.value = false
            _isVaultUnlocked.value = false
        }
        return success
    }

    fun clearCache() {
        viewModelScope.launch {
            repository.clearCache()
        }
    }

    fun setAppearance(mode: String) {
        _appearance.value = mode
    }
}

class PublisherViewModelFactory(
    private val repository: PublisherRepository,
    private val pinManager: PinManager,
    private val secureStorage: SecureStorageManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PublisherViewModel::class.java)) {
            return PublisherViewModel(repository, pinManager, secureStorage) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
