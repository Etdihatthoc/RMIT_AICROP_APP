package com.example.ai_crop_doctor.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.ai_crop_doctor.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

// DataStore extension property
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constants.PREF_NAME)

/**
 * Hilt module that provides application-level dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provides the application context
     */
    @Provides
    @Singleton
    fun provideApplicationContext(@ApplicationContext context: Context): Context {
        return context
    }

    /**
     * Provides DataStore for storing user preferences and auth tokens
     */
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    /**
     * Provides IO Dispatcher for IO operations
     */
    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    /**
     * Provides Main Dispatcher for UI operations
     */
    @Provides
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    /**
     * Provides Default Dispatcher for CPU-intensive operations
     */
    @Provides
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}

/**
 * Qualifier annotations for different Coroutine Dispatchers
 */
@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class IoDispatcher

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class MainDispatcher

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class DefaultDispatcher
