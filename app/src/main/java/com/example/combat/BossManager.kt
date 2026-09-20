package com.example.combat

import com.example.model.BossDefinition
import com.example.model.Difficulty
import com.example.model.EnemyCombatState
import com.example.model.MathConceptEffect
import com.example.model.MathQuestion
import com.example.model.MathTopic
import java.util.UUID

class BossManager {

    fun getAllBosses(): List<BossDefinition> = BossDefinition.ALL_BOSSES

    fun getBossById(id: String): BossDefinition? {
        return BossDefinition.ALL_BOSSES.find { it.id == id }
    }

    fun createBossCombatState(boss: BossDefinition): EnemyCombatState {
        val totalPhases = boss.phaseEquations.size
        return EnemyCombatState(
            name = boss.name,
            title = boss.subtitle,
            maxHp = 100f * totalPhases,
            hp = 100f * totalPhases,
            shield = 40f,
            maxShield = 40f,
            color = boss.color,
            isBoss = true,
            currentPhase = 1,
            totalPhases = totalPhases,
            symbolAffinity = boss.topic.symbol,
            attackName = boss.specialMechanic
        )
    }

    fun generateBossPhaseQuestion(boss: BossDefinition, phase: Int): MathQuestion {
        val safePhase = (phase - 1).coerceIn(0, boss.phaseEquations.lastIndex)
        val equationStr = boss.phaseEquations[safePhase]
        val (prompt, correct, distractors, expl) = when (boss.id) {
            "boss_algebra" -> {
                when (safePhase) {
                    0 -> Quad("Solve Phase 1: x + 7 = 15", "8", listOf("7", "9", "22"), "Subtract 7 from both sides: x = 15 − 7 = 8.")
                    1 -> Quad("Solve Phase 2: 3x = 27", "9", listOf("8", "6", "24"), "Divide both sides by 3: x = 27 ÷ 3 = 9.")
                    2 -> Quad("Solve Phase 3: 2x + 5 = 19", "7", listOf("12", "6", "14"), "2x = 19 − 5 = 14 → x = 14 ÷ 2 = 7.")
                    else -> Quad("Solve Phase 4: 3(x + 2) = 21", "5", listOf("7", "4", "9"), "x + 2 = 21 ÷ 3 = 7 → x = 7 − 2 = 5.")
                }
            }
            "boss_integer" -> {
                when (safePhase) {
                    0 -> Quad("Solve Phase 1: |x − 8| = 12 (positive root)", "20", listOf("4", "-4", "16"), "|20 − 8| = |12| = 12. Root is 20.")
                    1 -> Quad("Solve Phase 2: −4x + 16 = −8", "6", listOf("-6", "8", "-2"), "−4x = −24 → x = −24 ÷ (−4) = 6.")
                    else -> Quad("Solve Phase 3: x² − 25 = 0 (positive root)", "5", listOf("25", "-5", "10"), "x² = 25 → x = √25 = 5.")
                }
            }
            "boss_fraction" -> {
                when (safePhase) {
                    0 -> Quad("Solve Phase 1: 3/4 + 2/5 = x/20", "23", listOf("5", "20", "15"), "(15 + 8)/20 = 23/20 → x = 23.")
                    1 -> Quad("Solve Phase 2: x/6 = 7/2", "21", listOf("14", "12", "42"), "x = 6 × (7/2) = 21.")
                    else -> Quad("Solve Phase 3: (2x + 1)/3 = 5", "7", listOf("8", "14", "6"), "2x + 1 = 15 → 2x = 14 → x = 7.")
                }
            }
            "boss_geometry" -> {
                when (safePhase) {
                    0 -> Quad("Solve Phase 1: Right triangle legs 3, 4. Hypotenuse c = ?", "5", listOf("7", "6", "12"), "c² = 3² + 4² = 9 + 16 = 25 → c = 5.")
                    1 -> Quad("Solve Phase 2: Pentagon internal angles sum = ?", "540°", listOf("360°", "720°", "180°"), "(n − 2) × 180° = (5 − 2) × 180° = 540°.")
                    else -> Quad("Solve Phase 3: Circle radius r = 3, Area = ? (in terms of π)", "9π", listOf("6π", "3π", "18π"), "Area = πr² = π(3)² = 9π.")
                }
            }
            else -> {
                when (safePhase) {
                    0 -> Quad("Solve Phase 1: f(x) = 2x² − 3. Find f(4)", "29", listOf("25", "32", "13"), "f(4) = 2(16) − 3 = 32 − 3 = 29.")
                    1 -> Quad("Solve Phase 2: f(x) = x², g(x) = x + 1. Find f(g(2))", "9", listOf("5", "6", "8"), "g(2) = 3 → f(3) = 3² = 9.")
                    else -> Quad("Solve Phase 3: lim(x→∞) [3x / (x + 2)]", "3", listOf("∞", "0", "1.5"), "Dividing by x gives lim [3 / (1 + 2/x)] = 3/1 = 3.")
                }
            }
        }

        val allChoices = (distractors + correct).shuffled()
        val correctIdx = allChoices.indexOf(correct)

        return MathQuestion(
            id = UUID.randomUUID().toString(),
            topic = boss.topic,
            difficulty = Difficulty.HARD,
            prompt = prompt,
            formulaDisplay = equationStr,
            choices = allChoices,
            correctIndex = correctIdx,
            explanation = expl,
            timeLimitSec = 14,
            conceptEffect = MathConceptEffect.EQUATION
        )
    }

    private data class Quad(val prompt: String, val correct: String, val distractors: List<String>, val explanation: String)
}
