package com.example.ai_crop_doctor

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Application class for AI Crop Doctor
 *
 * This class initializes Hilt dependency injection and Timber logging
 * for the entire application lifecycle.
 */
@HiltAndroidApp
class AICropDoctorApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.d("AI Crop Doctor App initialized in DEBUG mode")
        }

        Timber.i("Application created successfully")
    }
}
