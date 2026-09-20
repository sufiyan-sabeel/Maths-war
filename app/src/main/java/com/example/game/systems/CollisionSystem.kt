package com.example.game.systems

import com.example.game.model.MathEntity
import com.example.game.model.PlayerFighter
import com.example.game.model.Projectile
import kotlin.math.abs

data class HitEvent(
    val isPlayerAttacker: Boolean,
    val targetEnemy: MathEntity?,
    val damage: Float,
    val knockbackX: Float,
    val knockbackY: Float,
    val hitPointX: Float,
    val hitPointY: Float,
    val hitStopDuration: Float = 0.08f,
    val isSpecial: Boolean = false,
    val symbolEffect: String = ""
)

class CollisionSystem {

    fun checkCollisions(
        player: PlayerFighter,
        enemies: List<MathEntity>,
        projectiles: MutableList<Projectile>,
        onHit: (HitEvent) -> Unit
    ) {
        val playerAttackBox = player.attackHitbox

        // 1. Player melee attack vs Enemies
        if (playerAttackBox != null && !player.hasHitCurrentAttack) {
            for (enemy in enemies) {
                if (!enemy.isDead && playerAttackBox.overlaps(enemy.hurtbox)) {
                    player.hasHitCurrentAttack = true
                    val dir = if (player.isFacingRight) 1f else -1f
                    val (dmg, kbX, kbY, hitStop) = when (player.action) {
                        com.example.game.model.StickmanAction.PUNCH_1 -> Quad(15f, dir * 200f, -80f, 0.06f)
                        com.example.game.model.StickmanAction.PUNCH_2 -> Quad(20f, dir * 250f, -100f, 0.07f)
                        com.example.game.model.StickmanAction.KICK -> Quad(28f, dir * 350f, -140f, 0.08f)
                        com.example.game.model.StickmanAction.HEAVY_STRIKE -> Quad(45f, dir * 550f, -220f, 0.12f)
                        com.example.game.model.StickmanAction.AIR_ATTACK -> Quad(30f, dir * 300f, 150f, 0.09f)
                        else -> Quad(10f, dir * 100f, 0f, 0.05f)
                    }

                    onHit(
                        HitEvent(
                            isPlayerAttacker = true,
                            targetEnemy = enemy,
                            damage = dmg,
                            knockbackX = kbX,
                            knockbackY = kbY,
                            hitPointX = (playerAttackBox.x + enemy.pos.x) / 2f,
                            hitPointY = enemy.pos.y - enemy.height * 0.5f,
                            hitStopDuration = hitStop,
                            isSpecial = false
                        )
                    )
                    break
                }
            }
        }

        // 2. Enemy attack vs Player
        for (enemy in enemies) {
            if (!enemy.isDead) {
                val enemyAtk = enemy.attackHitbox
                if (enemyAtk != null && enemyAtk.overlaps(player.hurtbox)) {
                    val dir = if (enemy.pos.x < player.pos.x) 1f else -1f
                    val dmg = when (enemy.type) {
                        com.example.game.model.EnemyType.NUM_8, com.example.game.model.EnemyType.NUM_9 -> 22f
                        com.example.game.model.EnemyType.OP_MULTIPLY -> 18f
                        else -> 12f
                    }

                    onHit(
                        HitEvent(
                            isPlayerAttacker = false,
                            targetEnemy = null,
                            damage = dmg,
                            knockbackX = dir * 280f,
                            knockbackY = -120f,
                            hitPointX = (enemyAtk.x + player.pos.x) / 2f,
                            hitPointY = player.pos.y - 45f,
                            hitStopDuration = 0.08f
                        )
                    )
                }
            }
        }

        // 3. Projectiles vs Targets
        val it = projectiles.iterator()
        while (it.hasNext()) {
            val proj = it.next()
            if (proj.isPlayer) {
                // Player projectile vs enemies
                for (enemy in enemies) {
                    if (!enemy.isDead && proj.hitbox.overlaps(enemy.hurtbox)) {
                        proj.hasCollided = true
                        val dir = if (proj.vel.x >= 0) 1f else -1f
                        onHit(
                            HitEvent(
                                isPlayerAttacker = true,
                                targetEnemy = enemy,
                                damage = proj.damage,
                                knockbackX = dir * 250f,
                                knockbackY = -80f,
                                hitPointX = proj.pos.x,
                                hitPointY = proj.pos.y,
                                hitStopDuration = 0.08f,
                                isSpecial = true,
                                symbolEffect = proj.symbol
                            )
                        )
                        if (!proj.piercing) break
                    }
                }
            } else {
                // Enemy projectile vs player
                if (proj.hitbox.overlaps(player.hurtbox)) {
                    proj.hasCollided = true
                    val dir = if (proj.vel.x >= 0) 1f else -1f
                    onHit(
                        HitEvent(
                            isPlayerAttacker = false,
                            targetEnemy = null,
                            damage = proj.damage,
                            knockbackX = dir * 220f,
                            knockbackY = -100f,
                            hitPointX = proj.pos.x,
                            hitPointY = proj.pos.y,
                            hitStopDuration = 0.08f
                        )
                    )
                }
            }

            if (proj.hasCollided && !proj.piercing) {
                it.remove()
            }
        }

        // 4. Overlap resolution between player and enemies
        for (enemy in enemies) {
            if (!enemy.isDead) {
                val dx = player.pos.x - enemy.pos.x
                val minDist = (player.hurtbox.width + enemy.width) * 0.4f
                if (abs(dx) < minDist && abs(player.pos.y - enemy.pos.y) < 60f) {
                    val push = (minDist - abs(dx)) * 0.5f
                    val sign = if (dx >= 0) 1f else -1f
                    player.pos.x += sign * push
                    enemy.pos.x -= sign * push
                }
            }
        }
    }

    private data class Quad(val dmg: Float, val kbX: Float, val kbY: Float, val hitStop: Float)
}
