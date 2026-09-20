package com.example.game.systems

import androidx.compose.ui.graphics.Color
import com.example.game.model.EnemyType
import com.example.game.model.MathEntity
import com.example.game.model.Vec2
import kotlin.random.Random

class MathEntitySystem {

    fun spawnEntity(type: EnemyType, spawnX: Float, groundY: Float = 720f): MathEntity {
        val id = System.nanoTime() + Random.nextLong(1000)
        return when (type) {
            EnemyType.NUM_0 -> MathEntity(
                id = id,
                type = type,
                pos = Vec2(spawnX, groundY),
                hp = 90f,
                maxHp = 90f,
                shield = 80f,
                maxShield = 80f,
                width = 54f,
                height = 68f,
                color = Color(0xFF8A8D98),
                glowColor = Color(0xFFD4A373),
                symbolString = "0"
            )
            EnemyType.NUM_1 -> MathEntity(
                id = id,
                type = type,
                pos = Vec2(spawnX, groundY),
                hp = 65f,
                maxHp = 65f,
                width = 40f,
                height = 70f,
                color = Color(0xFFEDEDF2),
                symbolString = "1"
            )
            EnemyType.NUM_2 -> MathEntity(
                id = id,
                type = type,
                pos = Vec2(spawnX, groundY),
                hp = 70f,
                maxHp = 70f,
                width = 48f,
                height = 64f,
                color = Color(0xFFD4A373),
                symbolString = "2"
            )
            EnemyType.NUM_3 -> MathEntity(
                id = id,
                type = type,
                pos = Vec2(spawnX, groundY),
                hp = 75f,
                maxHp = 75f,
                width = 48f,
                height = 66f,
                color = Color(0xFFC05621),
                symbolString = "3"
            )
            EnemyType.NUM_4 -> MathEntity(
                id = id,
                type = type,
                pos = Vec2(spawnX, groundY),
                hp = 80f,
                maxHp = 80f,
                width = 50f,
                height = 68f,
                color = Color(0xFF9A8C98),
                symbolString = "4"
            )
            EnemyType.NUM_5 -> MathEntity(
                id = id,
                type = type,
                pos = Vec2(spawnX, groundY),
                hp = 80f,
                maxHp = 80f,
                width = 50f,
                height = 68f,
                color = Color(0xFFE67E22),
                symbolString = "5"
            )
            EnemyType.NUM_6 -> MathEntity(
                id = id,
                type = type,
                pos = Vec2(spawnX, groundY),
                hp = 85f,
                maxHp = 85f,
                width = 52f,
                height = 70f,
                color = Color(0xFF6B7280),
                symbolString = "6"
            )
            EnemyType.NUM_7 -> MathEntity(
                id = id,
                type = type,
                pos = Vec2(spawnX, groundY - 200f),
                hp = 80f,
                maxHp = 80f,
                width = 50f,
                height = 66f,
                color = Color(0xFFC0392B),
                symbolString = "7"
            )
            EnemyType.NUM_8 -> MathEntity(
                id = id,
                type = type,
                pos = Vec2(spawnX, groundY),
                hp = 140f,
                maxHp = 140f,
                width = 62f,
                height = 76f,
                color = Color(0xFFD35400),
                symbolString = "8"
            )
            EnemyType.NUM_9 -> MathEntity(
                id = id,
                type = type,
                pos = Vec2(spawnX, groundY),
                hp = 180f,
                maxHp = 180f,
                width = 68f,
                height = 80f,
                color = Color(0xFFA93226),
                symbolString = "9"
            )
            EnemyType.OP_PLUS -> MathEntity(
                id = id,
                type = type,
                pos = Vec2(spawnX, groundY - 120f),
                hp = 75f,
                maxHp = 75f,
                width = 50f,
                height = 50f,
                color = Color(0xFFE67E22),
                symbolString = "+"
            )
            EnemyType.OP_MINUS -> MathEntity(
                id = id,
                type = type,
                pos = Vec2(spawnX, groundY - 60f),
                hp = 70f,
                maxHp = 70f,
                width = 56f,
                height = 36f,
                color = Color(0xFFB0B3BC),
                symbolString = "−"
            )
            EnemyType.OP_MULTIPLY -> MathEntity(
                id = id,
                type = type,
                pos = Vec2(spawnX, groundY - 50f),
                hp = 85f,
                maxHp = 85f,
                width = 52f,
                height = 52f,
                color = Color(0xFFC0392B),
                symbolString = "×"
            )
            EnemyType.OP_DIVIDE -> MathEntity(
                id = id,
                type = type,
                pos = Vec2(spawnX, groundY),
                hp = 80f,
                maxHp = 80f,
                width = 50f,
                height = 56f,
                color = Color(0xFFD35400),
                symbolString = "÷"
            )
            EnemyType.OP_EQUALS -> MathEntity(
                id = id,
                type = type,
                pos = Vec2(spawnX, groundY),
                hp = 110f,
                maxHp = 110f,
                width = 56f,
                height = 48f,
                color = Color(0xFFDFE2E8),
                symbolString = "="
            )
            EnemyType.OP_ROOT -> MathEntity(
                id = id,
                type = type,
                pos = Vec2(spawnX, groundY),
                hp = 95f,
                maxHp = 95f,
                width = 52f,
                height = 64f,
                color = Color(0xFFD4AC0D),
                symbolString = "√"
            )
            EnemyType.OP_PERCENT -> MathEntity(
                id = id,
                type = type,
                pos = Vec2(spawnX, groundY - 40f),
                hp = 80f,
                maxHp = 80f,
                width = 52f,
                height = 52f,
                color = Color(0xFF9E9D89),
                symbolString = "%"
            )
            // Advanced Math Monsters
            EnemyType.MONSTER_FRACTION -> MathEntity(
                id = id,
                type = type,
                pos = Vec2(spawnX, groundY - 80f),
                hp = 110f,
                maxHp = 110f,
                width = 56f,
                height = 70f,
                color = Color(0xFFE59866),
                symbolString = "½"
            )
            EnemyType.MONSTER_GEOMETRY_DELTA -> MathEntity(
                id = id,
                type = type,
                pos = Vec2(spawnX, groundY - 40f),
                hp = 90f,
                maxHp = 90f,
                width = 54f,
                height = 54f,
                color = Color(0xFFE67E22),
                symbolString = "Δ"
            )
            EnemyType.MONSTER_GEOMETRY_CIRCLE -> MathEntity(
                id = id,
                type = type,
                pos = Vec2(spawnX, groundY - 50f),
                hp = 95f,
                maxHp = 95f,
                width = 56f,
                height = 56f,
                color = Color(0xFFD4A373),
                symbolString = "○"
            )
            EnemyType.MONSTER_ALGEBRA_X -> MathEntity(
                id = id,
                type = type,
                pos = Vec2(spawnX, groundY),
                hp = 85f,
                maxHp = 85f,
                width = 48f,
                height = 64f,
                color = Color(0xFFCCD1D9),
                symbolString = "x"
            )
            EnemyType.MONSTER_EQUATION -> MathEntity(
                id = id,
                type = type,
                pos = Vec2(spawnX, groundY),
                hp = 160f,
                maxHp = 160f,
                width = 72f,
                height = 72f,
                color = Color(0xFFC0392B),
                symbolString = "E=mc²"
            )
            // Math Soldiers (Original Stickman Combatants)
            EnemyType.SOLDIER_SIGMA -> MathEntity(
                id = id,
                type = type,
                pos = Vec2(spawnX, groundY),
                hp = 115f,
                maxHp = 115f,
                shield = 40f,
                maxShield = 40f,
                width = 46f,
                height = 84f,
                color = Color(0xFFD4A373),
                symbolString = "Σ",
                isSoldier = true,
                soldierStyle = "SIGMA"
            )
            EnemyType.SOLDIER_PI -> MathEntity(
                id = id,
                type = type,
                pos = Vec2(spawnX, groundY),
                hp = 85f,
                maxHp = 85f,
                width = 44f,
                height = 80f,
                color = Color(0xFFE67E22),
                symbolString = "π",
                isSoldier = true,
                soldierStyle = "PI"
            )
            EnemyType.SOLDIER_THETA -> MathEntity(
                id = id,
                type = type,
                pos = Vec2(spawnX, groundY),
                hp = 95f,
                maxHp = 95f,
                width = 44f,
                height = 82f,
                color = Color(0xFFC05621),
                symbolString = "θ",
                isSoldier = true,
                soldierStyle = "THETA"
            )
            EnemyType.SOLDIER_DELTA -> MathEntity(
                id = id,
                type = type,
                pos = Vec2(spawnX, groundY),
                hp = 80f,
                maxHp = 80f,
                width = 42f,
                height = 78f,
                color = Color(0xFFB0B3BC),
                symbolString = "Δ",
                isSoldier = true,
                soldierStyle = "DELTA"
            )
            EnemyType.BOSS_INTEGER_CORE -> MathEntity(
                id = id,
                type = type,
                pos = Vec2(spawnX, groundY),
                hp = 600f,
                maxHp = 600f,
                shield = 150f,
                maxShield = 150f,
                width = 110f,
                height = 130f,
                color = Color(0xFFC0392B),
                symbolString = "|Z|",
                isBoss = true,
                scale = 1.4f
            )
            EnemyType.BOSS_FRACTION_ENGINE -> MathEntity(
                id = id,
                type = type,
                pos = Vec2(spawnX, groundY),
                hp = 750f,
                maxHp = 750f,
                shield = 200f,
                maxShield = 200f,
                width = 120f,
                height = 140f,
                color = Color(0xFFD35400),
                symbolString = "p/q",
                isBoss = true,
                scale = 1.5f
            )
            EnemyType.BOSS_ALGEBRA_BEAST -> MathEntity(
                id = id,
                type = type,
                pos = Vec2(spawnX, groundY),
                hp = 900f,
                maxHp = 900f,
                shield = 250f,
                maxShield = 250f,
                width = 130f,
                height = 150f,
                color = Color(0xFFB9770E),
                symbolString = "3x² + 2x",
                isBoss = true,
                scale = 1.6f
            )
            EnemyType.BOSS_FUNCTION_MACHINE -> MathEntity(
                id = id,
                type = type,
                pos = Vec2(spawnX, groundY),
                hp = 1100f,
                maxHp = 1100f,
                shield = 300f,
                maxShield = 300f,
                width = 140f,
                height = 160f,
                color = Color(0xFFA93226),
                symbolString = "f(g(x))",
                isBoss = true,
                scale = 1.7f
            )
            EnemyType.BOSS_EQUATION -> MathEntity(
                id = id,
                type = type,
                pos = Vec2(spawnX, groundY),
                hp = 1500f,
                maxHp = 1500f,
                shield = 400f,
                maxShield = 400f,
                width = 150f,
                height = 170f,
                color = Color(0xFFE67E22),
                symbolString = "∫ e^(iπ) + 1 = 0",
                isBoss = true,
                scale = 1.8f
            )
            else -> MathEntity(
                id = id,
                type = type,
                pos = Vec2(spawnX, groundY),
                hp = 70f,
                maxHp = 70f,
                width = 50f,
                height = 60f,
                color = Color.White,
                symbolString = "x"
            )
        }
    }

    fun handleSplits(
        deadEntities: List<MathEntity>,
        currentEnemies: MutableList<MathEntity>,
        groundY: Float
    ) {
        for (dead in deadEntities) {
            if (dead.type == EnemyType.OP_DIVIDE && dead.scale > 0.6f) {
                // Split into two smaller dividing entities
                val child1 = spawnEntity(EnemyType.OP_DIVIDE, dead.pos.x - 35f, groundY).apply {
                    scale = 0.65f
                    hp = 40f
                    maxHp = 40f
                    vel.x = -160f
                    vel.y = -250f
                }
                val child2 = spawnEntity(EnemyType.OP_DIVIDE, dead.pos.x + 35f, groundY).apply {
                    scale = 0.65f
                    hp = 40f
                    maxHp = 40f
                    vel.x = 160f
                    vel.y = -250f
                }
                currentEnemies.add(child1)
                currentEnemies.add(child2)
            }
        }
    }
}
