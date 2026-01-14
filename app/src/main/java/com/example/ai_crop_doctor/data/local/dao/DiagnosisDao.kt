package com.example.ai_crop_doctor.data.local.dao

import androidx.room.*
import com.example.ai_crop_doctor.data.local.entity.DiagnosisEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DiagnosisDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiagnosis(diagnosis: DiagnosisEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiagnoses(diagnoses: List<DiagnosisEntity>)

    @Query("SELECT * FROM diagnoses WHERE id = :diagnosisId")
    suspend fun getDiagnosisById(diagnosisId: Int): DiagnosisEntity?

    @Query("SELECT * FROM diagnoses ORDER BY createdAt DESC")
    suspend fun getAllDiagnoses(): List<DiagnosisEntity>

    @Query("SELECT * FROM diagnoses ORDER BY createdAt DESC")
    fun getAllDiagnosesFlow(): Flow<List<DiagnosisEntity>>

    @Query("SELECT * FROM diagnoses WHERE farmerId = :farmerId ORDER BY createdAt DESC")
    suspend fun getDiagnosesByFarmer(farmerId: String): List<DiagnosisEntity>

    @Query("SELECT * FROM diagnoses WHERE diseaseDetected LIKE '%' || :diseaseName || '%'")
    suspend fun searchByDisease(diseaseName: String): List<DiagnosisEntity>

    @Query("SELECT * FROM diagnoses WHERE confidence >= :minConfidence ORDER BY confidence DESC")
    suspend fun filterByConfidence(minConfidence: Double): List<DiagnosisEntity>

    @Query("SELECT * FROM diagnoses WHERE confidence < 0.70 AND expertReviewed = 0")
    suspend fun getDiagnosesNeedingExpertReview(): List<DiagnosisEntity>

    @Query("SELECT * FROM diagnoses WHERE expertReviewed = :reviewed ORDER BY createdAt DESC")
    suspend fun getByExpertReviewStatus(reviewed: Boolean): List<DiagnosisEntity>

    @Query("SELECT * FROM diagnoses WHERE isSynced = 0")
    suspend fun getUnsyncedDiagnoses(): List<DiagnosisEntity>

    @Query("DELETE FROM diagnoses WHERE id = :diagnosisId")
    suspend fun deleteDiagnosis(diagnosisId: Int)

    @Query("DELETE FROM diagnoses")
    suspend fun deleteAllDiagnoses()

    @Query("SELECT COUNT(*) FROM diagnoses")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(*) FROM diagnoses WHERE confidence < 0.70 AND expertReviewed = 0")
    suspend fun getCountNeedingExpertReview(): Int
}
