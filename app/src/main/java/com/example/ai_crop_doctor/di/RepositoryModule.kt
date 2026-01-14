package com.example.ai_crop_doctor.di

import com.example.ai_crop_doctor.data.local.dao.DiagnosisDao
import com.example.ai_crop_doctor.data.remote.api.DiagnosisApiService
import com.example.ai_crop_doctor.data.repository.DiagnosisRepositoryImpl
import com.example.ai_crop_doctor.domain.repository.DiagnosisRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides repository implementations
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideDiagnosisRepository(
        apiService: DiagnosisApiService,
        diagnosisDao: DiagnosisDao
    ): DiagnosisRepository {
        return DiagnosisRepositoryImpl(apiService, diagnosisDao)
    }
}
