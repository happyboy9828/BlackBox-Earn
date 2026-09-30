package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AdNetworkEntity
import com.example.data.model.AdUnitEntity
import com.example.data.model.DailyMetricEntity
import com.example.data.model.MediationTierEntity
import com.example.data.model.PayoutEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BlackBoxDao {

    // --- Ad Networks ---
    @Query("SELECT * FROM ad_networks ORDER BY revenue DESC")
    fun getAllNetworks(): Flow<List<AdNetworkEntity>>

    @Query("SELECT * FROM ad_networks WHERE id = :id")
    suspend fun getNetworkById(id: Int): AdNetworkEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNetwork(network: AdNetworkEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNetworks(networks: List<AdNetworkEntity>)

    @Update
    suspend fun updateNetwork(network: AdNetworkEntity)

    @Delete
    suspend fun deleteNetwork(network: AdNetworkEntity)

    @Query("UPDATE ad_networks SET isActive = :isActive WHERE id = :id")
    suspend fun toggleNetworkActive(id: Int, isActive: Boolean)

    @Query("UPDATE ad_networks SET latencyMs = :latency WHERE id = :id")
    suspend fun updateNetworkLatency(id: Int, latency: Int)

    // --- Ad Units ---
    @Query("SELECT * FROM ad_units ORDER BY revenue DESC")
    fun getAllAdUnits(): Flow<List<AdUnitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdUnit(adUnit: AdUnitEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdUnits(adUnits: List<AdUnitEntity>)

    @Update
    suspend fun updateAdUnit(adUnit: AdUnitEntity)

    @Delete
    suspend fun deleteAdUnit(adUnit: AdUnitEntity)

    @Query("UPDATE ad_units SET isActive = :isActive WHERE id = :id")
    suspend fun toggleAdUnitActive(id: Int, isActive: Boolean)

    // --- Daily Metrics ---
    @Query("SELECT * FROM daily_metrics ORDER BY id ASC")
    fun getAllDailyMetrics(): Flow<List<DailyMetricEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyMetrics(metrics: List<DailyMetricEntity>)

    // --- Payouts ---
    @Query("SELECT * FROM payouts ORDER BY id DESC")
    fun getAllPayouts(): Flow<List<PayoutEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayout(payout: PayoutEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayouts(payouts: List<PayoutEntity>)

    // --- Mediation Tiers ---
    @Query("SELECT * FROM mediation_tiers ORDER BY orderIndex ASC")
    fun getAllMediationTiers(): Flow<List<MediationTierEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediationTiers(tiers: List<MediationTierEntity>)

    @Update
    suspend fun updateMediationTier(tier: MediationTierEntity)

    @Query("SELECT COUNT(*) FROM ad_networks")
    suspend fun getNetworkCount(): Int
}
