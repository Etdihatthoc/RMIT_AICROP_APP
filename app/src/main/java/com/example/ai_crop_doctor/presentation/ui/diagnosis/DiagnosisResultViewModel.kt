package com.example.ai_crop_doctor.presentation.ui.diagnosis

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai_crop_doctor.domain.model.Diagnosis
import com.example.ai_crop_doctor.domain.repository.DiagnosisRepository
import com.example.ai_crop_doctor.util.LocationHelper
import com.example.ai_crop_doctor.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DiagnosisResultViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val diagnosisRepository: DiagnosisRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _diagnosisResult = MutableLiveData<Diagnosis?>()
    val diagnosisResult: LiveData<Diagnosis?> = _diagnosisResult

    private val _savingSuccess = MutableLiveData<Boolean>(false)
    val savingSuccess: LiveData<Boolean> = _savingSuccess

    /**
     * Submit diagnosis with image file
     */
    fun submitDiagnosis(
        imageFile: File,
        question: String? = null,
        audioFile: File? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // Get current location
                val locationData = LocationHelper.getCurrentLocation(context)

                // Submit diagnosis
                val result = diagnosisRepository.createDiagnosis(
                    imageFile = imageFile,
                    audioFile = audioFile,
                    question = question,
                    farmerId = null, // TODO: Get from user session
                    latitude = locationData?.latitude,
                    longitude = locationData?.longitude,
                    province = locationData?.province,
                    district = locationData?.district,
                    temperature = null, // TODO: Get from weather API
                    humidity = null, // TODO: Get from weather API
                    weatherConditions = null // TODO: Get from weather API
                )

                when (result) {
                    is Resource.Success -> {
                        Timber.d("Diagnosis successful: ${result.data?.diseaseDetected}")
                        _diagnosisResult.value = result.data
                    }
                    is Resource.Error -> {
                        Timber.e("Diagnosis failed: ${result.message}")
                        _errorMessage.value = result.message
                    }
                    is Resource.Loading -> {
                        // Loading state already set
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Exception during diagnosis submission")
                _errorMessage.value = "Có lỗi xảy ra: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Request expert review for a diagnosis
     */
    fun requestExpertReview(diagnosisId: Int) {
        viewModelScope.launch {
            try {
                // TODO: Implement expert review request API call
                // For now, just log
                Timber.d("Requesting expert review for diagnosis: $diagnosisId")
            } catch (e: Exception) {
                Timber.e(e, "Failed to request expert review")
                _errorMessage.value = "Không thể gửi yêu cầu chuyên gia: ${e.message}"
            }
        }
    }

    /**
     * Save diagnosis to local database
     */
    fun saveDiagnosis(diagnosis: Diagnosis) {
        viewModelScope.launch {
            try {
                // TODO: Implement local database save
                // For now, just mark as successful
                Timber.d("Saving diagnosis: ${diagnosis.id}")
                _savingSuccess.value = true
            } catch (e: Exception) {
                Timber.e(e, "Failed to save diagnosis")
                _errorMessage.value = "Không thể lưu chẩn đoán: ${e.message}"
            }
        }
    }

    /**
     * Load a specific diagnosis by ID
     */
    fun loadDiagnosis(diagnosisId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val result = diagnosisRepository.getDiagnosis(diagnosisId)

                when (result) {
                    is Resource.Success -> {
                        Timber.d("Diagnosis loaded: ${result.data?.diseaseDetected}")
                        _diagnosisResult.value = result.data
                    }
                    is Resource.Error -> {
                        Timber.e("Failed to load diagnosis: ${result.message}")
                        _errorMessage.value = result.message
                    }
                    is Resource.Loading -> {
                        // Loading state already set
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Exception loading diagnosis")
                _errorMessage.value = "Có lỗi xảy ra: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }
}
