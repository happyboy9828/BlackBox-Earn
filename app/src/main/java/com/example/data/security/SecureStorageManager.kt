package com.example.data.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

enum class TokenStatus {
    NOT_SET,
    ACTIVE,
    INVALID,
    CHECKING
}

class SecureStorageManager(context: Context) {

    private val sharedPreferences: SharedPreferences by lazy {
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                "secure_api_store",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            // Fallback for edge cases in environments without Android KeyStore hardware backing
            context.getSharedPreferences("secure_api_store_fallback", Context.MODE_PRIVATE)
        }
    }

    companion object {
        private const val KEY_ADSTERRA_TOKEN = "adsterra_api_token"
        private const val KEY_MONETAG_TOKEN = "monetag_api_token"
        private const val KEY_MONETAG_WITHDRAWALS = "monetag_total_withdrawals"
        private const val KEY_ADSTERRA_STATUS = "adsterra_token_status"
        private const val KEY_MONETAG_STATUS = "monetag_token_status"
        private const val KEY_VERCEL_TOKEN = "vercel_api_token"
        private const val KEY_VERCEL_STATUS = "vercel_token_status"

        fun maskToken(token: String): String {
            if (token.isBlank()) return "Not Set"
            return if (token.length <= 4) {
                "****"
            } else {
                "****" + token.takeLast(4)
            }
        }
    }

    fun getAdsterraToken(): String {
        return sharedPreferences.getString(KEY_ADSTERRA_TOKEN, "") ?: ""
    }

    fun saveAdsterraToken(token: String) {
        sharedPreferences.edit()
            .putString(KEY_ADSTERRA_TOKEN, token.trim())
            .apply()
    }

    fun deleteAdsterraToken() {
        sharedPreferences.edit()
            .remove(KEY_ADSTERRA_TOKEN)
            .putString(KEY_ADSTERRA_STATUS, TokenStatus.NOT_SET.name)
            .apply()
    }

    fun getMonetagToken(): String {
        return sharedPreferences.getString(KEY_MONETAG_TOKEN, "") ?: ""
    }

    fun saveMonetagToken(token: String) {
        sharedPreferences.edit()
            .putString(KEY_MONETAG_TOKEN, token.trim())
            .apply()
    }

    fun deleteMonetagToken() {
        sharedPreferences.edit()
            .remove(KEY_MONETAG_TOKEN)
            .putString(KEY_MONETAG_STATUS, TokenStatus.NOT_SET.name)
            .apply()
    }

    fun getMonetagWithdrawals(): Double {
        return sharedPreferences.getString(KEY_MONETAG_WITHDRAWALS, "0.0")?.toDoubleOrNull() ?: 0.0
    }

    fun saveMonetagWithdrawals(withdrawals: Double) {
        sharedPreferences.edit()
            .putString(KEY_MONETAG_WITHDRAWALS, withdrawals.toString())
            .apply()
    }

    fun getAdsterraStatus(): TokenStatus {
        val raw = sharedPreferences.getString(KEY_ADSTERRA_STATUS, TokenStatus.NOT_SET.name)
        return try {
            TokenStatus.valueOf(raw ?: TokenStatus.NOT_SET.name)
        } catch (e: Exception) {
            TokenStatus.NOT_SET
        }
    }

    fun setAdsterraStatus(status: TokenStatus) {
        sharedPreferences.edit().putString(KEY_ADSTERRA_STATUS, status.name).apply()
    }

    fun getMonetagStatus(): TokenStatus {
        val raw = sharedPreferences.getString(KEY_MONETAG_STATUS, TokenStatus.NOT_SET.name)
        return try {
            TokenStatus.valueOf(raw ?: TokenStatus.NOT_SET.name)
        } catch (e: Exception) {
            TokenStatus.NOT_SET
        }
    }

    fun setMonetagStatus(status: TokenStatus) {
        sharedPreferences.edit().putString(KEY_MONETAG_STATUS, status.name).apply()
    }

    fun getVercelToken(): String {
        return sharedPreferences.getString(KEY_VERCEL_TOKEN, "") ?: ""
    }

    fun saveVercelToken(token: String) {
        sharedPreferences.edit()
            .putString(KEY_VERCEL_TOKEN, token.trim())
            .apply()
    }

    fun deleteVercelToken() {
        sharedPreferences.edit()
            .remove(KEY_VERCEL_TOKEN)
            .putString(KEY_VERCEL_STATUS, TokenStatus.NOT_SET.name)
            .apply()
    }

    fun getVercelStatus(): TokenStatus {
        val raw = sharedPreferences.getString(KEY_VERCEL_STATUS, TokenStatus.NOT_SET.name)
        return try {
            TokenStatus.valueOf(raw ?: TokenStatus.NOT_SET.name)
        } catch (e: Exception) {
            TokenStatus.NOT_SET
        }
    }

    fun setVercelStatus(status: TokenStatus) {
        sharedPreferences.edit().putString(KEY_VERCEL_STATUS, status.name).apply()
    }

    fun clearAllSecrets() {
        sharedPreferences.edit().clear().apply()
    }
}
