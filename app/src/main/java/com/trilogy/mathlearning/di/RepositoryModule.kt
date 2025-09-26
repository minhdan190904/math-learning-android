package com.trilogy.mathlearning.di

import com.google.firebase.auth.FirebaseAuth
import com.trilogy.mathlearning.data.repository.AuthRepositoryImpl
import com.trilogy.mathlearning.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth
    ): AuthRepository = AuthRepositoryImpl(auth)
}