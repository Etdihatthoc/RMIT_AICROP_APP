package com.example.ai_crop_doctor.domain.repository

import com.example.ai_crop_doctor.domain.model.Diagnosis
import com.example.ai_crop_doctor.util.Resource
import kotlinx.coroutines.flow.Flow
import java.io.File

/**
 * Repository interface for Diagnosis operations
 */
interface DiagnosisRepository {

    /**
     * Create a new diagnosis by uploading image and optional audio/question
     */
    suspend fun createDiagnosis(
        imageFile: File,
        question: String? = null,
        audioFile: File? = null,
        farmerId: String? = null,
        latitude: Double? = null,
        longitude: Double? = null,
        province: String? = null,
        district: String? = null,
        temperature: Double? = null,
        humidity: Double? = null,
        weatherConditions: String? = null
    ): Resource<Diagnosis>

    /**
     * Get diagnosis by ID
     */
    suspend fun getDiagnosisById(diagnosisId: Int): Resource<Diagnosis>

    /**
     * Get diagnosis history with pagination
     */
    suspend fun getDiagnosisHistory(
        farmerId: String? = null,
        limit: Int = 10,
        offset: Int = 0
    ): Resource<List<Diagnosis>>

    /**
     * Get all diagnoses as Flow for real-time updates
     */
    fun getAllDiagnosesFlow(): Flow<List<Diagnosis>>

    /**
     * Save diagnosis to local database
     */
    suspend fun saveDiagnosisLocally(diagnosis: Diagnosis)

    /**
     * Search diagnoses by disease name
     */
    suspend fun searchByDisease(diseaseName: String): List<Diagnosis>

    /**
     * Filter diagnoses by confidence threshold
     */
    suspend fun filterByConfidence(minConfidence: Double): List<Diagnosis>

    /**
     * Get diagnoses that need expert review
     */
    suspend fun getDiagnosesNeedingExpertReview(): List<Diagnosis>
}
