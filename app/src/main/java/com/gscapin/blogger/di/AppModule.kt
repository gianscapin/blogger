package com.gscapin.blogger.di

import com.gscapin.blogger.data.remote.auth.AuthDataSource
import com.gscapin.blogger.domain.auth.AuthRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    fun provideAuthRepository(dataSource: AuthDataSource) = AuthRepoImpl(dataSource)
}