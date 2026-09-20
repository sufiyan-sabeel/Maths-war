package com.example.game.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.sqrt

data class Vec2(var x: Float = 0f, var y: Float = 0f) {
    operator fun plus(other: Vec2) = Vec2(x + other.x, y + other.y)
    operator fun minus(other: Vec2) = Vec2(x - other.x, y - other.y)
    operator fun times(scalar: Float) = Vec2(x * scalar, y * scalar)
    fun length(): Float = sqrt(x * x + y * y)
    fun normalized(): Vec2 {
        val len = length()
        return if (len > 0.0001f) Vec2(x / len, y / len) else Vec2(0f, 0f)
    }
}

data class BoundingBox(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
) {
    val left get() = x - width / 2f
    val right get() = x + width / 2f
    val top get() = y - height
    val bottom get() = y

    fun overlaps(other: BoundingBox): Boolean {
        return left < other.right && right > other.left && top < other.bottom && bottom > other.top
    }
}

enum class StickmanAction {
    IDLE,
    RUN,
    DASH,
    JUMP,
    DOUBLE_JUMP,
    FALL,
    PUNCH_1,
    PUNCH_2,
    KICK,
    HEAVY_STRIKE,
    AIR_ATTACK,
    BLOCK,
    COUNTER,
    HIT_REACTION,
    KNOCKBACK,
    SPECIAL_CAST,
    VICTORY,
    DEFEAT
}

enum class EnemyType {
    // Number Monsters
    NUM_0, // Shield / Nullification void
    NUM_1, // Spear runner
    NUM_2, // Fast jumper
    NUM_3, // Triple projectile
    NUM_4, // Corner rebounder
    NUM_5, // Spiral dasher
    NUM_6, // Rolling boulder
    NUM_7, // Scythe diver
    NUM_8, // Heavy ground stomper
    NUM_9, // Giant brute

    // Operator Monsters
    OP_PLUS,     // Radial cross burst, support healing
    OP_MINUS,    // High velocity subtraction dash cutter
    OP_MULTIPLY, // Spinning buzzsaw shuriken shuriken
    OP_DIVIDE,   // Divides the air; physically splits into 2 on defeat!
    OP_EQUALS,   // Sturdy horizontal energy barrier
    OP_ROOT,     // Sweeping radical ground hook
    OP_PERCENT,  // Orbiting charged twin modulus orbs

    // Advanced Math Monsters
    MONSTER_FRACTION,        // Floating two-tier orbital fraction beast
    MONSTER_GEOMETRY_DELTA,  // Razor-sharp triangular charge beast
    MONSTER_GEOMETRY_CIRCLE, // Expanding geometry shockwave entity
    MONSTER_ALGEBRA_X,       // Unpredictable variable shift stalker
    MONSTER_EQUATION,        // Multi-symbol energy conduit monster

    // Math Soldiers (Original Stickman Combatants)
    SOLDIER_SIGMA, // Heavy brawler stickman with sum gauntlets
    SOLDIER_PI,    // Agile martial artist stickman with rapid kick flurries
    SOLDIER_THETA, // Disciplined spearman stickman with angle lance
    SOLDIER_DELTA, // Acrobatic skirmisher stickman with twin delta daggers

    // Boss Core
    BOSS_INTEGER_CORE,
    BOSS_FRACTION_ENGINE,
    BOSS_ALGEBRA_BEAST,
    BOSS_FUNCTION_MACHINE,
    BOSS_EQUATION
}

enum class SpecialAttackType(
    val displayName: String,
    val description: String,
    val symbol: String,
    val energyCost: Float
) {
    ADDITION_BURST("ADDITION BURST", "Creates multiple + symbols combining into a massive impact", "+", 35f),
    SUBTRACTION_BREAK("SUBTRACTION BREAK", "High-frequency − energy wave that shatters enemy shields", "−", 40f),
    MULTIPLICATION_STORM("MULTIPLICATION STORM", "Multiplies spinning × blades across the screen", "×", 50f),
    DIVISION_SPLIT("DIVISION SPLIT", "Divides attack into four directional splitting beams", "÷", 45f),
    POWER_STRIKE("POWER STRIKE (x²)", "Exponential energy surge with heavy knockback", "x²", 60f),
    ROOT_WAVE("ROOT WAVE (√)", "Curved square-root energy blade cutting through lines of enemies", "√", 50f),
    EQUATION_BREAKER("EQUATION BREAKER", "Unleashes complete mathematical formula devastating the arena", "∫ f(x) dx", 100f)
}

data class PlayerFighter(
    var pos: Vec2 = Vec2(300f, 600f),
    var vel: Vec2 = Vec2(0f, 0f),
    var action: StickmanAction = StickmanAction.IDLE,
    var actionTimer: Float = 0f,
    var actionDuration: Float = 0.3f,
    var isFacingRight: Boolean = true,
    var isGrounded: Boolean = true,
    var canDoubleJump: Boolean = true,
    var hp: Float = 100f,
    var maxHp: Float = 100f,
    var shield: Float = 30f,
    var maxShield: Float = 30f,
    var specialEnergy: Float = 0f,
    var maxSpecialEnergy: Float = 100f,
    var isInvulnerable: Boolean = false,
    var invulnerableTimer: Float = 0f,
    var comboCount: Int = 0,
    var comboTimer: Float = 0f,
    var selectedSpecial: SpecialAttackType = SpecialAttackType.ADDITION_BURST,
    var attackHitboxActive: Boolean = false,
    var hasHitCurrentAttack: Boolean = false,
    var primaryColor: Color = Color(0xFFF0F0F5),
    var energyColor: Color = Color(0xFFE67E22)
) {
    val hurtbox: BoundingBox
        get() = BoundingBox(pos.x, pos.y, 40f, 90f)

    val attackHitbox: BoundingBox?
        get() {
            if (!attackHitboxActive) return null
            val offsetDir = if (isFacingRight) 48f else -48f
            return when (action) {
                StickmanAction.PUNCH_1, StickmanAction.PUNCH_2 ->
                    BoundingBox(pos.x + offsetDir, pos.y - 45f, 54f, 36f)
                StickmanAction.KICK ->
                    BoundingBox(pos.x + offsetDir * 1.25f, pos.y - 42f, 66f, 42f)
                StickmanAction.HEAVY_STRIKE ->
                    BoundingBox(pos.x + offsetDir * 1.35f, pos.y - 50f, 80f, 65f)
                StickmanAction.AIR_ATTACK ->
                    BoundingBox(pos.x + offsetDir, pos.y - 25f, 70f, 62f)
                else -> null
            }
        }
}

data class MathEntity(
    val id: Long,
    var type: EnemyType,
    var pos: Vec2,
    var vel: Vec2 = Vec2(0f, 0f),
    var hp: Float,
    var maxHp: Float,
    var shield: Float = 0f,
    var maxShield: Float = 0f,
    var width: Float = 50f,
    var height: Float = 60f,
    var isFacingRight: Boolean = false,
    var isGrounded: Boolean = true,
    var rotation: Float = 0f,
    var rotationSpeed: Float = 0f,
    var color: Color = Color.White,
    var glowColor: Color = Color(0xFFE67E22),
    var aiStateTimer: Float = 0f,
    var aiSubState: Int = 0,
    var attackCooldown: Float = 0f,
    var hitFlashTimer: Float = 0f,
    var isDead: Boolean = false,
    var symbolString: String = "",
    var isBoss: Boolean = false,
    var bossPhase: Int = 1,
    var scale: Float = 1.0f,
    // Math Soldier Stickman fields
    var isSoldier: Boolean = false,
    var soldierStyle: String = "", // "SIGMA", "PI", "THETA", "DELTA"
    var action: StickmanAction = StickmanAction.IDLE,
    var actionTimer: Float = 0f,
    var attackHitboxActive: Boolean = false
) {
    val hurtbox: BoundingBox
        get() = BoundingBox(pos.x, pos.y, width * scale, height * scale)

    val attackHitbox: BoundingBox?
        get() {
            if (isSoldier && attackHitboxActive) {
                val offsetDir = if (isFacingRight) width * 0.7f else -width * 0.7f
                return BoundingBox(pos.x + offsetDir, pos.y - height * 0.45f, width * 0.9f, height * 0.6f)
            }
            // When entity is in attacking state
            if (aiSubState == 2 && attackCooldown <= 0.20f) {
                val offsetDir = if (isFacingRight) width * 0.6f else -width * 0.6f
                return BoundingBox(pos.x + offsetDir, pos.y - height * 0.4f, width * 0.8f, height * 0.6f)
            }
            return null
        }
}

data class Projectile(
    val id: Long,
    var pos: Vec2,
    var vel: Vec2,
    val damage: Float,
    val isPlayer: Boolean,
    val symbol: String,
    val size: Float = 24f,
    val color: Color = Color(0xFFF0F0F5),
    val glowColor: Color = Color(0xFFE67E22),
    var lifetime: Float = 3.0f,
    var rotation: Float = 0f,
    var rotationSpeed: Float = 0f,
    val piercing: Boolean = false,
    var hasCollided: Boolean = false
) {
    val hitbox: BoundingBox
        get() = BoundingBox(pos.x, pos.y, size, size)
}

data class MathParticle(
    val id: Long,
    var pos: Vec2,
    var vel: Vec2,
    val text: String,
    var size: Float,
    val color: Color,
    var alpha: Float = 1.0f,
    var rotation: Float = 0f,
    var vRot: Float = 0f,
    var lifetime: Float = 0.8f,
    val maxLife: Float = 0.8f,
    val isDust: Boolean = false
)

data class ImpactEffect(
    val id: Long,
    val pos: Vec2,
    val type: String, // "RING", "SLASH", "SHOCKWAVE", "BURST", "IMPACT_FRAME", "DUST_PUFF"
    val color: Color,
    var radius: Float = 10f,
    val maxRadius: Float = 70f,
    var alpha: Float = 1.0f,
    var progress: Float = 0f,
    val duration: Float = 0.22f
)

data class CameraState(
    var pos: Vec2 = Vec2(600f, 500f),
    var zoom: Float = 1.0f,
    var targetZoom: Float = 1.0f,
    var shakeTimer: Float = 0f,
    var shakeIntensity: Float = 0f
)

data class TouchInput(
    var joystickMove: Vec2 = Vec2(0f, 0f),
    var attackTriggered: Boolean = false,
    var jumpTriggered: Boolean = false,
    var dashTriggered: Boolean = false,
    var specialTriggered: Boolean = false,
    var blockHeld: Boolean = false
)
