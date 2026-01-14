package com.example.ai_crop_doctor.data.repository

import com.example.ai_crop_doctor.data.local.dao.DiagnosisDao
import com.example.ai_crop_doctor.data.local.entity.DiagnosisEntity
import com.example.ai_crop_doctor.data.remote.api.DiagnosisApiService
import com.example.ai_crop_doctor.domain.model.Diagnosis
import com.example.ai_crop_doctor.domain.repository.DiagnosisRepository
import com.example.ai_crop_doctor.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import java.io.File
import javax.inject.Inject

/**
 * Implementation of DiagnosisRepository with offline-first architecture
 */
class DiagnosisRepositoryImpl @Inject constructor(
    private val apiService: DiagnosisApiService,
    private val diagnosisDao: DiagnosisDao
) : DiagnosisRepository {

    override suspend fun createDiagnosis(
        imageFile: File,
        question: String?,
        audioFile: File?,
        farmerId: String?,
        latitude: Double?,
        longitude: Double?
    ): Resource<Diagnosis> {
        return try {
            // Prepare image multipart
            val imagePart = MultipartBody.Part.createFormData(
                "image",
                imageFile.name,
                imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            )

            // Prepare optional parts
            val questionPart = question?.toRequestBody("text/plain".toMediaTypeOrNull())
            val farmerIdPart = farmerId?.toRequestBody("text/plain".toMediaTypeOrNull())
            val latitudePart = latitude?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val longitudePart = longitude?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())

            val audioPart = audioFile?.let {
                MultipartBody.Part.createFormData(
                    "audio",
                    it.name,
                    it.asRequestBody("audio/*".toMediaTypeOrNull())
                )
            }

            // Call API
            val response = apiService.createDiagnosis(
                image = imagePart,
                question = questionPart,
                audio = audioPart,
                farmerId = farmerIdPart,
                latitude = latitudePart,
                longitude = longitudePart
            )

            val diagnosis = response.toDomainModel()

            // Cache to local database
            diagnosisDao.insertDiagnosis(DiagnosisEntity.fromDomainModel(diagnosis))

            Timber.d("Diagnosis created successfully: ${diagnosis.id}")
            Resource.Success(diagnosis)
        } catch (e: Exception) {
            Timber.e(e, "Error creating diagnosis")
            Resource.Error("Có lỗi xảy ra khi tạo chẩn đoán: ${e.message}")
        }
    }

    override suspend fun getDiagnosisById(diagnosisId: Int): Resource<Diagnosis> {
        return try {
            // Try API first
            val response = apiService.getDiagnosis(diagnosisId)
            val diagnosis = response.toDomainModel()

            // Update local cache
            diagnosisDao.insertDiagnosis(DiagnosisEntity.fromDomainModel(diagnosis))

            Resource.Success(diagnosis)
        } catch (e: Exception) {
            Timber.w(e, "API failed, trying local database")

            // Fallback to local database
            val localEntity = diagnosisDao.getDiagnosisById(diagnosisId)
            if (localEntity != null) {
                Resource.Success(localEntity.toDomainModel())
            } else {
                Resource.Error("Không tìm thấy chẩn đoán")
            }
        }
    }

    override suspend fun getDiagnosisHistory(
        farmerId: String?,
        limit: Int,
        offset: Int
    ): Resource<List<Diagnosis>> {
        return try {
            // Try API first
            val response = apiService.getDiagnosisHistory(farmerId, limit, offset)
            val diagnosisList = response.diagnoses.map { it.toDomainModel() }

            // Update local cache
            diagnosisDao.insertDiagnoses(
                diagnosisList.map { DiagnosisEntity.fromDomainModel(it) }
            )

            Resource.Success(diagnosisList)
        } catch (e: Exception) {
            Timber.w(e, "API failed, loading from local database")

            // Fallback to local database
            val localEntities = if (farmerId != null) {
                diagnosisDao.getDiagnosesByFarmer(farmerId)
            } else {
                diagnosisDao.getAllDiagnoses()
            }

            if (localEntities.isNotEmpty()) {
                Resource.Success(localEntities.map { it.toDomainModel() })
            } else {
                Resource.Error("Không có dữ liệu chẩn đoán")
            }
        }
    }

    override fun getAllDiagnosesFlow(): Flow<List<Diagnosis>> {
        return diagnosisDao.getAllDiagnosesFlow()
            .map { entities -> entities.map { it.toDomainModel() } }
    }

    override suspend fun saveDiagnosisLocally(diagnosis: Diagnosis) {
        diagnosisDao.insertDiagnosis(DiagnosisEntity.fromDomainModel(diagnosis))
    }

    override suspend fun searchByDisease(diseaseName: String): List<Diagnosis> {
        return diagnosisDao.searchByDisease(diseaseName)
            .map { it.toDomainModel() }
    }

    override suspend fun filterByConfidence(minConfidence: Double): List<Diagnosis> {
        return diagnosisDao.filterByConfidence(minConfidence)
            .map { it.toDomainModel() }
    }

    override suspend fun getDiagnosesNeedingExpertReview(): List<Diagnosis> {
        return diagnosisDao.getDiagnosesNeedingExpertReview()
            .map { it.toDomainModel() }
    }
}
