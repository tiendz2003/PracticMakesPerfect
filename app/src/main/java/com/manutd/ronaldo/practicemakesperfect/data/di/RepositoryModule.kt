package com.manutd.ronaldo.practicemakesperfect.data.di

import com.manutd.ronaldo.practicemakesperfect.data.repository.NewsRepositoryImpl
import com.manutd.ronaldo.practicemakesperfect.domain.repository.NewsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindNewsRepository(
        newsRepositoryImpl: NewsRepositoryImpl
    ): NewsRepository

}