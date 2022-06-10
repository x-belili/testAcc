package com.ingenico.demoacclib.domain.di

import com.ingenico.demoacclib.data.repository.EmvAidRepository
import com.ingenico.demoacclib.data.repository.EmvCaKeyRepository
import com.ingenico.demoacclib.data.repository.EmvCommonRepository
import com.ingenico.persistence.dao.EmvAidDao
import com.ingenico.persistence.dao.EmvCAkeyDao
import com.ingenico.persistence.dao.EmvCommonDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideEmvCommonRepository(
        emvCommonDao: EmvCommonDao
    ): EmvCommonRepository {
        return EmvCommonRepository(emvCommonDao)
    }

    @Singleton
    @Provides
    fun provideEmvAidRepository(
        emvAidDao: EmvAidDao
    ): EmvAidRepository {
        return EmvAidRepository(emvAidDao)
    }

    @Singleton
    @Provides
    fun provideEmvCaKeyRepository(
        emvCAkeyDao: EmvCAkeyDao
    ): EmvCaKeyRepository {
        return EmvCaKeyRepository(emvCAkeyDao)
    }
}