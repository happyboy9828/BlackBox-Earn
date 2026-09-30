package com.example.data.api

import android.util.Log
import com.example.data.security.SecureStorageManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface AdsterraApiService {
    @GET("domains.json")
    suspend fun getDomains(
        @Header("X-API-Key") apiKey: String
    ): retrofit2.Response<AdsterraDomainsResponse>

    @GET("placements.json")
    suspend fun getPlacements(
        @Header("X-API-Key") apiKey: String
    ): retrofit2.Response<ResponseBody>

    @GET("stats.json")
    suspend fun getStats(
        @Header("X-API-Key") apiKey: String,
        @Query("start_date") startDate: String,
        @Query("finish_date") finishDate: String,
        @Query("domain") domain: String? = null,
        @Query("placement") placement: String? = null,
        @Query("group_by[]") groupBy: List<String> = listOf("date", "domain")
    ): retrofit2.Response<AdsterraStatsResponse>
}

interface MonetagApiService {
    @GET("statistics")
    suspend fun getStatisticsWithApiKey(
        @Header("X-API-Key") apiKey: String,
        @Query("date_from") dateFrom: String,
        @Query("date_to") dateTo: String,
        @Query("group_by[]") groupBy: List<String> = listOf("date", "domain")
    ): retrofit2.Response<MonetagStatsResponse>

    @GET("statistics")
    suspend fun getStatisticsWithBearer(
        @Header("Authorization") bearerToken: String,
        @Query("date_from") dateFrom: String,
        @Query("date_to") dateTo: String,
        @Query("group_by[]") groupBy: List<String> = listOf("date", "domain")
    ): retrofit2.Response<MonetagStatsResponse>

    @GET("profile")
    suspend fun testProfileWithApiKey(
        @Header("X-API-Key") apiKey: String
    ): retrofit2.Response<ResponseBody>

    @GET("profile")
    suspend fun testProfileWithBearer(
        @Header("Authorization") bearerToken: String
    ): retrofit2.Response<ResponseBody>
}

interface VercelApiService {
    @GET("v5/domains")
    suspend fun getDomains(
        @Header("Authorization") bearerToken: String
    ): retrofit2.Response<VercelDomainsResponse>

    @GET("v9/projects")
    suspend fun getProjects(
        @Header("Authorization") bearerToken: String
    ): retrofit2.Response<VercelProjectsResponse>

    @GET("v6/deployments")
    suspend fun getDeployments(
        @Header("Authorization") bearerToken: String,
        @Query("limit") limit: Int = 20
    ): retrofit2.Response<ResponseBody>
}

object ApiClientProvider {

    private const val TAG = "SecureApiClient"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    // Masked Logging Interceptor that ensures no raw tokens are logged
    private val maskedLoggingInterceptor = Interceptor { chain ->
        val request = chain.request()
        val apiKeyHeader = request.header("X-API-Key")
        val authHeader = request.header("Authorization")

        if (apiKeyHeader != null) {
            Log.d(TAG, "Outgoing request: ${request.url} with X-API-Key: ${SecureStorageManager.maskToken(apiKeyHeader)}")
        }
        if (authHeader != null) {
            val token = authHeader.removePrefix("Bearer ").trim()
            Log.d(TAG, "Outgoing request: ${request.url} with Bearer: ${SecureStorageManager.maskToken(token)}")
        }

        chain.proceed(request)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(maskedLoggingInterceptor)
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()

    val adsterraService: AdsterraApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api3.adsterratools.com/publisher/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(AdsterraApiService::class.java)
    }

    val monetagService: MonetagApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.monetag.com/v5/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(MonetagApiService::class.java)
    }

    val vercelService: VercelApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.vercel.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(VercelApiService::class.java)
    }
}
