package com.trilogy.mathlearning.di

import android.app.Application
import com.trilogy.mathlearning.network.NetworkMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideNetworkMonitor(application: Application): NetworkMonitor {
        return NetworkMonitor(application)
    }
}