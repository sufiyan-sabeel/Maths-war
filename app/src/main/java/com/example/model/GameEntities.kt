package com.example.model

import androidx.compose.ui.graphics.Color

/**
 * Player state in active combat.
 */
data class PlayerCombatState(
    val maxHp: Float = 100f,
    val hp: Float = 100f,
    val shield: Float = 0f,
    val maxShield: Float = 50f,
    val specialMeter: Float = 0f, // 0 to 100
    val maxSpecial: Float = 100f,
    val currentPose: StickmanPose = StickmanPose.IDLE,
    val isMathRage: Boolean = false,
    val isEquationBreakerReady: Boolean = false,
    val poseTimerMs: Long = 0L,
    val skin: StickmanSkin = StickmanSkin.ALL_SKINS[0]
)

/**
 * Enemy stickman/construct in active combat.
 */
data class EnemyCombatState(
    val name: String,
    val title: String,
    val maxHp: Float,
    val hp: Float,
    val shield: Float,
    val maxShield: Float,
    val color: Color,
    val pose: StickmanPose = StickmanPose.IDLE,
    val isBoss: Boolean = false,
    val currentPhase: Int = 1,
    val totalPhases: Int = 1,
    val symbolAffinity: String = "+",
    val attackName: String = "Formula Strike"
)

/**
 * Mathematical boss definition with multi-phase equations.
 */
data class BossDefinition(
    val id: String,
    val name: String,
    val subtitle: String,
    val topic: MathTopic,
    val worldId: Int,
    val color: Color,
    val phaseDescriptions: List<String>,
    val phaseEquations: List<String>,
    val specialMechanic: String
) {
    companion object {
        val ALL_BOSSES = listOf(
            BossDefinition(
                id = "boss_integer",
                name = "THE INTEGER",
                subtitle = "Monarch of Whole Quantities",
                topic = MathTopic.BASIC_ALGEBRA,
                worldId = 1,
                color = Color(0xFFFF5252),
                phaseDescriptions = listOf(
                    "Phase 1: Absolute Magnitude - Sign Inversions",
                    "Phase 2: Integer Matrix - Negative Multipliers",
                    "Phase 3: Prime Factor Storm - Complex Arithmetic"
                ),
                phaseEquations = listOf(
                    "|x - 8| = 12",
                    "-4x + 16 = -8",
                    "x² - 25 = 0"
                ),
                specialMechanic = "Inverts signs on incoming strikes if answer is unoptimized"
            ),
            BossDefinition(
                id = "boss_fraction",
                name = "THE FRACTION",
                subtitle = "Split Dimension Nexus",
                topic = MathTopic.FRACTIONS,
                worldId = 1,
                color = Color(0xFFFFB300),
                phaseDescriptions = listOf(
                    "Phase 1: Common Denominators",
                    "Phase 2: Reciprocal Cross-Multiplication",
                    "Phase 3: Improper Reduction Cataclysm"
                ),
                phaseEquations = listOf(
                    "3/4 + 2/5 = x/20",
                    "x/6 = 7/2",
                    "(2x + 1)/3 = 5"
                ),
                specialMechanic = "Generates dual-layered shield dividing player attack power"
            ),
            BossDefinition(
                id = "boss_algebra",
                name = "THE ALGEBRA GUARDIAN",
                subtitle = "Keeper of the Unknown Variables",
                topic = MathTopic.LINEAR_EQUATIONS,
                worldId = 2,
                color = Color(0xFF00E676),
                phaseDescriptions = listOf(
                    "Phase 1: Direct Single-Step Transposition",
                    "Phase 2: Coefficient Division Barrier",
                    "Phase 3: Multi-Term Linear Isolation",
                    "Phase 4: Parenthetical Distributive Surge"
                ),
                phaseEquations = listOf(
                    "x + 7 = 15",
                    "3x = 27",
                    "2x + 5 = 19",
                    "3(x + 2) = 21"
                ),
                specialMechanic = "Shield shifts variables every phase change"
            ),
            BossDefinition(
                id = "boss_geometry",
                name = "THE GEOMETRY CORE",
                subtitle = "Architect of Euclidean Space",
                topic = MathTopic.GEOMETRY,
                worldId = 3,
                color = Color(0xFF00B0FF),
                phaseDescriptions = listOf(
                    "Phase 1: Triangulation & Internal Angles",
                    "Phase 2: Pythagorean Hypotenuse Shield",
                    "Phase 3: Coordinate Rotation Matrix"
                ),
                phaseEquations = listOf(
                    "a² + b² = c² (3, 4, ?)",
                    "Internal angles sum = (n - 2) * 180°",
                    "Area of Circle = π · r²"
                ),
                specialMechanic = "Rotating polygon forcefield that changes weak points"
            ),
            BossDefinition(
                id = "boss_function",
                name = "THE FUNCTION MASTER",
                subtitle = "Omnipotent Operator of Relations",
                topic = MathTopic.ORDER_OF_OPERATIONS,
                worldId = 4,
                color = Color(0xFFE040FB),
                phaseDescriptions = listOf(
                    "Phase 1: Function Mapping f(x)",
                    "Phase 2: Composite Functions f(g(x))",
                    "Phase 3: Quadratic Asymptote & Infinity Surge"
                ),
                phaseEquations = listOf(
                    "f(x) = 2x² - 3, find f(4)",
                    "f(g(x)) where g(x)=x+1",
                    "lim(x→∞) [3x / (x + 2)]"
                ),
                specialMechanic = "Transforms question parameters dynamically"
            )
        )
    }
}

/**
 * World representation for Story Mode.
 */
data class WorldData(
    val id: Int,
    val name: String,
    val subtitle: String,
    val description: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val symbolTheme: String,
    val unlocked: Boolean = false,
    val starsEarned: Int = 0,
    val totalStars: Int = 15,
    val stagesCount: Int = 5,
    val associatedBossId: String
) {
    companion object {
        val ALL_WORLDS = listOf(
            WorldData(
                id = 1,
                name = "NUMBER PLAINS",
                subtitle = "World 1",
                description = "Vast flatlands of arithmetic glyphs, prime numbers, and fundamental operations.",
                primaryColor = Color(0xFF00E5FF),
                secondaryColor = Color(0xFF00B0FF),
                symbolTheme = "+  −  ×  ÷",
                unlocked = true,
                associatedBossId = "boss_integer"
            ),
            WorldData(
                id = 2,
                name = "ALGEBRA CITY",
                subtitle = "World 2",
                description = "Metropolis of towering variables x, y, z with glowing coordinate intersections.",
                primaryColor = Color(0xFFFF6D00),
                secondaryColor = Color(0xFFFF9100),
                symbolTheme = "x² + 2x = y",
                unlocked = false,
                associatedBossId = "boss_algebra"
            ),
            WorldData(
                id = 3,
                name = "GEOMETRY REALM",
                subtitle = "World 3",
                description = "Abstract dimension of rotating polygons, crystalline angles, and Euclidean planes.",
                primaryColor = Color(0xFF00E676),
                secondaryColor = Color(0xFF76FF03),
                symbolTheme = "△  □  ○  θ",
                unlocked = false,
                associatedBossId = "boss_geometry"
            ),
            WorldData(
                id = 4,
                name = "FUNCTION SECTOR",
                subtitle = "World 4",
                description = "High-frequency electromagnetic grid shaped by sine, cosine, and quadratic waveforms.",
                primaryColor = Color(0xFFD500F9),
                secondaryColor = Color(0xFFEA80FC),
                symbolTheme = "f(x) = sin(x)",
                unlocked = false,
                associatedBossId = "boss_function"
            ),
            WorldData(
                id = 5,
                name = "CALCULUS CORE",
                subtitle = "World 5",
                description = "The singularity where infinite sums converge and derivative tangents collide.",
                primaryColor = Color(0xFFFF1744),
                secondaryColor = Color(0xFFFF5252),
                symbolTheme = "∫  ∂  lim  ∞",
                unlocked = false,
                associatedBossId = "boss_function"
            )
        )
    }
}
