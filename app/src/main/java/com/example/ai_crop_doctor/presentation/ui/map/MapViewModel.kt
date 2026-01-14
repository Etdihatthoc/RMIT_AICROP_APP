package com.example.ai_crop_doctor.presentation.ui.map

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai_crop_doctor.domain.model.EpidemicAlert
import com.example.ai_crop_doctor.util.LocationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _epidemicAlerts = MutableLiveData<List<EpidemicAlert>>()
    val epidemicAlerts: LiveData<List<EpidemicAlert>> = _epidemicAlerts

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _currentLocation = MutableLiveData<LocationHelper.LocationData?>()
    val currentLocation: LiveData<LocationHelper.LocationData?> = _currentLocation

    init {
        loadEpidemicAlerts()
    }

    /**
     * Load epidemic alerts from API
     * For demo purposes, using mock data
     */
    fun loadEpidemicAlerts() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // TODO: Replace with actual API call
                // For now, using mock data
                val mockAlerts = getMockEpidemicAlerts()
                _epidemicAlerts.value = mockAlerts
                Timber.d("Loaded ${mockAlerts.size} epidemic alerts")
            } catch (e: Exception) {
                Timber.e(e, "Error loading epidemic alerts")
                _errorMessage.value = "Không thể tải bản đồ dịch bệnh"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Get current user location
     */
    fun getCurrentLocation() {
        viewModelScope.launch {
            try {
                val location = LocationHelper.getCurrentLocation(context)
                _currentLocation.value = location
                Timber.d("Got current location: ${location?.province}")
            } catch (e: Exception) {
                Timber.e(e, "Error getting current location")
            }
        }
    }

    /**
     * Filter alerts by severity
     */
    fun filterBySeverity(severity: String?) {
        val allAlerts = _epidemicAlerts.value ?: return

        val filtered = if (severity == null) {
            allAlerts
        } else {
            allAlerts.filter { it.severity.equals(severity, ignoreCase = true) }
        }

        _epidemicAlerts.value = filtered
    }

    /**
     * Filter alerts by disease type
     */
    fun filterByDiseaseType(diseaseType: String?) {
        val allAlerts = _epidemicAlerts.value ?: return

        val filtered = if (diseaseType == null) {
            allAlerts
        } else {
            allAlerts.filter { it.diseaseType.contains(diseaseType, ignoreCase = true) }
        }

        _epidemicAlerts.value = filtered
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Mock epidemic alerts data for demonstration
     */
    private fun getMockEpidemicAlerts(): List<EpidemicAlert> {
        val now = Date()
        return listOf(
            EpidemicAlert(
                id = 1,
                diseaseType = "Bệnh đạo ôn",
                severity = "high",
                affectedArea = "Châu Đốc",
                province = "An Giang",
                district = "Châu Đốc",
                latitude = 10.7051,
                longitude = 105.1180,
                radius = 15.0,
                caseCount = 45,
                description = "Dịch bệnh đạo ôn đang lan rộng trong khu vực trồng lúa. Cần áp dụng biện pháp phòng ngừa ngay.",
                recommendations = "Phun thuốc diệt khuẩn, tăng cường thoát nước, loại bỏ cây nhiễm bệnh.",
                reportedAt = Date(now.time - 86400000), // 1 day ago
                updatedAt = now,
                isActive = true
            ),
            EpidemicAlert(
                id = 2,
                diseaseType = "Bệnh bạc lá",
                severity = "medium",
                affectedArea = "Tam Nông",
                province = "Đồng Tháp",
                district = "Tam Nông",
                latitude = 10.6481,
                longitude = 105.5972,
                radius = 10.0,
                caseCount = 28,
                description = "Phát hiện bệnh bạc lá trên cây lúa trong khu vực.",
                recommendations = "Sử dụng giống chống chịu, áp dụng phân bón cân đối.",
                reportedAt = Date(now.time - 172800000), // 2 days ago
                updatedAt = now,
                isActive = true
            ),
            EpidemicAlert(
                id = 3,
                diseaseType = "Bệnh khô vằn",
                severity = "low",
                affectedArea = "Châu Thành",
                province = "An Giang",
                district = "Châu Thành",
                latitude = 10.6703,
                longitude = 105.1540,
                radius = 8.0,
                caseCount = 12,
                description = "Một số hộ dân báo cáo bệnh khô vằn xuất hiện.",
                recommendations = "Theo dõi và xử lý cục bộ, tăng cường dinh dưỡng cho cây.",
                reportedAt = Date(now.time - 259200000), // 3 days ago
                updatedAt = now,
                isActive = true
            ),
            EpidemicAlert(
                id = 4,
                diseaseType = "Sâu cuốn lá",
                severity = "high",
                affectedArea = "Cao Lãnh",
                province = "Đồng Tháp",
                district = "Cao Lãnh",
                latitude = 10.4596,
                longitude = 105.6327,
                radius = 12.0,
                caseCount = 67,
                description = "Sâu cuốn lá bùng phát mạnh, gây thiệt hại lớn cho cây lúa.",
                recommendations = "Phun thuốc trừ sâu, thu gom và tiêu hủy sâu bệnh.",
                reportedAt = Date(now.time - 43200000), // 12 hours ago
                updatedAt = now,
                isActive = true
            ),
            EpidemicAlert(
                id = 5,
                diseaseType = "Bệnh đốm lá vi khuẩn",
                severity = "medium",
                affectedArea = "Long Xuyên",
                province = "An Giang",
                district = "Long Xuyên",
                latitude = 10.3833,
                longitude = 105.4358,
                radius = 10.0,
                caseCount = 31,
                description = "Bệnh đốm lá vi khuẩn xuất hiện trên diện rộng.",
                recommendations = "Cải thiện thoát nước, phun thuốc diệt khuẩn định kỳ.",
                reportedAt = Date(now.time - 345600000), // 4 days ago
                updatedAt = now,
                isActive = true
            )
        )
    }
}
