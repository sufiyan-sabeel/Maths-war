package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

class AudioManager(context: Context) {

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private val scope = CoroutineScope(Dispatchers.Default)
    private var bgmJob: Job? = null
    var soundEnabled: Boolean = true
    var hapticsEnabled: Boolean = true
    var bgmEnabled: Boolean = true

    // Plays a synthesized tone or chord sequence
    private fun playTone(frequencies: List<Int>, durationMs: Int, volume: Float = 0.6f) {
        if (!soundEnabled) return
        scope.launch {
            try {
                val sampleRate = 22050
                val numSamples = (sampleRate * (durationMs / 1000f)).toInt()
                val generatedSnd = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    var sampleVal = 0.0
                    for (freq in frequencies) {
                        val angle = 2.0 * Math.PI * i / (sampleRate.toDouble() / freq)
                        sampleVal += sin(angle)
                    }
                    sampleVal /= frequencies.size
                    // Apply fade in and fade out envelope to avoid clicks
                    val envelope = when {
                        i < 200 -> i / 200.0
                        i > numSamples - 300 -> (numSamples - i) / 300.0
                        else -> 1.0
                    }
                    generatedSnd[i] = (sampleVal * 32767 * volume * envelope).toInt().toShort()
                }

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
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
                    .setBufferSizeInBytes(numSamples * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(generatedSnd, 0, numSamples)
                audioTrack.play()
                delay(durationMs.toLong() + 50)
                audioTrack.stop()
                audioTrack.release()
            } catch (e: Exception) {
                // Ignore audio errors gracefully
            }
        }
    }

    private fun triggerHaptic(durationMs: Long, amplitude: Int = 180) {
        if (!hapticsEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(durationMs, amplitude))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(durationMs)
            }
        } catch (e: Exception) {
            // Ignore haptic errors gracefully
        }
    }

    fun playButtonClick() {
        playTone(listOf(520), 40, 0.4f)
        triggerHaptic(20, 100)
    }

    fun playCorrectAnswer(combo: Int) {
        // Ascending harmonic arpeggio based on combo
        val baseFreq = 440 + minOf(combo * 60, 600)
        scope.launch {
            playTone(listOf(baseFreq), 70, 0.5f)
            delay(50)
            playTone(listOf((baseFreq * 1.25).toInt(), (baseFreq * 1.5).toInt()), 120, 0.6f)
        }
        triggerHaptic(35, 160)
    }

    fun playIncorrectAnswer() {
        scope.launch {
            playTone(listOf(180, 130), 180, 0.6f)
            delay(120)
            playTone(listOf(110), 180, 0.7f)
        }
        triggerHaptic(90, 240)
    }

    fun playComboSurge(combo: Int) {
        val root = 523 // C5
        val chord = when (combo % 4) {
            0 -> listOf(root, (root * 1.25).toInt(), (root * 1.5).toInt())
            1 -> listOf((root * 1.125).toInt(), (root * 1.33).toInt(), (root * 1.66).toInt())
            2 -> listOf((root * 1.25).toInt(), (root * 1.5).toInt(), root * 2)
            else -> listOf(root * 2, (root * 2.5).toInt(), root * 3)
        }
        playTone(chord, 200, 0.7f)
        triggerHaptic(60, 200)
    }

    fun playMathRage() {
        scope.launch {
            for (freq in listOf(440, 554, 659, 880)) {
                playTone(listOf(freq), 60, 0.7f)
                delay(40)
            }
        }
        triggerHaptic(120, 255)
    }

    fun playEquationBreaker() {
        scope.launch {
            for (freq in listOf(330, 440, 550, 660, 880, 1100)) {
                playTone(listOf(freq), 70, 0.8f)
                delay(30)
            }
        }
        triggerHaptic(180, 255)
    }

    fun playBossWarning() {
        scope.launch {
            repeat(3) {
                playTone(listOf(220, 225), 140, 0.8f)
                delay(160)
            }
        }
        triggerHaptic(200, 255)
    }

    fun playVictory() {
        scope.launch {
            val notes = listOf(523, 659, 784, 1046) // C - E - G - C6
            for (n in notes) {
                playTone(listOf(n), 150, 0.7f)
                delay(120)
            }
            delay(100)
            playTone(listOf(784, 1046, 1318), 350, 0.8f)
        }
        triggerHaptic(100, 200)
    }

    fun playDefeat() {
        scope.launch {
            val notes = listOf(440, 392, 349, 293)
            for (n in notes) {
                playTone(listOf(n), 160, 0.6f)
                delay(140)
            }
        }
        triggerHaptic(150, 200)
    }

    fun startBgm() {
        if (!bgmEnabled || bgmJob?.isActive == true) return
        bgmJob = scope.launch {
            val baseNotes = listOf(110, 110, 165, 147, 110, 130, 147, 165)
            var idx = 0
            while (isActive && bgmEnabled) {
                val f = baseNotes[idx % baseNotes.size]
                playTone(listOf(f), 120, 0.15f)
                delay(240)
                idx++
            }
        }
    }

    fun stopBgm() {
        bgmJob?.cancel()
        bgmJob = null
    }

    fun release() {
        stopBgm()
    }
}
