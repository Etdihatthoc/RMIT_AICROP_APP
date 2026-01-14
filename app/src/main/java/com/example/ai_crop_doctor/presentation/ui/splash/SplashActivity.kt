package com.example.ai_crop_doctor.presentation.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.ai_crop_doctor.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Splash screen activity that displays app logo and transitions to MainActivity
 *
 * Uses AndroidX SplashScreen API for Material Design splash screen
 */
@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen before super.onCreate()
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        Timber.d("SplashActivity created")

        // Keep splash screen visible while loading
        splashScreen.setKeepOnScreenCondition { true }

        // Navigate to MainActivity after short delay
        lifecycleScope.launch {
            delay(SPLASH_DURATION_MS)
            navigateToMain()
        }
    }

    /**
     * Navigate to MainActivity and finish splash screen
     */
    private fun navigateToMain() {
        Timber.d("Navigating to MainActivity")

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()

        // Optional: Add transition animation
        // overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    companion object {
        private const val SPLASH_DURATION_MS = 1500L // 1.5 seconds
    }
}
