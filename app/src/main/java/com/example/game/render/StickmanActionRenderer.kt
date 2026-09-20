package com.example.game.render

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.game.model.MathEntity
import com.example.game.model.PlayerFighter
import com.example.game.model.StickmanAction
import kotlin.math.cos
import kotlin.math.sin

object StickmanActionRenderer {

    fun drawStickman(
        drawScope: DrawScope,
        player: PlayerFighter,
        animTick: Float
    ) {
        val px = player.pos.x
        val py = player.pos.y
        val dir = if (player.isFacingRight) 1f else -1f
        val color = player.primaryColor
        val energyColor = player.energyColor

        // Dash motion shadows / graphite motion lines
        if (player.action == StickmanAction.DASH) {
            for (i in 1..3) {
                val shadowAlpha = 0.28f / i
                val shadowX = px - (dir * i * 32f)
                drawStickmanPose(
                    drawScope, shadowX, py, dir,
                    player.action, color.copy(alpha = shadowAlpha),
                    energyColor.copy(alpha = shadowAlpha), 1.0f, player.actionTimer, isSoldier = false
                )
            }
            // Motion speed lines behind player
            for (i in 0..2) {
                val lineY = py - 60f + (i * 22f)
                drawScope.drawLine(
                    color = Color(0x558A8D98),
                    start = Offset(px - dir * 30f, lineY),
                    end = Offset(px - dir * 90f, lineY),
                    strokeWidth = 2f,
                    cap = StrokeCap.Round
                )
            }
        }

        // Combo heat aura - minimal warm subtle ring
        if (player.comboCount >= 5) {
            val auraRadius = 42f + sin(animTick * 6f) * 4f
            drawScope.drawCircle(
                color = energyColor.copy(alpha = 0.08f),
                radius = auraRadius,
                center = Offset(px, py - 45f)
            )
            drawScope.drawCircle(
                color = energyColor.copy(alpha = 0.35f),
                radius = auraRadius,
                center = Offset(px, py - 45f),
                style = Stroke(width = 1.5f)
            )
        }

        // Main stickman pose with invulnerability blink
        val mainAlpha = if (player.isInvulnerable && (animTick * 22f).toInt() % 2 == 0) 0.35f else 1.0f
        drawStickmanPose(
            drawScope, px, py, dir,
            player.action, color.copy(alpha = mainAlpha),
            energyColor.copy(alpha = mainAlpha), animTick, player.actionTimer, isSoldier = false
        )

        // Attack slash arcs
        if (player.attackHitboxActive) {
            drawAttackSlash(drawScope, px, py, dir, player.action, energyColor)
        }
    }

    fun drawSoldierStickman(
        drawScope: DrawScope,
        soldier: MathEntity,
        animTick: Float
    ) {
        val ex = soldier.pos.x
        val ey = soldier.pos.y
        val dir = if (soldier.isFacingRight) 1f else -1f
        val color = if (soldier.hitFlashTimer > 0f) Color.White else soldier.color
        val accentColor = Color(0xFFE67E22)

        // Draw soldier stickman pose
        drawStickmanPose(
            drawScope, ex, ey, dir,
            soldier.action, color,
            accentColor, animTick, soldier.actionTimer, isSoldier = true, soldierStyle = soldier.soldierStyle
        )

        // Soldier attack slash
        if (soldier.attackHitboxActive) {
            drawAttackSlash(drawScope, ex, ey, dir, soldier.action, color)
        }

        // Mini Health Bar above soldier
        val barWidth = 44f
        val barHeight = 4f
        val barX = ex - barWidth / 2f
        val barY = ey - 88f
        val hpPct = (soldier.hp / soldier.maxHp).coerceIn(0f, 1f)

        drawScope.drawRect(
            color = Color(0x66000000),
            topLeft = Offset(barX, barY),
            size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
        )
        drawScope.drawRect(
            color = if (soldier.hitFlashTimer > 0f) Color.White else soldier.color,
            topLeft = Offset(barX, barY),
            size = androidx.compose.ui.geometry.Size(barWidth * hpPct, barHeight)
        )
    }

    private fun drawStickmanPose(
        drawScope: DrawScope,
        x: Float,
        y: Float,
        dir: Float,
        action: StickmanAction,
        color: Color,
        energyColor: Color,
        tick: Float,
        actionTimer: Float,
        isSoldier: Boolean,
        soldierStyle: String = ""
    ) {
        val strokeW = if (isSoldier && soldierStyle == "SIGMA") 5.5f else 4.2f

        // Head center
        var headY = y - 76f
        var headX = x

        // Torso
        var neckY = headY + 12f
        var hipX = x
        var hipY = y - 36f

        // Limbs offsets
        var leftHand = Offset(x - dir * 16f, y - 48f)
        var rightHand = Offset(x + dir * 18f, y - 46f)
        var leftKnee = Offset(x - dir * 12f, y - 20f)
        var rightKnee = Offset(x + dir * 12f, y - 20f)
        var leftFoot = Offset(x - dir * 16f, y)
        var rightFoot = Offset(x + dir * 16f, y)

        // Anticipation (0.0 .. 0.35) -> Impact (0.35 .. 0.70) -> Recovery (0.70 .. 1.0)
        val phase = (actionTimer * 4f).coerceIn(0f, 1f)

        when (action) {
            StickmanAction.IDLE -> {
                val bob = sin(tick * 4f) * 2.2f
                headY += bob
                neckY += bob
                leftHand = Offset(x - dir * 14f, y - 44f + bob)
                rightHand = Offset(x + dir * 14f, y - 44f + bob)
                leftFoot = Offset(x - dir * 15f, y)
                rightFoot = Offset(x + dir * 15f, y)
            }
            StickmanAction.RUN -> {
                val legCycle = sin(tick * 13f)
                hipX += dir * 6f
                headX += dir * 12f
                leftHand = Offset(x - dir * legCycle * 26f, y - 50f)
                rightHand = Offset(x + dir * legCycle * 26f, y - 50f)
                leftFoot = Offset(x + dir * legCycle * 28f, y - (if (legCycle > 0) 14f else 0f))
                rightFoot = Offset(x - dir * legCycle * 28f, y - (if (legCycle < 0) 14f else 0f))
            }
            StickmanAction.JUMP, StickmanAction.DOUBLE_JUMP -> {
                headX += dir * 8f
                headY -= 6f
                neckY -= 6f
                hipY -= 10f
                leftHand = Offset(x - dir * 20f, y - 72f)
                rightHand = Offset(x + dir * 24f, y - 76f)
                leftKnee = Offset(x - dir * 10f, y - 35f)
                rightKnee = Offset(x + dir * 15f, y - 32f)
                leftFoot = Offset(x - dir * 14f, y - 22f)
                rightFoot = Offset(x + dir * 18f, y - 18f)
            }
            StickmanAction.FALL -> {
                leftHand = Offset(x - dir * 22f, y - 65f)
                rightHand = Offset(x + dir * 22f, y - 65f)
                leftFoot = Offset(x - dir * 18f, y - 10f)
                rightFoot = Offset(x + dir * 18f, y - 8f)
            }
            StickmanAction.DASH -> {
                headX += dir * 26f
                headY += 10f
                neckY += 10f
                hipX += dir * 10f
                leftHand = Offset(x - dir * 32f, y - 48f)
                rightHand = Offset(x + dir * 42f, y - 48f)
                leftFoot = Offset(x - dir * 35f, y - 15f)
                rightFoot = Offset(x + dir * 10f, y - 12f)
            }
            StickmanAction.PUNCH_1 -> {
                if (phase < 0.35f) {
                    // Anticipation: Cock arm back, crouch slightly
                    headX -= dir * 4f
                    headY += 2f
                    rightHand = Offset(x - dir * 10f, y - 50f)
                    leftHand = Offset(x + dir * 8f, y - 46f)
                } else if (phase < 0.75f) {
                    // Impact: Explosive thrust
                    headX += dir * 18f
                    leftHand = Offset(x - dir * 12f, y - 50f)
                    rightHand = Offset(x + dir * 48f, y - 52f)
                    leftFoot = Offset(x - dir * 22f, y)
                    rightFoot = Offset(x + dir * 18f, y)
                } else {
                    // Follow-through recovery
                    headX += dir * 8f
                    rightHand = Offset(x + dir * 32f, y - 48f)
                    leftHand = Offset(x - dir * 14f, y - 46f)
                }
            }
            StickmanAction.PUNCH_2 -> {
                if (phase < 0.35f) {
                    headX -= dir * 6f
                    leftHand = Offset(x - dir * 15f, y - 52f)
                    rightHand = Offset(x + dir * 10f, y - 48f)
                } else if (phase < 0.75f) {
                    headX += dir * 20f
                    leftHand = Offset(x + dir * 52f, y - 54f) // Powerful cross
                    rightHand = Offset(x - dir * 10f, y - 46f)
                    leftFoot = Offset(x - dir * 20f, y)
                    rightFoot = Offset(x + dir * 24f, y)
                } else {
                    headX += dir * 10f
                    leftHand = Offset(x + dir * 35f, y - 50f)
                    rightHand = Offset(x - dir * 12f, y - 46f)
                }
            }
            StickmanAction.KICK -> {
                if (phase < 0.35f) {
                    // Chambering leg
                    headX -= dir * 12f
                    rightFoot = Offset(x - dir * 4f, y - 28f)
                } else if (phase < 0.75f) {
                    // Full extension high roundhouse
                    headX -= dir * 10f
                    hipX += dir * 14f
                    leftHand = Offset(x - dir * 22f, y - 56f)
                    rightHand = Offset(x - dir * 16f, y - 50f)
                    leftFoot = Offset(x - dir * 15f, y)
                    rightFoot = Offset(x + dir * 60f, y - 50f)
                } else {
                    rightFoot = Offset(x + dir * 40f, y - 25f)
                }
            }
            StickmanAction.HEAVY_STRIKE -> {
                if (phase < 0.4f) {
                    // Big overhead windup
                    headX -= dir * 12f
                    headY -= 4f
                    leftHand = Offset(x - dir * 15f, y - 82f)
                    rightHand = Offset(x - dir * 8f, y - 86f)
                } else if (phase < 0.8f) {
                    // Ground slam impact
                    headX += dir * 24f
                    headY += 10f
                    leftHand = Offset(x + dir * 54f, y - 24f)
                    rightHand = Offset(x + dir * 60f, y - 26f)
                    leftFoot = Offset(x - dir * 28f, y)
                    rightFoot = Offset(x + dir * 34f, y)
                } else {
                    headX += dir * 16f
                    leftHand = Offset(x + dir * 45f, y - 36f)
                    rightHand = Offset(x + dir * 50f, y - 40f)
                }
            }
            StickmanAction.AIR_ATTACK -> {
                leftHand = Offset(x - dir * 18f, y - 65f)
                rightHand = Offset(x + dir * 42f, y - 30f)
                leftFoot = Offset(x - dir * 10f, y - 28f)
                rightFoot = Offset(x + dir * 54f, y - 14f)
            }
            StickmanAction.BLOCK -> {
                headX -= dir * 6f
                leftHand = Offset(x + dir * 16f, y - 62f)
                rightHand = Offset(x + dir * 18f, y - 54f)
                leftFoot = Offset(x - dir * 18f, y)
                rightFoot = Offset(x + dir * 12f, y)

                // Minimal clean shield barrier line
                drawScope.drawLine(
                    color = energyColor,
                    start = Offset(x + dir * 28f, y - 78f),
                    end = Offset(x + dir * 28f, y - 12f),
                    strokeWidth = 3.5f,
                    cap = StrokeCap.Round
                )
            }
            StickmanAction.COUNTER -> {
                headX += dir * 22f
                leftHand = Offset(x + dir * 48f, y - 55f)
                rightHand = Offset(x + dir * 48f, y - 45f)
            }
            StickmanAction.KNOCKBACK -> {
                headX -= dir * 22f
                headY -= 15f
                leftHand = Offset(x - dir * 30f, y - 60f)
                rightHand = Offset(x - dir * 25f, y - 50f)
                leftFoot = Offset(x + dir * 15f, y - 25f)
                rightFoot = Offset(x - dir * 20f, y - 10f)
            }
            StickmanAction.SPECIAL_CAST -> {
                headY -= 8f
                leftHand = Offset(x - dir * 28f, y - 75f)
                rightHand = Offset(x + dir * 42f, y - 75f)
            }
            StickmanAction.VICTORY -> {
                val armBob = sin(tick * 6f) * 4f
                leftHand = Offset(x - dir * 25f, y - 85f + armBob)
                rightHand = Offset(x + dir * 25f, y - 85f - armBob)
            }
            StickmanAction.DEFEAT -> {
                headY = y - 12f
                neckY = y - 8f
                hipY = y - 6f
                headX += dir * 30f
                leftHand = Offset(x - 20f, y - 4f)
                rightHand = Offset(x + 20f, y - 4f)
                leftFoot = Offset(x - 35f, y)
                rightFoot = Offset(x - 25f, y)
            }
            else -> {}
        }

        // Draw Limbs
        // Legs
        drawScope.drawLine(color, Offset(hipX, hipY), leftKnee, strokeW, StrokeCap.Round)
        drawScope.drawLine(color, leftKnee, leftFoot, strokeW, StrokeCap.Round)
        drawScope.drawLine(color, Offset(hipX, hipY), rightKnee, strokeW, StrokeCap.Round)
        drawScope.drawLine(color, rightKnee, rightFoot, strokeW, StrokeCap.Round)

        // Spine
        drawScope.drawLine(color, Offset(hipX, hipY), Offset(headX, neckY), strokeW + 0.8f, StrokeCap.Round)

        // Arms
        val shoulder = Offset(headX, neckY + 4f)
        drawScope.drawLine(color, shoulder, leftHand, strokeW, StrokeCap.Round)
        drawScope.drawLine(color, shoulder, rightHand, strokeW, StrokeCap.Round)

        // Soldier Weapons / Emblems
        if (isSoldier) {
            when (soldierStyle) {
                "SIGMA" -> {
                    // Heavy spiked mathematical gauntlets on fists
                    drawScope.drawCircle(color, radius = 7f, center = rightHand)
                    drawScope.drawCircle(energyColor, radius = 4f, center = rightHand)
                }
                "PI" -> {
                    // Pi arc trail at kicking foot
                    drawScope.drawCircle(color, radius = 5f, center = rightFoot)
                }
                "THETA" -> {
                    // Compass spear held in right hand
                    val spearStart = Offset(rightHand.x - dir * 25f, rightHand.y + 15f)
                    val spearTip = Offset(rightHand.x + dir * 55f, rightHand.y - 12f)
                    drawScope.drawLine(color, spearStart, spearTip, 3f, StrokeCap.Round)
                    drawScope.drawCircle(energyColor, radius = 4f, center = spearTip)
                }
                "DELTA" -> {
                    // Twin delta daggers in both hands
                    drawScope.drawLine(color, leftHand, Offset(leftHand.x + dir * 20f, leftHand.y - 10f), 3f, StrokeCap.Round)
                    drawScope.drawLine(color, rightHand, Offset(rightHand.x + dir * 24f, rightHand.y - 10f), 3f, StrokeCap.Round)
                }
            }
        } else {
            // Player Martial Headband ribbon flowing in the wind behind head
            val ribbon1 = Offset(headX - dir * 14f, headY - 2f)
            val ribbon2 = Offset(headX - dir * 26f, headY + sin(tick * 8f) * 4f)
            val ribbon3 = Offset(headX - dir * 36f, headY + sin(tick * 8f + 1f) * 6f)
            drawScope.drawLine(energyColor, ribbon1, ribbon2, 2.5f, StrokeCap.Round)
            drawScope.drawLine(energyColor, ribbon2, ribbon3, 2f, StrokeCap.Round)
        }

        // Head (filled circle with ocular eye)
        drawScope.drawCircle(
            color = color,
            radius = 12f,
            center = Offset(headX, headY),
            style = Stroke(width = strokeW)
        )
        // Visor / Eye
        val eyeX = headX + dir * 5f
        val eyeY = headY - 1f
        drawScope.drawCircle(
            color = energyColor,
            radius = 2.5f,
            center = Offset(eyeX, eyeY)
        )
    }

    private fun drawAttackSlash(
        drawScope: DrawScope,
        x: Float,
        y: Float,
        dir: Float,
        action: StickmanAction,
        color: Color
    ) {
        val slashPath = Path()
        when (action) {
            StickmanAction.PUNCH_1, StickmanAction.PUNCH_2 -> {
                slashPath.moveTo(x + dir * 15f, y - 70f)
                slashPath.quadraticTo(
                    x + dir * 58f, y - 50f,
                    x + dir * 20f, y - 30f
                )
            }
            StickmanAction.KICK -> {
                slashPath.moveTo(x + dir * 10f, y - 82f)
                slashPath.quadraticTo(
                    x + dir * 78f, y - 48f,
                    x + dir * 30f, y - 10f
                )
            }
            StickmanAction.HEAVY_STRIKE -> {
                slashPath.moveTo(x + dir * 15f, y - 92f)
                slashPath.quadraticTo(
                    x + dir * 92f, y - 45f,
                    x + dir * 35f, y + 4f
                )
            }
            StickmanAction.AIR_ATTACK -> {
                slashPath.moveTo(x + dir * 10f, y - 62f)
                slashPath.quadraticTo(
                    x + dir * 68f, y - 20f,
                    x + dir * 20f, y + 12f
                )
            }
            else -> return
        }

        drawScope.drawPath(
            path = slashPath,
            color = color.copy(alpha = 0.85f),
            style = Stroke(width = 3.5f, cap = StrokeCap.Round)
        )
    }
}
