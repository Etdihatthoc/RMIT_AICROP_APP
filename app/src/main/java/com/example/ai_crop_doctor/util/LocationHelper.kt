package com.example.ai_crop_doctor.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.Locale

/**
 * Data class to hold location information
 */
data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val province: String?,
    val district: String?
)

/**
 * Helper class for location operations
 */
class LocationHelper(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    companion object {
        /**
         * Static method to get current location with province/district
         */
        suspend fun getCurrentLocation(context: Context): LocationData? {
            val helper = LocationHelper(context)
            val location = helper.getCurrentLocation() ?: return null

            val (province, district) = helper.getProvinceAndDistrict(
                location.latitude,
                location.longitude
            )

            return LocationData(
                latitude = location.latitude,
                longitude = location.longitude,
                province = province,
                district = district
            )
        }
    }

    /**
     * Get current location
     */
    suspend fun getCurrentLocation(): Location? {
        if (!hasLocationPermission()) {
            Timber.w("Location permission not granted")
            return null
        }

        return try {
            val cancellationToken = CancellationTokenSource()
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationToken.token
            ).await()
        } catch (e: Exception) {
            Timber.e(e, "Error getting current location")
            null
        }
    }

    /**
     * Get address from coordinates using Geocoder
     */
    suspend fun getAddressFromLocation(latitude: Double, longitude: Double): Address? {
        return try {
            val geocoder = Geocoder(context, Locale("vi", "VN"))
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            addresses?.firstOrNull()
        } catch (e: Exception) {
            Timber.e(e, "Error getting address from location")
            null
        }
    }

    /**
     * Get province and district from coordinates
     */
    suspend fun getProvinceAndDistrict(latitude: Double, longitude: Double): Pair<String?, String?> {
        val address = getAddressFromLocation(latitude, longitude)
        return if (address != null) {
            val province = address.adminArea ?: address.subAdminArea
            val district = address.subAdminArea ?: address.locality
            Pair(normalizeProvinceName(province), district)
        } else {
            Pair(null, null)
        }
    }

    /**
     * Normalize Vietnamese province names
     */
    private fun normalizeProvinceName(province: String?): String? {
        if (province == null) return null

        // Remove common prefixes
        val normalized = province
            .replace("Tỉnh ", "")
            .replace("Thành phố ", "")
            .replace("TP. ", "")

        return normalized
    }

    /**
     * Check if location permission is granted
     */
    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Calculate distance between two points in kilometers
     */
    fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0] / 1000 // Convert to kilometers
    }
}
