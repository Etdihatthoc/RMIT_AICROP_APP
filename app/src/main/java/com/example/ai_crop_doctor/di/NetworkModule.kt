package com.example.ai_crop_doctor.di

import com.example.ai_crop_doctor.BuildConfig
import com.example.ai_crop_doctor.util.Constants
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt module that provides networking dependencies (Retrofit, OkHttp, API services)
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Provides Gson instance for JSON serialization/deserialization
     */
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .serializeNulls()
            .create()
    }

    /**
     * Provides HttpLoggingInterceptor for debugging API calls
     * Only logs in DEBUG builds
     */
    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor { message ->
            Timber.tag("OkHttp").d(message)
        }.apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    /**
     * Provides OkHttpClient with interceptors and timeouts
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(Constants.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(Constants.READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(Constants.WRITE_TIMEOUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    /**
     * Provides Retrofit instance configured with base URL and converters
     */
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        Timber.d("Creating Retrofit with BASE_URL: ${BuildConfig.BASE_URL}")

        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    /**
     * Provides DiagnosisApiService for diagnosis endpoints
     * Will be created in Phase 2
     */
    // @Provides
    // @Singleton
    // fun provideDiagnosisApiService(retrofit: Retrofit): DiagnosisApiService {
    //     return retrofit.create(DiagnosisApiService::class.java)
    // }

    /**
     * Provides EpidemicApiService for epidemic alert endpoints
     * Will be created in Phase 4
     */
    // @Provides
    // @Singleton
    // fun provideEpidemicApiService(retrofit: Retrofit): EpidemicApiService {
    //     return retrofit.create(EpidemicApiService::class.java)
    // }

    /**
     * Provides ExpertApiService for expert validation endpoints
     * Will be created in Phase 5
     */
    // @Provides
    // @Singleton
    // fun provideExpertApiService(retrofit: Retrofit): ExpertApiService {
    //     return retrofit.create(ExpertApiService::class.java)
    // }
}
