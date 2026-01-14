package com.example.ai_crop_doctor.data.remote.api

import com.example.ai_crop_doctor.data.remote.dto.DiagnosisHistoryResponse
import com.example.ai_crop_doctor.data.remote.dto.DiagnosisResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * Retrofit API service for Diagnosis endpoints
 *
 * Connects to backend API at http://10.0.2.2:5050/api/v1/diagnose
 */
interface DiagnosisApiService {

    /**
     * Create a new diagnosis by uploading image and optional audio/text question
     *
     * POST /api/v1/diagnose
     *
     * @param image Image file (multipart)
     * @param question Text question (optional)
     * @param audio Audio file (optional)
     * @param farmerId Farmer identifier (optional)
     * @param latitude GPS latitude (optional)
     * @param longitude GPS longitude (optional)
     * @param province Province name (optional)
     * @param district District name (optional)
     * @param temperature Temperature in Celsius (optional)
     * @param humidity Humidity percentage (optional)
     * @param weatherConditions Weather description (optional)
     */
    @Multipart
    @POST("api/v1/diagnose")
    suspend fun createDiagnosis(
        @Part image: MultipartBody.Part,
        @Part("question") question: RequestBody?,
        @Part audio: MultipartBody.Part?,
        @Part("farmer_id") farmerId: RequestBody?,
        @Part("latitude") latitude: RequestBody?,
        @Part("longitude") longitude: RequestBody?,
        @Part("province") province: RequestBody?,
        @Part("district") district: RequestBody?,
        @Part("temperature") temperature: RequestBody?,
        @Part("humidity") humidity: RequestBody?,
        @Part("weather_conditions") weatherConditions: RequestBody?
    ): DiagnosisResponse

    /**
     * Get diagnosis by ID
     *
     * GET /api/v1/diagnose/{id}
     *
     * @param diagnosisId Diagnosis ID
     */
    @GET("api/v1/diagnose/{id}")
    suspend fun getDiagnosis(
        @Path("id") diagnosisId: Int
    ): DiagnosisResponse

    /**
     * Get diagnosis history for a farmer
     *
     * GET /api/v1/diagnose/history
     *
     * @param farmerId Farmer identifier (optional)
     * @param limit Number of results to return
     * @param offset Pagination offset
     */
    @GET("api/v1/diagnose/history")
    suspend fun getDiagnosisHistory(
        @Query("farmer_id") farmerId: String?,
        @Query("limit") limit: Int = 10,
        @Query("offset") offset: Int = 0
    ): DiagnosisHistoryResponse
}
