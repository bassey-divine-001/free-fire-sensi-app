package com.delex.ffsensiboost.di

import android.content.Context
import com.delex.ffsensiboost.domain.repository.DeviceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideDeviceRepository(
        @ApplicationContext context: Context
    ): DeviceRepository {
        return DeviceRepository(context)
    }
}
