package com.masterofpuppets.thepips.di

import com.masterofpuppets.thepips.data.audio.AudioTrackPipPlayer
import com.masterofpuppets.thepips.domain.audio.PipAudioPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AudioModule {

    @Provides
    @Singleton
    fun providePipAudioPlayer(): PipAudioPlayer {
        return AudioTrackPipPlayer()
    }
}