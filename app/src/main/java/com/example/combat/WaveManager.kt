package com.example.combat

import androidx.compose.ui.graphics.Color
import com.example.model.Difficulty
import com.example.model.EnemyCombatState
import com.example.model.MathTopic
import kotlin.random.Random

data class WaveConfig(
    val waveNumber: Int,
    val waveTitle: String,
    val enemyName: String,
    val enemyTitle: String,
    val enemyHp: Float,
    val enemyShield: Float,
    val enemyColor: Color,
    val difficulty: Difficulty,
    val primaryTopic: MathTopic?,
    val isBossWave: Boolean,
    val bossId: String? = null
)

class WaveManager {

    fun generateWave(waveNumber: Int): WaveConfig {
        val isBoss = waveNumber % 5 == 0
        val difficulty = when {
            waveNumber >= 12 -> Difficulty.EXPERT
            waveNumber >= 7 -> Difficulty.HARD
            waveNumber >= 3 -> Difficulty.MEDIUM
            else -> Difficulty.EASY
        }

        if (isBoss) {
            val bossIdx = ((waveNumber / 5) - 1) % 5
            val bossNames = listOf("THE INTEGER", "THE FRACTION", "THE ALGEBRA GUARDIAN", "THE GEOMETRY CORE", "THE FUNCTION MASTER")
            val bossTitles = listOf("Lord of Discrete Values", "Nexus of Splitting Planes", "Sentinel of the Variable", "Colossus of Euclidean Space", "Sovereign of Infinite Sets")
            val bossColors = listOf(Color(0xFFFF5252), Color(0xFFFFB300), Color(0xFF00E676), Color(0xFF00B0FF), Color(0xFFE040FB))
            val bossTopics = listOf(MathTopic.BASIC_ALGEBRA, MathTopic.FRACTIONS, MathTopic.LINEAR_EQUATIONS, MathTopic.GEOMETRY, MathTopic.ORDER_OF_OPERATIONS)

            return WaveConfig(
                waveNumber = waveNumber,
                waveTitle = "BOSS APEX // WAVE $waveNumber",
                enemyName = bossNames[bossIdx],
                enemyTitle = bossTitles[bossIdx],
                enemyHp = 120f + (waveNumber * 15f),
                enemyShield = 40f + (waveNumber * 10f),
                enemyColor = bossColors[bossIdx],
                difficulty = difficulty,
                primaryTopic = bossTopics[bossIdx],
                isBossWave = true,
                bossId = "boss_${bossIdx + 1}"
            )
        }

        val enemyTypes = listOf(
            Triple("Vector Drone", "Autonomous Linear Entity", Color(0xFF00E5FF)),
            Triple("Integer Sentinel", "Discrete Logic Guard", Color(0xFFFF9100)),
            Triple("Matrix Automaton", "Multi-Dimensional Grunt", Color(0xFF76FF03)),
            Triple("Polynomial Beast", "Curved Coordinate Creature", Color(0xFFE040FB)),
            Triple("Radical Vanguard", "Shield-Clad Operator", Color(0xFFFF5252))
        )
        val selectedEnemy = enemyTypes[(waveNumber - 1) % enemyTypes.size]

        return WaveConfig(
            waveNumber = waveNumber,
            waveTitle = "SECTOR COMBAT // WAVE $waveNumber",
            enemyName = selectedEnemy.first,
            enemyTitle = selectedEnemy.second,
            enemyHp = 50f + (waveNumber * 12f),
            enemyShield = if (waveNumber >= 2) 20f + (waveNumber * 6f) else 0f,
            enemyColor = selectedEnemy.third,
            difficulty = difficulty,
            primaryTopic = MathTopic.values()[(waveNumber - 1) % MathTopic.values().size],
            isBossWave = false
        )
    }

    fun spawnEnemyForWave(config: WaveConfig): EnemyCombatState {
        return EnemyCombatState(
            name = config.enemyName,
            title = config.enemyTitle,
            maxHp = config.enemyHp,
            hp = config.enemyHp,
            shield = config.enemyShield,
            maxShield = config.enemyShield,
            color = config.enemyColor,
            isBoss = config.isBossWave,
            currentPhase = 1,
            totalPhases = if (config.isBossWave) 4 else 1,
            symbolAffinity = config.primaryTopic?.symbol ?: "+"
        )
    }
}
