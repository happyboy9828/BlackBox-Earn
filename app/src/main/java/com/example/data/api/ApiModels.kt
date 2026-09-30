package com.example.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// --- Adsterra Models ---

@JsonClass(generateAdapter = true)
data class AdsterraStatsResponse(
    @Json(name = "items") val items: List<AdsterraStatItem>? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "message") val message: String? = null
)

@JsonClass(generateAdapter = true)
data class AdsterraStatItem(
    @Json(name = "date") val date: String? = null,
    @Json(name = "domain") val domain: String? = null,
    @Json(name = "placement") val placement: String? = null,
    @Json(name = "country") val country: String? = null,
    @Json(name = "impressions") val impressions: Long? = 0,
    @Json(name = "clicks") val clicks: Long? = 0,
    @Json(name = "revenue") val revenue: Double? = 0.0,
    @Json(name = "cpm") val cpm: Double? = 0.0
)

@JsonClass(generateAdapter = true)
data class AdsterraDomainsResponse(
    @Json(name = "items") val items: List<AdsterraDomainItem>? = null
)

@JsonClass(generateAdapter = true)
data class AdsterraDomainItem(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "status") val status: String? = null
)

// --- Monetag Models ---

@JsonClass(generateAdapter = true)
data class MonetagStatsResponse(
    @Json(name = "result") val result: List<MonetagStatItem>? = null,
    @Json(name = "error") val error: String? = null,
    @Json(name = "message") val message: String? = null
)

@JsonClass(generateAdapter = true)
data class MonetagStatItem(
    @Json(name = "date") val date: String? = null,
    @Json(name = "domain") val domain: String? = null,
    @Json(name = "country") val country: String? = null,
    @Json(name = "zone_id") val zoneId: Long? = null,
    @Json(name = "impressions") val impressions: Long? = 0,
    @Json(name = "clicks") val clicks: Long? = 0,
    @Json(name = "profit") val profit: Double? = null,
    @Json(name = "revenue") val revenue: Double? = null,
    @Json(name = "cpm") val cpm: Double? = 0.0
) {
    val effectiveRevenue: Double
        get() = profit ?: revenue ?: 0.0
}

// --- Vercel Models ---

@JsonClass(generateAdapter = true)
data class VercelProjectsResponse(
    @Json(name = "projects") val projects: List<VercelProjectItem>? = null
)

@JsonClass(generateAdapter = true)
data class VercelProjectItem(
    @Json(name = "id") val id: String? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "framework") val framework: String? = null,
    @Json(name = "updatedAt") val updatedAt: Long? = null
)

@JsonClass(generateAdapter = true)
data class VercelDomainsResponse(
    @Json(name = "domains") val domains: List<VercelDomainItem>? = null
)

@JsonClass(generateAdapter = true)
data class VercelDomainItem(
    @Json(name = "name") val name: String? = null,
    @Json(name = "apexName") val apexName: String? = null,
    @Json(name = "projectId") val projectId: String? = null,
    @Json(name = "verified") val verified: Boolean? = true,
    @Json(name = "createdAt") val createdAt: Long? = null
)

