package com.example.ai_crop_doctor.domain.model

import java.util.Date

/**
 * Domain model for Diagnosis
 *
 * Represents a crop disease diagnosis with AI results and expert validation
 */
data class Diagnosis(
    val id: Int,
    val farmerId: String?,
    val imagePath: String,
    val audioPath: String?,
    val question: String?,

    // Location
    val latitude: Double?,
    val longitude: Double?,
    val province: String?,
    val district: String?,

    // Context
    val temperature: Double?,
    val humidity: Double?,
    val weatherConditions: String?,

    // AI Results
    val diseaseDetected: String,
    val confidence: Double,
    val severity: String?,
    val fullResponse: String,
    val treatmentSuggestions: String?,
    val preventionTips: String?,
    val symptoms: String?,
    val causes: String?,

    // Expert Review
    val expertReviewed: Boolean = false,
    val expertComment: String?,

    // Timestamps
    val createdAt: Date,
    val updatedAt: Date?
) {
    /**
     * Check if confidence is low and needs expert review
     */
    fun needsExpertReview(): Boolean {
        return confidence < 0.70 && !expertReviewed
    }

    /**
     * Get confidence level in Vietnamese
     */
    fun getConfidenceLevelVietnamese(): String {
        return when {
            confidence >= 0.80 -> "CAO"
            confidence >= 0.50 -> "TRUNG BÌNH"
            else -> "THẤP"
        }
    }

    /**
     * Get severity display text
     */
    fun getSeverityDisplay(): String {
        return when (severity?.lowercase()) {
            "high" -> "Nặng"
            "medium" -> "Trung bình"
            "low" -> "Nhẹ"
            else -> "Không xác định"
        }
    }

    /**
     * Get location display string
     */
    fun getLocationDisplay(): String {
        val parts = mutableListOf<String>()
        district?.let { parts.add(it) }
        province?.let { parts.add(it) }
        return if (parts.isNotEmpty()) {
            parts.joinToString(", ")
        } else {
            "Không xác định"
        }
    }

    /**
     * Returns formatted confidence percentage
     */
    fun getConfidencePercentage(): String {
        return "${(confidence * 100).toInt()}%"
    }

    /**
     * Returns confidence badge color based on confidence level
     */
    fun getConfidenceBadgeColor(): String {
        return when {
            confidence >= 0.80 -> "#4CAF50" // Green
            confidence >= 0.50 -> "#FF9800" // Orange
            else -> "#F44336" // Red
        }
    }

    /**
     * Checks if diagnosis has location data
     */
    fun hasLocation(): Boolean {
        return latitude != null && longitude != null
    }

    /**
     * Checks if diagnosis has weather data
     */
    fun hasWeatherData(): Boolean {
        return temperature != null || humidity != null || weatherConditions != null
    }

    /**
     * Returns formatted weather information
     */
    fun getWeatherDisplay(): String {
        val parts = mutableListOf<String>()
        temperature?.let { parts.add("${it.toInt()}°C") }
        humidity?.let { parts.add("${it.toInt()}% độ ẩm") }
        weatherConditions?.let { parts.add(it) }
        return parts.joinToString(" • ")
    }
}
