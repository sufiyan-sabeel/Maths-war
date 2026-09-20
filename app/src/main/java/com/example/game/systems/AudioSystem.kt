package com.example.game.systems

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

class AudioSystem {

    private val sampleRate = 22050
    private val scope = CoroutineScope(Dispatchers.Default)

    var soundEnabled: Boolean = true
    var hapticsEnabled: Boolean = true

    private fun playTone(
        durationMs: Int,
        startFreq: Float,
        endFreq: Float,
        type: ToneType = ToneType.SINE,
        volume: Float = 0.6f
    ) {
        if (!soundEnabled) return
        scope.launch {
            try {
                val numSamples = (sampleRate * durationMs / 1000)
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val progress = i.toFloat() / numSamples
                    val currentFreq = startFreq + (endFreq - startFreq) * progress
                    val t = 2.0 * PI * currentFreq * i / sampleRate

                    val sampleValue = when (type) {
                        ToneType.SINE -> sin(t)
                        ToneType.SQUARE -> if (sin(t) >= 0) 1.0 else -1.0
                        ToneType.NOISE -> (Random.nextDouble() * 2.0 - 1.0)
                        ToneType.SAWTOOTH -> (2.0 * (t / (2.0 * PI) - kotlin.math.floor(t / (2.0 * PI) + 0.5)))
                    }

                    // Envelope: fast attack, linear decay
                    val envelope = (1.0f - progress).coerceIn(0f, 1f)
                    val finalSample = (sampleValue * envelope * volume * Short.MAX_VALUE).toInt()
                    buffer[i] = finalSample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
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
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(buffer, 0, buffer.size)
                audioTrack.play()
                // Wait for playback and release
                kotlinx.coroutines.delay(durationMs.toLong() + 50)
                audioTrack.release()
            } catch (e: Exception) {
                // Ignore audio track exceptions on container or headless test
            }
        }
    }

    enum class ToneType { SINE, SQUARE, NOISE, SAWTOOTH }

    fun playPunchSwing() = playTone(50, 280f, 140f, ToneType.NOISE, 0.4f)
    fun playKickSwing() = playTone(70, 220f, 90f, ToneType.NOISE, 0.5f)
    fun playHeavySwing() = playTone(120, 360f, 60f, ToneType.SAWTOOTH, 0.7f)
    fun playJump() = playTone(90, 260f, 520f, ToneType.SINE, 0.35f)
    fun playDoubleJump() = playTone(110, 420f, 740f, ToneType.SINE, 0.4f)
    fun playDash() = playTone(80, 500f, 180f, ToneType.NOISE, 0.45f)

    fun playHitImpact(combo: Int = 1) {
        val baseFreq = (300f + minOf(combo * 30f, 400f))
        playTone(90, baseFreq, 120f, ToneType.SQUARE, 0.6f)
    }

    fun playHeavyImpact() = playTone(160, 180f, 45f, ToneType.SQUARE, 0.8f)
    fun playBlock() = playTone(90, 800f, 600f, ToneType.SQUARE, 0.5f)
    fun playPlayerHurt() = playTone(140, 220f, 70f, ToneType.SAWTOOTH, 0.65f)
    fun playEnemyDefeat() = playTone(150, 440f, 880f, ToneType.SINE, 0.55f)
    fun playSpecialCast() = playTone(250, 400f, 1200f, ToneType.SAWTOOTH, 0.75f)

    fun playBossEntrance() = playTone(400, 110f, 55f, ToneType.SAWTOOTH, 0.85f)
    fun playBossAttack() = playTone(200, 220f, 90f, ToneType.SQUARE, 0.7f)

    fun playWaveStart() = playTone(180, 523f, 659f, ToneType.SINE, 0.5f)
    fun playWaveComplete() = playTone(280, 587f, 880f, ToneType.SINE, 0.6f)
    fun playVictory() = playTone(500, 523f, 1046f, ToneType.SINE, 0.7f)
    fun playDefeat() = playTone(600, 300f, 80f, ToneType.SAWTOOTH, 0.7f)
}
