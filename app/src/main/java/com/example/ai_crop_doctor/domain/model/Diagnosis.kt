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

    // Expert Review
    val status: String = "pending", // pending, confirmed, corrected, rejected
    val expertReviewed: Boolean = false,
    val expertComment: String?,
    val expertId: String?,

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
        return when {
            province != null && district != null -> "$district, $province"
            province != null -> province
            else -> "Không có vị trí"
        }
    }
}
