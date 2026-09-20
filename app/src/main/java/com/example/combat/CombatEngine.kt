package com.example.combat

import androidx.compose.ui.graphics.Color
import com.example.model.EnemyCombatState
import com.example.model.FloatingCombatText
import com.example.model.MathConceptEffect
import com.example.model.MathParticle
import com.example.model.PlayerCombatState
import com.example.model.StickmanPose
import kotlin.random.Random

data class CombatResult(
    val updatedPlayer: PlayerCombatState,
    val updatedEnemy: EnemyCombatState,
    val combo: Int,
    val scoreGained: Long,
    val particles: List<MathParticle>,
    val floatingTexts: List<FloatingCombatText>,
    val isEnemyDefeated: Boolean,
    val isPlayerDefeated: Boolean,
    val eventMessage: String
)

class CombatEngine {

    fun processCorrectAnswer(
        player: PlayerCombatState,
        enemy: EnemyCombatState,
        currentCombo: Int,
        conceptEffect: MathConceptEffect,
        timeRemainingRatio: Float,
        activeParticles: List<MathParticle>,
        activeTexts: List<FloatingCombatText>
    ): CombatResult {
        val newCombo = currentCombo + 1
        val isMathRage = newCombo >= 5
        val isEqBreaker = newCombo >= 10

        // Base damage scaled with combo and speed
        val speedMultiplier = 1.0f + (timeRemainingRatio * 0.5f)
        val comboMultiplier = 1.0f + (newCombo * 0.2f)
        val rageMultiplier = if (isMathRage) 1.5f else 1.0f
        var baseDamage = 20f * speedMultiplier * comboMultiplier * rageMultiplier

        // Concept effects
        var enemyShield = enemy.shield
        var playerShield = player.shield
        var specialGain = 15f * comboMultiplier
        var effectMessage = ""

        when (conceptEffect) {
            MathConceptEffect.ADDITION -> {
                specialGain += 20f
                playerShield = minOf(player.maxShield, playerShield + 15f)
                effectMessage = "+ ENERGY SURGE!"
            }
            MathConceptEffect.SUBTRACTION -> {
                enemyShield = maxOf(0f, enemyShield - 30f)
                baseDamage *= 1.2f
                effectMessage = "− SHIELD PIERCER!"
            }
            MathConceptEffect.MULTIPLICATION -> {
                baseDamage *= 1.4f
                effectMessage = "× MULTI-IMPACT!"
            }
            MathConceptEffect.DIVISION -> {
                enemyShield = maxOf(0f, enemyShield / 2f)
                effectMessage = "÷ DISRUPTOR SPLIT!"
            }
            MathConceptEffect.POWERS -> {
                baseDamage *= 1.6f
                specialGain += 25f
                effectMessage = "x² EXPONENTIAL BLAST!"
            }
            MathConceptEffect.SQUARE_ROOT -> {
                enemyShield = 0f // break shield completely
                baseDamage *= 1.3f
                effectMessage = "√ ROOT SHIELD BREAKER!"
            }
            MathConceptEffect.EQUATION -> {
                baseDamage *= 2.0f
                effectMessage = "= EQUATION RESOLUTION!"
            }
        }

        // Apply damage to enemy shield first, then HP
        var remainingDmg = baseDamage
        if (enemyShield > 0f) {
            if (enemyShield >= remainingDmg) {
                enemyShield -= remainingDmg
                remainingDmg = 0f
            } else {
                remainingDmg -= enemyShield
                enemyShield = 0f
            }
        }
        val newEnemyHp = maxOf(0f, enemy.hp - remainingDmg)
        val enemyDefeated = newEnemyHp <= 0f

        val newSpecial = minOf(player.maxSpecial, player.specialMeter + specialGain)

        // Pose
        val playerPose = if (newCombo % 2 == 0) StickmanPose.BASIC_ATTACK_KICK else StickmanPose.BASIC_ATTACK_PUNCH
        val enemyPose = if (enemyDefeated) StickmanPose.DEFEAT else StickmanPose.HIT_REACTION

        val updatedPlayer = player.copy(
            shield = playerShield,
            specialMeter = newSpecial,
            currentPose = playerPose,
            isMathRage = isMathRage,
            isEquationBreakerReady = isEqBreaker || newSpecial >= player.maxSpecial
        )

        val updatedEnemy = enemy.copy(
            hp = newEnemyHp,
            shield = enemyShield,
            pose = enemyPose
        )

        // Score calculation
        val score = (100 * comboMultiplier * speedMultiplier * (if (isMathRage) 2 else 1)).toLong()

        // Generate dynamic math particles & floating text
        val newParticles = mutableListOf<MathParticle>()
        val particleColor = player.skin.primaryColor
        val burstCount = if (conceptEffect == MathConceptEffect.MULTIPLICATION) 18 else 10
        val symbols = listOf(conceptEffect.symbol, "=", "∑", "π", "∫", "x", "√", "+")

        for (i in 0 until burstCount) {
            val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
            val speed = Random.nextFloat() * 0.015f + 0.005f
            newParticles.add(
                MathParticle(
                    id = System.nanoTime() + i,
                    symbol = symbols.random(),
                    xRatio = 0.72f, // target enemy location
                    yRatio = 0.40f,
                    vx = kotlin.math.cos(angle) * speed,
                    vy = kotlin.math.sin(angle) * speed,
                    color = if (i % 2 == 0) particleColor else Color.White,
                    size = Random.nextFloat() * 12f + 14f,
                    rotation = Random.nextFloat() * 360f,
                    vRot = (Random.nextFloat() - 0.5f) * 8f
                )
            )
        }

        val newTexts = mutableListOf<FloatingCombatText>()
        newTexts.add(
            FloatingCombatText(
                id = System.nanoTime(),
                text = "-${baseDamage.toInt()}",
                xRatio = 0.72f,
                yRatio = 0.35f,
                color = Color(0xFFFF5252),
                sizeSp = 22f
            )
        )
        if (effectMessage.isNotEmpty()) {
            newTexts.add(
                FloatingCombatText(
                    id = System.nanoTime() + 1,
                    text = effectMessage,
                    xRatio = 0.5f,
                    yRatio = 0.28f,
                    color = Color(0xFF00E5FF),
                    sizeSp = 18f
                )
            )
        }
        if (newCombo >= 2) {
            val comboColor = when {
                newCombo >= 10 -> Color(0xFFFF1744)
                newCombo >= 5 -> Color(0xFFFF9100)
                else -> Color(0xFF00E5FF)
            }
            val comboLabel = when {
                newCombo >= 10 -> "EQUATION BREAKER x$newCombo!"
                newCombo >= 5 -> "MATH RAGE x$newCombo!"
                else -> "COMBO x$newCombo!"
            }
            newTexts.add(
                FloatingCombatText(
                    id = System.nanoTime() + 2,
                    text = comboLabel,
                    xRatio = 0.35f,
                    yRatio = 0.30f,
                    color = comboColor,
                    sizeSp = 20f
                )
            )
        }

        return CombatResult(
            updatedPlayer = updatedPlayer,
            updatedEnemy = updatedEnemy,
            combo = newCombo,
            scoreGained = score,
            particles = (activeParticles + newParticles).takeLast(45),
            floatingTexts = (activeTexts + newTexts).takeLast(10),
            isEnemyDefeated = enemyDefeated,
            isPlayerDefeated = false,
            eventMessage = effectMessage
        )
    }

    fun processIncorrectAnswer(
        player: PlayerCombatState,
        enemy: EnemyCombatState,
        activeParticles: List<MathParticle>,
        activeTexts: List<FloatingCombatText>
    ): CombatResult {
        // Enemy strikes back, combo broken
        val incomingDmg = 18f
        var playerShield = player.shield
        var remainingDmg = incomingDmg

        if (playerShield > 0f) {
            if (playerShield >= remainingDmg) {
                playerShield -= remainingDmg
                remainingDmg = 0f
            } else {
                remainingDmg -= playerShield
                playerShield = 0f
            }
        }
        val newPlayerHp = maxOf(0f, player.hp - remainingDmg)
        val playerDefeated = newPlayerHp <= 0f

        val updatedPlayer = player.copy(
            hp = newPlayerHp,
            shield = playerShield,
            currentPose = if (playerDefeated) StickmanPose.DEFEAT else StickmanPose.HIT_REACTION,
            isMathRage = false,
            isEquationBreakerReady = false
        )

        val updatedEnemy = enemy.copy(
            pose = StickmanPose.BASIC_ATTACK_KICK
        )

        val newTexts = mutableListOf<FloatingCombatText>()
        newTexts.add(
            FloatingCombatText(
                id = System.nanoTime(),
                text = "COMBO BREAK!",
                xRatio = 0.30f,
                yRatio = 0.35f,
                color = Color(0xFFFF5252),
                sizeSp = 20f
            )
        )
        newTexts.add(
            FloatingCombatText(
                id = System.nanoTime() + 1,
                text = "-${incomingDmg.toInt()}",
                xRatio = 0.28f,
                yRatio = 0.30f,
                color = Color(0xFFFF1744),
                sizeSp = 22f
            )
        )

        // Stagger particles on player
        val newParticles = mutableListOf<MathParticle>()
        for (i in 0 until 8) {
            val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
            val speed = Random.nextFloat() * 0.01f + 0.003f
            newParticles.add(
                MathParticle(
                    id = System.nanoTime() + i,
                    symbol = listOf("≠", "!", "?", "0").random(),
                    xRatio = 0.28f,
                    yRatio = 0.40f,
                    vx = kotlin.math.cos(angle) * speed,
                    vy = kotlin.math.sin(angle) * speed,
                    color = Color(0xFFFF5252),
                    size = 16f
                )
            )
        }

        return CombatResult(
            updatedPlayer = updatedPlayer,
            updatedEnemy = updatedEnemy,
            combo = 0,
            scoreGained = 0L,
            particles = (activeParticles + newParticles).takeLast(40),
            floatingTexts = (activeTexts + newTexts).takeLast(10),
            isEnemyDefeated = false,
            isPlayerDefeated = playerDefeated,
            eventMessage = "Miscalculation! Combo Reset!"
        )
    }

    fun triggerSpecialAttack(
        player: PlayerCombatState,
        enemy: EnemyCombatState,
        activeParticles: List<MathParticle>,
        activeTexts: List<FloatingCombatText>
    ): CombatResult {
        if (player.specialMeter < 50f && !player.isEquationBreakerReady) {
            return CombatResult(
                player, enemy, 0, 0, activeParticles, activeTexts, false, false, "Special meter not full!"
            )
        }

        val specialDamage = 60f
        val newEnemyShield = 0f // Obliterates shield
        val newEnemyHp = maxOf(0f, enemy.hp - specialDamage)
        val enemyDefeated = newEnemyHp <= 0f

        val updatedPlayer = player.copy(
            specialMeter = 0f,
            isEquationBreakerReady = false,
            currentPose = StickmanPose.SPECIAL_RELEASE
        )
        val updatedEnemy = enemy.copy(
            hp = newEnemyHp,
            shield = newEnemyShield,
            pose = if (enemyDefeated) StickmanPose.DEFEAT else StickmanPose.HIT_REACTION
        )

        val newTexts = listOf(
            FloatingCombatText(
                id = System.nanoTime(),
                text = "ULTIMATE EQUATION BREAKER!!",
                xRatio = 0.5f,
                yRatio = 0.22f,
                color = Color(0xFFFFD600),
                sizeSp = 24f
            ),
            FloatingCombatText(
                id = System.nanoTime() + 1,
                text = "-${specialDamage.toInt()}",
                xRatio = 0.72f,
                yRatio = 0.32f,
                color = Color(0xFFFFD600),
                sizeSp = 26f
            )
        )

        // Huge shockwave and particle storm
        val newParticles = mutableListOf<MathParticle>()
        val symbols = listOf("∫", "∑", "e^(iπ)", "∞", "f(x)", "d/dx", "∇")
        for (i in 0 until 25) {
            val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
            val speed = Random.nextFloat() * 0.02f + 0.008f
            newParticles.add(
                MathParticle(
                    id = System.nanoTime() + i,
                    symbol = symbols.random(),
                    xRatio = 0.72f,
                    yRatio = 0.40f,
                    vx = kotlin.math.cos(angle) * speed,
                    vy = kotlin.math.sin(angle) * speed,
                    color = if (i % 2 == 0) Color(0xFFFFD600) else Color(0xFF00E5FF),
                    size = 20f
                )
            )
        }

        return CombatResult(
            updatedPlayer = updatedPlayer,
            updatedEnemy = updatedEnemy,
            combo = 10,
            scoreGained = 500L,
            particles = (activeParticles + newParticles).takeLast(50),
            floatingTexts = (activeTexts + newTexts).takeLast(10),
            isEnemyDefeated = enemyDefeated,
            isPlayerDefeated = false,
            eventMessage = "EQUATION BREAKER DEPLOYED!"
        )
    }
}
