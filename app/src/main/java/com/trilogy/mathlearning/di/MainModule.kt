package com.trilogy.mathlearning.di

import android.content.Context
import androidx.credentials.CredentialManager
import com.trilogy.mathlearning.data.repository.DataStoreRepositoryImpl
import com.trilogy.mathlearning.domain.repository.DataStoreRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainModule {

    @Provides
    @Singleton
    fun provideDataStoreRepository(
        impl: DataStoreRepositoryImpl
    ): DataStoreRepository = impl

    @Provides
    @Singleton
    fun provideCredentialManager(@ApplicationContext context: Context): CredentialManager =
        CredentialManager.create(context)
}
