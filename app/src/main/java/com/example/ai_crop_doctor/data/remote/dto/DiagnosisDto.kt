package com.example.ai_crop_doctor.data.remote.dto

import com.example.ai_crop_doctor.domain.model.Diagnosis
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

/**
 * Data Transfer Object for Diagnosis API responses
 *
 * Maps to backend API JSON structure
 */
data class DiagnosisResponse(
    @SerializedName("diagnosis_id")
    val diagnosisId: Int,

    @SerializedName("farmer_id")
    val farmerId: String?,

    @SerializedName("image_path")
    val imagePath: String,

    @SerializedName("audio_path")
    val audioPath: String?,

    @SerializedName("question")
    val question: String?,

    // Location
    @SerializedName("latitude")
    val latitude: Double?,

    @SerializedName("longitude")
    val longitude: Double?,

    @SerializedName("province")
    val province: String?,

    @SerializedName("district")
    val district: String?,

    // Context
    @SerializedName("temperature")
    val temperature: Double?,

    @SerializedName("humidity")
    val humidity: Double?,

    @SerializedName("weather_conditions")
    val weatherConditions: String?,

    // AI Results
    @SerializedName("disease_detected")
    val diseaseDetected: String,

    @SerializedName("confidence")
    val confidence: Double,

    @SerializedName("severity")
    val severity: String?,

    @SerializedName("full_response")
    val fullResponse: String,

    @SerializedName("treatment_suggestions")
    val treatmentSuggestions: String?,

    @SerializedName("prevention_tips")
    val preventionTips: String?,

    @SerializedName("symptoms")
    val symptoms: String?,

    @SerializedName("causes")
    val causes: String?,

    // Expert Review
    @SerializedName("expert_reviewed")
    val expertReviewed: Boolean = false,

    @SerializedName("expert_comment")
    val expertComment: String?,

    // Timestamps
    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String?
) {
    /**
     * Convert DTO to domain model
     */
    fun toDomainModel(): Diagnosis {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")

        return Diagnosis(
            id = diagnosisId,
            farmerId = farmerId,
            imagePath = imagePath,
            audioPath = audioPath,
            question = question,
            latitude = latitude,
            longitude = longitude,
            province = province,
            district = district,
            temperature = temperature,
            humidity = humidity,
            weatherConditions = weatherConditions,
            diseaseDetected = diseaseDetected,
            confidence = confidence,
            severity = severity,
            fullResponse = fullResponse,
            treatmentSuggestions = treatmentSuggestions,
            preventionTips = preventionTips,
            symptoms = symptoms,
            causes = causes,
            expertReviewed = expertReviewed,
            expertComment = expertComment,
            createdAt = try {
                dateFormat.parse(createdAt) ?: Date()
            } catch (e: Exception) {
                Date()
            },
            updatedAt = updatedAt?.let {
                try {
                    dateFormat.parse(it)
                } catch (e: Exception) {
                    null
                }
            }
        )
    }
}

/**
 * Response for diagnosis history list
 */
data class DiagnosisHistoryResponse(
    @SerializedName("diagnoses")
    val diagnoses: List<DiagnosisResponse>,

    @SerializedName("total")
    val total: Int,

    @SerializedName("offset")
    val offset: Int,

    @SerializedName("limit")
    val limit: Int
)
