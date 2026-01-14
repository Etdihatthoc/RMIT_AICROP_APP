package com.example.ai_crop_doctor.di

import android.content.Context
import androidx.room.Room
import com.example.ai_crop_doctor.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides database dependencies (Room database and DAOs)
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provides Room Database instance
     */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): com.example.ai_crop_doctor.data.local.database.AICropDoctorDatabase {
        return Room.databaseBuilder(
            context,
            com.example.ai_crop_doctor.data.local.database.AICropDoctorDatabase::class.java,
            Constants.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    /**
     * Provides DiagnosisDao for diagnosis database operations
     */
    @Provides
    @Singleton
    fun provideDiagnosisDao(database: com.example.ai_crop_doctor.data.local.database.AICropDoctorDatabase): com.example.ai_crop_doctor.data.local.dao.DiagnosisDao {
        return database.diagnosisDao()
    }

}
