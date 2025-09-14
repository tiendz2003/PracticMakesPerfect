package com.manutd.ronaldo.practicemakesperfect.data.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.manutd.ronaldo.practicemakesperfect.data.service.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "https://raw.githubusercontent.com/Akaizz/static/master/"

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(logging: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient,json: Json): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            isLenient = true
            encodeDefaults = true
            ignoreUnknownKeys = true
            explicitNulls = false
        }
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}