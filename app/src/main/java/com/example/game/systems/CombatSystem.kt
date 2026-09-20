package com.example.game.systems

import androidx.compose.ui.graphics.Color
import com.example.game.model.EnemyType
import com.example.game.model.ImpactEffect
import com.example.game.model.MathEntity
import com.example.game.model.MathParticle
import com.example.game.model.PlayerFighter
import com.example.game.model.Projectile
import com.example.game.model.SpecialAttackType
import com.example.game.model.StickmanAction
import com.example.game.model.Vec2
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class CombatSystem {

    var hitStopTimer: Float = 0f
    val comboDecayTime: Float = 2.4f

    fun updateCombo(dt: Float, player: PlayerFighter) {
        if (player.comboCount > 0) {
            player.comboTimer -= dt
            if (player.comboTimer <= 0f) {
                player.comboCount = 0
            }
        }

        if (player.invulnerableTimer > 0f) {
            player.invulnerableTimer -= dt
            if (player.invulnerableTimer <= 0f) {
                player.isInvulnerable = false
            }
        }
    }

    fun handleHit(
        event: HitEvent,
        player: PlayerFighter,
        particles: MutableList<MathParticle>,
        impacts: MutableList<ImpactEffect>,
        camera: CameraSystem,
        audio: AudioSystem
    ) {
        hitStopTimer = event.hitStopDuration
        camera.addShake(if (event.damage > 25f) 14f else 8f)

        if (event.isPlayerAttacker && event.targetEnemy != null) {
            val enemy = event.targetEnemy
            // Combo increment
            player.comboCount += 1
            player.comboTimer = comboDecayTime
            player.specialEnergy = minOf(player.maxSpecialEnergy, player.specialEnergy + 8f)

            // Shield / Damage calculation
            var remainingDmg = event.damage
            if (enemy.shield > 0f) {
                if (enemy.type == EnemyType.NUM_0) {
                    // Shield absorbs 50% extra
                    remainingDmg *= 0.5f
                }
                if (enemy.shield >= remainingDmg) {
                    enemy.shield -= remainingDmg
                    remainingDmg = 0f
                } else {
                    remainingDmg -= enemy.shield
                    enemy.shield = 0f
                }
            }

            enemy.hp = maxOf(0f, enemy.hp - remainingDmg)
            enemy.vel.x = event.knockbackX
            enemy.vel.y = event.knockbackY
            enemy.hitFlashTimer = 0.12f

            if (enemy.hp <= 0f) {
                enemy.isDead = true
                audio.playEnemyDefeat()
                spawnMathExplosion(enemy.pos, particles, enemy.color, enemy.symbolString)
            } else {
                audio.playHitImpact(player.comboCount)
            }

            // Impact effect & impact flash frame
            impacts.add(
                ImpactEffect(
                    id = System.nanoTime(),
                    pos = Vec2(event.hitPointX, event.hitPointY),
                    type = "IMPACT_FRAME",
                    color = Color(0xFFF5F5F7),
                    maxRadius = 55f,
                    duration = 0.15f
                )
            )
            impacts.add(
                ImpactEffect(
                    id = System.nanoTime() + 1,
                    pos = Vec2(event.hitPointX, event.hitPointY),
                    type = "RING",
                    color = Color(0xFFE67E22),
                    maxRadius = 65f,
                    duration = 0.22f
                )
            )

            // Dust puff on heavy knockback
            if (abs(event.knockbackX) > 200f) {
                impacts.add(
                    ImpactEffect(
                        id = System.nanoTime() + 2,
                        pos = Vec2(enemy.pos.x, enemy.pos.y),
                        type = "DUST_PUFF",
                        color = Color(0xFF6B7280),
                        maxRadius = 45f,
                        duration = 0.28f
                    )
                )
            }

            // Floating damage number particle
            particles.add(
                MathParticle(
                    id = System.nanoTime(),
                    pos = Vec2(event.hitPointX, event.hitPointY - 10f),
                    vel = Vec2((Random.nextFloat() - 0.5f) * 60f, -140f),
                    text = "${event.damage.toInt()}",
                    size = if (event.damage > 30f) 28f else 20f,
                    color = if (event.damage > 30f) Color(0xFFE67E22) else Color(0xFFF0F0F5),
                    lifetime = 0.65f,
                    maxLife = 0.65f
                )
            )

            // Mathematical symbol sparks
            val sparkSymbols = listOf(event.symbolEffect.ifEmpty { "+" }, "-", "×", "÷", "=", "x", "√")
            for (i in 0 until 5) {
                val angle = Random.nextFloat() * 6.28f
                val spd = Random.nextFloat() * 200f + 60f
                particles.add(
                    MathParticle(
                        id = System.nanoTime() + i,
                        pos = Vec2(event.hitPointX, event.hitPointY),
                        vel = Vec2(cos(angle) * spd, sin(angle) * spd),
                        text = sparkSymbols.random(),
                        size = 16f,
                        color = player.primaryColor,
                        rotation = Random.nextFloat() * 360f,
                        vRot = (Random.nextFloat() - 0.5f) * 400f,
                        lifetime = 0.45f,
                        maxLife = 0.45f
                    )
                )
            }
        } else if (!event.isPlayerAttacker) {
            // Enemy attacks player
            if (player.isInvulnerable) return

            if (player.action == StickmanAction.BLOCK) {
                // Block reduces damage by 80% and counters!
                val blockedDmg = event.damage * 0.2f
                player.hp = maxOf(0f, player.hp - blockedDmg)
                player.vel.x = event.knockbackX * 0.3f
                player.isInvulnerable = true
                player.invulnerableTimer = 0.25f
                player.action = StickmanAction.COUNTER
                audio.playBlock()

                impacts.add(
                    ImpactEffect(
                        id = System.nanoTime(),
                        pos = Vec2(event.hitPointX, event.hitPointY),
                        type = "RING",
                        color = Color(0xFF00E5FF)
                    )
                )
                particles.add(
                    MathParticle(
                        id = System.nanoTime(),
                        pos = Vec2(player.pos.x, player.pos.y - 40f),
                        vel = Vec2(0f, -100f),
                        text = "BLOCKED!",
                        size = 20f,
                        color = Color(0xFF00E5FF),
                        lifetime = 0.5f,
                        maxLife = 0.5f
                    )
                )
            } else {
                // Full hit taken
                var dmg = event.damage
                if (player.shield > 0f) {
                    if (player.shield >= dmg) {
                        player.shield -= dmg
                        dmg = 0f
                    } else {
                        dmg -= player.shield
                        player.shield = 0f
                    }
                }
                player.hp = maxOf(0f, player.hp - dmg)
                player.vel.x = event.knockbackX
                player.vel.y = event.knockbackY
                player.action = StickmanAction.KNOCKBACK
                player.actionTimer = 0.25f
                player.isInvulnerable = true
                player.invulnerableTimer = 0.6f
                player.comboCount = 0 // combo broken
                audio.playPlayerHurt()

                particles.add(
                    MathParticle(
                        id = System.nanoTime(),
                        pos = Vec2(player.pos.x, player.pos.y - 40f),
                        vel = Vec2((Random.nextFloat() - 0.5f) * 60f, -120f),
                        text = "-${event.damage.toInt()}",
                        size = 22f,
                        color = Color(0xFFFF1744),
                        lifetime = 0.6f,
                        maxLife = 0.6f
                    )
                )
            }
        }
    }

    fun castSpecialAttack(
        player: PlayerFighter,
        projectiles: MutableList<Projectile>,
        particles: MutableList<MathParticle>,
        impacts: MutableList<ImpactEffect>,
        audio: AudioSystem,
        camera: CameraSystem
    ): Boolean {
        val cost = player.selectedSpecial.energyCost
        if (player.specialEnergy < cost) return false

        player.specialEnergy -= cost
        player.action = StickmanAction.SPECIAL_CAST
        player.actionTimer = 0.35f
        camera.addShake(16f)
        audio.playSpecialCast()

        val dir = if (player.isFacingRight) 1f else -1f
        val startX = player.pos.x + dir * 40f
        val startY = player.pos.y - 45f

        when (player.selectedSpecial) {
            SpecialAttackType.ADDITION_BURST -> {
                // 3 + projectiles that launch forward
                for (i in -1..1) {
                    projectiles.add(
                        Projectile(
                            id = System.nanoTime() + i,
                            pos = Vec2(startX, startY + i * 20f),
                            vel = Vec2(dir * 600f, i * 80f),
                            damage = 35f,
                            isPlayer = true,
                            symbol = "+",
                            size = 32f,
                            color = Color(0xFFE67E22),
                            glowColor = Color(0xFFD35400),
                            rotationSpeed = 300f
                        )
                    )
                }
            }
            SpecialAttackType.SUBTRACTION_BREAK -> {
                // Piercing fast laser beam
                projectiles.add(
                    Projectile(
                        id = System.nanoTime(),
                        pos = Vec2(startX, startY),
                        vel = Vec2(dir * 1100f, 0f),
                        damage = 60f,
                        isPlayer = true,
                        symbol = "−−−−",
                        size = 45f,
                        color = Color(0xFFEDEDF2),
                        glowColor = Color(0xFFB0B3BC),
                        piercing = true
                    )
                )
            }
            SpecialAttackType.MULTIPLICATION_STORM -> {
                // 5 spinning buzzsaw blades in a fan
                for (i in -2..2) {
                    val angle = i * 0.25f
                    projectiles.add(
                        Projectile(
                            id = System.nanoTime() + i,
                            pos = Vec2(startX, startY),
                            vel = Vec2(dir * 700f * cos(angle), 700f * sin(angle)),
                            damage = 40f,
                            isPlayer = true,
                            symbol = "×",
                            size = 36f,
                            color = Color(0xFFC0392B),
                            glowColor = Color(0xFFA93226),
                            rotationSpeed = 800f
                        )
                    )
                }
            }
            SpecialAttackType.DIVISION_SPLIT -> {
                // Splits on launch
                for (angle in listOf(-0.4f, 0f, 0.4f)) {
                    projectiles.add(
                        Projectile(
                            id = System.nanoTime(),
                            pos = Vec2(startX, startY),
                            vel = Vec2(dir * 650f * cos(angle), 650f * sin(angle)),
                            damage = 38f,
                            isPlayer = true,
                            symbol = "÷",
                            size = 30f,
                            color = Color(0xFFD35400),
                            glowColor = Color(0xFFB8531D),
                            rotationSpeed = 400f
                        )
                    )
                }
            }
            SpecialAttackType.POWER_STRIKE -> {
                // Massive heavy single strike projectile
                projectiles.add(
                    Projectile(
                        id = System.nanoTime(),
                        pos = Vec2(startX, startY),
                        vel = Vec2(dir * 850f, 0f),
                        damage = 80f,
                        isPlayer = true,
                        symbol = "x²",
                        size = 50f,
                        color = Color(0xFFD4A373),
                        glowColor = Color(0xFFC05621)
                    )
                )
            }
            SpecialAttackType.ROOT_WAVE -> {
                // Curved blade that sweeps across the ground
                projectiles.add(
                    Projectile(
                        id = System.nanoTime(),
                        pos = Vec2(startX, startY + 15f),
                        vel = Vec2(dir * 750f, 0f),
                        damage = 55f,
                        isPlayer = true,
                        symbol = "√",
                        size = 48f,
                        color = Color(0xFFB9770E),
                        glowColor = Color(0xFFD4AC0D),
                        piercing = true,
                        rotationSpeed = 100f
                    )
                )
            }
            SpecialAttackType.EQUATION_BREAKER -> {
                // Massive screen clearing integral / formula wave
                projectiles.add(
                    Projectile(
                        id = System.nanoTime(),
                        pos = Vec2(startX, startY),
                        vel = Vec2(dir * 600f, 0f),
                        damage = 140f,
                        isPlayer = true,
                        symbol = "∫ f(x) dx = ∞",
                        size = 75f,
                        color = Color(0xFFE67E22),
                        glowColor = Color(0xFFEDEDF2),
                        piercing = true
                    )
                )
            }
        }
        return true
    }

    private fun spawnMathExplosion(
        pos: Vec2,
        particles: MutableList<MathParticle>,
        color: Color,
        symbol: String
    ) {
        val syms = listOf(symbol.ifEmpty { "0" }, "+", "−", "×", "÷", "=", "π")
        for (i in 0 until 18) {
            val angle = Random.nextFloat() * 6.28f
            val spd = Random.nextFloat() * 320f + 80f
            particles.add(
                MathParticle(
                    id = System.nanoTime() + i,
                    pos = Vec2(pos.x, pos.y - 30f),
                    vel = Vec2(cos(angle) * spd, sin(angle) * spd),
                    text = syms.random(),
                    size = Random.nextFloat() * 14f + 16f,
                    color = color,
                    rotation = Random.nextFloat() * 360f,
                    vRot = (Random.nextFloat() - 0.5f) * 600f,
                    lifetime = 0.7f,
                    maxLife = 0.7f
                )
            )
        }
    }
}
