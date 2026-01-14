package com.example.ai_crop_doctor.util

/**
 * Application-wide constants
 */
object Constants {

    // API Configuration
    const val API_BASE_PATH = "api/v1"
    const val CONNECT_TIMEOUT = 30L // seconds
    const val READ_TIMEOUT = 30L // seconds
    const val WRITE_TIMEOUT = 30L // seconds

    // Shared Preferences / DataStore
    const val PREF_NAME = "ai_crop_doctor_prefs"
    const val KEY_AUTH_TOKEN = "auth_token"
    const val KEY_EXPERT_ID = "expert_id"
    const val KEY_FARMER_ID = "farmer_id"

    // Database
    const val DATABASE_NAME = "ai_crop_doctor_db"
    const val DATABASE_VERSION = 1

    // Image Upload
    const val MAX_IMAGE_SIZE_MB = 10
    const val IMAGE_QUALITY = 85 // JPEG quality percentage
    const val IMAGE_MAX_WIDTH = 1920
    const val IMAGE_MAX_HEIGHT = 1080

    // Diagnosis Thresholds
    const val CONFIDENCE_HIGH_THRESHOLD = 0.80 // ≥80% is high confidence
    const val CONFIDENCE_LOW_THRESHOLD = 0.50 // <50% is low confidence
    const val EXPERT_REVIEW_THRESHOLD = 0.70 // <70% triggers expert review

    // Map Configuration
    const val DEFAULT_MAP_ZOOM = 10f
    const val EPIDEMIC_ALERT_ZOOM = 12f

    // Pagination
    const val DEFAULT_PAGE_SIZE = 10
    const val MAX_PAGE_SIZE = 50

    // Request Codes
    const val REQUEST_CODE_CAMERA = 1001
    const val REQUEST_CODE_GALLERY = 1002
    const val REQUEST_CODE_LOCATION = 1003

    // Intent Extras
    const val EXTRA_DIAGNOSIS_ID = "diagnosis_id"
    const val EXTRA_DISEASE_NAME = "disease_name"
    const val EXTRA_PROVINCE = "province"
    const val EXTRA_IMAGE_PATH = "image_path"

    // Vietnamese Provinces (Mekong Delta - Rice growing regions)
    val VIETNAM_PROVINCES = listOf(
        "An Giang",
        "Bạc Liêu",
        "Bến Tre",
        "Cà Mau",
        "Cần Thơ",
        "Đồng Tháp",
        "Hậu Giang",
        "Kiên Giang",
        "Long An",
        "Sóc Trăng",
        "Tiền Giang",
        "Trà Vinh",
        "Vĩnh Long"
    )

    // Common Rice Diseases
    val RICE_DISEASES = listOf(
        "Đạo ôn lúa (Rice Blast)",
        "Đốm nâu lúa (Brown Spot)",
        "Bạc lá lúa (Bacterial Leaf Blight)",
        "Khảo lá lúa (Sheath Blight)",
        "Bệnh vàng lá lúa (Yellow Dwarf)",
        "Bệnh lùn xoắn lá (Grassy Stunt)"
    )
}
