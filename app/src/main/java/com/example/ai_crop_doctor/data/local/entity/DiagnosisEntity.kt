package com.example.ai_crop_doctor.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ai_crop_doctor.domain.model.Diagnosis
import java.util.Date

@Entity(tableName = "diagnoses")
data class DiagnosisEntity(
    @PrimaryKey
    val id: Int,
    val farmerId: String?,
    val imagePath: String,
    val audioPath: String?,
    val question: String?,
    val latitude: Double?,
    val longitude: Double?,
    val province: String?,
    val district: String?,
    val temperature: Double?,
    val humidity: Double?,
    val weatherConditions: String?,
    val diseaseDetected: String,
    val confidence: Double,
    val severity: String?,
    val fullResponse: String,
    val treatmentSuggestions: String?,
    val preventionTips: String?,
    val symptoms: String?,
    val causes: String?,
    val expertReviewed: Boolean,
    val expertComment: String?,
    val createdAt: Long,
    val updatedAt: Long?,
    val isSynced: Boolean = true
) {
    fun toDomainModel(): Diagnosis {
        return Diagnosis(
            id = id,
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
            createdAt = Date(createdAt),
            updatedAt = updatedAt?.let { Date(it) }
        )
    }

    companion object {
        fun fromDomainModel(diagnosis: Diagnosis, isSynced: Boolean = true): DiagnosisEntity {
            return DiagnosisEntity(
                id = diagnosis.id,
                farmerId = diagnosis.farmerId,
                imagePath = diagnosis.imagePath,
                audioPath = diagnosis.audioPath,
                question = diagnosis.question,
                latitude = diagnosis.latitude,
                longitude = diagnosis.longitude,
                province = diagnosis.province,
                district = diagnosis.district,
                temperature = diagnosis.temperature,
                humidity = diagnosis.humidity,
                weatherConditions = diagnosis.weatherConditions,
                diseaseDetected = diagnosis.diseaseDetected,
                confidence = diagnosis.confidence,
                severity = diagnosis.severity,
                fullResponse = diagnosis.fullResponse,
                treatmentSuggestions = diagnosis.treatmentSuggestions,
                preventionTips = diagnosis.preventionTips,
                symptoms = diagnosis.symptoms,
                causes = diagnosis.causes,
                expertReviewed = diagnosis.expertReviewed,
                expertComment = diagnosis.expertComment,
                createdAt = diagnosis.createdAt.time,
                updatedAt = diagnosis.updatedAt?.time,
                isSynced = isSynced
            )
        }
    }
}
