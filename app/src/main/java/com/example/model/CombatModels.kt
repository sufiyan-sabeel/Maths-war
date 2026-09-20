package com.example.model

import androidx.compose.ui.graphics.Color

/**
 * Poses for the original stickman character.
 */
enum class StickmanPose {
    IDLE,
    RUN,
    JUMP,
    DODGE,
    BLOCK,
    BASIC_ATTACK_PUNCH,
    BASIC_ATTACK_KICK,
    HIT_REACTION,
    SPECIAL_CHANNEL,
    SPECIAL_RELEASE,
    VICTORY,
    DEFEAT
}

/**
 * Aesthetic skin/cosmetics for the stickman.
 */
data class StickmanSkin(
    val id: String,
    val name: String,
    val primaryColor: Color,
    val auraColor: Color,
    val symbolColor: Color,
    val requiredLevel: Int,
    val description: String
) {
    companion object {
        val ALL_SKINS = listOf(
            StickmanSkin("classic", "Neon Amber", Color(0xFFFF9100), Color(0xFFFF6D00), Color(0xFFFFFFFF), 1, "The original mathematical warrior"),
            StickmanSkin("cyan_cyber", "Cyber Matrix", Color(0xFF00E5FF), Color(0xFF00B0FF), Color(0xFFE0F7FA), 3, "Resonates with digital vector calculations"),
            StickmanSkin("quantum_violet", "Quantum Flux", Color(0xFFD500F9), Color(0xFFAA00FF), Color(0xFFF3E5F5), 5, "Harnesses complex coordinate theory"),
            StickmanSkin("golden_euler", "Euler Gold", Color(0xFFFFD600), Color(0xFFFFAB00), Color(0xFFFFF9C4), 8, "Attuned to identity e^(i*pi) + 1 = 0"),
            StickmanSkin("void_phantom", "Calculus Void", Color(0xFF00E676), Color(0xFF00C853), Color(0xFFE8F5E9), 12, "Manifests limits approaching infinity")
        )
    }
}

/**
 * Floating combat text shown in arena (damage, combos, effects).
 */
data class FloatingCombatText(
    val id: Long,
    val text: String,
    val xRatio: Float,
    val yRatio: Float,
    val color: Color,
    val sizeSp: Float = 16f,
    val alpha: Float = 1f,
    val vy: Float = -0.003f
)

/**
 * Stylized geometric and mathematical particle.
 */
data class MathParticle(
    val id: Long,
    val symbol: String,
    val xRatio: Float,
    val yRatio: Float,
    val vx: Float,
    val vy: Float,
    val color: Color,
    val size: Float,
    val alpha: Float = 1f,
    val rotation: Float = 0f,
    val vRot: Float = 0f,
    val lifeMs: Long = 800L
)
