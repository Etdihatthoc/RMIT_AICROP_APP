package com.example.ai_crop_doctor.presentation.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai_crop_doctor.domain.model.Diagnosis
import com.example.ai_crop_doctor.domain.repository.DiagnosisRepository
import com.example.ai_crop_doctor.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val diagnosisRepository: DiagnosisRepository
) : ViewModel() {

    private val _diagnoses = MutableLiveData<List<Diagnosis>>()
    val diagnoses: LiveData<List<Diagnosis>> = _diagnoses

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isEmpty = MutableLiveData<Boolean>(false)
    val isEmpty: LiveData<Boolean> = _isEmpty

    // Filter state
    private var currentFarmerId: String? = null
    private var currentSearchQuery: String? = null

    init {
        loadDiagnosisHistory()
    }

    /**
     * Load diagnosis history from repository
     */
    fun loadDiagnosisHistory(
        farmerId: String? = currentFarmerId,
        forceRefresh: Boolean = false
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val result = diagnosisRepository.getDiagnosisHistory(
                    farmerId = farmerId,
                    limit = 100,
                    offset = 0
                )

                when (result) {
                    is Resource.Success -> {
                        val diagnosisList = result.data ?: emptyList()
                        _diagnoses.value = diagnosisList
                        _isEmpty.value = diagnosisList.isEmpty()
                        Timber.d("Loaded ${diagnosisList.size} diagnoses")
                    }
                    is Resource.Error -> {
                        Timber.e("Failed to load diagnoses: ${result.message}")
                        _errorMessage.value = result.message
                        _isEmpty.value = _diagnoses.value?.isEmpty() ?: true
                    }
                    is Resource.Loading -> {
                        // Loading state already set
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Exception loading diagnoses")
                _errorMessage.value = "Có lỗi xảy ra: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Search diagnoses by disease name
     */
    fun searchDiagnoses(query: String) {
        currentSearchQuery = query

        if (query.isBlank()) {
            loadDiagnosisHistory()
            return
        }

        val currentList = _diagnoses.value ?: emptyList()
        val filteredList = currentList.filter { diagnosis ->
            diagnosis.diseaseDetected.contains(query, ignoreCase = true) ||
            diagnosis.province?.contains(query, ignoreCase = true) == true ||
            diagnosis.district?.contains(query, ignoreCase = true) == true
        }

        _diagnoses.value = filteredList
        _isEmpty.value = filteredList.isEmpty()
    }

    /**
     * Filter by expert reviewed status
     */
    fun filterByExpertReviewed(expertReviewed: Boolean?) {
        val currentList = _diagnoses.value ?: emptyList()

        val filteredList = if (expertReviewed == null) {
            currentList
        } else {
            currentList.filter { it.expertReviewed == expertReviewed }
        }

        _diagnoses.value = filteredList
        _isEmpty.value = filteredList.isEmpty()
    }

    /**
     * Filter by confidence level
     */
    fun filterByConfidence(minConfidence: Double?) {
        val currentList = _diagnoses.value ?: emptyList()

        val filteredList = if (minConfidence == null) {
            currentList
        } else {
            currentList.filter { it.confidence >= minConfidence }
        }

        _diagnoses.value = filteredList
        _isEmpty.value = filteredList.isEmpty()
    }

    /**
     * Clear all filters and reload
     */
    fun clearFilters() {
        currentSearchQuery = null
        loadDiagnosisHistory()
    }

    /**
     * Refresh diagnosis history
     */
    fun refresh() {
        loadDiagnosisHistory(forceRefresh = true)
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }
}
