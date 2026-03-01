package com.masterofpuppets.thepips.di

import android.content.Context
import androidx.room.Room
import com.masterofpuppets.thepips.data.local.PipDao
import com.masterofpuppets.thepips.data.local.ScheduleDao
import com.masterofpuppets.thepips.data.local.ThePipsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideThePipsDatabase(@ApplicationContext context: Context): ThePipsDatabase {
        return Room.databaseBuilder(
            context,
            ThePipsDatabase::class.java,
            ThePipsDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun providePipDao(database: ThePipsDatabase): PipDao {
        return database.pipDao
    }

    @Provides
    @Singleton
    fun provideScheduleDao(database: ThePipsDatabase): ScheduleDao {
        return database.scheduleDao
    }
}