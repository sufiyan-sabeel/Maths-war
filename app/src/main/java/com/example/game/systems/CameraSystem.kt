package com.example.game.systems

import com.example.game.model.CameraState
import com.example.game.model.PlayerFighter
import com.example.game.model.Vec2
import kotlin.random.Random

class CameraSystem(
    val viewportWidth: Float = 1280f,
    val viewportHeight: Float = 720f
) {
    val state: CameraState = CameraState()

    fun update(
        dt: Float,
        player: PlayerFighter,
        isBossActive: Boolean
    ) {
        // Target tracking with predictive lead
        val leadX = if (player.isFacingRight) 100f else -100f
        val targetX = player.pos.x + leadX
        val targetY = player.pos.y - 120f

        // Smooth camera lerp
        val smoothFactor = 6.0f * dt
        state.pos.x += (targetX - state.pos.x) * smoothFactor.coerceIn(0f, 1f)
        state.pos.y += (targetY - state.pos.y) * smoothFactor.coerceIn(0f, 1f)

        // Dynamic Zoom
        state.targetZoom = if (isBossActive) 0.85f else 1.0f
        state.zoom += (state.targetZoom - state.zoom) * (3.0f * dt).coerceIn(0f, 1f)

        // Camera Shake
        if (state.shakeTimer > 0f) {
            state.shakeTimer -= dt
            val intensity = state.shakeIntensity * (state.shakeTimer / 0.25f).coerceIn(0f, 1f)
            val shakeOffsetX = (Random.nextFloat() - 0.5f) * intensity * 2f
            val shakeOffsetY = (Random.nextFloat() - 0.5f) * intensity * 2f
            state.pos.x += shakeOffsetX
            state.pos.y += shakeOffsetY
        }
    }

    fun addShake(intensity: Float, duration: Float = 0.22f) {
        state.shakeIntensity = maxOf(state.shakeIntensity, intensity)
        state.shakeTimer = duration
    }

    fun punchZoom(zoomDelta: Float = 0.08f) {
        state.zoom = (state.zoom + zoomDelta).coerceIn(0.7f, 1.3f)
    }
}
