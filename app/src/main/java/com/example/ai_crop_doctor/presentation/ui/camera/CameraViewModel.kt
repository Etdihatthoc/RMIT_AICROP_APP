package com.example.ai_crop_doctor.presentation.ui.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai_crop_doctor.domain.model.Diagnosis
import com.example.ai_crop_doctor.domain.repository.DiagnosisRepository
import com.example.ai_crop_doctor.util.LocationData
import com.example.ai_crop_doctor.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val diagnosisRepository: DiagnosisRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _capturedImage = MutableLiveData<File?>()
    val capturedImage: LiveData<File?> = _capturedImage

    private val _diagnosisResult = MutableLiveData<Diagnosis?>()
    val diagnosisResult: LiveData<Diagnosis?> = _diagnosisResult

    private var currentLocationData: LocationData? = null

    /**
     * Called when user captures or selects an image
     */
    fun onImageCaptured(imageFile: File) {
        _capturedImage.value = imageFile
        Timber.d("Image captured: ${imageFile.absolutePath}")
    }

    /**
     * Set location data for diagnosis
     */
    fun setLocationData(locationData: LocationData?) {
        currentLocationData = locationData
        Timber.d("Location data set: ${locationData?.province}, ${locationData?.district}")
    }

    /**
     * Submit diagnosis with captured image and optional parameters
     */
    fun submitDiagnosis(
        imageFile: File,
        question: String? = null,
        audioFile: File? = null,
        temperature: Double? = null,
        humidity: Double? = null,
        weatherConditions: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val result = diagnosisRepository.createDiagnosis(
                    imageFile = imageFile,
                    audioFile = audioFile,
                    question = question,
                    farmerId = null, // TODO: Get from user session
                    latitude = currentLocationData?.latitude,
                    longitude = currentLocationData?.longitude,
                    province = currentLocationData?.province,
                    district = currentLocationData?.district,
                    temperature = temperature,
                    humidity = humidity,
                    weatherConditions = weatherConditions
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
     * Set loading state (used by Fragment for UI feedback)
     */
    fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Clear captured image
     */
    fun clearCapturedImage() {
        _capturedImage.value = null
    }

    /**
     * Clear diagnosis result
     */
    fun clearDiagnosisResult() {
        _diagnosisResult.value = null
    }
}
