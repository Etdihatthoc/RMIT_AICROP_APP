package com.example.ai_crop_doctor.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/**
 * Helper object for handling runtime permissions
 */
object PermissionHelper {

    /**
     * Camera permission
     */
    const val CAMERA = Manifest.permission.CAMERA

    /**
     * Location permissions
     */
    const val LOCATION_FINE = Manifest.permission.ACCESS_FINE_LOCATION
    const val LOCATION_COARSE = Manifest.permission.ACCESS_COARSE_LOCATION

    /**
     * Audio permission
     */
    const val RECORD_AUDIO = Manifest.permission.RECORD_AUDIO

    /**
     * Storage permissions (varies by Android version)
     */
    val STORAGE_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO
        )
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    /**
     * Check if camera permission is granted
     */
    fun hasCameraPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Check if location permissions are granted
     */
    fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            LOCATION_FINE
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    LOCATION_COARSE
                ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Check if audio recording permission is granted
     */
    fun hasAudioPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Check if storage permissions are granted
     */
    fun hasStoragePermission(context: Context): Boolean {
        return STORAGE_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Get all required permissions for diagnosis feature
     */
    fun getDiagnosisPermissions(): Array<String> {
        return arrayOf(
            CAMERA,
            LOCATION_FINE,
            LOCATION_COARSE
        ) + STORAGE_PERMISSIONS
    }

    /**
     * Get all required permissions for map feature
     */
    fun getMapPermissions(): Array<String> {
        return arrayOf(
            LOCATION_FINE,
            LOCATION_COARSE
        )
    }
}
