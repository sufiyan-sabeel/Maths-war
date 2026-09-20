package com.example.game.systems

import androidx.compose.ui.graphics.Color
import com.example.game.model.EnemyType
import com.example.game.model.MathEntity
import com.example.game.model.PlayerFighter
import com.example.game.model.Projectile
import com.example.game.model.StickmanAction
import com.example.game.model.Vec2
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

class EnemyAIController {

    fun update(
        dt: Float,
        enemies: List<MathEntity>,
        player: PlayerFighter,
        projectiles: MutableList<Projectile>,
        audio: AudioSystem
    ) {
        for (enemy in enemies) {
            if (enemy.isDead) continue

            if (enemy.hitFlashTimer > 0f) {
                enemy.hitFlashTimer -= dt
            }

            enemy.aiStateTimer += dt
            if (enemy.attackCooldown > 0f) {
                enemy.attackCooldown -= dt
            }

            val distToPlayer = player.pos.x - enemy.pos.x
            enemy.isFacingRight = distToPlayer > 0f

            when (enemy.type) {
                EnemyType.NUM_0 -> updateShieldEnemy(dt, enemy, player, distToPlayer)
                EnemyType.NUM_1 -> updateSpearRunner(dt, enemy, player, distToPlayer)
                EnemyType.NUM_2 -> updateJumper(dt, enemy, player, distToPlayer)
                EnemyType.NUM_7 -> updateScytheDiver(dt, enemy, player, distToPlayer)
                EnemyType.NUM_8 -> updateHeavyStomper(dt, enemy, player, distToPlayer, projectiles, audio)
                EnemyType.NUM_9 -> updateGiantBrute(dt, enemy, player, distToPlayer)
                EnemyType.OP_PLUS -> updatePlusSpawner(dt, enemy, player, projectiles)
                EnemyType.OP_MINUS -> updateMinusLaser(dt, enemy, player, distToPlayer, projectiles)
                EnemyType.OP_MULTIPLY -> updateMultiplyBuzzsaw(dt, enemy, distToPlayer)
                EnemyType.OP_DIVIDE -> updateDivideEntity(dt, enemy, distToPlayer)
                EnemyType.OP_EQUALS -> updateEqualsBarrier(dt, enemy, distToPlayer)
                EnemyType.OP_ROOT -> updateRootCurved(dt, enemy, distToPlayer, projectiles)
                EnemyType.OP_PERCENT -> updatePercentOrbiter(dt, enemy, distToPlayer)
                EnemyType.SOLDIER_SIGMA -> updateSoldierSigma(dt, enemy, player, distToPlayer, audio)
                EnemyType.SOLDIER_PI -> updateSoldierPi(dt, enemy, player, distToPlayer, audio)
                EnemyType.SOLDIER_THETA -> updateSoldierTheta(dt, enemy, player, distToPlayer, audio)
                EnemyType.SOLDIER_DELTA -> updateSoldierDelta(dt, enemy, player, distToPlayer, audio)
                EnemyType.MONSTER_FRACTION -> updateFractionMonster(dt, enemy, player, distToPlayer, projectiles)
                EnemyType.MONSTER_GEOMETRY_DELTA -> updateGeometryDelta(dt, enemy, player, distToPlayer)
                EnemyType.MONSTER_GEOMETRY_CIRCLE -> updateGeometryCircle(dt, enemy, player, distToPlayer, projectiles)
                EnemyType.MONSTER_ALGEBRA_X -> updateAlgebraX(dt, enemy, player, distToPlayer)
                EnemyType.MONSTER_EQUATION -> updateEquationMonster(dt, enemy, player, distToPlayer, projectiles)
                EnemyType.BOSS_INTEGER_CORE,
                EnemyType.BOSS_FRACTION_ENGINE,
                EnemyType.BOSS_ALGEBRA_BEAST,
                EnemyType.BOSS_FUNCTION_MACHINE,
                EnemyType.BOSS_EQUATION -> updateBossAI(dt, enemy, player, distToPlayer, projectiles, audio)
                else -> updateStandardEnemy(dt, enemy, distToPlayer)
            }
        }
    }

    private fun updateShieldEnemy(dt: Float, enemy: MathEntity, player: PlayerFighter, dist: Float) {
        // Keeps guard up, steps towards player slowly
        val dir = if (dist > 0) 1f else -1f
        if (abs(dist) > 90f) {
            enemy.vel.x = dir * 100f
            enemy.aiSubState = 0
        } else {
            // In melee range, shield bash!
            if (enemy.attackCooldown <= 0f) {
                enemy.aiSubState = 2
                enemy.attackCooldown = 2.0f
                enemy.vel.x = dir * 250f
            } else {
                enemy.aiSubState = 1
                enemy.vel.x = 0f
            }
        }
    }

    private fun updateSpearRunner(dt: Float, enemy: MathEntity, player: PlayerFighter, dist: Float) {
        val dir = if (dist > 0) 1f else -1f
        if (abs(dist) > 70f) {
            enemy.vel.x = dir * 260f
            enemy.aiSubState = 0
        } else {
            if (enemy.attackCooldown <= 0f) {
                enemy.aiSubState = 2
                enemy.attackCooldown = 1.2f
                enemy.vel.x = dir * 400f
            } else {
                enemy.aiSubState = 0
            }
        }
    }

    private fun updateJumper(dt: Float, enemy: MathEntity, player: PlayerFighter, dist: Float) {
        val dir = if (dist > 0) 1f else -1f
        if (enemy.isGrounded && enemy.aiStateTimer > 1.4f) {
            enemy.aiStateTimer = 0f
            enemy.vel.y = -520f
            enemy.vel.x = dir * 280f
            enemy.aiSubState = 2
        }
    }

    private fun updateScytheDiver(dt: Float, enemy: MathEntity, player: PlayerFighter, dist: Float) {
        val dir = if (dist > 0) 1f else -1f
        if (enemy.aiSubState == 0) {
            // Ascending
            enemy.vel.y = -220f
            enemy.vel.x = dir * 120f
            if (enemy.pos.y < 350f) {
                enemy.aiSubState = 1
                enemy.aiStateTimer = 0f
            }
        } else if (enemy.aiSubState == 1) {
            // Hovering before dive
            enemy.vel.x = 0f
            enemy.vel.y = 0f
            if (enemy.aiStateTimer > 0.6f) {
                enemy.aiSubState = 2 // Dive!
                enemy.vel.x = dir * 480f
                enemy.vel.y = 550f
                enemy.attackCooldown = 0.15f
            }
        } else {
            // Grounded recovery
            if (enemy.isGrounded) {
                enemy.aiSubState = 0
            }
        }
    }

    private fun updateHeavyStomper(
        dt: Float,
        enemy: MathEntity,
        player: PlayerFighter,
        dist: Float,
        projectiles: MutableList<Projectile>,
        audio: AudioSystem
    ) {
        val dir = if (dist > 0) 1f else -1f
        if (abs(dist) > 120f) {
            enemy.vel.x = dir * 110f
            enemy.aiSubState = 0
        } else {
            if (enemy.attackCooldown <= 0f) {
                // Ground Stomp Shockwave
                enemy.attackCooldown = 2.8f
                enemy.aiSubState = 2
                audio.playHeavyImpact()
                projectiles.add(
                    Projectile(
                        id = System.nanoTime(),
                        pos = Vec2(enemy.pos.x, enemy.pos.y),
                        vel = Vec2(dir * 380f, 0f),
                        damage = 25f,
                        isPlayer = false,
                        symbol = "8",
                        size = 35f,
                        color = Color(0xFFFF9100)
                    )
                )
            } else {
                enemy.aiSubState = 0
            }
        }
    }

    private fun updateGiantBrute(dt: Float, enemy: MathEntity, player: PlayerFighter, dist: Float) {
        val dir = if (dist > 0) 1f else -1f
        if (abs(dist) > 90f) {
            enemy.vel.x = dir * 140f
            enemy.aiSubState = 0
        } else {
            if (enemy.attackCooldown <= 0f) {
                enemy.aiSubState = 2
                enemy.attackCooldown = 2.2f
                enemy.vel.x = dir * 260f
            } else {
                enemy.aiSubState = 0
            }
        }
    }

    private fun updatePlusSpawner(
        dt: Float,
        enemy: MathEntity,
        player: PlayerFighter,
        projectiles: MutableList<Projectile>
    ) {
        // Floats and shoots + cross projectiles
        enemy.pos.y += sin(enemy.aiStateTimer * 3f) * 1.5f
        if (enemy.attackCooldown <= 0f) {
            enemy.attackCooldown = 3.0f
            val dir = if (player.pos.x > enemy.pos.x) 1f else -1f
            projectiles.add(
                Projectile(
                    id = System.nanoTime(),
                    pos = Vec2(enemy.pos.x, enemy.pos.y - 30f),
                    vel = Vec2(dir * 340f, 0f),
                    damage = 18f,
                    isPlayer = false,
                    symbol = "+",
                    size = 28f,
                    color = Color(0xFFFF5252),
                    rotationSpeed = 200f
                )
            )
        }
    }

    private fun updateMinusLaser(
        dt: Float,
        enemy: MathEntity,
        player: PlayerFighter,
        dist: Float,
        projectiles: MutableList<Projectile>
    ) {
        val dir = if (dist > 0) 1f else -1f
        enemy.vel.x = dir * 80f
        if (enemy.attackCooldown <= 0f) {
            enemy.attackCooldown = 2.6f
            projectiles.add(
                Projectile(
                    id = System.nanoTime(),
                    pos = Vec2(enemy.pos.x, enemy.pos.y - 30f),
                    vel = Vec2(dir * 580f, 0f),
                    damage = 22f,
                    isPlayer = false,
                    symbol = "−−",
                    size = 32f,
                    color = Color(0xFFFF1744)
                )
            )
        }
    }

    private fun updateMultiplyBuzzsaw(dt: Float, enemy: MathEntity, dist: Float) {
        val dir = if (dist > 0) 1f else -1f
        enemy.vel.x = dir * 240f
        enemy.rotationSpeed = 600f
        enemy.aiSubState = 2 // always in damaging contact
    }

    private fun updateDivideEntity(dt: Float, enemy: MathEntity, dist: Float) {
        val dir = if (dist > 0) 1f else -1f
        enemy.vel.x = dir * 180f
    }

    private fun updateEqualsBarrier(dt: Float, enemy: MathEntity, dist: Float) {
        // Creates static barrier between player and other enemies
        enemy.vel.x = 0f
        enemy.aiSubState = 2
    }

    private fun updateRootCurved(
        dt: Float,
        enemy: MathEntity,
        dist: Float,
        projectiles: MutableList<Projectile>
    ) {
        val dir = if (dist > 0) 1f else -1f
        enemy.vel.x = dir * 120f
        if (enemy.attackCooldown <= 0f) {
            enemy.attackCooldown = 2.8f
            projectiles.add(
                Projectile(
                    id = System.nanoTime(),
                    pos = Vec2(enemy.pos.x, enemy.pos.y - 20f),
                    vel = Vec2(dir * 420f, -40f),
                    damage = 20f,
                    isPlayer = false,
                    symbol = "√",
                    size = 36f,
                    color = Color(0xFF00E5FF),
                    rotationSpeed = 150f
                )
            )
        }
    }

    private fun updatePercentOrbiter(dt: Float, enemy: MathEntity, dist: Float) {
        val dir = if (dist > 0) 1f else -1f
        enemy.vel.x = dir * 190f
        enemy.rotation += dt * 300f
    }

    private fun updateStandardEnemy(dt: Float, enemy: MathEntity, dist: Float) {
        val dir = if (dist > 0) 1f else -1f
        if (abs(dist) > 60f) {
            enemy.vel.x = dir * 180f
            enemy.aiSubState = 0
        } else {
            enemy.aiSubState = 2
            enemy.vel.x = dir * 120f
        }
    }

    private fun updateBossAI(
        dt: Float,
        boss: MathEntity,
        player: PlayerFighter,
        dist: Float,
        projectiles: MutableList<Projectile>,
        audio: AudioSystem
    ) {
        val dir = if (dist > 0) 1f else -1f

        // Phase shifting based on HP
        val hpPct = boss.hp / boss.maxHp
        val phase = when {
            hpPct > 0.75f -> 1
            hpPct > 0.50f -> 2
            hpPct > 0.25f -> 3
            else -> 4
        }
        boss.bossPhase = phase

        // Speed and aggression scales with phase
        val speed = 120f + (phase * 35f)
        if (abs(dist) > 130f) {
            boss.vel.x = dir * speed
            boss.aiSubState = 0
        } else {
            boss.aiSubState = 2
            boss.vel.x = dir * (speed * 1.4f)
        }

        if (boss.attackCooldown <= 0f) {
            boss.attackCooldown = 3.5f - (phase * 0.5f)
            audio.playBossAttack()

            // Boss projectile barrage
            when (phase) {
                1 -> {
                    // Single powerful formula blast
                    projectiles.add(
                        Projectile(
                            id = System.nanoTime(),
                            pos = Vec2(boss.pos.x, boss.pos.y - 60f),
                            vel = Vec2(dir * 460f, 0f),
                            damage = 25f,
                            isPlayer = false,
                            symbol = "x + 7",
                            size = 45f,
                            color = boss.color
                        )
                    )
                }
                2 -> {
                    // Triple fan formula blast
                    for (angle in listOf(-0.3f, 0f, 0.3f)) {
                        projectiles.add(
                            Projectile(
                                id = System.nanoTime(),
                                pos = Vec2(boss.pos.x, boss.pos.y - 60f),
                                vel = Vec2(dir * 500f * kotlin.math.cos(angle), 500f * kotlin.math.sin(angle)),
                                damage = 22f,
                                isPlayer = false,
                                symbol = "2x + 5",
                                size = 42f,
                                color = boss.color
                            )
                        )
                    }
                }
                3 -> {
                    // Arena shockwave + curved projectiles
                    for (i in -1..1) {
                        projectiles.add(
                            Projectile(
                                id = System.nanoTime() + i,
                                pos = Vec2(boss.pos.x, boss.pos.y - 40f + (i * 30f)),
                                vel = Vec2(dir * 550f, i * 120f),
                                damage = 28f,
                                isPlayer = false,
                                symbol = "3x²",
                                size = 48f,
                                color = Color(0xFFFF1744),
                                rotationSpeed = 350f
                            )
                        )
                    }
                }
                else -> {
                    // Phase 4: Ultimate Equation Barrage
                    for (angle in listOf(-0.5f, -0.25f, 0f, 0.25f, 0.5f)) {
                        projectiles.add(
                            Projectile(
                                id = System.nanoTime(),
                                pos = Vec2(boss.pos.x, boss.pos.y - 60f),
                                vel = Vec2(dir * 600f * kotlin.math.cos(angle), 600f * kotlin.math.sin(angle)),
                                damage = 32f,
                                isPlayer = false,
                                symbol = "∫x dx",
                                size = 52f,
                                color = Color(0xFFE67E22),
                                piercing = true
                            )
                        )
                    }
                }
            }
        }
    }

    // ==========================================
    // MATH SOLDIER AI (STICKMAN COMBATANTS)
    // ==========================================

    private fun updateSoldierSigma(dt: Float, soldier: MathEntity, player: PlayerFighter, dist: Float, audio: AudioSystem) {
        soldier.actionTimer += dt
        val dir = if (dist > 0) 1f else -1f

        if (abs(dist) > 95f) {
            // March toward player
            soldier.vel.x = dir * 160f
            soldier.action = StickmanAction.RUN
            soldier.attackHitboxActive = false
        } else {
            // In striking range
            soldier.vel.x = 0f
            if (soldier.attackCooldown <= 0f) {
                // Anticipation -> Heavy Strike Slam
                soldier.action = StickmanAction.HEAVY_STRIKE
                soldier.actionTimer = 0f
                soldier.attackCooldown = 2.0f
                soldier.attackHitboxActive = true
                audio.playHeavyImpact()
            } else if (soldier.actionTimer > 0.45f) {
                soldier.action = StickmanAction.IDLE
                soldier.attackHitboxActive = false
            }
        }
    }

    private fun updateSoldierPi(dt: Float, soldier: MathEntity, player: PlayerFighter, dist: Float, audio: AudioSystem) {
        soldier.actionTimer += dt
        val dir = if (dist > 0) 1f else -1f

        if (abs(dist) > 80f) {
            soldier.vel.x = dir * 250f
            soldier.action = StickmanAction.RUN
            soldier.attackHitboxActive = false
        } else {
            if (soldier.attackCooldown <= 0f) {
                // High speed kick combo
                soldier.action = StickmanAction.KICK
                soldier.actionTimer = 0f
                soldier.attackCooldown = 1.3f
                soldier.vel.x = dir * 200f
                soldier.attackHitboxActive = true
                audio.playKickSwing()
            } else if (soldier.actionTimer > 0.35f) {
                soldier.action = StickmanAction.IDLE
                soldier.attackHitboxActive = false
                soldier.vel.x = 0f
            }
        }
    }

    private fun updateSoldierTheta(dt: Float, soldier: MathEntity, player: PlayerFighter, dist: Float, audio: AudioSystem) {
        soldier.actionTimer += dt
        val dir = if (dist > 0) 1f else -1f

        // Stays at mid-range spear spacing (130-160px)
        if (abs(dist) > 140f) {
            soldier.vel.x = dir * 200f
            soldier.action = StickmanAction.RUN
            soldier.attackHitboxActive = false
        } else if (abs(dist) < 70f) {
            // Backstep to keep lance range
            soldier.vel.x = -dir * 180f
            soldier.action = StickmanAction.DASH
            soldier.attackHitboxActive = false
        } else {
            soldier.vel.x = 0f
            if (soldier.attackCooldown <= 0f) {
                // Precise angle lance thrust
                soldier.action = StickmanAction.PUNCH_1
                soldier.actionTimer = 0f
                soldier.attackCooldown = 1.6f
                soldier.attackHitboxActive = true
                audio.playPunchSwing()
            } else if (soldier.actionTimer > 0.32f) {
                soldier.action = StickmanAction.IDLE
                soldier.attackHitboxActive = false
            }
        }
    }

    private fun updateSoldierDelta(dt: Float, soldier: MathEntity, player: PlayerFighter, dist: Float, audio: AudioSystem) {
        soldier.actionTimer += dt
        val dir = if (dist > 0) 1f else -1f

        if (soldier.isGrounded && soldier.aiStateTimer > 1.8f && abs(dist) < 220f) {
            // Acrobatic leap
            soldier.aiStateTimer = 0f
            soldier.vel.y = -500f
            soldier.vel.x = dir * 320f
            soldier.action = StickmanAction.AIR_ATTACK
            soldier.attackHitboxActive = true
            audio.playDash()
        } else if (!soldier.isGrounded) {
            soldier.action = StickmanAction.AIR_ATTACK
            soldier.attackHitboxActive = true
        } else {
            soldier.attackHitboxActive = false
            if (abs(dist) > 90f) {
                soldier.vel.x = dir * 240f
                soldier.action = StickmanAction.RUN
            } else {
                soldier.vel.x = 0f
                if (soldier.attackCooldown <= 0f) {
                    soldier.action = StickmanAction.PUNCH_2
                    soldier.actionTimer = 0f
                    soldier.attackCooldown = 1.1f
                    soldier.attackHitboxActive = true
                    audio.playPunchSwing()
                } else if (soldier.actionTimer > 0.3f) {
                    soldier.action = StickmanAction.IDLE
                    soldier.attackHitboxActive = false
                }
            }
        }
    }

    // ==========================================
    // ADVANCED MATH MONSTERS AI
    // ==========================================

    private fun updateFractionMonster(
        dt: Float,
        enemy: MathEntity,
        player: PlayerFighter,
        dist: Float,
        projectiles: MutableList<Projectile>
    ) {
        val dir = if (dist > 0) 1f else -1f
        // Floats and rocks
        enemy.pos.y += sin(enemy.aiStateTimer * 4f) * 1.8f
        enemy.vel.x = dir * 90f

        if (enemy.attackCooldown <= 0f) {
            enemy.attackCooldown = 2.4f
            // Fires numerator then denominator projectiles
            projectiles.add(
                Projectile(
                    id = System.nanoTime(),
                    pos = Vec2(enemy.pos.x, enemy.pos.y - 25f),
                    vel = Vec2(dir * 380f, -40f),
                    damage = 16f,
                    isPlayer = false,
                    symbol = "p",
                    size = 28f,
                    color = Color(0xFFE59866)
                )
            )
            projectiles.add(
                Projectile(
                    id = System.nanoTime() + 1,
                    pos = Vec2(enemy.pos.x, enemy.pos.y + 15f),
                    vel = Vec2(dir * 380f, 40f),
                    damage = 16f,
                    isPlayer = false,
                    symbol = "q",
                    size = 28f,
                    color = Color(0xFFD4A373)
                )
            )
        }
    }

    private fun updateGeometryDelta(dt: Float, enemy: MathEntity, player: PlayerFighter, dist: Float) {
        val dir = if (dist > 0) 1f else -1f
        enemy.rotation += 180f * dt

        if (enemy.aiSubState == 0) {
            // Coiling anticipation
            enemy.vel.x = -dir * 40f
            if (enemy.aiStateTimer > 1.0f) {
                enemy.aiSubState = 1
                enemy.aiStateTimer = 0f
            }
        } else {
            // High speed razor charge
            enemy.vel.x = dir * 460f
            enemy.aiSubState = 2
            if (enemy.aiStateTimer > 0.8f) {
                enemy.aiSubState = 0
                enemy.aiStateTimer = 0f
            }
        }
    }

    private fun updateGeometryCircle(
        dt: Float,
        enemy: MathEntity,
        player: PlayerFighter,
        dist: Float,
        projectiles: MutableList<Projectile>
    ) {
        val dir = if (dist > 0) 1f else -1f
        enemy.vel.x = dir * 70f
        if (enemy.attackCooldown <= 0f) {
            enemy.attackCooldown = 3.2f
            // Expands radial pulse
            for (angle in listOf(0f, 1.57f, 3.14f, 4.71f)) {
                projectiles.add(
                    Projectile(
                        id = System.nanoTime(),
                        pos = Vec2(enemy.pos.x, enemy.pos.y - 20f),
                        vel = Vec2(320f * kotlin.math.cos(angle), 320f * kotlin.math.sin(angle)),
                        damage = 20f,
                        isPlayer = false,
                        symbol = "○",
                        size = 30f,
                        color = Color(0xFFD4A373)
                    )
                )
            }
        }
    }

    private fun updateAlgebraX(dt: Float, enemy: MathEntity, player: PlayerFighter, dist: Float) {
        val dir = if (dist > 0) 1f else -1f
        // Shifting variable trajectory
        val waveOffset = sin(enemy.aiStateTimer * 5f) * 140f
        enemy.vel.x = dir * 210f + waveOffset
        if (enemy.attackCooldown <= 0f && abs(dist) < 100f) {
            enemy.attackCooldown = 1.4f
            enemy.aiSubState = 2
            enemy.vel.x = dir * 380f
        }
    }

    private fun updateEquationMonster(
        dt: Float,
        enemy: MathEntity,
        player: PlayerFighter,
        dist: Float,
        projectiles: MutableList<Projectile>
    ) {
        val dir = if (dist > 0) 1f else -1f
        enemy.vel.x = dir * 110f

        if (enemy.attackCooldown <= 0f) {
            enemy.attackCooldown = 2.8f
            projectiles.add(
                Projectile(
                    id = System.nanoTime(),
                    pos = Vec2(enemy.pos.x, enemy.pos.y - 30f),
                    vel = Vec2(dir * 420f, 0f),
                    damage = 26f,
                    isPlayer = false,
                    symbol = "E=mc²",
                    size = 46f,
                    color = Color(0xFFC0392B),
                    piercing = true
                )
            )
        }
    }
}
