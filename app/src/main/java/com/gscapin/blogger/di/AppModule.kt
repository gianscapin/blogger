package com.gscapin.blogger.di

import com.gscapin.blogger.data.remote.auth.AuthDataSource
import com.gscapin.blogger.data.remote.home.HomeDataSource
import com.gscapin.blogger.data.remote.message.MessageDataSource
import com.gscapin.blogger.data.remote.post.PostDataSource
import com.gscapin.blogger.data.remote.profile.ProfileDataSource
import com.gscapin.blogger.domain.auth.AuthRepoImpl
import com.gscapin.blogger.domain.home.HomeRepoImpl
import com.gscapin.blogger.domain.message.MessageRepoImpl
import com.gscapin.blogger.domain.post.PostRepoImpl
import com.gscapin.blogger.domain.profile.ProfileRepoImpl
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

    @Singleton
    fun provideHomeRepository(dataSource: HomeDataSource) = HomeRepoImpl(dataSource)

    @Singleton
    fun provideProfileRepository(dataSource: ProfileDataSource) = ProfileRepoImpl(dataSource)

    @Singleton
    fun providePostRepository(dataSource: PostDataSource) = PostRepoImpl(dataSource)

    @Singleton
    fun provideMessageRepository(dataSource: MessageDataSource) = MessageRepoImpl(dataSource)
}