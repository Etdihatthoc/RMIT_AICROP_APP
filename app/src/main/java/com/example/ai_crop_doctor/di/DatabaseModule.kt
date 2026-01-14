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
     * Will be implemented in Phase 3 with Room database setup
     */
    // @Provides
    // @Singleton
    // fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
    //     return Room.databaseBuilder(
    //         context,
    //         AppDatabase::class.java,
    //         Constants.DATABASE_NAME
    //     )
    //         .fallbackToDestructiveMigration() // For development - remove in production
    //         .build()
    // }

    /**
     * Provides DiagnosisDao for diagnosis database operations
     * Will be implemented in Phase 3
     */
    // @Provides
    // @Singleton
    // fun provideDiagnosisDao(database: AppDatabase): DiagnosisDao {
    //     return database.diagnosisDao()
    // }

    /**
     * Provides EpidemicAlertDao for epidemic alert database operations
     * Will be implemented in Phase 4
     */
    // @Provides
    // @Singleton
    // fun provideEpidemicAlertDao(database: AppDatabase): EpidemicAlertDao {
    //     return database.epidemicAlertDao()
    // }
}
