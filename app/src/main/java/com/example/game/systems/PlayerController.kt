package com.example.game.systems

import com.example.game.model.PlayerFighter
import com.example.game.model.StickmanAction
import com.example.game.model.TouchInput
import kotlin.math.abs

class PlayerController {

    private val moveSpeed: Float = 360f
    private val jumpVelocity: Float = -680f
    private val doubleJumpVelocity: Float = -600f
    private val dashSpeed: Float = 950f

    fun update(
        dt: Float,
        player: PlayerFighter,
        input: TouchInput,
        audio: AudioSystem
    ) {
        // Manage active action timers
        if (player.actionTimer > 0f) {
            player.actionTimer -= dt
            if (player.actionTimer <= 0f) {
                onActionFinished(player)
            }
        }

        // Handle Movement when not in locked action
        val canMove = when (player.action) {
            StickmanAction.IDLE, StickmanAction.RUN, StickmanAction.JUMP,
            StickmanAction.DOUBLE_JUMP, StickmanAction.FALL -> true
            else -> false
        }

        if (canMove) {
            val jx = input.joystickMove.x
            if (abs(jx) > 0.15f) {
                player.vel.x = jx * moveSpeed
                player.isFacingRight = jx > 0f
                if (player.isGrounded) {
                    player.action = StickmanAction.RUN
                }
            } else {
                if (player.isGrounded && player.action == StickmanAction.RUN) {
                    player.action = StickmanAction.IDLE
                }
            }
        }

        // Jump Handling
        if (input.jumpTriggered) {
            input.jumpTriggered = false
            if (player.isGrounded) {
                player.vel.y = jumpVelocity
                player.isGrounded = false
                player.action = StickmanAction.JUMP
                player.actionTimer = 0f
                audio.playJump()
            } else if (player.canDoubleJump) {
                player.vel.y = doubleJumpVelocity
                player.canDoubleJump = false
                player.action = StickmanAction.DOUBLE_JUMP
                player.actionTimer = 0f
                audio.playDoubleJump()
            }
        }

        // Dash Handling
        if (input.dashTriggered) {
            input.dashTriggered = false
            val dir = if (player.isFacingRight) 1f else -1f
            player.vel.x = dir * dashSpeed
            player.vel.y = 0f
            player.action = StickmanAction.DASH
            player.actionTimer = 0.22f
            player.isInvulnerable = true
            player.invulnerableTimer = 0.22f
            audio.playDash()
        }

        // Block Handling
        if (input.blockHeld) {
            if (player.isGrounded && canMove) {
                player.action = StickmanAction.BLOCK
                player.vel.x = 0f
            }
        } else if (player.action == StickmanAction.BLOCK) {
            player.action = StickmanAction.IDLE
        }

        // Attack Combos
        if (input.attackTriggered) {
            input.attackTriggered = false
            triggerAttack(player, audio)
        }
    }

    private fun triggerAttack(player: PlayerFighter, audio: AudioSystem) {
        if (!player.isGrounded) {
            // Air Attack
            player.action = StickmanAction.AIR_ATTACK
            player.actionTimer = 0.28f
            player.attackHitboxActive = true
            player.hasHitCurrentAttack = false
            audio.playKickSwing()
            return
        }

        when (player.action) {
            StickmanAction.IDLE, StickmanAction.RUN -> {
                player.action = StickmanAction.PUNCH_1
                player.actionTimer = 0.20f
                player.attackHitboxActive = true
                player.hasHitCurrentAttack = false
                player.vel.x = if (player.isFacingRight) 60f else -60f
                audio.playPunchSwing()
            }
            StickmanAction.PUNCH_1 -> {
                player.action = StickmanAction.PUNCH_2
                player.actionTimer = 0.22f
                player.attackHitboxActive = true
                player.hasHitCurrentAttack = false
                player.vel.x = if (player.isFacingRight) 90f else -90f
                audio.playPunchSwing()
            }
            StickmanAction.PUNCH_2 -> {
                player.action = StickmanAction.KICK
                player.actionTimer = 0.26f
                player.attackHitboxActive = true
                player.hasHitCurrentAttack = false
                player.vel.x = if (player.isFacingRight) 140f else -140f
                audio.playKickSwing()
            }
            StickmanAction.KICK -> {
                player.action = StickmanAction.HEAVY_STRIKE
                player.actionTimer = 0.35f
                player.attackHitboxActive = true
                player.hasHitCurrentAttack = false
                player.vel.x = if (player.isFacingRight) 220f else -220f
                audio.playHeavySwing()
            }
            else -> {
                // If in recovery or other state, allow starting punch if timer almost done
                if (player.actionTimer <= 0.08f) {
                    player.action = StickmanAction.PUNCH_1
                    player.actionTimer = 0.20f
                    player.attackHitboxActive = true
                    player.hasHitCurrentAttack = false
                    audio.playPunchSwing()
                }
            }
        }
    }

    private fun onActionFinished(player: PlayerFighter) {
        player.attackHitboxActive = false
        player.hasHitCurrentAttack = false

        when (player.action) {
            StickmanAction.PUNCH_1, StickmanAction.PUNCH_2,
            StickmanAction.KICK, StickmanAction.HEAVY_STRIKE,
            StickmanAction.AIR_ATTACK, StickmanAction.DASH,
            StickmanAction.HIT_REACTION, StickmanAction.KNOCKBACK,
            StickmanAction.SPECIAL_CAST, StickmanAction.COUNTER -> {
                player.action = if (player.isGrounded) StickmanAction.IDLE else StickmanAction.FALL
            }
            else -> {}
        }
    }
}
