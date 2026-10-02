package com.example.data.local.Room

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Entity(tableName = "publisher_stats")
data class PublisherStatEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val network: String, // "ADSTERRA" or "MONETAG"
    val domain: String,
    val country: String = "ALL",
    val impressions: Long,
    val clicks: Long,
    val revenue: Double,
    val cpm: Double,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "connected_domains")
data class ConnectedDomainEntity(
    @PrimaryKey
    val domain: String,
    val connectedSystems: String, // comma-separated e.g. "ADSTERRA,MONETAG,VERCEL"
    val usersCount: Long,         // Traffic / Visitors from Vercel
    val pageViews: Long,          // Views from Vercel / analytics
    val bounceRate: Double,       // % bounce rate from Vercel
    val adsterraImpressions: Long = 0,
    val adsterraClicks: Long = 0,
    val adsterraRevenue: Double = 0.0,
    val monetagImpressions: Long = 0,
    val monetagClicks: Long = 0,
    val monetagRevenue: Double = 0.0,
    val totalRevenue: Double = 0.0,
    val totalImpressions: Long = 0,
    val totalClicks: Long = 0,
    val status: String = "ACTIVE",
    val framework: String = "Next.js",
    val updatedAt: Long = System.currentTimeMillis()
)

@Dao
interface PublisherDao {
    @Query("SELECT * FROM publisher_stats ORDER BY date DESC")
    fun getAllStats(): Flow<List<PublisherStatEntity>>

    @Query("SELECT * FROM publisher_stats WHERE network = :network ORDER BY date DESC")
    fun getStatsByNetwork(network: String): Flow<List<PublisherStatEntity>>

    @Query("SELECT * FROM publisher_stats WHERE date = :date")
    suspend fun getStatsForDate(date: String): List<PublisherStatEntity>

    @Query("SELECT * FROM publisher_stats WHERE date >= :startDate ORDER BY date ASC")
    fun getStatsFromDate(startDate: String): Flow<List<PublisherStatEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStats(stats: List<PublisherStatEntity>)

    @Query("DELETE FROM publisher_stats WHERE network = :network AND date = :date")
    suspend fun deleteStatsForNetworkDate(network: String, date: String)

    @Query("DELETE FROM publisher_stats")
    suspend fun clearAllStats()

    @Query("SELECT COUNT(*) FROM publisher_stats")
    suspend fun getStatsCount(): Int

    @Query("SELECT * FROM connected_domains ORDER BY totalRevenue DESC")
    fun getAllConnectedDomains(): Flow<List<ConnectedDomainEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConnectedDomains(domains: List<ConnectedDomainEntity>)

    @Query("DELETE FROM connected_domains")
    suspend fun clearConnectedDomains()
}

@Database(entities = [PublisherStatEntity::class, ConnectedDomainEntity::class], version = 2, exportSchema = false)
abstract class PublisherDatabase : RoomDatabase() {
    abstract fun publisherDao(): PublisherDao

    companion object {
        @Volatile
        private var INSTANCE: PublisherDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): PublisherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PublisherDatabase::class.java,
                    "publisher_earnings_db"
                )
                    .addCallback(PublisherDatabaseCallback(context.applicationContext, scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class PublisherDatabaseCallback(
        private val context: Context,
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialCache(database.publisherDao())
                }
            }
        }

        suspend fun populateInitialCache(dao: PublisherDao) {
            // Do NOT populate dummy data if any token is configured
            val secureStorage = com.example.data.security.SecureStorageManager(context)
            if (secureStorage.hasAnyToken()) {
                return
            }
            val stats = listOf(
                // Adsterra historical cache (last 7 days)
                PublisherStatEntity(date = "2026-09-24", network = "ADSTERRA", domain = "techpulse.io", impressions = 42100, clicks = 890, revenue = 210.50, cpm = 5.00),
                PublisherStatEntity(date = "2026-09-25", network = "ADSTERRA", domain = "techpulse.io", impressions = 45200, clicks = 920, revenue = 235.04, cpm = 5.20),
                PublisherStatEntity(date = "2026-09-26", network = "ADSTERRA", domain = "techpulse.io", impressions = 48900, clicks = 1040, revenue = 268.95, cpm = 5.50),
                PublisherStatEntity(date = "2026-09-27", network = "ADSTERRA", domain = "techpulse.io", impressions = 52300, clicks = 1110, revenue = 292.88, cpm = 5.60),
                PublisherStatEntity(date = "2026-09-28", network = "ADSTERRA", domain = "techpulse.io", impressions = 50100, clicks = 1060, revenue = 275.55, cpm = 5.50),
                PublisherStatEntity(date = "2026-09-29", network = "ADSTERRA", domain = "techpulse.io", impressions = 54200, clicks = 1180, revenue = 314.36, cpm = 5.80),
                PublisherStatEntity(date = "2026-09-30", network = "ADSTERRA", domain = "techpulse.io", impressions = 38400, clicks = 820, revenue = 241.92, cpm = 6.30),

                // Monetag historical cache (last 7 days)
                PublisherStatEntity(date = "2026-09-24", network = "MONETAG", domain = "techpulse.io", impressions = 35200, clicks = 610, revenue = 183.04, cpm = 5.20),
                PublisherStatEntity(date = "2026-09-25", network = "MONETAG", domain = "techpulse.io", impressions = 38400, clicks = 680, revenue = 207.36, cpm = 5.40),
                PublisherStatEntity(date = "2026-09-26", network = "MONETAG", domain = "techpulse.io", impressions = 41200, clicks = 740, revenue = 234.84, cpm = 5.70),
                PublisherStatEntity(date = "2026-09-27", network = "MONETAG", domain = "techpulse.io", impressions = 44500, clicks = 810, revenue = 258.10, cpm = 5.80),
                PublisherStatEntity(date = "2026-09-28", network = "MONETAG", domain = "techpulse.io", impressions = 42800, clicks = 760, revenue = 243.96, cpm = 5.70),
                PublisherStatEntity(date = "2026-09-29", network = "MONETAG", domain = "techpulse.io", impressions = 46900, clicks = 850, revenue = 286.09, cpm = 6.10),
                PublisherStatEntity(date = "2026-09-30", network = "MONETAG", domain = "techpulse.io", impressions = 34100, clicks = 620, revenue = 221.65, cpm = 6.50)
            )
            dao.insertStats(stats)

            val initialDomains = listOf(
                ConnectedDomainEntity(
                    domain = "techpulse.io",
                    connectedSystems = "ADSTERRA,MONETAG,VERCEL",
                    usersCount = 14850,
                    pageViews = 48200,
                    bounceRate = 34.5,
                    adsterraImpressions = 38400,
                    adsterraClicks = 820,
                    adsterraRevenue = 241.92,
                    monetagImpressions = 34100,
                    monetagClicks = 620,
                    monetagRevenue = 221.65,
                    totalRevenue = 463.57,
                    totalImpressions = 72500,
                    totalClicks = 1440,
                    framework = "Next.js"
                ),
                ConnectedDomainEntity(
                    domain = "streamzone.app",
                    connectedSystems = "ADSTERRA,MONETAG,VERCEL",
                    usersCount = 9200,
                    pageViews = 31400,
                    bounceRate = 29.8,
                    adsterraImpressions = 26500,
                    adsterraClicks = 590,
                    adsterraRevenue = 172.25,
                    monetagImpressions = 22400,
                    monetagClicks = 480,
                    monetagRevenue = 154.56,
                    totalRevenue = 326.81,
                    totalImpressions = 48900,
                    totalClicks = 1070,
                    framework = "Remix"
                ),
                ConnectedDomainEntity(
                    domain = "apexnews.net",
                    connectedSystems = "ADSTERRA,VERCEL",
                    usersCount = 6400,
                    pageViews = 19800,
                    bounceRate = 42.1,
                    adsterraImpressions = 18200,
                    adsterraClicks = 390,
                    adsterraRevenue = 118.30,
                    monetagImpressions = 0,
                    monetagClicks = 0,
                    monetagRevenue = 0.0,
                    totalRevenue = 118.30,
                    totalImpressions = 18200,
                    totalClicks = 390,
                    framework = "Astro"
                ),
                ConnectedDomainEntity(
                    domain = "gameportal.xyz",
                    connectedSystems = "MONETAG,VERCEL",
                    usersCount = 8100,
                    pageViews = 27300,
                    bounceRate = 26.4,
                    adsterraImpressions = 0,
                    adsterraClicks = 0,
                    adsterraRevenue = 0.0,
                    monetagImpressions = 25100,
                    monetagClicks = 510,
                    monetagRevenue = 168.17,
                    totalRevenue = 168.17,
                    totalImpressions = 25100,
                    totalClicks = 510,
                    framework = "SvelteKit"
                )
            )
            dao.insertConnectedDomains(initialDomains)
        }
    }
}
