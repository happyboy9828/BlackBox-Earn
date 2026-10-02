package com.example.data.security

import android.content.Context
import android.content.SharedPreferences
import androidx.biometric.BiometricManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.MessageDigest
import java.util.UUID

class PinManager(private val context: Context) {

    private val prefs: SharedPreferences by lazy {
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                "app_lock_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            context.getSharedPreferences("app_lock_prefs_fallback", Context.MODE_PRIVATE)
        }
    }

    companion object {
        private const val KEY_PIN_HASH = "app_lock_hash"
        private const val KEY_PIN_SALT = "app_lock_salt"
        private const val KEY_PASSWORD_HASH = "app_lock_pw_hash"
        private const val KEY_BIOMETRIC_ENABLED = "app_lock_biometric"
        private const val KEY_LOCK_TIMEOUT_MS = "app_lock_timeout_ms"
        private const val KEY_FAILED_ATTEMPTS = "app_lock_failed_attempts"
        private const val KEY_LOCKOUT_UNTIL = "app_lock_lockout_until"

        const val DEFAULT_TIMEOUT_MS = 0L // Immediately after app closed
        const val MAX_FAILED_ATTEMPTS = 3
        const val LOCKOUT_DURATION_MS = 60_000L // 60 seconds
    }

    fun isPinConfigured(): Boolean {
        return prefs.getString(KEY_PIN_HASH, null) != null
    }

    fun setupPin(pin: String, optionalPassword: String? = null, enableBiometric: Boolean = true): Boolean {
        if (pin.length != 6 || !pin.all { it.isDigit() }) return false

        val salt = UUID.randomUUID().toString()
        val pinHash = hashWithSalt(pin, salt)

        val editor = prefs.edit()
            .putString(KEY_PIN_SALT, salt)
            .putString(KEY_PIN_HASH, pinHash)
            .putInt(KEY_FAILED_ATTEMPTS, 0)
            .putLong(KEY_LOCKOUT_UNTIL, 0L)

        if (enableBiometric && isBiometricAvailable()) {
            editor.putBoolean(KEY_BIOMETRIC_ENABLED, true)
        }

        if (!optionalPassword.isNullOrBlank()) {
            val pwHash = hashWithSalt(optionalPassword, salt)
            editor.putString(KEY_PASSWORD_HASH, pwHash)
        } else {
            editor.remove(KEY_PASSWORD_HASH)
        }

        editor.apply()
        return true
    }

    fun verifyPin(input: String): PinVerifyResult {
        val lockoutUntil = getLockoutUntilTime()
        val currentTime = System.currentTimeMillis()
        if (currentTime < lockoutUntil) {
            val secondsRemaining = ((lockoutUntil - currentTime) / 1000).toInt().coerceAtLeast(1)
            return PinVerifyResult.LockedOut(secondsRemaining)
        }

        val storedHash = prefs.getString(KEY_PIN_HASH, null) ?: return PinVerifyResult.NotConfigured
        val salt = prefs.getString(KEY_PIN_SALT, "") ?: ""
        val inputHash = hashWithSalt(input, salt)

        val pwHash = prefs.getString(KEY_PASSWORD_HASH, null)
        val isMatch = inputHash == storedHash || (pwHash != null && hashWithSalt(input, salt) == pwHash)

        return if (isMatch) {
            resetFailedAttempts()
            PinVerifyResult.Success
        } else {
            val failed = prefs.getInt(KEY_FAILED_ATTEMPTS, 0) + 1
            if (failed >= MAX_FAILED_ATTEMPTS) {
                val lockUntil = currentTime + LOCKOUT_DURATION_MS
                prefs.edit()
                    .putInt(KEY_FAILED_ATTEMPTS, failed)
                    .putLong(KEY_LOCKOUT_UNTIL, lockUntil)
                    .apply()
                PinVerifyResult.LockedOut(60)
            } else {
                prefs.edit().putInt(KEY_FAILED_ATTEMPTS, failed).apply()
                PinVerifyResult.Failed(attemptsLeft = MAX_FAILED_ATTEMPTS - failed)
            }
        }
    }

    fun changePin(oldPin: String, newPin: String): Boolean {
        val verify = verifyPin(oldPin)
        if (verify !is PinVerifyResult.Success) return false
        return setupPin(newPin)
    }

    fun resetAllSecurity(oldPin: String): Boolean {
        val verify = verifyPin(oldPin)
        if (verify !is PinVerifyResult.Success) return false
        prefs.edit().clear().apply()
        return true
    }

    fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK
        ) == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun isBiometricEnabled(): Boolean {
        return prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false) && isBiometricAvailable()
    }

    fun setBiometricEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
    }

    fun getLockTimeout(): Long {
        return prefs.getLong(KEY_LOCK_TIMEOUT_MS, DEFAULT_TIMEOUT_MS)
    }

    fun setLockTimeout(timeoutMs: Long) {
        prefs.edit().putLong(KEY_LOCK_TIMEOUT_MS, timeoutMs).apply()
    }

    fun getLockoutUntilTime(): Long {
        return prefs.getLong(KEY_LOCKOUT_UNTIL, 0L)
    }

    private fun resetFailedAttempts() {
        prefs.edit()
            .putInt(KEY_FAILED_ATTEMPTS, 0)
            .putLong(KEY_LOCKOUT_UNTIL, 0L)
            .apply()
    }

    private fun hashWithSalt(input: String, salt: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest((salt + input).toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}

sealed class PinVerifyResult {
    object Success : PinVerifyResult()
    object NotConfigured : PinVerifyResult()
    data class Failed(val attemptsLeft: Int) : PinVerifyResult()
    data class LockedOut(val secondsRemaining: Int) : PinVerifyResult()
}
