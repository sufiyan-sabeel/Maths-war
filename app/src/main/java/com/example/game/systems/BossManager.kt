package com.example.game.systems

import androidx.compose.ui.graphics.Color
import com.example.game.model.EnemyType

data class BossPhaseInfo(
    val phaseNumber: Int,
    val phaseName: String,
    val formulaDisplay: String,
    val description: String
)

data class BossProfile(
    val type: EnemyType,
    val name: String,
    val subtitle: String,
    val color: Color,
    val phases: List<BossPhaseInfo>
)

class BossManager {

    val allBosses = listOf(
        BossProfile(
            type = EnemyType.BOSS_INTEGER_CORE,
            name = "THE INTEGER CORE",
            subtitle = "Sovereign of Absolute Values",
            color = Color(0xFFFF5252),
            phases = listOf(
                BossPhaseInfo(1, "Magnitude Inversion", "|x - 8| = 12", "Fires rapid linear integer beams"),
                BossPhaseInfo(2, "Negative Multiplier", "-4x + 16 = -8", "Triple radiating formula barrages"),
                BossPhaseInfo(3, "Factor Storm", "x² - 25 = 0", "Rotating buzzsaw roots and heavy shockwaves"),
                BossPhaseInfo(4, "Singularity Collapse", "lim(x→0) 1/x", "High-frequency arena-wide integer rays")
            )
        ),
        BossProfile(
            type = EnemyType.BOSS_FRACTION_ENGINE,
            name = "THE FRACTION ENGINE",
            subtitle = "Split-Dimension Core",
            color = Color(0xFFFFB300),
            phases = listOf(
                BossPhaseInfo(1, "Numerator/Denominator Split", "p / q", "Splits into orbiting fractional shields"),
                BossPhaseInfo(2, "Cross-Multiplication", "a/b = c/d", "Dual intersecting cutting beams"),
                BossPhaseInfo(3, "Improper Cataclysm", "x / 20 = 7/2", "Spawns cascade of splitting projectiles"),
                BossPhaseInfo(4, "Asymptotic Singularity", "f(x) = 1/(x-1)", "Distorts gravitational space")
            )
        ),
        BossProfile(
            type = EnemyType.BOSS_ALGEBRA_BEAST,
            name = "THE ALGEBRA BEAST",
            subtitle = "Keeper of Unknown Variables",
            color = Color(0xFF00E676),
            phases = listOf(
                BossPhaseInfo(1, "Single Variable Isolation", "2x + 5 = 19", "Rapid charge attacks and coordinate slams"),
                BossPhaseInfo(2, "Quadratic Factorization", "ax² + bx + c = 0", "Launches homing parabolic projectile arcs"),
                BossPhaseInfo(3, "Distributive Surge", "3(x + 2) = 21", "Radial expanding shockwave rings"),
                BossPhaseInfo(4, "System of Equations", "{ x+y=10, x-y=4 }", "Creates shadow equation clone")
            )
        ),
        BossProfile(
            type = EnemyType.BOSS_FUNCTION_MACHINE,
            name = "THE FUNCTION MACHINE",
            subtitle = "Omnipotent Operator of Relations",
            color = Color(0xFFD500F9),
            phases = listOf(
                BossPhaseInfo(1, "Input/Output Mapping", "f(x) = 2x² - 3", "Synthesizes mathematical waveforms"),
                BossPhaseInfo(2, "Composite Transmutation", "f(g(x))", "Periodic shifts between operator affinities"),
                BossPhaseInfo(3, "Harmonic Oscillations", "A sin(ωt + φ)", "Sine-wave laser beam across floor"),
                BossPhaseInfo(4, "Exponential Runaway", "e^x", "Accelerating attack speed and double projection")
            )
        ),
        BossProfile(
            type = EnemyType.BOSS_EQUATION,
            name = "THE EQUATION",
            subtitle = "The Grand Mathematical Singularity",
            color = Color(0xFFFF1744),
            phases = listOf(
                BossPhaseInfo(1, "Euler's Identity", "e^(iπ) + 1 = 0", "Fires rotating complex coordinate rings"),
                BossPhaseInfo(2, "Fundamental Theorem", "∫ f(x) dx", "Sweeps screen with massive integral waves"),
                BossPhaseInfo(3, "Taylor Series Expansion", "∑ (f^(n)(a)/n!)(x-a)^n", "Infinite projectile fountain"),
                BossPhaseInfo(4, "Universal Axiom", "1 = 1", "Ultimate phase: relentless equation storms")
            )
        )
    )

    fun getProfile(type: EnemyType): BossProfile? {
        return allBosses.find { it.type == type }
    }
}
