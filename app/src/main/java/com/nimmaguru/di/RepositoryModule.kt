package com.nimmaguru.di

import com.nimmaguru.data.repository.AuthRepositoryImpl
import com.nimmaguru.data.repository.ChatRepositoryImpl
import com.nimmaguru.data.repository.GuruRepositoryImpl
import com.nimmaguru.data.repository.UserRepositoryImpl
import com.nimmaguru.domain.repository.AuthRepository
import com.nimmaguru.domain.repository.ChatRepository
import com.nimmaguru.domain.repository.GuruRepository
import com.nimmaguru.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindGuruRepository(impl: GuruRepositoryImpl): GuruRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository
}
