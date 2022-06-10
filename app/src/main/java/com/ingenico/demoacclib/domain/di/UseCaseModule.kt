package com.ingenico.demoacclib.domain.di

import com.ingenico.demoacclib.data.repository.EmvAidRepository
import com.ingenico.demoacclib.data.repository.EmvCaKeyRepository
import com.ingenico.demoacclib.data.repository.EmvCommonRepository
import com.ingenico.demoacclib.domain.usecase.EmvAidUseCase
import com.ingenico.demoacclib.domain.usecase.EmvCakeyUseCase
import com.ingenico.demoacclib.domain.usecase.EmvCommonUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Singleton
    @Provides
    fun provideEmvAidUseCase(
        emvAidRepository: EmvAidRepository
    ): EmvAidUseCase {
        return EmvAidUseCase(emvAidRepository)
    }

    @Singleton
    @Provides
    fun provideEmvCakeyUseCase(
        emvCaKeyRepository: EmvCaKeyRepository
    ): EmvCakeyUseCase {
        return EmvCakeyUseCase(emvCaKeyRepository)
    }

    @Singleton
    @Provides
    fun provideCommonUseCase(
        emvCommonRepository: EmvCommonRepository
    ): EmvCommonUseCase {
        return EmvCommonUseCase(emvCommonRepository)
    }
}