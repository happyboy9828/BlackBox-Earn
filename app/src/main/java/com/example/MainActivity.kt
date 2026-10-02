package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.Room.PublisherDatabase
import com.example.data.security.PinManager
import com.example.data.security.SecureStorageManager
import com.example.domain.repository.PublisherRepository
import com.example.ui.PubDashApp
import com.example.ui.screens.PublisherViewModel
import com.example.ui.screens.PublisherViewModelFactory
import com.example.ui.theme.BlackBoxBg
import com.example.ui.theme.MyApplicationTheme

class MainActivity : FragmentActivity() {

    private var lastPauseTime: Long = 0L
    private lateinit var pinManager: PinManager
    private var currentViewModel: PublisherViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        pinManager = PinManager(applicationContext)

        setContent {
            MyApplicationTheme {
                val coroutineScope = rememberCoroutineScope()
                val secureStorage = remember { SecureStorageManager(applicationContext) }
                val database = remember { PublisherDatabase.getDatabase(applicationContext, coroutineScope) }
                val repository = remember { PublisherRepository(database.publisherDao(), secureStorage) }

                val viewModel: PublisherViewModel = viewModel(
                    factory = PublisherViewModelFactory(repository, pinManager, secureStorage)
                )
                currentViewModel = viewModel

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BlackBoxBg
                ) {
                    PubDashApp(
                        viewModel = viewModel,
                        onRequestBiometricPrompt = { onAuthSuccess ->
                            showBiometricPrompt(
                                onSuccess = {
                                    onAuthSuccess()
                                },
                                onError = { msg ->
                                    Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    )
                }
            }
        }
    }

    fun showBiometricPrompt(
        title: String = "BlackBox Earn Security",
        subtitle: String = "Touch fingerprint sensor to unlock",
        onSuccess: () -> Unit,
        onError: (String) -> Unit = {}
    ) {
        if (!pinManager.isBiometricAvailable() || !pinManager.isBiometricEnabled()) {
            return
        }

        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode != BiometricPrompt.ERROR_USER_CANCELED &&
                        errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON
                    ) {
                        onError(errString.toString())
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onError("Fingerprint not recognized. Please try again.")
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText("Use PIN")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    override fun onPause() {
        super.onPause()
        lastPauseTime = System.currentTimeMillis()
    }

    override fun onResume() {
        super.onResume()
        if (lastPauseTime > 0L) {
            val elapsed = System.currentTimeMillis() - lastPauseTime
            val timeout = pinManager.getLockTimeout()
            if (elapsed >= timeout) {
                currentViewModel?.lockApp()
                if (pinManager.isBiometricEnabled()) {
                    showBiometricPrompt(onSuccess = { currentViewModel?.unlockWithBiometrics() })
                }
            }
        }
    }
}
