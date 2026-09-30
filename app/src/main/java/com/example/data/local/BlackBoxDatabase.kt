package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AdNetworkEntity
import com.example.data.model.AdUnitEntity
import com.example.data.model.DailyMetricEntity
import com.example.data.model.MediationTierEntity
import com.example.data.model.PayoutEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        AdNetworkEntity::class,
        AdUnitEntity::class,
        DailyMetricEntity::class,
        PayoutEntity::class,
        MediationTierEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class BlackBoxDatabase : RoomDatabase() {
    abstract fun blackBoxDao(): BlackBoxDao

    companion object {
        @Volatile
        private var INSTANCE: BlackBoxDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): BlackBoxDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BlackBoxDatabase::class.java,
                    "blackbox_earn_db"
                )
                    .addCallback(BlackBoxDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class BlackBoxDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.blackBoxDao())
                }
            }
        }

        suspend fun populateInitialData(dao: BlackBoxDao) {
            val networks = listOf(
                AdNetworkEntity(
                    networkName = "Google AdMob",
                    code = "admob",
                    appId = "ca-app-pub-841920349281~1938201",
                    apiKey = "AIzaSyC8_AdMobLiveKey_94829104928",
                    mediationMode = "Hybrid",
                    waterfallTier = 1,
                    ecpmFloor = 16.50,
                    isActive = true,
                    sdkVersion = "23.4.0",
                    latencyMs = 38,
                    fillRate = 99.2,
                    ecpm = 19.45,
                    impressions = 82400,
                    clicks = 2140,
                    revenue = 1602.68,
                    colorHex = 0xFFFBBF24
                ),
                AdNetworkEntity(
                    networkName = "AppLovin MAX",
                    code = "applovin",
                    appId = "applovin-sdk-key-8319-max",
                    apiKey = "al_max_live_948201948201",
                    mediationMode = "Unified Bidding",
                    waterfallTier = 1,
                    ecpmFloor = 14.00,
                    isActive = true,
                    sdkVersion = "13.0.1",
                    latencyMs = 45,
                    fillRate = 98.1,
                    ecpm = 18.90,
                    impressions = 64200,
                    clicks = 1820,
                    revenue = 1213.38,
                    colorHex = 0xFF0284C7
                ),
                AdNetworkEntity(
                    networkName = "Unity Ads",
                    code = "unity",
                    appId = "unity-game-id-5291048",
                    apiKey = "unity_api_secret_48291048",
                    mediationMode = "Unified Bidding",
                    waterfallTier = 1,
                    ecpmFloor = 12.50,
                    isActive = true,
                    sdkVersion = "4.12.2",
                    latencyMs = 62,
                    fillRate = 95.4,
                    ecpm = 16.80,
                    impressions = 39500,
                    clicks = 1150,
                    revenue = 663.60,
                    colorHex = 0xFF22C55E
                ),
                AdNetworkEntity(
                    networkName = "Meta Audience Network",
                    code = "meta",
                    appId = "meta-app-94829104820",
                    apiKey = "meta_system_user_token_9381029",
                    mediationMode = "Unified Bidding",
                    waterfallTier = 2,
                    ecpmFloor = 10.00,
                    isActive = true,
                    sdkVersion = "6.17.0",
                    latencyMs = 54,
                    fillRate = 96.0,
                    ecpm = 15.20,
                    impressions = 28100,
                    clicks = 890,
                    revenue = 427.12,
                    colorHex = 0xFF3B82F6
                ),
                AdNetworkEntity(
                    networkName = "ironSource LevelPlay",
                    code = "ironsource",
                    appId = "is-app-key-92841029",
                    apiKey = "is_rest_api_key_849201948",
                    mediationMode = "Waterfall Tier",
                    waterfallTier = 2,
                    ecpmFloor = 8.50,
                    isActive = true,
                    sdkVersion = "8.3.0",
                    latencyMs = 88,
                    fillRate = 92.3,
                    ecpm = 13.60,
                    impressions = 19400,
                    clicks = 490,
                    revenue = 263.84,
                    colorHex = 0xFFF97316
                ),
                AdNetworkEntity(
                    networkName = "Mintegral",
                    code = "mintegral",
                    appId = "m_app_84920",
                    apiKey = "mintegral_app_key_849201",
                    mediationMode = "Unified Bidding",
                    waterfallTier = 2,
                    ecpmFloor = 6.00,
                    isActive = true,
                    sdkVersion = "16.8.61",
                    latencyMs = 74,
                    fillRate = 93.8,
                    ecpm = 14.10,
                    impressions = 15200,
                    clicks = 380,
                    revenue = 214.32,
                    colorHex = 0xFFA855F7
                ),
                AdNetworkEntity(
                    networkName = "Liftoff Vungle",
                    code = "liftoff",
                    appId = "vungle_app_id_8392",
                    apiKey = "liftoff_reporting_token_8392",
                    mediationMode = "Waterfall Tier",
                    waterfallTier = 3,
                    ecpmFloor = 4.00,
                    isActive = true,
                    sdkVersion = "7.4.0",
                    latencyMs = 112,
                    fillRate = 89.5,
                    ecpm = 11.50,
                    impressions = 8900,
                    clicks = 210,
                    revenue = 102.35,
                    colorHex = 0xFFEF4444
                ),
                AdNetworkEntity(
                    networkName = "InMobi",
                    code = "inmobi",
                    appId = "inmobi_acc_839201948",
                    apiKey = "inmobi_api_key_84920194",
                    mediationMode = "Waterfall Tier",
                    waterfallTier = 3,
                    ecpmFloor = 3.00,
                    isActive = false,
                    sdkVersion = "10.7.0",
                    latencyMs = 145,
                    fillRate = 84.2,
                    ecpm = 9.80,
                    impressions = 3100,
                    clicks = 65,
                    revenue = 30.38,
                    colorHex = 0xFF14B8A6
                )
            )
            dao.insertNetworks(networks)

            val adUnits = listOf(
                AdUnitEntity(
                    name = "Double Coins Rewarded",
                    adFormat = "Rewarded",
                    adUnitId = "ca-app-pub-841920/reward-001",
                    primaryNetwork = "Unity Ads",
                    floorPrice = 24.00,
                    fillRate = 98.6,
                    ecpm = 29.40,
                    impressions = 42100,
                    revenue = 1237.74,
                    isActive = true
                ),
                AdUnitEntity(
                    name = "Level Complete Interstitial",
                    adFormat = "Interstitial",
                    adUnitId = "applovin-zone-int-02",
                    primaryNetwork = "AppLovin MAX",
                    floorPrice = 15.00,
                    fillRate = 97.9,
                    ecpm = 17.80,
                    impressions = 61500,
                    revenue = 1094.70,
                    isActive = true
                ),
                AdUnitEntity(
                    name = "Main Menu Bottom Banner",
                    adFormat = "Banner",
                    adUnitId = "ca-app-pub-841920/banner-003",
                    primaryNetwork = "Google AdMob",
                    floorPrice = 3.50,
                    fillRate = 99.8,
                    ecpm = 4.20,
                    impressions = 112000,
                    revenue = 470.40,
                    isActive = true
                ),
                AdUnitEntity(
                    name = "App Launch Open Ad",
                    adFormat = "App Open",
                    adUnitId = "ca-app-pub-841920/appopen-004",
                    primaryNetwork = "Google AdMob",
                    floorPrice = 20.00,
                    fillRate = 98.2,
                    ecpm = 23.50,
                    impressions = 18400,
                    revenue = 432.40,
                    isActive = true
                ),
                AdUnitEntity(
                    name = "Mystery Chest Rewarded",
                    adFormat = "Rewarded",
                    adUnitId = "meta-placement-rew-05",
                    primaryNetwork = "Meta Audience Network",
                    floorPrice = 22.00,
                    fillRate = 96.4,
                    ecpm = 26.10,
                    impressions = 24800,
                    revenue = 647.28,
                    isActive = true
                ),
                AdUnitEntity(
                    name = "Feed Stream Sponsored Card",
                    adFormat = "Native",
                    adUnitId = "mintegral-unit-nat-06",
                    primaryNetwork = "Mintegral",
                    floorPrice = 5.00,
                    fillRate = 94.1,
                    ecpm = 6.80,
                    impressions = 48200,
                    revenue = 327.76,
                    isActive = true
                )
            )
            dao.insertAdUnits(adUnits)

            val metrics = listOf(
                DailyMetricEntity(dateStr = "2026-09-24", dayLabel = "Thu", revenue = 3120.40, impressions = 245000, clicks = 6120, ecpm = 12.73, fillRate = 96.8),
                DailyMetricEntity(dateStr = "2026-09-25", dayLabel = "Fri", revenue = 3410.15, impressions = 252000, clicks = 6480, ecpm = 13.53, fillRate = 97.1),
                DailyMetricEntity(dateStr = "2026-09-26", dayLabel = "Sat", revenue = 3780.80, impressions = 264000, clicks = 6920, ecpm = 14.32, fillRate = 97.8),
                DailyMetricEntity(dateStr = "2026-09-27", dayLabel = "Sun", revenue = 4150.50, impressions = 278000, clicks = 7350, ecpm = 14.93, fillRate = 98.2),
                DailyMetricEntity(dateStr = "2026-09-28", dayLabel = "Mon", revenue = 3980.90, impressions = 269000, clicks = 7050, ecpm = 14.79, fillRate = 98.0),
                DailyMetricEntity(dateStr = "2026-09-29", dayLabel = "Tue", revenue = 4350.25, impressions = 281000, clicks = 7620, ecpm = 15.48, fillRate = 98.6),
                DailyMetricEntity(dateStr = "2026-09-30", dayLabel = "Today", revenue = 4517.87, impressions = 261800, clicks = 7150, ecpm = 17.25, fillRate = 98.8)
            )
            dao.insertDailyMetrics(metrics)

            val mediationTiers = listOf(
                MediationTierEntity(
                    tierName = "Tier 0: Unified Header Bidding",
                    orderIndex = 0,
                    floorPrice = 0.0,
                    networkCodes = "admob,applovin,meta,mintegral",
                    estimatedFill = 68.5
                ),
                MediationTierEntity(
                    tierName = "Tier 1: Premium High Floor ($20.00+)",
                    orderIndex = 1,
                    floorPrice = 20.0,
                    networkCodes = "unity,admob",
                    estimatedFill = 18.2
                ),
                MediationTierEntity(
                    tierName = "Tier 2: Standard Floor ($12.00+)",
                    orderIndex = 2,
                    floorPrice = 12.0,
                    networkCodes = "applovin,meta,ironsource",
                    estimatedFill = 8.4
                ),
                MediationTierEntity(
                    tierName = "Tier 3: Global Floor Fill ($4.00+)",
                    orderIndex = 3,
                    floorPrice = 4.0,
                    networkCodes = "mintegral,liftoff",
                    estimatedFill = 3.9
                ),
                MediationTierEntity(
                    tierName = "Tier 4: Backfill & House Ads",
                    orderIndex = 4,
                    floorPrice = 0.0,
                    networkCodes = "house_ads",
                    estimatedFill = 1.0
                )
            )
            dao.insertMediationTiers(mediationTiers)

            val payouts = listOf(
                PayoutEntity(
                    payoutId = "PAY-202609-01",
                    amount = 48250.00,
                    method = "Bank Wire (SWIFT)",
                    status = "Completed",
                    date = "Sep 15, 2026",
                    destinationAccount = "Chase Tech Holdings •••• 9104"
                ),
                PayoutEntity(
                    payoutId = "PAY-202608-01",
                    amount = 42180.00,
                    method = "Bank Wire (SWIFT)",
                    status = "Completed",
                    date = "Aug 15, 2026",
                    destinationAccount = "Chase Tech Holdings •••• 9104"
                ),
                PayoutEntity(
                    payoutId = "PAY-202607-01",
                    amount = 39450.00,
                    method = "USDT (TRC20)",
                    status = "Completed",
                    date = "Jul 15, 2026",
                    destinationAccount = "0x71A...9F2b"
                ),
                PayoutEntity(
                    payoutId = "PAY-202610-PENDING",
                    amount = 52410.00,
                    method = "Bank Wire (SWIFT)",
                    status = "Scheduled",
                    date = "Oct 15, 2026",
                    destinationAccount = "Chase Tech Holdings •••• 9104"
                )
            )
            dao.insertPayouts(payouts)
        }
    }
}
