package com.masterofpuppets.thepips.di

import com.masterofpuppets.thepips.data.local.PipDao
import com.masterofpuppets.thepips.data.local.ScheduleDao
import com.masterofpuppets.thepips.data.repository.PipRepositoryImpl
import com.masterofpuppets.thepips.domain.repository.PipRepository
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
    fun providePipRepository(pipDao: PipDao, scheduleDao: ScheduleDao): PipRepository {
        return PipRepositoryImpl(pipDao, scheduleDao)
    }
}