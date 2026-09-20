package com.example.game.systems

import com.example.game.model.MathEntity
import com.example.game.model.PlayerFighter
import com.example.game.model.Projectile
import com.example.game.model.StickmanAction
import com.example.game.model.Vec2

class PhysicsSystem(
    val arenaLeft: Float = 60f,
    val arenaRight: Float = 1600f,
    val groundY: Float = 720f,
    val ceilingY: Float = 80f
) {
    val gravity: Float = 1400f // pixels / sec^2
    val friction: Float = 0.85f
    val airDrag: Float = 0.96f

    fun update(
        dt: Float,
        player: PlayerFighter,
        enemies: List<MathEntity>,
        projectiles: List<Projectile>
    ) {
        updatePlayerPhysics(dt, player)

        for (enemy in enemies) {
            updateEnemyPhysics(dt, enemy)
        }

        for (proj in projectiles) {
            proj.pos.x += proj.vel.x * dt
            proj.pos.y += proj.vel.y * dt
            proj.rotation += proj.rotationSpeed * dt
        }
    }

    private fun updatePlayerPhysics(dt: Float, player: PlayerFighter) {
        // Dash has zero gravity and fixed high speed
        if (player.action == StickmanAction.DASH) {
            player.pos.x += player.vel.x * dt
            player.pos.y += player.vel.y * dt
        } else {
            // Apply gravity if in air
            if (!player.isGrounded) {
                player.vel.y += gravity * dt
                player.vel.x *= airDrag
            } else {
                player.vel.x *= friction
            }

            player.pos.x += player.vel.x * dt
            player.pos.y += player.vel.y * dt
        }

        // Arena boundary collisions
        if (player.pos.x < arenaLeft) {
            player.pos.x = arenaLeft
            player.vel.x = 0f
        } else if (player.pos.x > arenaRight) {
            player.pos.x = arenaRight
            player.vel.x = 0f
        }

        if (player.pos.y >= groundY) {
            player.pos.y = groundY
            player.vel.y = 0f
            player.isGrounded = true
            player.canDoubleJump = true

            if (player.action == StickmanAction.JUMP || player.action == StickmanAction.DOUBLE_JUMP || player.action == StickmanAction.FALL) {
                player.action = StickmanAction.IDLE
            }
        } else {
            player.isGrounded = false
            if (player.vel.y > 50f && (player.action == StickmanAction.JUMP || player.action == StickmanAction.DOUBLE_JUMP)) {
                player.action = StickmanAction.FALL
            }
        }

        if (player.pos.y < ceilingY) {
            player.pos.y = ceilingY
            player.vel.y = 0f
        }
    }

    private fun updateEnemyPhysics(dt: Float, enemy: MathEntity) {
        if (!enemy.isGrounded) {
            enemy.vel.y += gravity * dt
            enemy.vel.x *= airDrag
        } else {
            enemy.vel.x *= friction
        }

        enemy.pos.x += enemy.vel.x * dt
        enemy.pos.y += enemy.vel.y * dt
        enemy.rotation += enemy.rotationSpeed * dt

        // Boundaries
        if (enemy.pos.x < arenaLeft) {
            enemy.pos.x = arenaLeft
            enemy.vel.x = -enemy.vel.x * 0.5f
        } else if (enemy.pos.x > arenaRight) {
            enemy.pos.x = arenaRight
            enemy.vel.x = -enemy.vel.x * 0.5f
        }

        if (enemy.pos.y >= groundY) {
            enemy.pos.y = groundY
            enemy.vel.y = 0f
            enemy.isGrounded = true
        } else {
            enemy.isGrounded = false
        }
    }
}
