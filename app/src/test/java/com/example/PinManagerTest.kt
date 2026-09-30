package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.security.PinManager
import com.example.data.security.PinVerifyResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PinManagerTest {

    private lateinit var context: Context
    private lateinit var pinManager: PinManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        pinManager = PinManager(context)
    }

    @Test
    fun testPinLifecycleAndLockout() {
        // Setup 6-digit PIN
        val success = pinManager.setupPin("123456")
        assertTrue(success)
        assertTrue(pinManager.isPinConfigured())

        // Invalid short PIN rejection
        val shortFail = pinManager.setupPin("123")
        assertFalse(shortFail)

        // Correct PIN
        val correctResult = pinManager.verifyPin("123456")
        assertTrue(correctResult is PinVerifyResult.Success)

        // Wrong attempt 1
        val wrong1 = pinManager.verifyPin("000000")
        assertTrue(wrong1 is PinVerifyResult.Failed)
        assertEquals(2, (wrong1 as PinVerifyResult.Failed).attemptsLeft)

        // Wrong attempt 2
        val wrong2 = pinManager.verifyPin("000000")
        assertTrue(wrong2 is PinVerifyResult.Failed)
        assertEquals(1, (wrong2 as PinVerifyResult.Failed).attemptsLeft)

        // Wrong attempt 3 -> Lockout!
        val wrong3 = pinManager.verifyPin("000000")
        assertTrue(wrong3 is PinVerifyResult.LockedOut)
    }
}
