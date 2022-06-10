package com.ingenico.demoacclib.di

import android.content.Context
import com.ingenico.persistence.AccDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun provideRoomAccInstance(
        @ApplicationContext context: Context
    ) = AccDatabase.getDatabase(context)

    @Singleton
    @Provides
    fun provideEmvCommonDao(databaseApp: AccDatabase) = databaseApp.emvCommonDao()

    @Singleton
    @Provides
    fun provideEmvAidDao(databaseApp: AccDatabase) = databaseApp.emvAidDao()

    @Singleton
    @Provides
    fun provideEmvCaKeyDao(databaseApp: AccDatabase) = databaseApp.emvCAKeyDao()

}