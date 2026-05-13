package com.garrettforsyth.tankwatch.di

import com.garrettforsyth.tankwatch.data.repository.TankWatchRepository
import com.garrettforsyth.tankwatch.data.repository.TankWatchRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTankWatchRepository(
        impl: TankWatchRepositoryImpl
    ): TankWatchRepository
}