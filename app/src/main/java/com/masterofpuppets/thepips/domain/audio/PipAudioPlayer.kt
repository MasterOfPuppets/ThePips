package com.masterofpuppets.thepips.domain.audio

import com.masterofpuppets.thepips.domain.model.Pip

interface PipAudioPlayer {
    /**
     * Synthesizes and plays the audio for the given Pip.
     */
    fun playPip(pip: Pip)

    /**
     * Stops any ongoing playback and releases resources if necessary.
     */
    fun stop()
}