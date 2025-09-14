package com.manutd.ronaldo.practicemakesperfect.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {
    @IODispatcher
    @Provides
    fun provideDispatcher() = Dispatchers.IO

    @DefaultDispatcher
    @Provides
    fun provideDefaultDispatcher() = Dispatchers.Default
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class IODispatcher

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class DefaultDispatcher

