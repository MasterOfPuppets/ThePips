package com.masterofpuppets.thepips.data.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.masterofpuppets.thepips.domain.audio.PipAudioPlayer
import com.masterofpuppets.thepips.domain.model.Pip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.sin

class AudioTrackPipPlayer : PipAudioPlayer {

    private val sampleRate = 44100
    private var audioTrack: AudioTrack? = null
    private var playJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    override fun playPip(pip: Pip) {
        stop() // Ensure previous playback is stopped

        playJob = scope.launch {
            // Grouping frequency and duration to iterate easily over the 8 slots
            val slots = listOf(
                Pair(pip.slot1Hz, pip.slot1DurationMs),
                Pair(pip.slot2Hz, pip.slot2DurationMs),
                Pair(pip.slot3Hz, pip.slot3DurationMs),
                Pair(pip.slot4Hz, pip.slot4DurationMs),
                Pair(pip.slot5Hz, pip.slot5DurationMs),
                Pair(pip.slot6Hz, pip.slot6DurationMs),
                Pair(pip.slot7Hz, pip.slot7DurationMs),
                Pair(pip.slot8Hz, pip.slot8DurationMs)
            )

            // Calculate total samples required for the entire buffer
            var totalSamples = 0
            for ((_, durationMs) in slots) {
                totalSamples += (sampleRate * durationMs) / 1000
            }

            // If all durations are 0, there is nothing to play
            if (totalSamples == 0) return@launch

            // 50ms of silence padding at the end to flush the hardware buffer cleanly
            val paddingSamples = (sampleRate * 50) / 1000
            val audioBuffer = ShortArray(totalSamples + paddingSamples)
            var currentIndex = 0

            // Generate sine waves or silence dynamically for each slot
            for ((freq, durationMs) in slots) {
                val samplesForSlot = (sampleRate * durationMs) / 1000

                // 3ms fade to prevent audio popping (clicks)
                val fadeDurationMs = 3
                val maxFadeSamples = (sampleRate * fadeDurationMs) / 1000
                val fadeSamples = minOf(maxFadeSamples, samplesForSlot / 2)

                for (i in 0 until samplesForSlot) {
                    val time = i / sampleRate.toDouble()
                    var sample = if (freq > 0) {
                        (sin(2.0 * Math.PI * freq * time) * Short.MAX_VALUE * pip.globalVolume)
                    } else {
                        0.0
                    }

                    // Apply Fade-in and Fade-out to smooth the sine wave ends
                    if (freq > 0 && fadeSamples > 0) {
                        if (i < fadeSamples) {
                            sample *= (i.toDouble() / fadeSamples) // Fade-in
                        } else if (i >= samplesForSlot - fadeSamples) {
                            sample *= ((samplesForSlot - i).toDouble() / fadeSamples) // Fade-out
                        }
                    }

                    audioBuffer[currentIndex++] = sample.toInt().toShort()
                }
            }

            // Configure AudioTrack (Requires API 26+)
            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(audioBuffer.size * 2) // 16-bit PCM = 2 bytes per sample
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack?.write(audioBuffer, 0, audioBuffer.size)
            audioTrack?.play()
        }
    }

    override fun stop() {
        playJob?.cancel()
        audioTrack?.let {
            if (it.playState == AudioTrack.PLAYSTATE_PLAYING) {
                it.stop()
            }
            it.release()
        }
        audioTrack = null
    }
}