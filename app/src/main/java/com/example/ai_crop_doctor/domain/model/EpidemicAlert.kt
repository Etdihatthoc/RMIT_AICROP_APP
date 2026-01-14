package com.example.ai_crop_doctor.domain.model

import android.graphics.Color
import com.google.android.gms.maps.model.LatLng
import java.util.Date
import kotlin.math.*

/**
 * Domain model for epidemic alerts
 *
 * Represents disease outbreak information for map visualization
 */
data class EpidemicAlert(
    val id: Int,
    val diseaseType: String,
    val severity: String, // low, medium, high
    val latitude: Double,
    val longitude: Double,
    val radius: Double, // in kilometers
    val caseCount: Int,
    val affectedArea: String, // province/district name
    val description: String,
    val reportedDate: Date,
    val lastUpdated: Date
) {
    /**
     * Get color for severity level
     */
    fun getSeverityColor(): Int {
        return when (severity.lowercase()) {
            "high" -> Color.parseColor("#F44336") // Red
            "medium" -> Color.parseColor("#FF9800") // Orange
            "low" -> Color.parseColor("#FFC107") // Yellow
            else -> Color.parseColor("#9E9E9E") // Gray
        }
    }

    /**
     * Get severity display text in Vietnamese
     */
    fun getSeverityDisplay(): String {
        return when (severity.lowercase()) {
            "high" -> "Nghiêm trọng"
            "medium" -> "Trung bình"
            "low" -> "Nhẹ"
            else -> "Không xác định"
        }
    }

    /**
     * Get LatLng for Google Maps
     */
    fun getLatLng(): LatLng {
        return LatLng(latitude, longitude)
    }

    /**
     * Check if a given location is within the affected radius
     */
    fun isWithinDistance(lat: Double, lon: Double, distanceKm: Double): Boolean {
        val distance = calculateDistance(lat, lon, latitude, longitude)
        return distance <= distanceKm
    }

    /**
     * Calculate distance between two points using Haversine formula
     * Returns distance in kilometers
     */
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371.0 // km

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }

    /**
     * Get formatted case count
     */
    fun getCaseCountDisplay(): String {
        return "$caseCount ca nhiễm"
    }

    /**
     * Get marker title for map
     */
    fun getMarkerTitle(): String {
        return diseaseType
    }

    /**
     * Get marker snippet for map
     */
    fun getMarkerSnippet(): String {
        return "$caseCount ca - ${getSeverityDisplay()}"
    }
}
