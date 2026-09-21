package com.example.game.levels

import com.example.game.model.ArenaType
import com.example.game.model.EnemyType

data class LevelDefinition(
    val levelNumber: Int,
    val chapter: Int,
    val chapterTitle: String,
    val levelTitle: String,
    val levelSubtitle: String,
    val arenaType: ArenaType,
    val enemyQueue: List<EnemyType>,
    val spawnInterval: Float = 1.2f,
    val isBossLevel: Boolean = false,
    val bossType: EnemyType? = null,
    val gravityModifier: Float = 1.0f,
    val targetScoreFor3Stars: Long = 1200L,
    val newFeatureIntro: String = ""
) {
    val title: String get() = levelTitle
    val subtitle: String get() = levelSubtitle
}

object LevelDefinitions {

    val LEVELS: List<LevelDefinition> = (1..50).map { lvl ->
        val chapter = ((lvl - 1) / 10) + 1
        val chapterTitle = when (chapter) {
            1 -> "WORLD 1: INTEGER AWAKENING"
            2 -> "WORLD 2: OPERATOR HORIZON"
            3 -> "WORLD 3: GEOMETRIC RUINS"
            4 -> "WORLD 4: ALGEBRAIC CITADEL"
            else -> "WORLD 5: CALCULUS SINGULARITY"
        }

        val arena = when (chapter) {
            1 -> ArenaType.NUMBER_LAB
            2 -> ArenaType.EQUATION_FACTORY
            3 -> ArenaType.GEOMETRY_RUINS
            4 -> ArenaType.ALGEBRA_CITY
            else -> if (lvl == 50) ArenaType.FINAL_EQUATION_CORE else ArenaType.FUNCTION_CHAMBER
        }

        val isBoss = (lvl % 10 == 0)
        val bossType = when (lvl) {
            10 -> EnemyType.BOSS_INTEGER_CORE
            20 -> EnemyType.BOSS_FRACTION_ENGINE
            30 -> EnemyType.BOSS_ALGEBRA_BEAST
            40 -> EnemyType.BOSS_FUNCTION_MACHINE
            50 -> EnemyType.BOSS_EQUATION
            else -> null
        }

        val enemies = when {
            isBoss -> listOf(bossType!!)
            lvl == 1 -> listOf(EnemyType.OP_PLUS, EnemyType.SOLDIER_SIGMA, EnemyType.SOLDIER_PI)
            lvl == 2 -> listOf(EnemyType.SOLDIER_THETA, EnemyType.OP_MINUS, EnemyType.SOLDIER_DELTA, EnemyType.OP_EQUALS)
            lvl == 3 -> listOf(EnemyType.MONSTER_GEOMETRY_DELTA, EnemyType.SOLDIER_SIGMA, EnemyType.MONSTER_GEOMETRY_CIRCLE, EnemyType.NUM_7)
            lvl == 4 -> listOf(EnemyType.OP_MULTIPLY, EnemyType.SOLDIER_PI, EnemyType.OP_MULTIPLY, EnemyType.SOLDIER_THETA)
            lvl == 5 -> listOf(EnemyType.MONSTER_FRACTION, EnemyType.OP_DIVIDE, EnemyType.NUM_8, EnemyType.SOLDIER_DELTA)
            lvl == 6 -> listOf(EnemyType.MONSTER_ALGEBRA_X, EnemyType.SOLDIER_SIGMA, EnemyType.NUM_9, EnemyType.OP_ROOT)
            lvl == 7 -> listOf(EnemyType.MONSTER_EQUATION, EnemyType.SOLDIER_PI, EnemyType.SOLDIER_THETA, EnemyType.OP_PERCENT)
            lvl == 8 -> listOf(EnemyType.OP_ROOT, EnemyType.NUM_9, EnemyType.OP_DIVIDE, EnemyType.NUM_7, EnemyType.OP_PLUS)
            lvl == 9 -> listOf(EnemyType.NUM_8, EnemyType.NUM_9, EnemyType.OP_MULTIPLY, EnemyType.OP_DIVIDE, EnemyType.NUM_0)
            else -> {
                val base = listOf(
                    EnemyType.NUM_7, EnemyType.NUM_8, EnemyType.NUM_9,
                    EnemyType.OP_MULTIPLY, EnemyType.OP_DIVIDE, EnemyType.OP_ROOT,
                    EnemyType.SOLDIER_SIGMA, EnemyType.SOLDIER_PI, EnemyType.SOLDIER_THETA, EnemyType.SOLDIER_DELTA,
                    EnemyType.MONSTER_FRACTION, EnemyType.MONSTER_GEOMETRY_DELTA, EnemyType.MONSTER_ALGEBRA_X
                )
                List(3 + (lvl % 5)) { base[(lvl + it) % base.size] }
            }
        }

        val title = if (isBoss) "STAGE $lvl: BOSS CLASH" else "STAGE $lvl: SECTOR ${((lvl - 1) % 10) + 1}"
        val subtitle = when {
            isBoss -> "TITAN GUARDIAN OF CHAPTER $chapter"
            lvl == 1 -> "NUMERAL COMBAT INITIATION"
            lvl == 2 -> "OPERATOR SUBTRACTION DASH"
            lvl == 3 -> "GEOMETRIC SHARP CORNERS"
            else -> "MATHEMATICAL ASSAULT WAVE"
        }

        val gravityMod = when (chapter) {
            3 -> 1.15f
            5 -> 1.25f
            else -> 1.0f
        }

        val targetScore = 800L + (lvl * 150L)

        val intro = when (lvl) {
            1 -> "NEW: Basic Attack Combo & Stickman Soldiers!"
            2 -> "NEW: Subtraction Dash & Shield Breakers!"
            3 -> "NEW: Geometric Shockwave Enemies!"
            4 -> "NEW: Multiplication Shuriken Barrage!"
            5 -> "NEW: Fraction Twin Orbits!"
            10 -> "WARNING: Chapter 1 Boss - Integer Core!"
            20 -> "WARNING: Chapter 2 Boss - Fraction Engine!"
            30 -> "WARNING: Chapter 3 Boss - Algebra Beast!"
            40 -> "WARNING: Chapter 4 Boss - Function Machine!"
            50 -> "WARNING: Final Boss - The Equation Singularity!"
            else -> ""
        }

        LevelDefinition(
            levelNumber = lvl,
            chapter = chapter,
            chapterTitle = chapterTitle,
            levelTitle = title,
            levelSubtitle = subtitle,
            arenaType = arena,
            enemyQueue = enemies,
            spawnInterval = maxOf(0.7f, 1.4f - (lvl * 0.015f)),
            isBossLevel = isBoss,
            bossType = bossType,
            gravityModifier = gravityMod,
            targetScoreFor3Stars = targetScore,
            newFeatureIntro = intro
        )
    }

    fun getLevel(levelNumber: Int): LevelDefinition {
        val clamped = levelNumber.coerceIn(1, 50)
        return LEVELS[clamped - 1]
    }
}
